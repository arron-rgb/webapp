package com.neu.edu.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author arronshentu
 */
public interface AwsService {
  Map<String, String> upload(MultipartFile file) throws IOException;

  void delete(String key, boolean throwEx);

  String writeToken(String userId);

  void sendSns(String to, String token);

  String getUserIdFromToken(String token);

  void deleteToken(String token);

  boolean isValid(String token);
}
