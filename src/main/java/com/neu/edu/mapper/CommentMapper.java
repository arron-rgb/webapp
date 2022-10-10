package com.neu.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.edu.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author arronshentu
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
