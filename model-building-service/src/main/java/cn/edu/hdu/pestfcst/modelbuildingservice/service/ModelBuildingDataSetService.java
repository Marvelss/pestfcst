package cn.edu.hdu.pestfcst.modelbuildingservice.service;


import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelingRecord;

import java.io.IOException;
import java.util.List;


public interface ModelBuildingDataSetService {
//    ModelBuildingDataSet getModelBuildingDataSetByID(String id);

//    ModelingRecord getModelBuildingRecordSetByID(String id);

//    List<ModelBuildingDataSet> getAllModelBuildingDataSets();

    void buildModel(String userID);

    void saveBuildResult(String result) throws IOException;

//    ModelBuildingDataSet getModelBuildingDataSetByID(long id);
}
