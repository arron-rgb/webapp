package com.neu.edu.util;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author arronshentu
 */
public interface Constants {
  int SUCCESS = 1;
  int FAIL = 0;

  enum TaskState {
    /**
     * A Task can be in one of the following states:
     * TODO
     * COMPLETE
     * OVERDUE
     */
    TODO("todo"), COMPLETE("complete"), OVERDUE("overdue");

    public final String state;

    TaskState(String state) {
      this.state = state;
    }
  }

  enum TaskPriority {
    /**
     * Priority (High, Medium, Low)
     */
    HIGH("high", 2), MEDIUM("medium", 1), LOW("low", 0);
    public final String label;
    @EnumValue
    public int value = 0;


    TaskPriority(String label) {
      this.label = label;
    }

    TaskPriority(String label, int value) {
      this.label = label;
      this.value = value;
    }
    
    @JsonCreator
    TaskPriority fromValue(int i) {
      switch (i) {
        case 1:
          return MEDIUM;
        case 2:
          return HIGH;
        case 0:
        default:
          return LOW;
      }
    }
  }
}
