package com.neu.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author arronshentu
 */
@Data
@TableName("t_list")
public class TodoList {
  private String id;
  private String name;
  private LocalDateTime createdTime;
  private LocalDateTime updatedTime;
  private String userId;
}
