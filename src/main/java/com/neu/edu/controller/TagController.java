package com.neu.edu.controller;

import com.neu.edu.entity.Tag;
import com.neu.edu.exception.CustomException;
import com.neu.edu.mapper.TagMapper;
import com.neu.edu.mapper.TagTaskMapper;
import com.neu.edu.mapper.TaskMapper;
import com.neu.edu.service.UserService;
import com.neu.edu.util.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
    tagTaskMapper.untag(tagId, taskId);
  }

  @PostMapping("attach")
  void tag(@RequestParam("tagId") String tagId, @RequestParam("taskId") String taskId) {
    if (tagTaskMapper.countTags(taskId) >= maxTagAmount) {
      throw new CustomException("");
    }
    tagTaskMapper.tag(tagId, taskId);
  }

  @PostMapping
  Result<Object> createTag(@RequestBody Tag tag) {
    return Result.buildOkData(userService.createTag(tag.getName()));
  }

  @PutMapping
  Result<Object> updateTag(Tag tag) {
    Tag query = tagMapper.selectById(tag.getId());
    userService.hasPermissionToEditTag(tag.getName());
    query.setName(tag.getName());
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
