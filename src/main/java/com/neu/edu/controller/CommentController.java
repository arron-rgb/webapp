package com.neu.edu.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.neu.edu.entity.Comment;
import com.neu.edu.exception.CustomException;
import com.neu.edu.exception.PermissionDeniedException;
import com.neu.edu.mapper.CommentMapper;
import com.neu.edu.service.UserService;
import com.neu.edu.util.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author arronshentu
 */
@RequestMapping("/${api-version}/comment")
@RestController
public class CommentController {
  @Resource
  UserService userService;
  @Resource
  CommentMapper commentMapper;

  @DeleteMapping()
  void comment(@RequestParam("id") String commentId) {
    Comment query = commentMapper.selectById(commentId);
    if (query == null) {
      throw new CustomException("");
    }
    if (!userService.hasPermissionToEditTask(query.getTaskId())) {
      throw new PermissionDeniedException("");
    }
    commentMapper.deleteById(commentId);
  }

  @PostMapping
  Result<Object> createComment(@RequestBody Comment comment) {
    if (!userService.hasPermissionToEditTask(comment.getTaskId())) {
      throw new PermissionDeniedException("");
    }
    commentMapper.insert(comment);
    return Result.buildOkData(comment);
  }

  @PutMapping
  Result<Object> updateComment(@RequestBody Comment comment) {
    Comment query = commentMapper.selectById(comment.getId());
    if (query == null) {
      throw new CustomException("");
    }
    // 如果task不属于这个user 拒绝
    if (!userService.hasPermissionToEditTask(query.getTaskId())) {
      throw new PermissionDeniedException("");
    }
    query.setComment(comment.getComment());
    commentMapper.updateById(query);
    return Result.buildOkData(query);
  }

  @GetMapping("")
  Result<Object> getComment(@RequestParam("taskId") String taskId) {
    return Result.buildOkData(commentMapper.selectList(Wrappers.<Comment>lambdaQuery().eq(Comment::getTaskId, taskId)));
  }

}
