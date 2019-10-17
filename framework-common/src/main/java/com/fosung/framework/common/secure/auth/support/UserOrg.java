package com.fosung.framework.common.secure.auth.support;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class UserOrg implements Serializable {

    private static final long serialVersionUID = 1L ;

    private Long id ;

    private String code ;

    private String name ;

    private String source ;

    private String outId ;
}
