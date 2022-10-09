package com.neu.edu.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neu.edu.entity.Task;

@Mapper
public interface TaskMapper extends BaseMapper<Task>{
  
}
