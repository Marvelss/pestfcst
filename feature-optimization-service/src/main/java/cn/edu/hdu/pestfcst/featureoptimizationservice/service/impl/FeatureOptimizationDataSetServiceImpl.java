package cn.edu.hdu.pestfcst.featureoptimizationservice.service.impl;/*
 * @Author : Vagrant
 * @Time: 2025-03-19 10:43
 * @File : FeatureOptimizationDataSetServiceImpl.java
 * @Description :
 */

import cn.edu.hdu.pestfcst.featureoptimizationservice.bean.FeatureOptimizationDataSet;
import cn.edu.hdu.pestfcst.featureoptimizationservice.dao.FeatureOptimizationDataRepository;
import cn.edu.hdu.pestfcst.featureoptimizationservice.service.FeatureOptimizationDataSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FeatureOptimizationDataSetServiceImpl implements FeatureOptimizationDataSetService{

    @Autowired
    private FeatureOptimizationDataRepository featureOptimizationDataRepository;

    @Override
    public FeatureOptimizationDataSet getFeatureOptimizationDataSetByID(long id) {
        return featureOptimizationDataRepository.findById(id).orElseThrow(RuntimeException::new);
    }
}
