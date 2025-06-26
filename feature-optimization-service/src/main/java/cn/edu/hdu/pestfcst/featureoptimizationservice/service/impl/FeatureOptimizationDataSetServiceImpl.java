package cn.edu.hdu.pestfcst.featureoptimizationservice.service.impl;/*
 * @Author : Vagrant
 * @Time: 2025-03-19 10:43
 * @File : FeatureOptimizationDataSetServiceImpl.java
 * @Description :
 */

import cn.edu.hdu.pestfcst.featureoptimizationservice.bean.FeatureOptimizationDataSet;
import cn.edu.hdu.pestfcst.featureoptimizationservice.dao.FeatureOptimizationDataRepository;
import cn.edu.hdu.pestfcst.featureoptimizationservice.service.FeatureOptimizationDataSetService;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;
import weka.attributeSelection.ReliefFAttributeEval;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.attributeSelection.Ranker;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FeatureOptimizationDataSetServiceImpl implements FeatureOptimizationDataSetService {

    @Autowired
    private FeatureOptimizationDataRepository featureOptimizationDataRepository;

    @Override
    public FeatureOptimizationDataSet getFeatureOptimizationDataSetByID(long id) {
        return featureOptimizationDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("数据集不存在，ID: " + id));
    }

    @Override
    public FeatureOptimizationDataSet createFeatureOptimizationDataSet(FeatureOptimizationDataSet dataSet) {
        dataSet.setCreateTime(new Date());
        dataSet.setUpdateTime(new Date());
        dataSet.setStatus(0); // 初始状态：创建
        return featureOptimizationDataRepository.save(dataSet);
    }

    @Override
    public FeatureOptimizationDataSet updateFeatureOptimizationDataSet(FeatureOptimizationDataSet dataSet) {
        dataSet.setUpdateTime(new Date());
        return featureOptimizationDataRepository.save(dataSet);
    }

    @Override
    public List<FeatureOptimizationDataSet> getFeatureOptimizationDataSetsByUserId(long userId) {
        return featureOptimizationDataRepository.findByUserId(userId);
    }

    @Override
    public Map<String, Object> performTTest(String targetVariable, List<String> comparedVariables, 
                                          double threshold, String dataFilePath) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 读取Excel文件
            Map<String, List<Double>> dataMap = readExcelData(dataFilePath, comparedVariables, targetVariable);
            
            // 获取目标变量数据
            List<Double> targetData = dataMap.get(targetVariable);
            
            // 执行t检验
            Map<String, Double> pValues = new HashMap<>();
            TTest tTest = new TTest();
            
            for (String feature : comparedVariables) {
                List<Double> featureData = dataMap.get(feature);
                if (featureData != null && featureData.size() > 1) {
                    double pValue = tTest.tTest(
                            toDoubleArray(featureData),
                            toDoubleArray(targetData)
                    );
                    pValues.put(feature, pValue);
                }
            }
            
            // 筛选p值小于阈值的特征
            List<String> selectedFeatures = pValues.entrySet().stream()
                    .filter(entry -> entry.getValue() <= threshold)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            
            result.put("pValues", pValues);
            result.put("selectedFeatures", selectedFeatures);
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("执行T检验失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> performReliefF(String targetVariable, List<String> featureVariables,
                                            String selectionMethod, String selectionParam, String dataFilePath) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 将Excel转换为临时CSV文件，因为Weka更容易处理CSV
            String tempCsvPath = convertExcelToCsv(dataFilePath);
            
            // 加载CSV文件
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(tempCsvPath));
            Instances data = loader.getDataSet();
            
            // 设置目标变量为类属性
            int targetIndex = -1;
            for (int i = 0; i < data.numAttributes(); i++) {
                if (data.attribute(i).name().equals(targetVariable)) {
                    targetIndex = i;
                    break;
                }
            }
            
            if (targetIndex == -1) {
                throw new RuntimeException("找不到目标变量: " + targetVariable);
            }
            
            data.setClassIndex(targetIndex);
            
            // 配置ReliefF评估器
            ReliefFAttributeEval evaluator = new ReliefFAttributeEval();
            evaluator.setNumNeighbours(10);
            evaluator.buildEvaluator(data);
            
            // 使用Ranker进行特征排序
            Ranker ranker = new Ranker();
            ranker.setNumToSelect(-1); // 选择所有特征并排序
            
            // 计算特征重要性得分
            Map<String, Double> featureScores = new HashMap<>();
            for (int i = 0; i < data.numAttributes(); i++) {
                if (i != targetIndex) {
                    String featureName = data.attribute(i).name();
                    if (featureVariables.contains(featureName)) {
                        double score = evaluator.evaluateAttribute(i);
                        featureScores.put(featureName, score);
                    }
                }
            }
            
            // 按分数降序排序特征
            Map<String, Double> sortedFeatureScores = featureScores.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey, 
                            Map.Entry::getValue, 
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));
            
            // 根据选择方法和参数选择特征
            List<String> selectedFeatures;
            if ("按百分比选取".equals(selectionMethod)) {
                int percentage = Integer.parseInt(selectionParam);
                int numToSelect = (int) Math.ceil(sortedFeatureScores.size() * percentage / 100.0);
                selectedFeatures = sortedFeatureScores.keySet().stream()
                        .limit(numToSelect)
                        .collect(Collectors.toList());
            } else if ("按权重值计算".equals(selectionMethod)) {
                double threshold = Double.parseDouble(selectionParam);
                selectedFeatures = sortedFeatureScores.entrySet().stream()
                        .filter(entry -> entry.getValue() > threshold)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
            } else {
                throw new RuntimeException("不支持的选择方法: " + selectionMethod);
            }
            
            // 删除临时CSV文件
            Files.deleteIfExists(Paths.get(tempCsvPath));
            
            result.put("featureScores", sortedFeatureScores);
            result.put("selectedFeatures", selectedFeatures);
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("执行ReliefF失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> performPearson(String targetVariable, List<String> featureVariables,
                                            double threshold, String dataFilePath) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 读取Excel文件
            Map<String, List<Double>> dataMap = readExcelData(dataFilePath, featureVariables, targetVariable);
            
            // 获取目标变量数据
            List<Double> targetData = dataMap.get(targetVariable);
            
            // 执行Pearson相关性分析
            Map<String, Double> correlationValues = new HashMap<>();
            PearsonsCorrelation correlation = new PearsonsCorrelation();
            
            for (String feature : featureVariables) {
                List<Double> featureData = dataMap.get(feature);
                if (featureData != null && featureData.size() > 1) {
                    double correlationValue = correlation.correlation(
                            toDoubleArray(featureData),
                            toDoubleArray(targetData)
                    );
                    correlationValues.put(feature, Math.abs(correlationValue)); // 使用相关性绝对值
                }
            }
            
            // 筛选相关性高于阈值的特征
            List<String> selectedFeatures = correlationValues.entrySet().stream()
                    .filter(entry -> entry.getValue() >= threshold)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            
            result.put("correlationValues", correlationValues);
            result.put("selectedFeatures", selectedFeatures);
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("执行Pearson相关性分析失败: " + e.getMessage(), e);
        }
    }
    
    // 工具方法：将List<Double>转换为double[]
    private double[] toDoubleArray(List<Double> list) {
        double[] array = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
    
    // 工具方法：从Excel读取数据
    private Map<String, List<Double>> readExcelData(String filePath, List<String> features, String targetVariable) throws Exception {
        Map<String, List<Double>> dataMap = new HashMap<>();
        List<String> allColumns = new ArrayList<>(features);
        allColumns.add(targetVariable);
        
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // 读取表头行，找到列的索引
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnIndices = new HashMap<>();
            
            for (String column : allColumns) {
                for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                    Cell cell = headerRow.getCell(i);
                    if (cell != null && column.equals(cell.getStringCellValue())) {
                        columnIndices.put(column, i);
                        dataMap.put(column, new ArrayList<>());
                        break;
                    }
                }
            }
            
            // 检查是否所有需要的列都找到了
            for (String column : allColumns) {
                if (!columnIndices.containsKey(column)) {
                    throw new Exception("找不到列: " + column);
                }
            }
            
            // 读取数据行
            int rowCount = sheet.getLastRowNum();
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    for (String column : allColumns) {
                        int colIndex = columnIndices.get(column);
                        Cell cell = row.getCell(colIndex);
                        if (cell != null) {
                            double value;
                            switch (cell.getCellType()) {
                                case NUMERIC -> value = cell.getNumericCellValue();
                                case STRING -> value = Double.parseDouble(cell.getStringCellValue());
                                case BLANK -> value = 0.0;
                                default -> value = 0.0;
                            }
                            dataMap.get(column).add(value);
                        } else {
                            dataMap.get(column).add(0.0);
                        }
                    }
                }
            }
        }
        
        return dataMap;
    }
    
    // 工具方法：将Excel转换为CSV以便Weka处理
    private String convertExcelToCsv(String excelFilePath) throws Exception {
        String csvFilePath = excelFilePath.substring(0, excelFilePath.lastIndexOf('.')) + ".csv";
        
        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis);
             BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // 处理表头
            Row headerRow = sheet.getRow(0);
            StringBuilder header = new StringBuilder();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    header.append(cell.getStringCellValue());
                }
                if (i < headerRow.getLastCellNum() - 1) {
                    header.append(",");
                }
            }
            writer.write(header.toString());
            writer.newLine();
            
            // 处理数据行
            int rowCount = sheet.getLastRowNum();
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    StringBuilder rowData = new StringBuilder();
                    for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                        Cell cell = row.getCell(j);
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case NUMERIC -> rowData.append(cell.getNumericCellValue());
                                case STRING -> rowData.append(cell.getStringCellValue());
                                case BOOLEAN -> rowData.append(cell.getBooleanCellValue());
                                case FORMULA -> rowData.append(cell.getCellFormula());
                                default -> rowData.append("");
                            }
                        }
                        if (j < headerRow.getLastCellNum() - 1) {
                            rowData.append(",");
                        }
                    }
                    writer.write(rowData.toString());
                    writer.newLine();
                }
            }
        }
        
        return csvFilePath;
    }
}
