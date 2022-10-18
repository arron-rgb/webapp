package com.neu.edu.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.neu.edu.util.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author arronshentu
 */
@Data
@EqualsAndHashCode
@TableName("t_task")
public class Task {
  private String id;

  private LocalDateTime due;
  /**
   * Priority (High, Medium, Low)
   *
   * @see com.neu.edu.util.Constants.TaskPriority
   */
  private Constants.TaskPriority priority;
  /**
   * enum type
   * don't persist in DB
   * act as a view object field
   *
   * @see com.neu.edu.util.Constants.TaskState
   */
  @TableField(exist = false)
  private Constants.TaskState state;
  private String userId;
  private String listId;
  private Integer completed;
  private String task;
  /**
   * 50 chars
   */
  private String summary;
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createdTime;
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updatedTime;

  @TableField(exist = false)
  private List<Attachment> attachments;
  @TableField(exist = false)
  private List<Reminder> reminders;
  @TableField(exist = false)
  private List<Comment> comments;

}
