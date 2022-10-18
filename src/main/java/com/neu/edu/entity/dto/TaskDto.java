package com.neu.edu.entity.dto;

import com.neu.edu.util.Constants;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * @author arronshentu
 */
@Data
public class TaskDto {
  String id;
  String listId;
  @NotEmpty
  @Size(max = 50)
  String summary;
  @Size(max = 255)
  String task;
  LocalDateTime due;
  Constants.TaskPriority priority;
}
