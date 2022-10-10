package com.neu.edu.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author arronshentu
 */
@Data
@TableName("t_list")
public class TodoList {
  private String id;
  private String name;
  private String userId;
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createdTime;
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updatedTime;
  @TableField(exist = false)
  private List<Task> tasks;
}
