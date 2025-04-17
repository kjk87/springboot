package kr.co.pplus.store.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class S3 {

    private static S3 s3Instace = null ;
    private static final Logger logger = LoggerFactory.getLogger(S3.class);
    private AmazonS3 amazonS3Client;
    private String bucket ;
    private String region ;
    private String key ;
    private String secretKey ;
    private String storeType;

    public static S3 getInstance(String storeType) {
        if( s3Instace == null ) {
            s3Instace = new S3() ;
            s3Instace.storeType = storeType;
            s3Instace.init() ;
            return s3Instace ;
        } else {
            s3Instace.storeType = storeType;
            return s3Instace ;
        }
    }

    public void shutdown() {
        this.amazonS3Client.shutdown();
    }

    public void init() {
        try {
            InputStream is = StoreUtil.getClassLoaderFile("kr/co/pplus/store/config/rootkey.csv");
            Properties prop = new Properties();
            prop.load(is);
            key = prop.getProperty("AWSAccessKeyId");
            secretKey = prop.getProperty("AWSSecretKey");
            if(storeType.equals("PROD")){
                bucket = prop.getProperty("AWSBucket");
            }else{
                bucket = prop.getProperty("AWSBucket_stage");
            }

            region = prop.getProperty("AWSRegion");
            is.close();

            BasicAWSCredentials credentials = new BasicAWSCredentials(key, secretKey);
            AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
            amazonS3Client = AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).withRegion(region).build();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));

        return upload(uploadFile, dirName);
    }

    public String upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    public String putS3(File uploadFile, String fileKey) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileKey, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileKey).toString();
    }

    public void deleteS3(String fileKey){
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileKey));
    }

    public void removeS3(String fileKey) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileKey));
    }

    public void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            logger.info("파일이 삭제되었습니다.");
        } else {
            logger.error("파일이 삭제되지 못했습니다.");
        }
    }

    public Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        if(convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

}
