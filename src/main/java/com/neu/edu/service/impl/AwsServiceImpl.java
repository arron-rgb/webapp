package com.neu.edu.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.edu.entity.Message;
import com.neu.edu.exception.AttachmentNotFoundException;
import com.neu.edu.exception.CustomException;
import com.neu.edu.service.AwsService;
import com.neu.edu.util.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


/**
 * @author arronshentu
 */
@Service
@Slf4j
public class AwsServiceImpl implements AwsService {
  @Resource
  SnsClient sns;
  @Resource
  DynamoDbClient dynamoDB;
  @Resource
  AmazonS3 s3;

  @Value("${server.verify-endpoint}")
  String endpoint;
  @Value("${aws.sns.from}")
  String from;
  @Value("${aws.sns.topic-name}")
  String topicName;
  @Value("${aws.s3.bucket-name}")
  String bucketName;
  @Value("${aws.s3.localpath.default}")
  String path;
  @Value("${aws.dynamo-db.table-name}")
  String tableName;
  @Value("${aws.dynamo-db.ttl}")
  Long ttl;

  /**
   * @param file
   * @return
   * @throws IOException
   */
  @Override
  public Map<String, String> upload(MultipartFile file) throws IOException {
    String name = FilenameUtils.getName(file.getOriginalFilename());
    String key = addTimestamp(name);
    File dest = new File(path + File.separator + key);
    FileUtils.touch(dest);
    file.transferTo(dest);
    PutObjectResult result = s3.putObject(bucketName, key, dest);
    FileUtils.delete(dest);
    // todo object expiration set permanent when this object was attached to a task
    ObjectMetadata metadata = result.getMetadata();
    Map<String, String> map = new HashMap<>(4);
    map.put("filename", key);
    map.put("eTag", String.valueOf(metadata.getETag()));
    map.put("url", bucketName + "/" + key);
    return map;
  }

  @Override
  public void delete(String key, boolean throwEx) {
    if (throwEx && !s3.doesObjectExist(bucketName, key)) {
      throw new AttachmentNotFoundException(key + "does not exist");
    }
    s3.deleteObject(bucketName, key);
  }

  @Override
  public String writeToken(String userId) {
    String token = TokenProvider.generate();
    Map<String, AttributeValue> map = buildMap(token);
    map.put("userId", AttributeValue.builder().s(userId).build());
    long value = Instant.now().getEpochSecond();
    value += 60;
    map.put("ttl", AttributeValue.builder().n(String.valueOf(value)).build());
    map.put("used", AttributeValue.builder().bool(false).build());
    dynamoDB.putItem(builder -> {
      builder.tableName(tableName);
      builder.item(map);
    });
    return token;
  }

  ObjectMapper mapper = new ObjectMapper();

  @Override
  public void sendSns(String to, String token) {
    CreateTopicResponse result = sns.createTopic((req) -> req.name(topicName));
    Message message = new Message();
    message.setSubject("Notification");
    message.setFrom(from);
    message.setTo(to);
    message.setToken(token);
    message.setContent(generateTemplate(message));
    log.info(message.getContent());
    sns.publish((req) -> {
      req.topicArn(result.topicArn());
      try {
        String body = mapper.writeValueAsString(message);
        req.message(body);
        log.info(body);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    });

    dynamoDB.updateItem((req) -> {
      req.tableName(tableName);
      req.key(buildMap(token));
      Map<String, AttributeValueUpdate> attributeUpdates = new HashMap<>();
      attributeUpdates.put("used",
        AttributeValueUpdate.builder().value(AttributeValue.builder().bool(true).build()).build());
      req.attributeUpdates(attributeUpdates);
    });
  }

  // TODO 添加认证的url和邮件模板
  String generateTemplate(Message message) {
    return "<h1>Example HTML Message Body</h1> <br>" + "        <p>Here is the link for your last registeration  <br>"
      + "        <a href=" + endpoint + message.getToken() + ">Click Here</a> <br>" + "        </p> <br>"
      + "        <p>Check your name is " + message.getTo() + " <br>" + "        </p>";
  }

  String addTimestamp(String filename) {
    String ext = FilenameUtils.getExtension(filename);
    String name = FilenameUtils.getBaseName(filename);
    return name + "_" + System.currentTimeMillis() + "." + ext;
  }

  @Override
  public String getUserIdFromToken(String token) {
    GetItemResponse response = dynamoDB.getItem((req) -> {
      req.tableName(tableName);
      req.key(buildMap(token));
    });
    if (!response.hasItem()) {
      throw new CustomException("This link has expired");
    }
    String ttl = response.item().get("ttl").n();
    if (Long.parseLong(ttl) < Instant.now().getEpochSecond()) {
      throw new CustomException("This link has expired");
    }
    return response.item().get("userId").s();
  }

  @Override
  public void deleteToken(String token) {
    dynamoDB.deleteItem((req) -> {
      req.tableName(tableName);
      req.key(buildMap(token));
    });
  }

  @Override
  public boolean isValid(String token) {
    GetItemResponse response = dynamoDB.getItem((req) -> {
      req.tableName(tableName);
      req.key(buildMap(token));
    });
    return response.hasItem();
  }

  public Map<String, AttributeValue> buildMap(String token) {
    Map<String, AttributeValue> map = new HashMap<>(2);
    map.put("token", AttributeValue.builder().s(token).build());
    return map;
  }

}
