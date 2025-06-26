package cn.edu.hdu.pestfcst.datamanagementservice.service.impl;

import cn.edu.hdu.pestfcst.datamanagementservice.bean.SpatialDataset;
import cn.edu.hdu.pestfcst.datamanagementservice.dao.SpatialDatasetRepository;
import cn.edu.hdu.pestfcst.datamanagementservice.service.SpatialDatasetService;
import cn.edu.hdu.pestfcst.datamanagementservice.util.HDFSUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * @Author : Claude
 * @Time: 2025-06-25
 * @File : SpatialDatasetServiceImpl.java
 * @Description : 空间数据集服务实现类
 */
@Service
public class SpatialDatasetServiceImpl implements SpatialDatasetService {
    @Autowired
    private SpatialDatasetRepository datasetRepository;

    @Autowired
    private HDFSUtil hdfsUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public SpatialDataset uploadDataset(Long userId, MultipartFile file, String dataType, String category, Map<String, Object> metadata) throws IOException {
        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String relativePath = "/" + userId + "/" + dataType + "/" + fileName;
        String hdfsPath = hdfsUtil.getFullPath(relativePath);

        // 上传文件到HDFS
        hdfsUtil.uploadFile(file, hdfsPath);

        // 创建数据集记录
        SpatialDataset dataset = new SpatialDataset();
        dataset.setUserId(userId);
        dataset.setName(file.getOriginalFilename());
        dataset.setDataType(dataType);
        dataset.setCategory(category);
        dataset.setFilePath(relativePath);
        dataset.setFileFormat(getFileExtension(file.getOriginalFilename()));
        dataset.setCreateTime(LocalDateTime.now());
        
        // 设置元数据
        if (metadata != null && !metadata.isEmpty()) {
            dataset.setMetadataInfo(objectMapper.writeValueAsString(metadata));
        }
        
        // 如果元数据中包含空间和时间信息，设置相应字段
        if (metadata != null) {
            if (metadata.containsKey("spatialExtent")) {
                dataset.setSpatialExtent(objectMapper.writeValueAsString(metadata.get("spatialExtent")));
            }
            if (metadata.containsKey("temporalExtent")) {
                dataset.setTemporalExtent(metadata.get("temporalExtent").toString());
            }
            if (metadata.containsKey("description")) {
                dataset.setDescription(metadata.get("description").toString());
            }
        }
        
        return datasetRepository.save(dataset);
    }

    @Override
    public SpatialDataset getDatasetById(Long datasetId) {
        return datasetRepository.findById(datasetId)
                .orElseThrow(() -> new NoSuchElementException("数据集不存在: " + datasetId));
    }

    @Override
    public List<SpatialDataset> getDatasetsByUserId(Long userId) {
        return datasetRepository.findByUserId(userId);
    }

    @Override
    public List<SpatialDataset> getDatasetsByUserIdAndType(Long userId, String dataType) {
        return datasetRepository.findByUserIdAndDataType(userId, dataType);
    }

    @Override
    public List<SpatialDataset> getDatasetsByUserIdAndCategory(Long userId, String category) {
        return datasetRepository.findByUserIdAndCategory(userId, category);
    }

    @Override
    public void downloadDataset(Long datasetId, String localPath) throws IOException {
        SpatialDataset dataset = getDatasetById(datasetId);
        String hdfsPath = hdfsUtil.getFullPath(dataset.getFilePath());
        hdfsUtil.downloadFile(hdfsPath, localPath);
    }

    @Override
    @Transactional
    public boolean deleteDataset(Long datasetId) throws IOException {
        SpatialDataset dataset = getDatasetById(datasetId);
        String hdfsPath = hdfsUtil.getFullPath(dataset.getFilePath());
        
        // 删除HDFS上的文件
        boolean deleted = hdfsUtil.deleteFile(hdfsPath);
        
        if (deleted) {
            // 删除数据库记录
            datasetRepository.deleteById(datasetId);
        }
        
        return deleted;
    }

    @Override
    public List<String> readDatasetContent(Long datasetId, int limit) throws IOException {
        SpatialDataset dataset = getDatasetById(datasetId);
        String hdfsPath = hdfsUtil.getFullPath(dataset.getFilePath());
        
        // 只读取文本格式文件
        String format = dataset.getFileFormat();
        if (format != null && (format.equalsIgnoreCase("csv") || format.equalsIgnoreCase("txt"))) {
            List<String> lines = hdfsUtil.readFile(hdfsPath);
            
            // 如果有限制行数且行数大于限制，则截取
            if (limit > 0 && lines.size() > limit) {
                return lines.subList(0, limit);
            }
            
            return lines;
        } else {
            throw new UnsupportedOperationException("不支持读取此类型的文件内容: " + format);
        }
    }
    
    /**
     * 获取文件扩展名
     * @param fileName 文件名
     * @return 扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
} 