package com.neu.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author arronshentu
 */
@Data
@TableName("t_reminder")
public class Reminder {
  private String id;
  private LocalDateTime reminderTime;
  private LocalDateTime createdTime;
  private LocalDateTime updatedTime;
  private String senderId;
  private String receiverId;
}
