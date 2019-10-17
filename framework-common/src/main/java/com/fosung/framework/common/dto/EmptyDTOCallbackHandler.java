package com.fosung.framework.common.dto;

import com.fosung.framework.common.dto.support.DTOCallbackHandler;

import java.util.Map;

/**
 * 对于dto不做任何处理
 */
public class EmptyDTOCallbackHandler implements DTOCallbackHandler {
    @Override
    public void doHandler(Map<String, Object> dtoMap , Class<?> itemClass) {

    }
}
