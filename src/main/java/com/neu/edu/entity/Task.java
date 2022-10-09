package com.neu.edu.entity;

import com.neu.edu.util.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author arronshentu
 */
@Data
@EqualsAndHashCode
public class Task {
  private Long id;

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
  private Constants.TaskState state;
  private String userId;
  private String listId;

  private String task;
  /**
   * 50 chars
   */
  private String summary;
  private LocalDateTime createdTime;
  private LocalDateTime updatedTime;

}
