package com.neu.edu.controller;

import com.neu.edu.entity.Attachment;
import com.neu.edu.entity.Task;
import com.neu.edu.exception.CustomException;
import com.neu.edu.mapper.AttachmentMapper;
import com.neu.edu.service.AttachmentService;
import com.neu.edu.service.AwsService;
import com.neu.edu.service.UserService;
import com.neu.edu.util.Result;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * @author arronshentu
 */
@RequestMapping("/${api-version}/attachment")
@RestController
public class AttachmentController {

  @Resource
  AttachmentMapper attachmentMapper;
  @Resource
  UserService userService;
  @Resource
  AttachmentService attachmentService;
  @Resource
  AwsService awsService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Result<Object> uploadAttachment(@RequestParam("name") String name, @RequestParam("attachment") MultipartFile file) {
    Attachment attachment = new Attachment();
    attachment.setName(name);
    attachment.setUserId(userService.getInfo().getId());
    attachment.setSize(file.getSize());
    try {
      Map<String, String> upload = awsService.upload(file);
      attachment.setObjectKey(upload.get("filename"));
      attachment.setUrl(upload.get("url"));
      attachmentMapper.insert(attachment);
      return Result.buildOkData(attachment);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @PostMapping("attach")
  @ResponseStatus(HttpStatus.CREATED)
  Task attach(@RequestBody Attachment attachment) {
    if (!StringUtils.hasText(attachment.getId())) {
      throw new CustomException("cannot be empty");
    }
    if (!StringUtils.hasText(attachment.getTaskId())) {
      throw new CustomException("cannot be empty");
    }
    return attachmentService.attach(attachment);
  }

  @GetMapping("")
  Attachment getInfo(@RequestParam("id") String id) {
    return attachmentMapper.selectById(id);
  }


  @DeleteMapping("")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void delete(@RequestParam("id") String id) {
    attachmentService.detach(id);
  }
}
