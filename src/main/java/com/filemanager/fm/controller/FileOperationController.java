package com.filemanager.fm.controller;

import com.filemanager.fm.service.FileOperationService;
import com.filemanager.fm.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/fileManager")
//@CrossOrigin(origins = "http://192.168.0.25:8080", maxAge = 3600)
//针对服务器文件进行操作，支持上传，下载，重命名，删除
public class FileOperationController {

    @Autowired
    FileOperationService fileService;

    /**
     * 多文件上传
     *
     * @param request  请求信息
     * @param filePath 要上传的目录路径
     * @return
     */
    @PostMapping("/multiUpload")
    @ResponseBody
    public Object multiUpload(HttpServletRequest request, String filePath) {
        Map<String, Object> map = new HashMap<>();
        try {
            Object data = fileService.multiUpload(request, filePath);
            map.put("data", data);
            map.put("code", 200);
            map.put("msg", "恭喜，请求成功");
        } catch (Exception e) {
            map.put("data", "");
            map.put("code", 500);
            map.put("msg", "抱歉，操作失败");
            e.printStackTrace();
        }

        return map;

    }

    /**
     * 文件下载
     *
     * @param response
     * @param filePath 被下载的文件在服务器中的路径
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/download", method = RequestMethod.POST)
    private String download(HttpServletResponse response, String filePath) throws UnsupportedEncodingException {
        File file = new File(filePath);
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);//被下载文件的名称
        if (file.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ";filename*=utf-8''"
                    + URLEncoder.encode(fileName, "UTF-8"));

            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream outputStream = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    outputStream.write(buffer, 0, i);
                    i = bis.read(buffer);
                }

                return "下载成功";
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "下载失败";
    }


    /**
     * 新建目录
     *
     * @param filePath
     */
    @RequestMapping(value = "/makeDirs", method = RequestMethod.POST)
    public Object makeDirs(String filePath) {
        Map<String, Object> dataMap = new HashMap<>();
        boolean bn = false;
        try {
            bn = fileService.makeDirs(filePath);
            if (bn == true) {
                dataMap.put("data", bn);
                dataMap.put("code", 200);
                dataMap.put("msg", "恭喜，创建成功");
            } else {
                dataMap.put("data", bn);
                dataMap.put("code", 500);
                dataMap.put("msg", "抱歉，创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            dataMap.put("data", bn);
            dataMap.put("code", 500);
            dataMap.put("msg", "抱歉，创建失败");
        }
        return dataMap;
    }

    /**
     * 重命名目录
     *
     * @param filePath
     */
    @RequestMapping(value = "/FixFileName", method = RequestMethod.POST)
    public Object FixFileName(String filePath, String newName) {
        Map<String, Object> dataMap = new HashMap<>();
        try {
            String newPath = fileService.FixFileName(filePath, newName);
            dataMap.put("data", newPath);
            dataMap.put("code", 200);
            dataMap.put("msg", "恭喜，修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            dataMap.put("data", "");
            dataMap.put("code", 500);
            dataMap.put("msg", "抱歉，修改失败");
        }
        return dataMap;
    }


    /**
     * 删除目录及其子文件夹
     *
     * @param filePath
     */
    @RequestMapping(value = "/deleteDirs", method = RequestMethod.DELETE)
    public Object deleteDirs(String filePath) {
        Map<String, Object> dataMap = new HashMap<>();
        boolean bn = false;
        try {
            File deletePath = new File(filePath);
            bn = fileService.deleteFile(deletePath);
            if (bn == true) {
                dataMap.put("data", bn);
                dataMap.put("code", 200);
                dataMap.put("msg", "恭喜，删除成功");
            } else {
                dataMap.put("data", bn);
                dataMap.put("code", 500);
                dataMap.put("msg", "抱歉，删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            dataMap.put("data", bn);
            dataMap.put("code", 500);
            dataMap.put("msg", "抱歉，删除失败");
        }
        return dataMap;
    }

    /**
     * 初始化，获取当前路径下的所有目录
     */
    @RequestMapping(value = "/getAllDirs", method = RequestMethod.POST)
    public Object getAllDirs(String filePath) {
        Map<String, Object> dataMap = new HashMap<>();
        try {
            List<String> dirList = fileService.getAllDirection(filePath);
            dataMap.put("data", dirList);
            dataMap.put("code", 200);
            dataMap.put("root", fileService.getDirection());
            dataMap.put("msg", "恭喜，查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            dataMap.put("data", "");
            dataMap.put("code", 500);
            dataMap.put("root", "");
            dataMap.put("msg", "抱歉，查询失败");
        }
        return dataMap;
    }

    /**
     * 根据目录获取改目录下的所有文件
     *
     * @param filePath
     */
    @RequestMapping(value = "/getAllFiles", method = RequestMethod.POST)
    public Object getAllFiles(String filePath) {
        Map<String, Object> dataMap = new HashMap<>();
        try {
            List<Object> dirList = fileService.getAllFiles(filePath);
            dataMap.put("data", dirList);
            dataMap.put("code", 200);
            dataMap.put("msg", "恭喜，查询成功");
        } catch (Exception e) {
            e.printStackTrace();
            dataMap.put("data", "");
            dataMap.put("code", 500);
            dataMap.put("msg", "抱歉，查询失败");
        }
        return dataMap;
    }

    /**
     * 上传单个文件
     *
     * @param file
     * @param filePath 要上传的文件路径
     * @return
     */
    @RequestMapping(value = "/uploadSingle", method = RequestMethod.POST)
    public Object uploadSingle(@RequestParam("file") MultipartFile file, String filePath) {
        Map<String, Object> dataMap = new HashMap<>();
        try {
            if (file.isEmpty()) {
                dataMap.put("data", "");
                dataMap.put("code", 500);
                dataMap.put("msg", "file is empty");
                return dataMap;
            }
            String fileName = file.getOriginalFilename();
            //要上传的文件路径
            filePath = filePath + "\\";
            String path = filePath + fileName;
            File dest = new File(path);
            //检测是否存在目录
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();//新建文件夹
            }
            file.transferTo(dest);//文件写入
            dataMap.put("data", "");
            dataMap.put("code", 200);
            dataMap.put("msg", "upload success");
            return dataMap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        dataMap.put("data", "");
        dataMap.put("code", 500);
        dataMap.put("msg", "upload failure!");
        return dataMap;

    }

    /**
     * 上传多个文件
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/uploadMuilt", method = RequestMethod.POST)
    public Object uploadMuilt(HttpServletRequest request, String filePath) {
        Map<String, Object> dataMap = new HashMap<>();
        try {
            String str = fileService.uploadMuilt(request, filePath);
            dataMap.put("data", str);
            dataMap.put("code", 200);
            dataMap.put("msg", "多文件上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            dataMap.put("data", "");
            dataMap.put("code", 500);
            dataMap.put("msg", "多文件上传失败");
        }
        return dataMap;
    }

    /**
     * 上传文件夹
     *
     * @param file     待上传的文件夹
     * @param filePath 要上传到的目录路径
     * @return
     */
    @RequestMapping(value = "/uploadFolder", method = RequestMethod.POST)
    public Object uploadFolder(MultipartFile[] file, String filePath) {
        Map<String, Object> dataMap = new HashMap<>();
        try {
            FileUtil.saveMultiFile(filePath, file);
            // FileUtil.uploadTest(filePath, file);
            dataMap.put("data", "");
            dataMap.put("code", 200);
            dataMap.put("msg", "文件夹上传成功");
        } catch (Exception e) {
            dataMap.put("data", "");
            dataMap.put("code", 500);
            dataMap.put("msg", "文件夹上传失败");
            e.printStackTrace();
        }

        return dataMap;
    }

    @RequestMapping(value = "/urlTest", method = RequestMethod.GET)
    public Object urlTest() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("data", "，恭喜，后台接口通了");
        dataMap.put("code", 200);
        dataMap.put("msg", "文件夹上传成功");
        return dataMap;
    }

}
