package com.neu.edu.util;

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
    HIGH("high"), MEDIUM("medium"), LOW("low");
    public final String label;

    TaskPriority(String label) {
      this.label = label;
    }
  }
}
