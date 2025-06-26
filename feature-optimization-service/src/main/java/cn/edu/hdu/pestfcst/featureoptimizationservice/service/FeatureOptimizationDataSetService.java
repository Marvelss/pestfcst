package cn.edu.hdu.pestfcst.featureoptimizationservice.service;

import cn.edu.hdu.pestfcst.featureoptimizationservice.bean.FeatureOptimizationDataSet;
import java.util.List;
import java.util.Map;

public interface FeatureOptimizationDataSetService {
    
    /**
     * 根据ID获取特征优化数据集
     * @param id 数据集ID
     * @return 特征优化数据集
     */
    FeatureOptimizationDataSet getFeatureOptimizationDataSetByID(long id);
    
    /**
     * 创建一个新的特征优化任务
     * @param dataSet 特征优化数据集
     * @return 创建后的数据集（包含ID）
     */
    FeatureOptimizationDataSet createFeatureOptimizationDataSet(FeatureOptimizationDataSet dataSet);
    
    /**
     * 更新特征优化数据集
     * @param dataSet 特征优化数据集
     * @return 更新后的数据集
     */
    FeatureOptimizationDataSet updateFeatureOptimizationDataSet(FeatureOptimizationDataSet dataSet);
    
    /**
     * 根据用户ID获取其所有特征优化数据集
     * @param userId 用户ID
     * @return 特征优化数据集列表
     */
    List<FeatureOptimizationDataSet> getFeatureOptimizationDataSetsByUserId(long userId);
    
    /**
     * 执行T检验特征优化
     * @param targetVariable 目标变量
     * @param comparedVariables 比较变量列表
     * @param threshold P值阈值
     * @param dataFilePath 数据文件路径
     * @return 优化结果，包含每个特征的P值和选择的特征列表
     */
    Map<String, Object> performTTest(String targetVariable, List<String> comparedVariables, 
                                     double threshold, String dataFilePath);
    
    /**
     * 执行ReliefF特征优化
     * @param targetVariable 目标变量
     * @param featureVariables 特征变量列表
     * @param selectionMethod 选择方法（按百分比选取/按权重值计算）
     * @param selectionParam 选择参数（百分比或阈值）
     * @param dataFilePath 数据文件路径
     * @return 优化结果，包含特征重要性得分和选择的特征列表
     */
    Map<String, Object> performReliefF(String targetVariable, List<String> featureVariables, 
                                      String selectionMethod, String selectionParam, String dataFilePath);
    
    /**
     * 执行Pearson相关性特征优化
     * @param targetVariable 目标变量
     * @param featureVariables 特征变量列表
     * @param threshold 相关性阈值
     * @param dataFilePath 数据文件路径
     * @return 优化结果，包含相关性系数和选择的特征列表
     */
    Map<String, Object> performPearson(String targetVariable, List<String> featureVariables, 
                                      double threshold, String dataFilePath);
}
