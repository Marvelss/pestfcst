package cn.edu.hdu.pestfcst.preprocessingservice.bean;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @Author : Claude
 * @Time: 2025-06-25
 * @File : PreprocessingTask.java
 * @Description : 预处理任务实体类
 */
@Data
@Entity
@Table(name = "preprocessing_task")
public class PreprocessingTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long datasetId; // 关联的数据集ID

    @Column(nullable = false)
    private String taskType; // 任务类型：数据清洗、空间点提取、标准化等

    @Column
    private String taskParams; // 任务参数（JSON格式）

    @Column(nullable = false)
    private String status; // 任务状态：待处理、处理中、已完成、失败

    @Column
    private String statusMessage; // 状态信息或错误消息

    @Column
    private String inputPath; // 输入文件路径

    @Column
    private String outputPath; // 输出文件路径

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column
    private Integer progress; // 进度百分比：0-100
} 