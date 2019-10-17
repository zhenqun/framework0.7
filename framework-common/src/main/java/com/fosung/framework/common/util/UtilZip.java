package com.fosung.framework.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import java.io.*;
import java.util.Enumeration;

/**
 * zip管理操作类
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Slf4j
public class UtilZip {
    
    /**
     * 使用GBK编码可以避免压缩中文文件名乱码
     */
    private static final String CHINESE_CHARSET = "GBK";
    
    /**
     * 文件读取缓冲区大小
     */
    private static final int CACHE_SIZE = 1024;
    
    /**
     * <p>
     * 压缩文件
     * </p>
     * 
     * @param sourceFolder 压缩文件夹
     * @param zipFilePath 压缩文件输出路径
     * @throws Exception
     */
    public static void zip(String sourceFolder, String zipFilePath) {
        OutputStream out = null ;
        BufferedOutputStream bos = null ;
        ZipOutputStream zos = null ;

        try {
            out = new FileOutputStream(zipFilePath);
            bos = new BufferedOutputStream(out);
            zos = new ZipOutputStream(bos);

            // 解决中文文件名乱码
            zos.setEncoding(CHINESE_CHARSET);
            File file = new File(sourceFolder);
            String basePath = null;
            if (file.isDirectory()) {
                basePath = file.getPath();
            } else {
                basePath = file.getParent();
            }
            zipFile(file, basePath, zos , zipFilePath);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(zos!=null){
                    zos.closeEntry();
                    zos.close();
                }
                if(bos!=null){
                    bos.close();
                }
                if(out!=null){
                    out.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    
    /**
     * <p>
     * 递归压缩文件
     * </p>
     * 
     * @param targetFile
     * @param basePath
     * @param zos
     * @throws Exception
     */
    private static void zipFile(File targetFile , String basePath, ZipOutputStream zos , String zipFilePath) throws Exception {
        File[] files = null ;
        if (targetFile.isDirectory()) {
            files = targetFile.listFiles();
        } else {
            files = new File[1] ;
            files[0] = targetFile ;
        }
        String pathName;
        InputStream is;
        BufferedInputStream bis;
        byte[] cache = new byte[CACHE_SIZE];
        for (File file : files) {
            //避免对输出zip文件进行打包
            if(StringUtils.equalsIgnoreCase( file.getAbsolutePath() , zipFilePath )){
                log.info("不对{}进行zip压缩" , zipFilePath);
                continue;
            }
            if (file.isDirectory()) {
                pathName = file.getPath().substring(basePath.length() + 1) + "/";
                zos.putNextEntry(new ZipEntry(pathName));
                zipFile(file , basePath , zos , zipFilePath);
            } else {
                pathName = file.getPath().substring(basePath.length() + 1);
                is = new FileInputStream(file) ;
                bis = new BufferedInputStream(is) ;
                zos.putNextEntry(new ZipEntry(pathName)) ;
                log.info("zip打包{}" , file.getAbsolutePath()) ;
                int nRead = 0;
                while ((nRead = bis.read(cache, 0, CACHE_SIZE)) != -1) {
                    zos.write(cache, 0, nRead);
                }
                bis.close();
                is.close();
            }
        }
    }
    
    /**
     * <p>
     * 解压压缩包
     * </p>
     * 
     * @param zipFilePath 压缩文件路径
     * @param destDir 压缩包释放目录
     * @throws Exception
     */
    public static void unZip(String zipFilePath, String destDir) throws Exception {
        ZipFile zipFile = new ZipFile(zipFilePath, CHINESE_CHARSET);
        Enumeration<?> emu = zipFile.getEntries();
        BufferedInputStream bis;
        FileOutputStream fos;
        BufferedOutputStream bos;
        File file, parentFile;
        ZipEntry entry;
        byte[] cache = new byte[CACHE_SIZE];
        while (emu.hasMoreElements()) {
            entry = (ZipEntry) emu.nextElement();
            if (entry.isDirectory()) {
                new File(destDir + entry.getName()).mkdirs();
                continue;
            }
            bis = new BufferedInputStream(zipFile.getInputStream(entry));
            file = new File(destDir + entry.getName());
            parentFile = file.getParentFile();
            if (parentFile != null && (!parentFile.exists())) {
                parentFile.mkdirs();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos, CACHE_SIZE);
            int nRead = 0;
            while ((nRead = bis.read(cache, 0, CACHE_SIZE)) != -1) {
                fos.write(cache, 0, nRead);
            }
            bos.flush();
            bos.close();
            fos.close();
            bis.close();
        }
        zipFile.close();
    }

}

