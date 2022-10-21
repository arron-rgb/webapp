package com.neu.edu.util;

import com.neu.edu.entity.Message;

/**
 * @author arronshentu
 */
public interface EmailTemplate {

  String CHANGE_COMPLETED = "<h1>Example HTML Message Body</h1> <br> <p>Your username has been transfer to your current email address!</p>";

  static String generateVerificationContent(Message message) {
    return String.format("<h1>Example HTML Message Body</h1> <br>" + "        <p>Here is the link for your last registration  <br>"
      + "        <a href=%s?token=%s>Click Here</a> <br>" + "        </p> <br>"
      + "        <p>Check your name is %s<br>" + "        </p>", ApplicationConfig.getEndpoint("server.verify-path"), message.getToken(), message.getTo());
  }

  static String revertContent(String token) {
    return String.format("Your account changed email successfully! If you didn't do it, click this link get account back. %s?token=%s", ApplicationConfig.getEndpoint("server.revert-path"), token);
  }
}
