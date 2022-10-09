package com.neu.edu.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neu.edu.entity.Tag;
import com.neu.edu.entity.TagTaskRelation;
import com.neu.edu.entity.Task;
import com.neu.edu.exception.CustomException;
import com.neu.edu.mapper.TagMapper;
import com.neu.edu.mapper.TagTaskMapper;
import com.neu.edu.mapper.TaskMapper;
import com.neu.edu.service.UserService;
import com.neu.edu.util.Result;

@RequestMapping("/${api-version}/tags")
@RestController
public class TagController {
  @Resource
  TaskMapper taskMapper;
  @Resource
  TagTaskMapper tagTaskMapper;
  @Resource
  UserService userService;
  @Resource
  TagMapper tagMapper;
  

  @DeleteMapping("/{tagId}/{taskId}")
  void untag(@PathVariable String tagId, @PathVariable String taskId){
    // 如果这个tag不属于这个user 拒绝
    Tag tag = tagMapper.selectById(tagId);
    if(tag == null){
      throw new CustomException("");
    }
    if(!Objects.equals(tag.getUserId(), userService.getInfo().getId()){
      throw new CustomException("");
    }
    tagTaskMapper.untag(tagId, taskId);
  }

  @PostMapping("/{tagId}/{taskId}")
  void tag(@PathVariable String tagId, @PathVariable String taskId){
    Tag tag = tagMapper.selectById(tagId);
    if(tag == null){
      throw new CustomException("");
    }
    if(!Objects.equals(tag.getUserId(), userService.getInfo().getId()){
      throw new CustomException("");
    }
    // 如果task不属于这个user 拒绝
    Task task = taskMapper.selectById(taskId);
    if(task == null){
      throw new CustomException("");
    }
    if(!Objects.equals(task.getUserId(), userService.getInfo().getId()){
      throw new CustomException("");
    }
    if(tagTaskMapper.countTags(taskId) >= 10L){
      throw new CustomException("");
    }
    tagTaskMapper.tag(tagId, taskId);
  }

  @PostMapping
  Result<Object> createTag(Tag tag){
    tag.setUserId(userService.getInfo().getId());
    tag.setCreatedTime(LocalDateTime.now());
    tagMapper.insert(tag);
    return Result.buildOkData(tag);
  }

  @GetMapping("/")
  Result<Object> getAllTags(){
    String id = userService.getInfo().getId();
    List<Tag> tags = tagMapper.listTags(id);
    return Result.buildOkData(tags);
  }
  
}
