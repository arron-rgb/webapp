package com.neu.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.edu.entity.TagTaskRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author arronshentu
 */
@Mapper
public interface TagTaskMapper extends BaseMapper<TagTaskRelation> {
}
