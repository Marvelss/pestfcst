package cn.edu.hdu.pestfcst.preprocessingservice.bean;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @Author : Claude
 * @Time: 2025-06-25
 * @File : PreprocessingResult.java
 * @Description : 预处理结果实体类
 */
@Data
@Entity
@Table(name = "preprocessing_result")
public class PreprocessingResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long taskId; // 关联的预处理任务ID

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String resultType; // 结果类型：点数据、清洗后数据、标准化数据等

    @Column
    private String resultPath; // 结果文件路径

    @Column
    private String metadataInfo; // 元数据信息（JSON格式）

    @Column(nullable = false)
    private Integer rowCount; // 数据行数

    @Column
    private Integer columnCount; // 数据列数

    @Column
    private String spatialInfo; // 空间信息（GeoJSON格式）

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column
    private String description;
} 