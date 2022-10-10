package com.neu.edu.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.edu.entity.*;
import com.neu.edu.exception.CustomException;
import com.neu.edu.exception.MailExistsException;
import com.neu.edu.exception.PermissionDeniedException;
import com.neu.edu.mapper.*;
import com.neu.edu.service.AwsService;
import com.neu.edu.service.UserService;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author arronshentu
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService, UserDetailsService {
  @Resource
  PasswordEncoder passwordEncoder;
  @Resource
  AttachmentMapper attachmentMapper;
  @Resource
  CommentMapper commentMapper;
  @Resource
  TagMapper tagMapper;
  @Resource
  TaskMapper taskMapper;
  @Resource
  ListMapper listMapper;
  @Resource
  AwsService awsService;
  @Resource
  UserMapper mapper;

  @Override
  public void verify(String token) {
    String userId = awsService.getUserIdFromToken(token);
    User user = mapper.selectById(userId);
    if (StringUtils.isEmpty(userId) || Objects.isNull(user)) {
      throw new CustomException("User does not exist");
    }
    if (user.isVerified()) {
      throw new CustomException("User has already activated");
    }
    user.setVerified(true);
    mapper.updateById(user);
  }

  @Override
  @Transactional(rollbackOn = Exception.class)
  public User signUp(User detail) {
    User user = mapper.findByUsername(detail.getUsername());
    if (!Objects.isNull(user)) {
      throw new MailExistsException("Username exists!");
    }
    user = new User();
    user.setPassword(passwordEncoder.encode(detail.getPassword()));
    user.setUsername(detail.getUsername());
    user.setLastName(detail.getLastName());
    user.setFirstName(detail.getFirstName());
    mapper.insert(user);

    TodoList defaultList = new TodoList();
    defaultList.setUserId(user.getId());
    defaultList.setName("default");
    defaultList.setCreatedTime(LocalDateTime.now());
    listMapper.insert(defaultList);

//    String token = awsService.writeToken(user.getId());
//    awsService.sendSns(detail.getUsername(), token);

    return user;
  }

  @Override
  public User getInfo() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!StringUtils.hasText(auth.getName()) || "anonymousUser".equals(auth.getName())) {
      throw new AuthenticationServiceException("Unauthorized");
    }
    return checkIfExist(auth.getName());
  }

  public User checkIfExist(String username) {
    if (!StringUtils.hasText(username)) {
      throw new CustomException("Username cannot be blank");
    }
    User one = mapper.findByUsername(username);
    if (one == null) {
      throw new CustomException("User does not exist");
    }
    return one;
  }

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    return new CustomUserDetails(checkIfExist(s));
  }


  @Override
  public List<TodoList> getTodoLists() {
    User user = getInfo();
    return listMapper.selectList(Wrappers.<TodoList>lambdaQuery().eq(TodoList::getUserId, user.getId()));
  }

  @Override
  public TodoList updateTodoList(TodoList list) {
    if (!hasPermissionToEditList(list.getId())) {
      throw new PermissionDeniedException("");
    }
    TodoList one = listMapper.selectById(list.getId());
    one.setName(list.getName());
    listMapper.updateById(one);
    return one;
  }

  @Override
  public boolean hasPermissionToEditTask(String taskId) {
    String userId = getInfo().getId();
    Task task = taskMapper.selectById(taskId);
    if (task == null) {
      throw new CustomException("Task does not exist");
    }
    return Objects.equals(task.getUserId(), userId);
  }

  @Override
  public boolean hasPermissionToEditTag(String tagId) {
    String userId = getInfo().getId();
    Tag task = tagMapper.selectById(tagId);
    if (task == null) {
      throw new CustomException("Tag does not exist");
    }
    return Objects.equals(task.getUserId(), userId);
  }

  @Override
  public boolean hasPermissionToEditList(String id) {
    String userId = getInfo().getId();
    TodoList task = listMapper.selectById(id);
    if (task == null) {
      throw new CustomException("List does not exist");
    }
    return Objects.equals(task.getUserId(), userId);
  }

  @Override
  public boolean hasPermissionToEditAttachment(String tag) {
    String userId = getInfo().getId();
    Attachment attachment = attachmentMapper.selectById(tag);
    if (attachment == null) {
      throw new CustomException("Attachment does not exist");
    }
    return Objects.equals(attachment.getUserId(), userId);
  }

  @Override
  @Transactional(rollbackOn = Exception.class)
  public void deleteTask(String taskId) {
    if (!hasPermissionToEditTask(taskId)) {
      throw new PermissionDeniedException("");
    }

    List<Attachment> attachments = attachmentMapper.selectList(Wrappers.<Attachment>lambdaQuery().eq(Attachment::getTaskId, taskId));
    attachments.forEach(attachment -> {
      awsService.delete(attachment.getObjectKey(), false);
      attachmentMapper.deleteById(attachment.getId());
    });
    commentMapper.delete(Wrappers.<Comment>lambdaQuery().eq(Comment::getTaskId, taskId));
    taskMapper.deleteById(taskId);
  }

}
