package edu.byu.cs.tweeter.server.dao.implementation;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.interfaces.S3DAOInterface;

public class S3DAO implements S3DAOInterface {

    String BUCKET_NAME = "mycs340bucket";

    @Override
    public String upload(String userAlias, String imageByteArray) throws DataAccessException {

        URL url = null;

        try {

            byte[] imagebytes = Base64.getDecoder().decode(imageByteArray);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imagebytes);

            ObjectMetadata metadata = new ObjectMetadata();

            metadata.setContentType("image/jpeg");
            metadata.setContentLength(imagebytes.length);

            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-west-1").build();

            String fileName = userAlias + ".jpg";

            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, fileName, byteArrayInputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            s3.putObject(putObjectRequest);

            url = s3.getUrl(BUCKET_NAME, fileName);

        } catch (AmazonS3Exception e) {
            System.out.println(e.getMessage());
            throw new DataAccessException("[Internal Server] - Unable to upload image to s3");
        }


        return url.toString();

    }
}
