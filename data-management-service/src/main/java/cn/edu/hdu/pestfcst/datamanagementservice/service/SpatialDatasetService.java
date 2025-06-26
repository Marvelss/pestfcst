package cn.edu.hdu.pestfcst.datamanagementservice.service;

import cn.edu.hdu.pestfcst.datamanagementservice.bean.SpatialDataset;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author : Claude
 * @Time: 2025-06-25
 * @File : SpatialDatasetService.java
 * @Description : 空间数据集服务接口
 */
public interface SpatialDatasetService {
    /**
     * 上传空间数据集
     * @param userId 用户ID
     * @param file 文件
     * @param dataType 数据类型
     * @param category 数据类别
     * @param metadata 元数据
     * @return 创建的数据集
     * @throws IOException IO异常
     */
    SpatialDataset uploadDataset(Long userId, MultipartFile file, String dataType, 
                                String category, Map<String, Object> metadata) throws IOException;
    
    /**
     * 根据ID获取数据集
     * @param datasetId 数据集ID
     * @return 数据集
     */
    SpatialDataset getDatasetById(Long datasetId);
    
    /**
     * 获取用户的所有数据集
     * @param userId 用户ID
     * @return 数据集列表
     */
    List<SpatialDataset> getDatasetsByUserId(Long userId);
    
    /**
     * 根据类型获取用户的数据集
     * @param userId 用户ID
     * @param dataType 数据类型
     * @return 数据集列表
     */
    List<SpatialDataset> getDatasetsByUserIdAndType(Long userId, String dataType);
    
    /**
     * 根据类别获取用户的数据集
     * @param userId 用户ID
     * @param category 数据类别
     * @return 数据集列表
     */
    List<SpatialDataset> getDatasetsByUserIdAndCategory(Long userId, String category);
    
    /**
     * 下载数据集文件
     * @param datasetId 数据集ID
     * @param localPath 本地保存路径
     * @throws IOException IO异常
     */
    void downloadDataset(Long datasetId, String localPath) throws IOException;
    
    /**
     * 删除数据集
     * @param datasetId 数据集ID
     * @return 是否删除成功
     * @throws IOException IO异常
     */
    boolean deleteDataset(Long datasetId) throws IOException;
    
    /**
     * 读取数据集内容（适用于CSV等文本格式）
     * @param datasetId 数据集ID
     * @param limit 读取行数限制，-1表示全部读取
     * @return 数据内容
     * @throws IOException IO异常
     */
    List<String> readDatasetContent(Long datasetId, int limit) throws IOException;
} 