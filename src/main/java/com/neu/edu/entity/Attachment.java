package com.neu.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author arronshentu
 */
@Data
@TableName("t_attachment")
public class Attachment {
  private String id;

  /**
   * Each task has 0 or up to 5 tags
   */
  private String taskId;

  private String name;
  /**
   * byte size
   */
  private Double size;
  private LocalDateTime attachedTime;
  private LocalDateTime createdTime;
  private LocalDateTime updatedTime;
}
