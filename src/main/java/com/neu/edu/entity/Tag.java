package com.neu.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author arronshentu
 */
@Data
@TableName("t_tag")
public class Tag {
  private String id;
  
  private String userId;
  private String name;

  private LocalDateTime createdTime;
  private LocalDateTime updatedTime;
}
