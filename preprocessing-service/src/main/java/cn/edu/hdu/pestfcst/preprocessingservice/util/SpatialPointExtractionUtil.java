package cn.edu.hdu.pestfcst.preprocessingservice.util;

import org.geotools.api.data.FeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @Author : Claude
 * @Time: 2025-06-25
 * @File : SpatialPointExtractionUtil.java
 * @Description : 空间点提取工具类
 */
@Component
public class SpatialPointExtractionUtil {

    /**
     * 从栅格数据中提取点数据
     * @param rasterPath 栅格文件路径
     * @param outputPath 输出CSV文件路径
     * @param sampleInterval 采样间隔
     * @return 提取的点数据数量
     * @throws Exception 异常
     */
    public int extractPointsFromRaster(String rasterPath, String outputPath, int sampleInterval) throws Exception {
        File rasterFile = new File(rasterPath);
        GeoTiffReader reader = new GeoTiffReader(rasterFile);
        
        GridCoverage2D coverage = reader.read(null);
        CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem();
        
        // 获取栅格的范围
        org.geotools.geometry.Envelope2D envelope = coverage.getEnvelope2D();
        double minX = envelope.getMinX();
        double minY = envelope.getMinY();
        double maxX = envelope.getMaxX();
        double maxY = envelope.getMaxY();
        
        // 创建输出CSV文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            // 写入表头
            writer.write("id,x,y,value\n");
            
            int pointCount = 0;
            int id = 1;
            
            // 按指定间隔采样点
            for (double x = minX; x <= maxX; x += sampleInterval) {
                for (double y = minY; y <= maxY; y += sampleInterval) {
                    DirectPosition2D position = new DirectPosition2D(x, y);
                    
                    if (envelope.contains(position)) {
                        double[] values = new double[1];
                        try {
                            values = coverage.evaluate(position, values);
                            double value = values[0];
                            
                            // 写入点数据
                            writer.write(id + "," + x + "," + y + "," + value + "\n");
                            pointCount++;
                            id++;
                        } catch (Exception e) {
                            // 如果点在栅格范围之外，忽略并继续
                            continue;
                        }
                    }
                }
            }
            
            reader.dispose();
            return pointCount;
        }
    }
    
    /**
     * 从矢量数据中提取点数据
     * @param shapePath 矢量文件路径
     * @param outputPath 输出CSV文件路径
     * @return 提取的点数据数量
     * @throws Exception 异常
     */
    public int extractPointsFromVector(String shapePath, String outputPath) throws Exception {
        File shapeFile = new File(shapePath);
        ShapefileDataStore dataStore = new ShapefileDataStore(shapeFile.toURI().toURL());
        dataStore.setCharset(Charset.forName("UTF-8"));
        
        String typeName = dataStore.getTypeNames()[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
        SimpleFeatureCollection features = (SimpleFeatureCollection) source.getFeatures();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            // 获取属性字段
            SimpleFeatureType schema = features.getSchema();
            List<String> attributeNames = new ArrayList<>();
            for (int i = 0; i < schema.getAttributeCount(); i++) {
                String attrName = schema.getDescriptor(i).getLocalName();
                if (!attrName.equals("the_geom")) {
                    attributeNames.add(attrName);
                }
            }
            
            // 写入表头
            writer.write("id,x,y,wkt");
            for (String attr : attributeNames) {
                writer.write("," + attr);
            }
            writer.write("\n");
            
            int pointCount = 0;
            int id = 1;
            WKTWriter wktWriter = new WKTWriter();
            
            try (SimpleFeatureIterator iterator = features.features()) {
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();
                    Geometry geometry = (Geometry) feature.getDefaultGeometry();
                    
                    // 处理不同类型的几何对象
                    if (geometry instanceof Point) {
                        Point point = (Point) geometry;
                        writePointToCSV(writer, id, point, wktWriter, feature, attributeNames);
                        pointCount++;
                        id++;
                    } else if (geometry instanceof MultiPoint) {
                        MultiPoint multiPoint = (MultiPoint) geometry;
                        for (int i = 0; i < multiPoint.getNumGeometries(); i++) {
                            Point point = (Point) multiPoint.getGeometryN(i);
                            writePointToCSV(writer, id, point, wktWriter, feature, attributeNames);
                            pointCount++;
                            id++;
                        }
                    } else if (geometry instanceof Polygon) {
                        // 从多边形中提取中心点
                        Point centroid = geometry.getCentroid();
                        writePointToCSV(writer, id, centroid, wktWriter, feature, attributeNames);
                        pointCount++;
                        id++;
                    } else if (geometry instanceof MultiPolygon) {
                        // 从多多边形中提取中心点
                        MultiPolygon multiPolygon = (MultiPolygon) geometry;
                        for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
                            Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
                            Point centroid = polygon.getCentroid();
                            writePointToCSV(writer, id, centroid, wktWriter, feature, attributeNames);
                            pointCount++;
                            id++;
                        }
                    } else if (geometry instanceof LineString) {
                        // 从线中提取中心点
                        Point centroid = geometry.getCentroid();
                        writePointToCSV(writer, id, centroid, wktWriter, feature, attributeNames);
                        pointCount++;
                        id++;
                    }
                }
            }
            
            dataStore.dispose();
            return pointCount;
        }
    }
    
    /**
     * 将点写入CSV文件
     * @param writer 文件写入器
     * @param id 点ID
     * @param point 点几何对象
     * @param wktWriter WKT写入器
     * @param feature 要素
     * @param attributeNames 属性名称列表
     * @throws IOException IO异常
     */
    private void writePointToCSV(BufferedWriter writer, int id, Point point, WKTWriter wktWriter, 
                                SimpleFeature feature, List<String> attributeNames) throws IOException {
        writer.write(id + "," + point.getX() + "," + point.getY() + "," + wktWriter.write(point));
        for (String attr : attributeNames) {
            Object value = feature.getAttribute(attr);
            writer.write("," + (value != null ? value.toString().replace(",", ";") : ""));
        }
        writer.write("\n");
    }
} 