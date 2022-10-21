package com.neu.edu.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.edu.entity.Message;
import com.neu.edu.exception.AttachmentNotFoundException;
import com.neu.edu.service.AwsService;
import com.neu.edu.util.EmailTemplate;
import com.neu.edu.util.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.sns.SnsClient;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.neu.edu.util.Constants.*;


/**
 * @author arronshentu
 */
@Service
@Slf4j
public class AwsServiceImpl implements AwsService {
  @Resource
  SnsClient sns;
  @Resource
  DynamoDbClient dynamoDbClient;
  @Resource
  AmazonS3 s3;
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
  public String writeToken(String userId, String username) {
    String token = TokenProvider.generate();
    Map<String, AttributeValue> map = new HashMap<>();
    map.put("userId", AttributeValue.builder().s(userId).build());
    if (StringUtils.hasText(username)) {
      map.put("username", AttributeValue.builder().s(username).build());
    }
    getFullMap(token, map);
    dynamoDbClient.putItem(builder -> {
      builder.tableName(tableName);
      builder.item(map);
    });
    return token;
  }

  ObjectMapper mapper = new ObjectMapper();

  @Override
  public void sendSns(String to, String token, String subject, String newEmail) {
    Message message = new Message().setSubject(subject).setFrom(from).setTo(to).setToken(token);

    switch (subject) {
      case CHANGE_USER_EMAIL_SUBJECT:
      case VERIFICATION_SUBJECT:
        message.setContent(EmailTemplate.generateVerificationContent(message));
        break;
      case CHANGE_USER_EMAIL_COMPLETION_SUBJECT:
        message.setContent(EmailTemplate.CHANGE_COMPLETED);
        break;
      case "Revert Email Change":
        message.setContent(EmailTemplate.revertContent(token));
        break;
      default:
        log.error("unknown subject, {}", subject);
        return;
    }

    log.info(message.getContent());
    sns.publish((req) -> {
      req.topicArn(topicName);
      try {
        String body = mapper.writeValueAsString(message);
        req.message(body);
        log.info(body);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    });
  }

  String addTimestamp(String filename) {
    String ext = FilenameUtils.getExtension(filename);
    String name = FilenameUtils.getBaseName(filename);
    return name + "_" + System.currentTimeMillis() + "." + ext;
  }

  @Override
  public String getUserIdFromToken(String token) {
    Map<String, AttributeValue> map = query(token);
    return map.get("userId").s();
  }

  @Override
  public void deleteToken(String token) {
    dynamoDbClient.deleteItem((req) -> {
      req.tableName(tableName);
      req.key(getTokenMap(token));
    });
  }

  @Override
  public boolean isValid(String token) {
    GetItemResponse response = dynamoDbClient.getItem((req) -> {
      req.tableName(tableName);
      req.key(getTokenMap(token));
    });
    return response.hasItem();
  }

  @Override
  public void publishMessage(Message message) {
    sns.publish((req) -> {
      req.topicArn(topicName);
      try {
        String body = mapper.writeValueAsString(message);
        req.message(body);
        log.info(body);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    });
  }

  @Override
  public boolean putItem(Map<String, AttributeValue> map) {
    PutItemResponse response = dynamoDbClient.putItem(builder -> {
      builder.tableName(tableName);
      builder.item(map);
    });
    return response.hasAttributes();
  }

  public Map<String, AttributeValue> getTokenMap(String token) {
    Map<String, AttributeValue> map = new HashMap<>(2);
    map.put("token", AttributeValue.builder().s(token).build());
    return map;
  }


  @Override
  public Map<String, AttributeValue> getFullMap(String token, Map<String, AttributeValue> map) {
    long value = Instant.now().getEpochSecond();
    value += ttl;
    map.put("token", AttributeValue.builder().s(token).build());
    map.put("ttl", AttributeValue.builder().n(String.valueOf(value)).build());
    map.put("used", AttributeValue.builder().bool(false).build());
    return map;
  }


  @Override
  public Map<String, AttributeValue> query(String token) {
    Map<String, AttributeValue> queryMap = getTokenMap(token);
    GetItemResponse itemResponse = dynamoDbClient.getItem(req -> {
      req.key(queryMap);
      req.tableName(tableName);
    });
    if (!itemResponse.hasItem()) {
      log.error("try to get item {}, but failed", token);
      // todo check ttl
      throw new RuntimeException("");
    }

//    String ttl = itemResponse.item().get("ttl").n();
//    if (Long.parseLong(ttl) < Instant.now().getEpochSecond()) {
//      throw new CustomException("This link has expired");
//    }
    return itemResponse.item();
  }

  @Override
  public AttributeValue getValue(String token, String key) {
    Map<String, AttributeValue> test = query(token);
    return test.get(key);
  }

}
