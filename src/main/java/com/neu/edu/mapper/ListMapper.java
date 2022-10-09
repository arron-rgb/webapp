package com.neu.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.edu.entity.TodoList;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author arronshentu
 */
@Mapper
public interface ListMapper extends BaseMapper<TodoList> {
}
