package cn.edu.hdu.pestfcst.datamanagementservice.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.apache.hadoop.fs.Path;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author : Claude
 * @Time: 2025-06-25
 * @File : HDFSUtil.java
 * @Description : HDFS操作工具类
 */
@Component
public class HDFSUtil {
    private FileSystem fileSystem;

    @Value("${hdfs.uri}")
    private String hdfsUri;

    @Value("${hdfs.user}")
    private String hdfsUser;

    @Value("${hdfs.basePath}")
    private String basePath;

    @PostConstruct
    public void init() throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", hdfsUri);
        System.setProperty("HADOOP_USER_NAME", hdfsUser);
        this.fileSystem = FileSystem.get(conf);
        
        // 确保基础路径存在
        Path baseDir = new Path(basePath);
        if (!fileSystem.exists(baseDir)) {
            fileSystem.mkdirs(baseDir);
        }
    }

    /**
     * 上传文件到HDFS
     * @param localFilePath 本地文件路径
     * @param hdfsFilePath HDFS文件路径
     * @throws IOException IO异常
     */
    public void uploadFile(String localFilePath, String hdfsFilePath) throws IOException {
        Path srcPath = new Path(localFilePath);
        Path dstPath = new Path(hdfsFilePath);
        
        // 检查目标父目录是否存在，不存在则创建
        Path parent = dstPath.getParent();
        if (parent != null && !fileSystem.exists(parent)) {
            fileSystem.mkdirs(parent);
        }
        
        // 上传文件
        fileSystem.copyFromLocalFile(srcPath, dstPath);
    }

    /**
     * 从MultipartFile上传文件到HDFS
     * @param file MultipartFile对象
     * @param hdfsFilePath HDFS文件路径
     * @throws IOException IO异常
     */
    public void uploadFile(MultipartFile file, String hdfsFilePath) throws IOException {
        // 创建临时文件
        java.nio.file.Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "hdfs-upload");
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
        }
        
        java.nio.file.Path localFile = Paths.get(tempDir.toString(), file.getOriginalFilename());
        file.transferTo(localFile.toFile());
        
        // 上传到HDFS
        uploadFile(localFile.toString(), hdfsFilePath);
        
        // 删除临时文件
        Files.deleteIfExists(localFile);
    }

    /**
     * 从HDFS下载文件到本地
     * @param hdfsFilePath HDFS文件路径
     * @param localFilePath 本地文件路径
     * @throws IOException IO异常
     */
    public void downloadFile(String hdfsFilePath, String localFilePath) throws IOException {
        Path srcPath = new Path(hdfsFilePath);
        Path dstPath = new Path(localFilePath);
        fileSystem.copyToLocalFile(srcPath, dstPath);
    }

    /**
     * 读取HDFS文件内容
     * @param hdfsFilePath HDFS文件路径
     * @return 文件内容
     * @throws IOException IO异常
     */
    public List<String> readFile(String hdfsFilePath) throws IOException {
        Path path = new Path(hdfsFilePath);
        List<String> content = new ArrayList<>();
        
        if (!fileSystem.exists(path)) {
            throw new IOException("File does not exist: " + hdfsFilePath);
        }
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fileSystem.open(path)))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.add(line);
            }
        }
        
        return content;
    }

    /**
     * 删除HDFS文件
     * @param hdfsFilePath HDFS文件路径
     * @return 是否删除成功
     * @throws IOException IO异常
     */
    public boolean deleteFile(String hdfsFilePath) throws IOException {
        Path path = new Path(hdfsFilePath);
        return fileSystem.delete(path, true);
    }

    /**
     * 列出HDFS目录下的文件
     * @param hdfsDirectoryPath HDFS目录路径
     * @return 文件列表
     * @throws IOException IO异常
     */
    public List<String> listFiles(String hdfsDirectoryPath) throws IOException {
        Path path = new Path(hdfsDirectoryPath);
        List<String> fileList = new ArrayList<>();
        
        FileStatus[] statuses = fileSystem.listStatus(path);
        for (FileStatus status : statuses) {
            fileList.add(status.getPath().getName());
        }
        
        return fileList;
    }

    /**
     * 获取完整的HDFS文件路径
     * @param relativePath 相对路径
     * @return 完整路径
     */
    public String getFullPath(String relativePath) {
        return basePath + (relativePath.startsWith("/") ? relativePath : "/" + relativePath);
    }
} 