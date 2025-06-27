package cn.edu.hdu.pestfcst.modelbuildingservice.bean;/*
 * @Author : Vagrant
 * @Time: 2025-03-31 20:19
 * @File : ModelingRecord.java
 * @Description : 模型训练任务记录
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@Data
@Entity
@Table(name = "modeling_tasks")
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class ModelingRecord {
    @Id
    @Column(name = "model_id", length = 16, nullable = false)
    private String modelId;

    @PrePersist
    public void generateId() {
        if (this.modelId == null) {
            this.modelId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }
    }

    @Column(name = "model_data", columnDefinition = "TEXT")
    private String modelData;

    @Column(name = "model_method_name", nullable = false)
    private String modelMethodName;

    @Type(type = "jsonb")
    @Column(name = "model_method_param", columnDefinition = "json")
    private JsonNode modelMethodParam;

    @Column(name = "features", columnDefinition = "TEXT")
    private String features; // 存储为JSON字符串

    @Column(name = "label", nullable = false)
    private String label;

    @Type(type = "jsonb")
    @Column(name = "evaluation_metrics", columnDefinition = "json")
    private JsonNode evaluationMetrics;

    @Column(name = "dataset_split_ratio")
    private String datasetSplitRatio;

    @Column(name = "model_structure", columnDefinition = "TEXT")
    private String modelStructure;

    @Column(name = "training_result", columnDefinition = "TEXT")
    private String trainingResult;

    @Column(name = "create_time", updatable = false)
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 添加日期时间格式化注解
    @JsonIgnore
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(name = "model_status", nullable = false)
    private Integer modelStatus;// 1: Pending, 2: Handling, 3: Success, 4: Failed

    @Column(name = "user_id")
    private Long userId;
}