package cn.edu.hdu.pestfcst.modelbuildingservice.dao;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:26
 * @File : ModelBuildingDataRepository.java
 * @Description :
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ModelingRecordRepository extends JpaRepository<ModelingRecord, String> {
    // Spring Data JPA 会自动实现这个方法
//    根据userId查询所有记录
    List<ModelingRecord> findByUserId(Long userId);

}