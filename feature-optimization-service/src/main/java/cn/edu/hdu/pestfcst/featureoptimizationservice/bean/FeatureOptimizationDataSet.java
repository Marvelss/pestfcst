package cn.edu.hdu.pestfcst.featureoptimizationservice.bean;/*
 * @Author : Vagrant
 * @Time: 2025-03-19 10:26
 * @File : FeatureOptimizationDataSet.java
 * @Description : 上一环节数据预览
 */


import lombok.*;
import javax.persistence.*;

@Data
@Entity
@Table(name = "tb_order")
public class FeatureOptimizationDataSet {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "price")
    private Long price;
    @Column(name = "name")
    private String name;
    @Column(name = "num")
    private Integer num;
    @Column(name = "user_id")
    private Long userId;
}
