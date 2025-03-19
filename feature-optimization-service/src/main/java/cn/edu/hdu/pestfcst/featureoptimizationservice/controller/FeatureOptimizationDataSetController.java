package cn.edu.hdu.pestfcst.featureoptimizationservice.controller;/*
 * @Author : Vagrant
 * @Time: 2025-03-19 10:29
 * @File : FeatureOptimizationDataSetController.java
 * @Description :
 */

import cn.edu.hdu.pestfcst.featureoptimizationservice.bean.FeatureOptimizationDataSet;
import cn.edu.hdu.pestfcst.featureoptimizationservice.service.FeatureOptimizationDataSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feature-optimization-dataset")
public class FeatureOptimizationDataSetController {
    @Autowired
    private FeatureOptimizationDataSetService featureOptimizationDataSetService;

    @GetMapping("/user/{id}")
    public FeatureOptimizationDataSet getFeatureOptimizationDataSetById (@PathVariable long id) {
        return featureOptimizationDataSetService.getFeatureOptimizationDataSetByID(id);
    }
}
