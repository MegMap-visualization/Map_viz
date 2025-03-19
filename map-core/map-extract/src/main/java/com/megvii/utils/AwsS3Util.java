package com.megvii.utils;

import com.megvii.properties.AwsS3Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.io.File;
import java.nio.file.Paths;

@Component
public class AwsS3Util {
    private S3Client s3Client;

    @Autowired
    private AwsS3Properties awsS3Properties;

    public AwsS3Util() {
    }

    public AwsS3Util init() throws URISyntaxException {
        // 初始化S3客户端
        System.out.println(awsS3Properties);
        AwsCredentials credentials = AwsBasicCredentials.create(awsS3Properties.getAccessKey(), awsS3Properties.getSecretKey());
        s3Client = S3Client.builder()
                .region(Region.US_EAST_1) // 设置所在的AWS区域
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(new URI(awsS3Properties.getEndPoint())) // 设置自定义域名
                .build();
        return this;
    }

    public void listBuckets() {
        // 列出所有存储桶
        List<Bucket> buckets = s3Client.listBuckets().buckets();
        for (Bucket bucket : buckets) {
            System.out.println("Bucket Name: " + bucket.name());
        }
    }

    public void uploadFile(String bucketName, String key, File file) {
        // 上传文件到指定的存储桶和键（对象Key）
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build(), Paths.get(file.getAbsolutePath()));
    }

    public void downloadFile(String bucketName, String key, File outputFile) {
        // 下载文件到指定的本地文件路径
        s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build(), Paths.get(outputFile.getAbsolutePath()));
    }

    public void deleteFile(String bucketName, String key) {
        // 删除指定存储桶和键（对象Key）的文件
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }

    // 可以根据需要添加更多的操作方法

    public void close() {
        // 关闭S3客户端
        s3Client.close();
    }
}
