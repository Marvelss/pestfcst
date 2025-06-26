package cn.edu.hdu.pestfcst.modelbuildingservice.dao;

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {
    
    /**
     * 根据用户ID查找所有活跃的文件记录
     */
    List<FileRecord> findByUserIdAndStatusOrderByUploadTimeDesc(Long userId, String status);
    
    /**
     * 根据用户ID查找所有文件记录
     */
    List<FileRecord> findByUserIdOrderByUploadTimeDesc(Long userId);
    
    /**
     * 根据存储文件名查找文件记录
     */
    Optional<FileRecord> findByStoredFilename(String storedFilename);
    
    /**
     * 根据原始文件名和用户ID查找文件记录
     */
    List<FileRecord> findByOriginalFilenameAndUserId(String originalFilename, Long userId);
    
    /**
     * 统计用户文件数量
     */
    @Query("SELECT COUNT(f) FROM FileRecord f WHERE f.userId = :userId AND f.status = 'active'")
    long countByUserIdAndStatusActive(@Param("userId") Long userId);
    
    /**
     * 统计用户文件总大小
     */
    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM FileRecord f WHERE f.userId = :userId AND f.status = 'active'")
    long sumFileSizeByUserIdAndStatusActive(@Param("userId") Long userId);
    
    /**
     * 根据文件类型查找文件
     */
    List<FileRecord> findByUserIdAndFileTypeAndStatusOrderByUploadTimeDesc(Long userId, String fileType, String status);
} 