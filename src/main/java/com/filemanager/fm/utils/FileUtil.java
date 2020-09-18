package com.filemanager.fm.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;

public class FileUtil {
    /**
     * 在basePath下保存上传的文件夹
     *
     * @param basePath
     * @param files
     */
    public static void saveMultiFile(String basePath, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return;
        }
        if (basePath.endsWith("/")) {
            basePath = basePath.substring(0, basePath.length() - 1);
        }
        for (MultipartFile file : files) {
            String filename = "";
           /* String filename = file.getOriginalFilename();
            String filePath = basePath + "/" + file.getOriginalFilename();*/
            CommonsMultipartFile cf= (CommonsMultipartFile)file;
            FileItem fileItem = cf.getFileItem();
            String fileStr = fileItem.toString();
            String[] split = fileStr.split(",");
            for (int i = 0; i <split.length ; i++) {
                if(split[i].contains("name=")){
                     filename = split[i].substring(5,split[i].length());
                }
            }
            String filePath = basePath + "/" + filename;
            makeDir(filePath);
            File dest = new File(filePath);
            try {
                file.transferTo(dest);
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 3.16上传文件夹测试
     * @param files
     * @return
     */
    public static String uploadTest(String basePath, MultipartFile[] files) {
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;


        //判断存储的文件夹是否存在
        File file = new File(basePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        try {
            //遍历文件夹
            for (MultipartFile mf : files) {
                if (!mf.isEmpty()) {
                    String originalFilename = mf.getOriginalFilename();
                    String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                    String fileName = originalFilename.substring(originalFilename.lastIndexOf("/") + 1);
                    //为避免文件同名覆盖，给文件名加上时间戳
                    int index = fileName.lastIndexOf(".");
                    String firstName = fileName.substring(0, index);
                    String lastName = fileName.substring(index);
                    fileName = firstName + "_" + System.currentTimeMillis() + lastName;
                    //读取文件
                    bis = new BufferedInputStream(mf.getInputStream());
                    //指定存储的路径
                    bos = new BufferedOutputStream(new FileOutputStream
                            (basePath + fileName));
                    int len = 0;
                    byte[] buffer = new byte[10240];
                    while ((len = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }
                    //刷新此缓冲的输出流，保证数据全部都能写出
                    bos.flush();
                }
            }
            if (bis != null) {
                bis.close();
            }
            if (bos != null) {
                bos.close();
            }
            return "ok";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "error";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    /**
         * 确保目录存在，不存在则创建
         *
         * @param filePath
         */
        private static void makeDir(String filePath) {
            if (filePath.lastIndexOf('/') > 0) {
                String dirPath = filePath.substring(0, filePath.lastIndexOf('/'));
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
    }
}

