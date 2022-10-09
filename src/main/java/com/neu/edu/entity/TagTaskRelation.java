package com.neu.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author arronshentu
 */
@Data
@TableName("t_tag_task")
public class TagTaskRelation {
  private String tagId;
  private String taskId;
}
