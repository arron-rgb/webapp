package com.neu.edu.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.neu.edu.entity.Attachment;
import com.neu.edu.entity.Task;
import com.neu.edu.exception.CustomException;
import com.neu.edu.exception.PermissionDeniedException;
import com.neu.edu.mapper.AttachmentMapper;
import com.neu.edu.mapper.TaskMapper;
import com.neu.edu.service.AttachmentService;
import com.neu.edu.service.AwsService;
import com.neu.edu.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author arronshentu
 */
@Service
public class AttachmentServiceImpl implements AttachmentService {

  @Resource
  UserService userService;
  @Resource
  AwsService awsService;
  @Resource
  AttachmentMapper attachmentMapper;
  @Resource
  TaskMapper taskMapper;
  @Value("${limit.attachment.amount}")
  Long maxAttachmentAmount;

  @Override
  public Task attach(Attachment attachment) {
    String taskId = attachment.getTaskId();
    String id = attachment.getId();
    if (!userService.hasPermissionToEditAttachment(id)) {
      throw new PermissionDeniedException("");
    }
    if (!userService.hasPermissionToEditTask(taskId)) {
      throw new PermissionDeniedException("");
    }
    Long count = attachmentMapper.selectCount(Wrappers.<Attachment>lambdaQuery().eq(Attachment::getTaskId, taskId));
    if (count >= maxAttachmentAmount) {
      throw new CustomException("");
    }
    Attachment query = attachmentMapper.selectById(id);
    if (StringUtils.hasText(query.getTaskId()) || Objects.equals(query.getTaskId(), taskId)) {
      throw new CustomException("already attached");
    }
    attachment.setAttachedTime(LocalDateTime.now());
    attachmentMapper.updateById(attachment);
    return taskMapper.selectById(taskId);
  }

  @Override
  public Task detach(String id) {
    if (!userService.hasPermissionToEditAttachment(id)) {
      throw new PermissionDeniedException("");
    }
    Attachment attachment = attachmentMapper.selectById(id);
    awsService.delete(attachment.getObjectKey(), false);
    return null;
  }

  @Override
  public List<Attachment> listAttachments(String taskId) {
    if (!userService.hasPermissionToEditTask(taskId)) {
      throw new PermissionDeniedException("");
    }
    return attachmentMapper.selectList(Wrappers.<Attachment>lambdaQuery().eq(Attachment::getTaskId, taskId));
  }
}
