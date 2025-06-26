package cn.edu.hdu.pestfcst.datamanagementservice.dao;

import cn.edu.hdu.pestfcst.datamanagementservice.bean.SpatialDataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author : Claude
 * @Time: 2025-06-25
 * @File : SpatialDatasetRepository.java
 * @Description : 空间数据集数据访问层
 */
@Repository
public interface SpatialDatasetRepository extends JpaRepository<SpatialDataset, Long> {
    List<SpatialDataset> findByUserId(Long userId);

    List<SpatialDataset> findByDataType(String dataType);

    List<SpatialDataset> findByCategory(String category);

    @Query("SELECT s FROM SpatialDataset s WHERE s.userId = ?1 AND s.dataType = ?2")
    List<SpatialDataset> findByUserIdAndDataType(Long userId, String dataType);

    @Query("SELECT s FROM SpatialDataset s WHERE s.userId = ?1 AND s.category = ?2")
    List<SpatialDataset> findByUserIdAndCategory(Long userId, String category);
} 