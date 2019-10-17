package com.fosung.framework.common.util.support;

public interface MapConverter<T,K,V> {
    K getKey(T item) ;

    V getValue(T item) ;
}