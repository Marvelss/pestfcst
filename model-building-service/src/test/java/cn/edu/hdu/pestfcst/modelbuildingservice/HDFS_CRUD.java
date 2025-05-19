package cn.edu.hdu.pestfcst.modelbuildingservice;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @Author : Vagrant
 * @Time: 2025-05-12 15:23
 * @File : HDFS_CRUD.java
 * @Description :
 */

public class HDFS_CRUD {
    FileSystem fs = null;
    // Junit单元测试框架中控制程序最先执行的注解，保证init()最先执行
    @Before
    public void init() throws Exception{
        System.setProperty("HADOOP_USER_NAME","Vagrant");
        // 创建Configuration类的对象conf，指定HDFS的配置信息
        Configuration conf = new Configuration();
        // set()指定HDFS中NameNode结点的通信地址（core-site.xml）
        conf.set("fs.defaultFS","hdfs://localhost:9000");
        // 指定拥有操作权限的用户root
        // 获取conf对象指定的配置信息，并创建HDFS的对象fs
        fs = FileSystem.get(conf);
    }
    @Test
    public void testListHdfsDirectory() throws IOException {
        // 指定HDFS目录地址
        Path dirPath = new Path("/data");

        System.out.println("Listing contents of HDFS directory: " + dirPath);

        // 获取目录中的文件和子目录
        FileStatus[] fileStatuses = fs.listStatus(dirPath);
        for (FileStatus fileStatus : fileStatuses) {
            System.out.println("File/Directory: " + fileStatus.getPath() + " (isDirectory: " + fileStatus.isDirectory() + ")");
        }

        // 关闭资源
        fs.close();
    }


    // 注解Test用于在Junit单元测试框架中测试方法testAddFileToHdfs()
    @Test
    public void testAddFileToHdfs() throws IOException {
        // 指定本地文件上传地址
        Path src = new Path("E:/a_idea/projects/pestfcst/model-building-service/src/test/java/cn/edu/hdu/pestfcst/modelbuildingservice/input2-predict2.csv");
        // 指定hdfs目录地址
        Path dst = new Path("/data");

        System.out.println("Uploading file from: " + src);
        System.out.println("To HDFS path: " + dst);

        // 上传
        fs.copyFromLocalFile(src, dst);

        // 检查文件是否上传成功
        FileStatus[] files = fs.listStatus(dst.getParent());
        for (FileStatus file : files) {
            System.out.println("File in HDFS: " + file.getPath());
        }

        // 关闭资源
        fs.close();
    }

    @Test
    public void testDownloadFileToLocal() throws IllegalArgumentException,IOException{
        fs.copyToLocalFile(new Path("/data/模型应用数据集.xlsx"),
                new Path("E:/a_idea/projects/pestfcst/model-building-service/src/test/java/cn/edu/hdu/pestfcst/modelbuildingservice"));
    }

}
