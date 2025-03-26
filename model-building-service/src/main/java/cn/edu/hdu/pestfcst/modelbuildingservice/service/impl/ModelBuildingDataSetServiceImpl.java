package cn.edu.hdu.pestfcst.modelbuildingservice.service.impl;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:26
 * @File : ModelBuildingDataSetServiceImpl.java
 * @Description :
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelBuildingDataSet;
import cn.edu.hdu.pestfcst.modelbuildingservice.dao.ModelBuildingDataRepository;
import cn.edu.hdu.pestfcst.modelbuildingservice.service.ModelBuildingDataSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ModelBuildingDataSetServiceImpl implements ModelBuildingDataSetService {

    @Autowired
    private ModelBuildingDataRepository modelBuildingDataRepository;

    @Override
    public List<ModelBuildingDataSet> getAllModelBuildingDataSets() {
        return modelBuildingDataRepository.findAll();
    }

    @Override
    public ModelBuildingDataSet getModelBuildingDataSetByID(String id) {
        return modelBuildingDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service-ModelBuildingDataSet not found with ID: " + id));

    }

//    @Autowired
//    private ModelBuildingDataRepository ModelBuildingDataRepository;
//
//    @Override
//    public ModelBuildingDataSet getModelBuildingDataSetByID(long id) {
//        return ModelBuildingDataRepository.findById(id).orElseThrow(RuntimeException::new);
//    }
}
