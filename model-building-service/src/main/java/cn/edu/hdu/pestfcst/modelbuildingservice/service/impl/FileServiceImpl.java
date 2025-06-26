package cn.edu.hdu.pestfcst.modelbuildingservice.service.impl;

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.FileRecord;
import cn.edu.hdu.pestfcst.modelbuildingservice.dao.FileRecordRepository;
import cn.edu.hdu.pestfcst.modelbuildingservice.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRecordRepository fileRecordRepository;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.max-size:10485760}") // 10MB default
    private long maxFileSize;

    @Override
    public Map<String, Object> uploadFile(MultipartFile file, Long userId, String description) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                result.put("success", false);
                result.put("message", "文件为空");
                return result;
            }

            if (file.getSize() > maxFileSize) {
                result.put("success", false);
                result.put("message", "文件大小超过限制");
                return result;
            }

            // 创建用户目录
            Path userUploadPath = Paths.get(uploadPath, "user_" + userId);
            if (!Files.exists(userUploadPath)) {
                Files.createDirectories(userUploadPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String timestamp = String.valueOf(System.currentTimeMillis());
            String storedFilename = timestamp + "_" + originalFilename;
            Path filePath = userUploadPath.resolve(storedFilename);

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            // 创建文件记录
            FileRecord fileRecord = new FileRecord();
            fileRecord.setUserId(userId);
            fileRecord.setOriginalFilename(originalFilename);
            fileRecord.setStoredFilename(storedFilename);
            fileRecord.setFilePath(filePath.toString());
            fileRecord.setFileSize(file.getSize());
            fileRecord.setFileType(fileExtension);
            fileRecord.setDescription(description);
            fileRecord.setUploadTime(LocalDateTime.now());
            fileRecord.setStatus("active");

            FileRecord savedRecord = fileRecordRepository.save(fileRecord);

            // 返回结果
            result.put("success", true);
            result.put("message", "文件上传成功");
            result.put("fileId", savedRecord.getId());
            result.put("fileName", originalFilename);
            result.put("fileSize", file.getSize());
            result.put("uploadTime", savedRecord.getUploadTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "文件上传失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, Object> uploadMultipleFiles(MultipartFile[] files, Long userId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> uploadResults = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        try {
            for (MultipartFile file : files) {
                Map<String, Object> fileResult = uploadFile(file, userId, null);
                uploadResults.add(fileResult);
                
                if ((Boolean) fileResult.get("success")) {
                    successCount++;
                } else {
                    failCount++;
                }
            }

            result.put("success", true);
            result.put("message", String.format("批量上传完成，成功: %d, 失败: %d", successCount, failCount));
            result.put("totalFiles", files.length);
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("uploadResults", uploadResults);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量上传失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Resource downloadFile(Long fileId, Long userId) {
        try {
            FileRecord fileRecord = fileRecordRepository.findById(fileId).orElse(null);
            
            if (fileRecord == null || !fileRecord.getUserId().equals(userId)) {
                return null;
            }

            Path filePath = Paths.get(fileRecord.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
        } catch (MalformedURLException e) {
            // 记录错误日志
        }
        
        return null;
    }

    @Override
    public Map<String, Object> deleteFile(Long fileId, Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            FileRecord fileRecord = fileRecordRepository.findById(fileId).orElse(null);
            
            if (fileRecord == null || !fileRecord.getUserId().equals(userId)) {
                result.put("success", false);
                result.put("message", "文件不存在或无权限");
                return result;
            }

            // 软删除：更新状态为deleted
            fileRecord.setStatus("deleted");
            fileRecordRepository.save(fileRecord);

            // 可选：物理删除文件
            // Path filePath = Paths.get(fileRecord.getFilePath());
            // Files.deleteIfExists(filePath);

            result.put("success", true);
            result.put("message", "文件删除成功");

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "文件删除失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public List<FileRecord> getUserFiles(Long userId, String status) {
        if ("active".equals(status)) {
            return fileRecordRepository.findByUserIdAndStatusOrderByUploadTimeDesc(userId, status);
        } else {
            return fileRecordRepository.findByUserIdOrderByUploadTimeDesc(userId);
        }
    }

    @Override
    public FileRecord getFileInfo(Long fileId, Long userId) {
        FileRecord fileRecord = fileRecordRepository.findById(fileId).orElse(null);
        if (fileRecord != null && fileRecord.getUserId().equals(userId)) {
            return fileRecord;
        }
        return null;
    }

    @Override
    public Map<String, Object> getUserFileStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        long fileCount = fileRecordRepository.countByUserIdAndStatusActive(userId);
        long totalSize = fileRecordRepository.sumFileSizeByUserIdAndStatusActive(userId);
        
        stats.put("fileCount", fileCount);
        stats.put("totalSize", totalSize);
        stats.put("totalSizeMB", String.format("%.2f", totalSize / (1024.0 * 1024.0)));
        
        return stats;
    }

    @Override
    public boolean fileExists(Long fileId, Long userId) {
        FileRecord fileRecord = fileRecordRepository.findById(fileId).orElse(null);
        return fileRecord != null && fileRecord.getUserId().equals(userId) && "active".equals(fileRecord.getStatus());
    }
} 