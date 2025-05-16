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
@Table(name = "modeling_tasks")
public class ModelingRecord {
    @Id
    @Column(name = "model_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String modelId; // 将类型改为 String

    @Column(name = "model_data", columnDefinition = "TEXT")
    private String modelData;

    @Column(name = "model_method_name", nullable = false)
    private String modelMethod;

    @Column(name = "model_method_param", columnDefinition = "jsonb")
    private String modelMethodParam; // 存储序列化的JSON字符串

    @Column(name = "features", columnDefinition = "TEXT")
    private String features; // 将 List 转换为 JSON 字符串

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "evaluation_metrics", columnDefinition = "jsonb")
    private String evaluationMetrics; // 存储序列化的JSON字符串

    @Column(name = "dataset_split_ratio")
    private String datasetSplitRatio;

    @Column(name = "model_structure", columnDefinition = "TEXT")
    private String modelStructure;

    @Column(name = "training_result", columnDefinition = "TEXT")
    private String trainingResult;

    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(name = "model_status", nullable = false)
    private Integer modelStatus;// 1: Pending, 2: Completed, 3: Failed
}