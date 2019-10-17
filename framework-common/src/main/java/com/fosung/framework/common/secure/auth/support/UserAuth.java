package com.fosung.framework.common.secure.auth.support;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class UserAuth implements Serializable {

    private static final long serialVersionUID = 1L ;

    private String roleCode ;

    private String roleName ;

    //管理组织机构id
    private Long orgId ;

    //管理组织机构编码
    private String orgCode ;

    //管理组织机构名称
    private String orgName ;

    //组织机构类型
    private String orgSource ;

}
