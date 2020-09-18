package com.filemanager.fm.controller;

import com.filemanager.fm.utils.FileUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/file")
public class FileController {
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(HttpServletRequest request) {
        MultipartHttpServletRequest params = ((MultipartHttpServletRequest) request);
        List<MultipartFile> files = params.getFiles("fileFolder");     //fileFolder为文件项的name值
        System.out.println("上传文件夹...");
        File file;
        String fileName = "";
        String filePath = "";
        for (MultipartFile f : files) {
            fileName = f.getOriginalFilename();
            String type = f.getContentType();
            System.out.println("\n" + fileName + " ," + type);
            //String substring = fileName.substring(0, fileName.lastIndexOf("\\"));
            filePath = "D:\\upload\\" + fileName.substring(0, fileName.lastIndexOf("\\"));
            if (!isDir(filePath)) {
                makeDirs(filePath);
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

    public boolean isFile(String filepath) {
        File f = new File(filepath);
        return f.exists() && f.isFile();
    }

    public boolean isDir(String dirPath) {
        File f = new File(dirPath);
        return f.exists() && f.isDirectory();
    }

    /**
     * 创建多级目录
     *
     * @param path
     */
    public void makeDirs(String path) {
        File file = new File(path);
        // 如果文件夹不存在则创建
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        } else {
            System.out.println("创建目录失败：" + path);
        }
    }

    /**
     * 上传文件夹
     *
     * @param file
     * @param path
     * @return
     */
    @PostMapping("/uploadDir")
    @ResponseBody
    public String uploadDir(@RequestParam("file") MultipartFile file, @RequestParam("path") String path) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaa");
        String fileName = file.getOriginalFilename();
        String filePath = path;
        System.out.println(filePath);

        File dest = new File(filePath + fileName);

        try {
            file.transferTo(dest);
            return "上传成功";
        } catch (IOException e) {
        }
        return "上传失败！";
    }

    /**
     * 上传多个文件
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/uploadMuilt", method = RequestMethod.POST)
    public Object uploadMuilt(HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        MultipartFile file = null;
        BufferedOutputStream stream = null;
        for (int i = 0; i < files.size(); i++) {
            file = files.get(i);
            String filePath = "D:\\upload7";
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    stream = new BufferedOutputStream(new FileOutputStream(
                            new File(filePath + file.getOriginalFilename())
                    ));
                    stream.write(bytes);
                    stream.close();
                } catch (Exception e) {
                    stream = null;
                    e.printStackTrace();
                    return "the" + i + "file upload failure";
                }
            } else {
                return "the" + i + "file is empty";
            }
        }
        return "upload multifile success";
    }


    /**
     * 上传文件夹
     * @param folder
     * @return
     */
    @RequestMapping(value = "/uploadFolder", method = RequestMethod.POST)
    public Object uploadFolder(MultipartFile[] folder) {
        Map<String,Object> map = new HashMap<>();
        try{
            FileUtil.saveMultiFile("D:/upload", folder);
            map.put("msg","上传成功");
        }catch (Exception e){
            map.put("msg","上传失败");
            e.printStackTrace();
        }

        return map;
    }

}
