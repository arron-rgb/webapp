package com.neu.edu.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.neu.edu.entity.Tag;

@Mapper
public interface TagMapper extends BaseMapper<Tag>{
  default List<Tag> listTags(String userId){
    return selectList(Wrappers.<Tag>lambdaQuery().eq(Tag::getUserId, userId));
  }
}
