package com.filemanager.fm.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.filemanager.fm.service.FileOperationService.isDir;

public class fileTest {
    public static void main(String[] args) {
        String path1 = "D:";
        List<String> allFile1 = getAllDirs(path1, true);
        System.out.println(allFile1.toString());

        String path2 = "D:\\fileManager\\fileManager\\target\\classes\\com\\filemanager\\fm\\controller";
        List<Object> allFile2 = getAllFiles(path2);
        System.out.println(allFile2.toString());

        String path3 = "D:\\上传文件测试\\wenjian\\Person.class";
        File dPath = new File(path3);
       /* boolean b = deleteAllByPath(dPath);
        if(b == true){
            System.out.println("删除成功");
        }else{
            System.out.println("删除失败");
        }*/

        boolean b1 = recursionDelete(dPath);
        if (b1 == true) {
            System.out.println("删除成功");
        } else {
            System.out.println("删除失败");
        }


        System.out.println("#############");
        String path4 = "D:\\上传文件测试\\boot测试\\测试1\\新建 Microsoft Excel 工作表.xlsx";
        FixFileName(path4,"表格");

    }

    /**
     * 获取路径下的所有文件/文件夹
     *
     * @param directoryPath  需要遍历的文件夹路径
     * @param isAddDirectory 是否将子文件夹的路径也添加到list集合中
     * @return
     */
    public static List<String> getAllDirs(String directoryPath, boolean isAddDirectory) {
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
     * 获取文件最后修改时间
     */
    public static String getFileTime(String filePath) {
        File file = new File(filePath);
        long time = file.lastModified();//返回文件最后修改时间，是以个long型毫秒数
        String ctime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(time));
        return ctime;
    }

    /**
     * 获取文件大小,整数
     */
    public static String getFileSize(String path) {
        File file = new File(path);
        long length = file.length();
        System.out.println(FormetFileSize(length));
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
    public static String FormetFileSize(long fileLength) {
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
     * 获取路径下的所有文件
     *
     * @param directoryPath 需要遍历的文件夹路径
     * @return
     */
    public static List<Object> getAllFiles(String directoryPath) {
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
     * 删除某个目录下所有文件及文件夹
     *
     * @param rootFilePath 根目录
     * @return boolean
     */
    private static boolean deleteAllByPath(File rootFilePath) {
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
                rootFilePath.delete();
            } catch (IOException e) {
                //log.error("Delete temp directory or file failed." + e.getMessage());
                System.out.println("Delete temp directory or file failed");
                return false;
            }
        }
        return true;
    }

    /**
     * 递归删除该文件或者该文件夹
     *
     * @param parentFile
     * @return
     */
    public static boolean recursionDelete(File parentFile) {
        if (parentFile.isDirectory()) {
            for (File file : parentFile.listFiles()) {
                recursionDelete(file);
            }
        }
        return parentFile.delete();
    }

    /**
     * 通过文件路径直接修改文件名
     *
     * @param filePath    需要修改的文件的完整路径
     * @param newFileName 需要修改的文件的名称
     * @return
     */
    private static String FixFileName(String filePath, String newFileName) {
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
            newFilePath = filePath.substring(0, filePath.lastIndexOf("\\")) + "\\" + newFileName;
        } else {
            newFilePath = filePath.substring(0, filePath.lastIndexOf("\\")) + "\\" + newFileName
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
     * 支持文件夹上传
     *
     * @param dir 文件夹
     * @return
     */
  /*  public String upload(@RequestParam("dir") MultipartFile[] dir) {
        System.out.println("上传文件夹...");
        File file;
        String fileName="";
        String filePath="";
        for (MultipartFile f : dir) {
            fileName=f.getOriginalFilename();
            String type=f.getContentType();
            System.out.println("\n"+fileName+" ,"+type);
            filePath="D:\\upload\\"+fileName.substring(0,fileName.lastIndexOf("/"));
            if(!FileUtil.isDir(filePath)){
                FileUtil.makeDirs(filePath);
            }
            file = new File("D:\\upload\\" + fileName);
            try {
                file.createNewFile();
                //将上传文件保存到一个目标文件中
                f.transferTo(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "filelist";
    }
*/
    /**
     * 创建多级目录
     *
     * @param path
     */
    public boolean makeDirs(String path) {
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
    }

}
