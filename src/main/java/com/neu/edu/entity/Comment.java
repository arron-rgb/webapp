package com.neu.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author arronshentu
 */
@Data
@TableName("t_comment")
public class Comment {
  private String id;
  private String taskId;
  private String comment;
  private LocalDateTime createdTime;
  private LocalDateTime updatedTime;
}
