package cn.edu.hdu.pestfcst.modelbuildingservice.dao;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:26
 * @File : ModelBuildingDataRepository.java
 * @Description :
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ModelingRecordRepository extends JpaRepository<ModelingRecord, String> {
    // Spring Data JPA 会自动实现这个方法
}