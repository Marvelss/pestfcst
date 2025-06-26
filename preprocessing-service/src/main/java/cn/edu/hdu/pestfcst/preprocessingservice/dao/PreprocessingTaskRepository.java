package cn.edu.hdu.pestfcst.preprocessingservice.dao;

import cn.edu.hdu.pestfcst.preprocessingservice.bean.PreprocessingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author : Claude
 * @Time: 2025-06-25
 * @File : PreprocessingTaskRepository.java
 * @Description : 预处理任务数据访问层
 */
@Repository
public interface PreprocessingTaskRepository extends JpaRepository<PreprocessingTask, Long> {
    List<PreprocessingTask> findByUserId(Long userId);
    
    List<PreprocessingTask> findByUserIdAndTaskType(Long userId, String taskType);
    
    List<PreprocessingTask> findByUserIdAndStatus(Long userId, String status);
    
    List<PreprocessingTask> findByDatasetId(Long datasetId);
    
    @Query("SELECT t FROM PreprocessingTask t WHERE t.status = 'PENDING' ORDER BY t.createTime ASC")
    List<PreprocessingTask> findPendingTasks();
} 