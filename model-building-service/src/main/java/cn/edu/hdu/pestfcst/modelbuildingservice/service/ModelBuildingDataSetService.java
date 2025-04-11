package cn.edu.hdu.pestfcst.modelbuildingservice.service;


import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelingRecord;

import java.util.List;


public interface ModelBuildingDataSetService {
//    ModelBuildingDataSet getModelBuildingDataSetByID(String id);

//    ModelingRecord getModelBuildingRecordSetByID(String id);

//    List<ModelBuildingDataSet> getAllModelBuildingDataSets();

    void buildModel(ModelingRecord modelingInfo);

    void saveBuildResult(String result);

//    ModelBuildingDataSet getModelBuildingDataSetByID(long id);
}
