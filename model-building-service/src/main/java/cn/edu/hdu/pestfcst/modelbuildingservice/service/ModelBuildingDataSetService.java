package cn.edu.hdu.pestfcst.modelbuildingservice.service;


import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelBuildingDataSet;

import java.util.List;


public interface ModelBuildingDataSetService {
    ModelBuildingDataSet getModelBuildingDataSetByID(String id);
    List<ModelBuildingDataSet> getAllModelBuildingDataSets();
//    ModelBuildingDataSet getModelBuildingDataSetByID(long id);
}
