package com.csrobot.course.mybatis;

public interface ITableFieldConditionDecision {

  Boolean isAllowNullValue();

  Boolean adjudge(String tableName, String fieldName);
}
