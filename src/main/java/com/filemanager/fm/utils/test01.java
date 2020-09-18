package com.filemanager.fm.utils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class test01 {
    public static void main(String[] args) {
       /* String filePath = "C:\\Users\\73686\\Desktop\\新建文件夹";
        MultipartFile mulFileByPath = getMulFileByPath(filePath);
        System.out.println(mulFileByPath.toString());*/

        String path = "D:\\upload7\\child1";
        makeDirs(path);
        System.out.println("");

    }

    private static MultipartFile getMulFileByPath(String picPath) {
        FileItem fileItem = createFileItem(picPath);
        MultipartFile mfile = new CommonsMultipartFile(fileItem);
        return mfile;
    }

    private static FileItem createFileItem(String filePath) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        String textFieldName = "textField";
        int num = filePath.lastIndexOf(".");
        String extFile = filePath.substring(num);
        FileItem item = factory.createItem(textFieldName, "text/plain", true, "MyFileName" + extFile);
        File newfile = new File(filePath);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            FileInputStream fis = new FileInputStream(newfile);
            OutputStream os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }

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
                String name = path.substring(path.lastIndexOf("\\")+1,path.length());
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

}
