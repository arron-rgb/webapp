package com.neu.edu.service;

import com.neu.edu.entity.Attachment;
import com.neu.edu.entity.Task;

import java.util.List;

/**
 * @author arronshentu
 */
public interface AttachmentService {
  Task attach(Attachment attachment);

  Task detach(String id);

  List<Attachment> listAttachments(String taskId);
}
