package com.pandaer.web.common.manager;

import com.pandaer.web.common.config.CosConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.transfer.Download;
import com.qcloud.cos.transfer.TransferManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * Cos 对象存储操作
 */
@Slf4j
@Component
public class CosManager {

    @Resource
    private CosConfig cosConfig;

    @Resource
    private COSClient cosClient;

    @Resource
    TransferManager transferManager;


    /**
     * 下载对象
     *
     * @param url 对象存储的文件路径
     * @return
     */
    public COSObject getObject(String url) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosConfig.getBucket(), url);
        try {
            return cosClient.getObject(getObjectRequest);
        } catch (CosClientException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 上传对象
     *
     * @param key           唯一键
     * @param localFilePath 本地文件路径
     * @return
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucket(), key, new File(localFilePath));
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * @param key        这个key包含https://
     * @param targetPath 下载到指定的文件中去
     * @return
     */
    public File download(String key, String targetPath) {
        // 存储桶的命名格式为 BucketName-APPID，此处填写的存储桶名称必须为此格式
        String bucketName = cosConfig.getBucket();
        // 本地文件路径
        File downloadFile = new File(targetPath);
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        try {
            // 返回一个异步结果 Download, 可同步的调用 waitForCompletion 等待下载结束, 成功返回 void, 失败抛出异常
            Download download = transferManager.download(getObjectRequest, downloadFile);
            download.waitForCompletion();
            return downloadFile;
        } catch (CosClientException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
