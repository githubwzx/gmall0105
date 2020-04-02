package com.example.gmall.manager.web.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by wzx on 2020/4/1 14:22
 */
public class PmsUploadUtil {

    private static TrackerClient trackerClient;
    private static TrackerServer trackerServer;
    private static StorageClient storageClient;

    public static String uploadImage(MultipartFile multipartFile) {
        String imgUrl =  "http://172.20.10.2";
        String[] uploadInfos ;
        // 配置fdfs的全局链接地址
        String tracker = PmsUploadUtil.class.getResource("/tracker.conf").getPath();// 获得配置文件的路径
        try {
            ClientGlobal.init(tracker);
            trackerClient = new TrackerClient();
            // 获得一个trackerServer的实例
            trackerServer= trackerClient.getTrackerServer();

            // 通过tracker获得一个Storage链接客户端
             storageClient = new StorageClient(trackerServer, null);

            byte[] bytes = multipartFile.getBytes();// 获得上传的二进制对象

            // 获得文件后缀名
            String originalFilename = multipartFile.getOriginalFilename();// a.jpg
            int i =originalFilename.lastIndexOf(".");
            String extName = originalFilename.substring(i+1);
             uploadInfos = storageClient.upload_file(bytes, extName, null);
            for (String uploadInfo:uploadInfos) {
                imgUrl += "/"+uploadInfo;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return imgUrl;
    }
}
