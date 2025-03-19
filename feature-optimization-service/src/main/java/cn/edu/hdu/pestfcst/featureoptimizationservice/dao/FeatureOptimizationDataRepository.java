package cn.edu.hdu.pestfcst.featureoptimizationservice.dao;/*
 * @Author : Vagrant
 * @Time: 2025-03-19 10:49
 * @File : FeatureOptimizationDataRepository.java
 * @Description :
 */

import cn.edu.hdu.pestfcst.featureoptimizationservice.bean.FeatureOptimizationDataSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FeatureOptimizationDataRepository extends JpaRepository<FeatureOptimizationDataSet, Long> {
    // Spring Data JPA 会自动实现这个方法
}