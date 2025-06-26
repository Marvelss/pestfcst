package cn.edu.hdu.pestfcst.modelbuildingservice.service;

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.FileRecord;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileService {
    
    /**
     * 上传文件
     * @param file 上传的文件
     * @param userId 用户ID
     * @param description 文件描述
     * @return 文件记录信息
     */
    Map<String, Object> uploadFile(MultipartFile file, Long userId, String description);
    
    /**
     * 批量上传文件
     * @param files 文件数组
     * @param userId 用户ID
     * @return 上传结果
     */
    Map<String, Object> uploadMultipleFiles(MultipartFile[] files, Long userId);
    
    /**
     * 下载文件
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 文件资源
     */
    Resource downloadFile(Long fileId, Long userId);
    
    /**
     * 删除文件
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 删除结果
     */
    Map<String, Object> deleteFile(Long fileId, Long userId);
    
    /**
     * 获取用户文件列表
     * @param userId 用户ID
     * @param status 文件状态
     * @return 文件列表
     */
    List<FileRecord> getUserFiles(Long userId, String status);
    
    /**
     * 获取文件信息
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 文件信息
     */
    FileRecord getFileInfo(Long fileId, Long userId);
    
    /**
     * 获取用户文件统计信息
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getUserFileStats(Long userId);
    
    /**
     * 检查文件是否存在
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 是否存在
     */
    boolean fileExists(Long fileId, Long userId);
} 