package cn.edu.hdu.pestfcst.featureoptimizationservice.dao;/*
 * @Author : Vagrant
 * @Time: 2025-03-19 10:49
 * @File : FeatureOptimizationDataRepository.java
 * @Description :
 */

import cn.edu.hdu.pestfcst.featureoptimizationservice.bean.FeatureOptimizationDataSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeatureOptimizationDataRepository extends JpaRepository<FeatureOptimizationDataSet, Long> {
    /**
     * 根据用户ID查找特征优化数据集
     * @param userId 用户ID
     * @return 用户的特征优化数据集列表
     */
    List<FeatureOptimizationDataSet> findByUserId(Long userId);
    
    /**
     * 根据数据集名称和用户ID查找特征优化数据集
     * @param datasetName 数据集名称
     * @param userId 用户ID
     * @return 特征优化数据集列表
     */
    List<FeatureOptimizationDataSet> findByDatasetNameAndUserId(String datasetName, Long userId);
    
    /**
     * 根据优化方法和用户ID查找特征优化数据集
     * @param optimizationMethod 优化方法
     * @param userId 用户ID
     * @return 特征优化数据集列表
     */
    List<FeatureOptimizationDataSet> findByOptimizationMethodAndUserId(String optimizationMethod, Long userId);
}