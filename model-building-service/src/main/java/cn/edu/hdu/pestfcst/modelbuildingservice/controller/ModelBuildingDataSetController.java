package cn.edu.hdu.pestfcst.modelbuildingservice.controller;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:26
 * @File : ModelBuildingDataSetController.java
 * @Description :
 */

import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelBuildingDataSet;
import cn.edu.hdu.pestfcst.modelbuildingservice.service.ModelBuildingDataSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/model-building-dataset")
public class ModelBuildingDataSetController {
    @Autowired
    private ModelBuildingDataSetService ModelBuildingDataSetService;

    @GetMapping("/username/{id}")
    public ModelBuildingDataSet getModelBuildingDataSetById (@PathVariable long id) {
        return ModelBuildingDataSetService.getModelBuildingDataSetByID(id);
    }
}
