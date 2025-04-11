package cn.edu.hdu.pestfcst.modelbuildingservice.bean;/*
 * @Author : Vagrant
 * @Time: 2025-03-31 20:19
 * @File : ModelingRecord.java
 * @Description : 模型训练任务记录
 */

import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@Table(name = "modeling_records")
public class ModelingRecord {
    @Id
    @Column(name = "model_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long modelId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "feature_optimization_id")
    private Long featureOptimizationId;
    @Column(name = "model_method")
    private String modelMethod;
    @Column(name = "model_method_param")
    private String modelMethodParam;
    @Column(name = "model_status", columnDefinition = "TINYINT")
    private Integer modelStatus;  // 推荐使用包装类型Integer
}
