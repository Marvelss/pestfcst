package cn.edu.hdu.pestfcst.featureoptimizationservice.bean;/*
 * @Author : Vagrant
 * @Time: 2025-03-19 10:26
 * @File : FeatureOptimizationDataSet.java
 * @Description : 上一环节数据预览
 */


import lombok.*;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "feature_optimization_dataset")
public class FeatureOptimizationDataSet {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "dataset_name")
    private String datasetName;
    
    @Column(name = "file_path")
    private String filePath;
    
    @Column(name = "features_json", columnDefinition = "TEXT")
    private String featuresJson;
    
    @Column(name = "target_variable")
    private String targetVariable;
    
    @Column(name = "optimization_method")
    private String optimizationMethod; // tTest, ReliefF, Pearson
    
    @Column(name = "optimization_params", columnDefinition = "TEXT")
    private String optimizationParams;
    
    @Column(name = "result_features", columnDefinition = "TEXT")
    private String resultFeatures;
    
    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    
    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;
    
    @Column(name = "status")
    private Integer status; // 0: 创建, 1: 处理中, 2: 已完成, 3: 失败
    
    @Column(name = "user_id")
    private Long userId;
}
