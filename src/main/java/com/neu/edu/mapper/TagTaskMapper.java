package com.neu.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.neu.edu.entity.TagTaskRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author arronshentu
 */
@Mapper
public interface TagTaskMapper extends BaseMapper<TagTaskRelation> {

  default TagTaskRelation tag(String tagId, String taskId) {
    TagTaskRelation relation = new TagTaskRelation();
    relation.setTagId(tagId);
    relation.setTaskId(taskId);
    insert(relation);
    return relation;
  }

  default void untag(String tagId, String taskId) {
    TagTaskRelation relation = new TagTaskRelation();
    relation.setTagId(tagId);
    relation.setTaskId(taskId);
    delete(Wrappers.<TagTaskRelation>lambdaQuery().eq(TagTaskRelation::getTagId, tagId).eq(TagTaskRelation::getTaskId, taskId));
  }

  default Long countTags(String taskId){
    return selectCount(Wrappers.<TagTaskRelation>lambdaQuery().eq(TagTaskRelation::getTaskId, taskId));
  }

}
