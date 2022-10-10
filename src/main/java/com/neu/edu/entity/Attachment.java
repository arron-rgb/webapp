package com.neu.edu.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
  @JsonIgnore
  private String userId;
  private String url;
  @JsonIgnore
  String objectKey;
  private String name;
  /**
   * byte size
   */
  private Long size;
  private LocalDateTime attachedTime;
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createdTime;
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updatedTime;
}
