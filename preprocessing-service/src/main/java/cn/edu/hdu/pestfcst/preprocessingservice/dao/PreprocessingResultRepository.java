package cn.edu.hdu.pestfcst.preprocessingservice.dao;

import cn.edu.hdu.pestfcst.preprocessingservice.bean.PreprocessingResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author : Claude
 * @Time: 2025-06-25
 * @File : PreprocessingResultRepository.java
 * @Description : 预处理结果数据访问层
 */
@Repository
public interface PreprocessingResultRepository extends JpaRepository<PreprocessingResult, Long> {
    List<PreprocessingResult> findByUserId(Long userId);
    
    List<PreprocessingResult> findByUserIdAndResultType(Long userId, String resultType);
    
    List<PreprocessingResult> findByTaskId(Long taskId);
} 