package com.neu.edu.controller;

import com.neu.edu.entity.Tag;
import com.neu.edu.exception.CustomException;
import com.neu.edu.exception.PermissionDeniedException;
import com.neu.edu.mapper.TagMapper;
import com.neu.edu.mapper.TagTaskMapper;
import com.neu.edu.mapper.TaskMapper;
import com.neu.edu.service.UserService;
import com.neu.edu.util.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author arronshentu
 */
@RequestMapping("/${api-version}/tag")
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
  @Value("${limit.tag.amount}")
  Long maxTagAmount;


  @DeleteMapping("detach")
  void untag(@RequestParam("tagId") String tagId, @RequestParam("taskId") String taskId) {
    if (!userService.hasPermissionToEditTag(tagId)) {
      throw new PermissionDeniedException("");
    }
    if (!userService.hasPermissionToEditTask(taskId)) {
      throw new PermissionDeniedException("");
    }
    tagTaskMapper.untag(tagId, taskId);
  }

  @PostMapping("attach")
  void tag(@RequestParam("tagId") String tagId, @RequestParam("taskId") String taskId) {
    if (!userService.hasPermissionToEditTag(tagId)) {
      throw new PermissionDeniedException("");
    }
    // 如果task不属于这个user 拒绝
    if (!userService.hasPermissionToEditTask(taskId)) {
      throw new PermissionDeniedException("");
    }
    if (tagTaskMapper.countTags(taskId) >= maxTagAmount) {
      throw new CustomException("");
    }
    tagTaskMapper.tag(tagId, taskId);
  }

  @PostMapping
  Result<Object> createTag(@RequestBody Tag tag) {
    tag.setUserId(userService.getInfo().getId());
    tag.setCreatedTime(LocalDateTime.now());
    tagMapper.insert(tag);
    return Result.buildOkData(tag);
  }

  @PutMapping
  Result<Object> updateTag(Tag tag) {
    Tag query = tagMapper.selectById(tag.getId());
    if (query == null) {
      throw new CustomException("");
    }
    if (!Objects.equals(query.getUserId(), userService.getInfo().getId())) {
      throw new CustomException("");
    }
    query.setName(tag.getName());
    query.setUpdatedTime(LocalDateTime.now());
    tagMapper.updateById(query);
    return Result.buildOkData(query);
  }

  @GetMapping("")
  Result<Object> getAllTags() {
    String id = userService.getInfo().getId();
    List<Tag> tags = tagMapper.listTags(id);
    return Result.buildOkData(tags);
  }

}
