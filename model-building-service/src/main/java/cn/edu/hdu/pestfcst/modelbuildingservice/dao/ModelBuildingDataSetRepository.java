package cn.edu.hdu.pestfcst.modelbuildingservice.dao;

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author : Vagrant
 * @Time: 2025-04-18 8:52
 * @File : ModelingRecordRepository.java
 * @Description :
 */

@Repository
public interface ModelBuildingDataSetRepository extends JpaRepository<ModelingRecord, Long> {
    // Spring Data JPA 会自动实现这个方法
}
