package com.neu.edu.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author arronshentu
 */
@Data
@Accessors(chain = true)
public class Message {
  String from;
  String to;
  String content;
  /**
   * Reminder/ Notification/ UsernameChange
   */
  String subject;
  String token;
  String reminderId;
  String newEmail;
  /**
   * serverless update reminder sent field
   * endpoint
   */
  String endpoint;
}
