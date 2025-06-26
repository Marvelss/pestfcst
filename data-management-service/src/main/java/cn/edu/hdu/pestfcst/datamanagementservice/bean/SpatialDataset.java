package cn.edu.hdu.pestfcst.datamanagementservice.bean;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @Author : Claude
 * @Time: 2025-06-25
 * @File : SpatialDataset.java
 * @Description : 空间数据集实体类
 */
@Data
@Entity
@Table(name = "spatial_dataset")
public class SpatialDataset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String dataType; // 栅格数据、矢量数据

    @Column(nullable = false)
    private String category; // 气象数据、植保数据、地理遥感数据等

    @Column(nullable = false)
    private String filePath; // HDFS路径

    @Column
    private String fileFormat; // 文件格式：CSV、TIF、SHP等

    @Column
    private String metadataInfo; // 元数据信息（JSON格式）

    @Column
    private String spatialExtent; // 空间范围（GeoJSON格式）

    @Column
    private String temporalExtent; // 时间范围，例如"2023-01-01,2023-12-31"

    @Column
    private String description;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;
} 