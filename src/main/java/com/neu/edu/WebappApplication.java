package com.neu.edu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.neu.edu.*"})
public class WebappApplication {
  public static void main(String[] args) {
    SpringApplication.run(WebappApplication.class, args);
  }
}
