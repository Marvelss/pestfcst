package cn.edu.hdu.pestfcst.modelbuildingservice.bean;/*
 * @Author : Vagrant
 * @Time: 2025-03-31 20:19
 * @File : ModelingRecord.java
 * @Description : 模型训练任务记录
 */

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "modeling_records")
public class ModelingRecord {
    @Id
    @Column(name = "model_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String modelId; // 将类型改为 String

    @Column(name = "model_method")
    private String modelMethod;

    @Column(name = "model_method_param")
    private String modelMethodParam;

    @Column(name = "features")
    private String features;

    @Column(name = "label")
    private String label;

    @Column(name = "evaluation_metrics")
    private String evaluationMetrics;

    @Column(name = "dataset_split_ratio")
    private String datasetSplitRatio;

    @Column(name = "model_structure")
    private String modelStructure;

    @Column(name = "training_result")
    private String trainingResult;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "model_status")
    private Integer modelStatus;  // 1: Pending, 2: Completed, 3: Failed
}