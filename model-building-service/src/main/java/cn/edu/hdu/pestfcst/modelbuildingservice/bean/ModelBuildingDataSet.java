package cn.edu.hdu.pestfcst.modelbuildingservice.bean;/*
 * @Author : Vagrant
 * @Time: 2025-03-26 10:26
 * @File : ModelBuildingDataSet.java
 * @Description : 上一环节-特征优选数据预览
 */


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tb_user")
public class ModelBuildingDataSet {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "address")
    private String address;
}
