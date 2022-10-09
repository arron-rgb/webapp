package com.neu.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.neu.edu.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author arronshentu
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
  
  default User findByUsername(String username) {
    return selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
  }
}

