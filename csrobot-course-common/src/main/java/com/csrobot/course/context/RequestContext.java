package com.csrobot.course.context;

import lombok.Data;

import java.io.Serializable;


@Data
public class RequestContext implements Serializable {
  private Long tenantId;
  private Long staffId;
  private String staffName;
  private Long companyId;

}
