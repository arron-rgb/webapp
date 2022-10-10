package com.neu.edu.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
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
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createdTime;
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updatedTime;
  private String senderId;
  private String receiverId;
}
