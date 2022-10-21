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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.neu.edu.util.Constants.TASK_PERMISSION_DENIED;

/**
 * @author arronshentu
 */
@Service
@Slf4j
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
    Map<String, AttributeValue> items = awsService.query(token);
    String userId = items.getOrDefault("userId", AttributeValue.builder().s("").build()).s();
    User user = mapper.selectById(userId);
    if (Objects.isNull(user)) {
      throw new CustomException("User does not exist");
    }
    if (user.isVerified()) {
      throw new CustomException("User has already activated");
    }

    if (items.containsKey("username")) {
      // 则说明这是一个更改的请求
      // 需要给旧邮箱发送提醒
      String email = items.get("username").s();
      log.info("email revert message");
      awsService.sendSns(email, token, "Revert Email Change", "");
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

    String token = awsService.writeToken(user.getId(), "");
    awsService.sendSns(detail.getUsername(), token, "Verification", null);

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
      throw new PermissionDeniedException(TASK_PERMISSION_DENIED);
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

    if (!Objects.equals(task.getUserId(), userId)) {
      throw new PermissionDeniedException(TASK_PERMISSION_DENIED);
    }
    return true;
  }

  @Override
  public boolean hasPermissionToEditTag(String tagId) {
    String userId = getInfo().getId();
    Tag task = tagMapper.selectById(tagId);
    if (task == null) {
      throw new CustomException("Tag does not exist");
    }
    if (!Objects.equals(task.getUserId(), userId)) {
      throw new PermissionDeniedException(TASK_PERMISSION_DENIED);
    }
    return true;
  }

  @Override
  public boolean hasPermissionToEditList(String id) {
    String userId = getInfo().getId();
    TodoList task = listMapper.selectById(id);
    if (task == null) {
      throw new CustomException("List does not exist");
    }
    if (!Objects.equals(task.getUserId(), userId)) {
      throw new PermissionDeniedException(TASK_PERMISSION_DENIED);
    }
    return true;
  }

  @Override
  public boolean hasPermissionToEditAttachment(String tag) {
    String userId = getInfo().getId();
    Attachment attachment = attachmentMapper.selectById(tag);
    if (attachment == null) {
      throw new CustomException("Attachment does not exist");
    }
    if (!Objects.equals(attachment.getUserId(), userId)) {
      throw new PermissionDeniedException("");
    }
    return true;
  }

  @Override
  @Transactional(rollbackOn = Exception.class)
  public void deleteTask(String taskId) {
    if (!hasPermissionToEditTask(taskId)) {
      throw new PermissionDeniedException(TASK_PERMISSION_DENIED);
    }

    List<Attachment> attachments = attachmentMapper.selectList(Wrappers.<Attachment>lambdaQuery().eq(Attachment::getTaskId, taskId));
    attachments.forEach(attachment -> {
      awsService.delete(attachment.getObjectKey(), false);
      attachmentMapper.deleteById(attachment.getId());
    });
    commentMapper.delete(Wrappers.<Comment>lambdaQuery().eq(Comment::getTaskId, taskId));
    taskMapper.deleteById(taskId);
  }

  @Override
  public Tag createTag(String name) {
    Tag tag = tagMapper.selectOne(Wrappers.<Tag>lambdaQuery().eq(Tag::getName, name));
    if (tag == null) {
      tag = new Tag();
      tag.setName(name);
      tag.setUserId(getInfo().getId());
      tagMapper.insert(tag);
    }
    return tag;
  }


  @Override
  public void revert(String token) {
    Map<String, AttributeValue> map = awsService.query(token);
    AttributeValue username = map.get("username");
    if (username == null || !StringUtils.hasText(username.s())) {
      log.error("username is null");
      return;
    }
    String email = username.s();
    AttributeValue userId = map.get("userId");
    if (userId == null || !StringUtils.hasText(userId.s())) {
      log.error("userId is null");
      return;
    }
    User user = mapper.selectById(userId.s());
    user.setUsername(email);
    
    mapper.updateById(user);
  }

}
