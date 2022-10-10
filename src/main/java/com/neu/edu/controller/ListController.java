package com.neu.edu.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.neu.edu.entity.Comment;
import com.neu.edu.entity.Task;
import com.neu.edu.entity.TodoList;
import com.neu.edu.exception.CustomException;
import com.neu.edu.exception.PermissionDeniedException;
import com.neu.edu.mapper.CommentMapper;
import com.neu.edu.mapper.ListMapper;
import com.neu.edu.mapper.TaskMapper;
import com.neu.edu.service.AttachmentService;
import com.neu.edu.service.UserService;
import com.neu.edu.util.Constants;
import com.neu.edu.util.Result;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author arronshentu
 */
@RequestMapping("/${api-version}/list")
@RestController
public class ListController {
  @Resource
  AttachmentService attachmentService;
  @Resource
  CommentMapper commentMapper;
  @Resource
  ListMapper listMapper;
  @Resource
  TaskMapper taskMapper;
  @Resource
  UserService userService;

  @GetMapping()
  Result<Object> getList(@RequestParam String id) {
    if (StringUtils.hasText(id)) {
      if (!userService.hasPermissionToEditList(id)) {
        throw new PermissionDeniedException("");
      }
      TodoList list = listMapper.selectById(id);
      List<Task> data = taskMapper.selectList(Wrappers.<Task>lambdaQuery().eq(Task::getListId, id));
      list.setTasks(data);
      data.forEach(task -> {
        task.setAttachments(attachmentService.listAttachments(task.getId()));
        task.setComments(commentMapper.selectList(Wrappers.<Comment>lambdaQuery().eq(Comment::getTaskId, task.getId())));
        task.setState(Constants.TaskState.fromValue(task.getDue(), task.getCompleted()));
      });
      return Result.buildOkData(list);
    }
    return Result.buildOkData(userService.getTodoLists());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Result<Object> createList(@RequestBody TodoList list) {
    if (!StringUtils.hasText(list.getName())) {
      throw new CustomException("Name cannot be empty");
    }
//    Long count = listMapper.selectCount(Wrappers.<TodoList>lambdaQuery().eq(TodoList::getName, list.getName()));
//    if(count > 0){
//      throw new CustomException("cannot use");
//    }
    list.setUserId(userService.getInfo().getId());
    listMapper.insert(list);
    return Result.buildOkData(list);
  }


  @PutMapping
  void updateList(@RequestBody TodoList list) {
    userService.updateTodoList(list);
  }
}
