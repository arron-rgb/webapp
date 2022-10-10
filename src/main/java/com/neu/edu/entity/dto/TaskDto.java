package com.neu.edu.entity.dto;

import com.neu.edu.util.Constants;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

/**
 * @author arronshentu
 */
@Data
public class TaskDto {
  String id;
  String listId;
  @NotEmpty
  @Max(50)
  String summary;
  @Max(255)
  String task;
  LocalDateTime due;
  Constants.TaskPriority priority;
}
