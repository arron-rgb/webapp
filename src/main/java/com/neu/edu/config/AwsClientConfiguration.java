package com.neu.edu.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sns.SnsClient;

/**
 * @author arronshentu
 */
@Configuration
@Slf4j
public class AwsClientConfiguration {

  @Value("${aws.s3.bucket-name}")
  String bucketName;

  @Bean(value = "s3")
  AmazonS3 amazonS3() {
    AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    boolean exist = s3.doesBucketExistV2(bucketName);
    if (!exist) {
      s3.createBucket(bucketName);
      log.info("Create Bucket {}", bucketName);
    }
    return s3;
  }

  @Bean(value = "sns")
  SnsClient sns() {
    return SnsClient.builder().region(Region.US_EAST_1).build();
  }

  @Bean(value = "dynamoDB")
  DynamoDbClient dynamoDB() {
    return DynamoDbClient.builder().region(Region.US_EAST_1).build();
  }

}
