package com.filemanager.fm.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@ConfigurationProperties(prefix = "application")
@PropertySource("classpath:application-file.properties")
public class FileOperationService {

    @Value("${fileManager.dirPath}")
    private  String dirPath;

    /**
     * @param request 请求信息
     * @param path    上传的目录路径
     * @return
     */
    public Object multiUpload(HttpServletRequest request, String path) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        //String filePath = "D:\\上传文件测试\\";
        String filePath = path;

        int failCount = 0;
        int succCount = 0;
        Map<String,Object> uploadMap = new HashMap<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (file.isEmpty()) {
                failCount++;
                return "上传第" + (i++) + "个文件失败";
            }
            String fileName = file.getOriginalFilename();

            File dest = new File(filePath + fileName);
            try {
                file.transferTo(dest);
                succCount++;
            } catch (IOException e) {
                //LOGGER.error(e.toString(), e);
                failCount++;
                e.printStackTrace();
                return "上传第" + (i++) + "个文件失败";
            }
        }
        uploadMap.put("failNumber",failCount);
        uploadMap.put("succCount",succCount);
        return uploadMap;
    }

    /**
     * 判断是否是文件
     * @param filepath
     * @return
     */
    public static boolean isFile(String filepath) {
        File f = new File(filepath);
        return f.exists() && f.isFile();
    }

    /**
     * 判断是否是目录
     * @param dirPath
     * @return
     */
    public static boolean isDir(String dirPath) {
        File f = new File(dirPath);
        return f.exists() && f.isDirectory();
    }

    /**
     * 创建多级目录
     *
     * @param path
     */
  /*  public boolean makeDirs(String path) {
        File file = new File(path);
        boolean flag = false;
        try {
            // 如果文件夹不存在则创建
            if (!file.exists() && !file.isDirectory()) {
                file.mkdirs();
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }*/

    /**
     * 创建多级目录
     *
     * @param path
     */
    public static boolean makeDirs(String path) {
        File file = new File(path);
        boolean flag = false;
        try {
            // 如果文件夹不存在则创建
            if (!file.exists() && !file.isDirectory()) {
                file.mkdirs();
                flag = true;
            }else{
                int i = 1;
                String name = path.substring(path.lastIndexOf("/")+1,path.length());
                while(file.exists()) {
                    String newFilename = name+"("+i+")";
                    String parentPath = file.getParent();
                    file = new File(parentPath+ File.separator+newFilename);
                    i++;

                }
                file.mkdirs();
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     */
    public void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除某个目录下所有文件及文件夹
     *
     * @param rootFilePath 根目录
     * @return boolean
     */
    public boolean deleteAllByPath(File rootFilePath) {
        File[] deleteFiles = rootFilePath.listFiles();
        if (deleteFiles == null) {
            return true;
        }
        for (int i = 0; i < deleteFiles.length; i++) {
            if (deleteFiles[i].isDirectory()) {
                deleteAllByPath(deleteFiles[i]);
            }
            try {
                Files.delete(deleteFiles[i].toPath());
            } catch (IOException e) {
                //log.error("Delete temp directory or file failed." + e.getMessage());
                System.out.println("Delete temp directory or file failed");
                return false;
            }
        }
        return true;
    }

    /**
     * 删除目录及其子文件夹
     *
     * @param rootFilePath 根目录
     * @return boolean
     */
    public boolean deleteFile(File rootFilePath) {
        if (rootFilePath.isDirectory()) {
            for (File file : rootFilePath.listFiles()) {
                deleteFile(file);
            }
        }
        return rootFilePath.delete();
    }

    /**
     * 通过文件路径直接修改文件名,支持文件，目录
     *
     * @param filePath    需要修改的文件的完整路径
     * @param newFileName 需要修改的文件的名称
     * @return
     */
    public String FixFileName(String filePath, String newFileName) {
        File f = new File(filePath);
        if (!f.exists()) { // 判断原文件是否存在（防止文件名冲突）
            return null;
        }
        newFileName = newFileName.trim();
        if ("".equals(newFileName) || newFileName == null) // 文件名不能为空
            return null;
        String newFilePath = null;
        if (f.isDirectory()) { // 判断是否为文件夹
            int i = filePath.lastIndexOf("\\");
            //windows系统使用\\分割，linux使用/分割
            newFilePath = filePath.substring(0, filePath.lastIndexOf("/")) + "/" + newFileName;
        } else {
            newFilePath = filePath.substring(0, filePath.lastIndexOf("/")) + "/" + newFileName
                    + filePath.substring(filePath.lastIndexOf("."));
        }
        File nf = new File(newFilePath);
        try {
            f.renameTo(nf); // 修改文件名
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return newFilePath;
    }

    /**
     * 获取路径下的所有文件/文件夹
     *
     * @return
     */
    public  List<String> getAllDirection(String filePath) {
        if(filePath == null || "".equals(filePath)){
            filePath = dirPath;
        }
        List<String> dirList = getAllDirs(filePath, true);
        return dirList;
    }

    /**
     * 从配置文件获取原始目录
     *
     * @return
     */
    public String getDirection() {
        return dirPath;
    }

    /**
     * 获取路径下的所有文件/文件夹
     *
     * @param directoryPath  需要遍历的文件夹路径
     * @param isAddDirectory 是否将子文件夹的路径也添加到list集合中
     * @return
     */
    public  List<String> getAllDirs(String directoryPath, boolean isAddDirectory) {
        List<String> list = new ArrayList<String>();
        File baseFile = new File(directoryPath);
        if (baseFile.isFile() || !baseFile.exists()) {
            return list;
        }
        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                if (isAddDirectory) {
                    list.add(file.getAbsolutePath());
                }
                list.addAll(getAllDirs(file.getAbsolutePath(), isAddDirectory));
            }
        }
        return list;
    }

    /**
     * 获取路径下的所有文件
     *
     * @param directoryPath 需要遍历的文件夹路径
     * @return
     */
    public  List<Object> getAllFiles(String directoryPath) {
        List<Object> list = new ArrayList<Object>();
        File baseFile = new File(directoryPath);
        if (baseFile.isFile() || !baseFile.exists()) {
            return list;
        }
        File[] files = baseFile.listFiles();
        for (File file : files) {
            Map<String, String> map = new HashMap<>();
            if (file.isFile()) {
                map.put("fileName", file.getName());
                map.put("filePath", file.getAbsolutePath());
                map.put("time", getFileTime(file.getAbsolutePath()));
                map.put("size", getFileSize(file.getAbsolutePath()));
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 获取文件最后修改时间
     */
    public  String getFileTime(String filePath) {
        File file = new File(filePath);
        long time = file.lastModified();//返回文件最后修改时间，是以个long型毫秒数
        String ctime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(time));
        return ctime;
    }

    /**
     * 获取文件大小,整数
     */
    public  String getFileSize(String path) {
        File file = new File(path);
        long length = file.length();
        return FormetFileSize(length);
    }

    /**
     * 转换文件大小，带单位的字符串
     *
     * @param fileLength 文件大小值
     * @return String 文件大小
     * @Title: FormetFileSize
     * @author projectNo
     */
    public  String FormetFileSize(long fileLength) {
        DecimalFormat df = new DecimalFormat("#.00");
        DecimalFormat d = new DecimalFormat("#");
        String fileSizeString = "";
        if (fileLength < 1024) {
            fileSizeString = d.format((double) fileLength) + "B";
        } else if (fileLength < 1048576) {
            fileSizeString = df.format((double) fileLength / 1024) + "KB";
        } else if (fileLength < 1073741824) {
            fileSizeString = df.format((double) fileLength / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileLength / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 多文件上传
     * @param request
     * @param filePath
     * @return
     */
    public String uploadMuilt(HttpServletRequest request,String filePath) {
        List<MultipartFile> files = ((MultipartHttpServletRequest)request).getFiles("file");
        MultipartFile file = null;
        BufferedOutputStream stream = null;
        for (int i = 0; i <files.size() ; i++) {
            file = files.get(i);
            filePath = filePath+"/";
            if(!file.isEmpty()){
                try{
                    byte[] bytes = file.getBytes();
                    stream = new BufferedOutputStream(new FileOutputStream(
                            new File(filePath+file.getOriginalFilename())
                    ));
                    stream.write(bytes);
                    stream.close();
                }catch(Exception e){
                    stream = null;
                    e.printStackTrace();
                    return "the"+i+"file upload failure";
                }
            }else {
                return "the"+i+"file is empty";
            }
        }
        return "upload multifile success";
    }

}
