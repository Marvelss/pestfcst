package cn.edu.hdu.pestfcst.modelbuildingservice.service;


import cn.edu.hdu.pestfcst.modelbuildingservice.bean.ModelingRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface ModelBuildingDataSetService {
//    ModelBuildingDataSet getModelBuildingDataSetByID(String id);

//    ModelingRecord getModelBuildingRecordSetByID(String id);

//    List<ModelBuildingDataSet> getAllModelBuildingDataSets();

    void buildModel(Long userID);

    void saveBuildResult(String result) throws IOException;

    /**
     * 保存上传的文件
     * @param file 上传的文件
     * @return 保存后的文件路径
     * @throws IOException 文件操作异常
     */
    String saveUploadedFile(MultipartFile file) throws IOException;

//    ModelBuildingDataSet getModelBuildingDataSetByID(long id);
}
