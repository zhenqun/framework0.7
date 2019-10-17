package com.fosung.framework.common.util;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @Author : liupeng
 * @Date : 2018/8/24 10:54
 * @Modified By
 */
public class UtilAnnotation extends AnnotationUtils {

    /**
     * 查找注解，查找顺序：<br>
     *     1. 在当前类的方法注解中查找 <br>
     *     2. 在父类的方法注解中查找 <br>
     *     3. 在当前类的注解中查找 <br>
     *     4. 在父类的注解中查找 <br>
     * @param method
     * @param annotationType
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A findAnnotationWithCascade(Method method, @Nullable Class<A> annotationType) {
        //在当前类方法 和 父类方法中查找
        A methodAnnotation = findAnnotation( method , annotationType ) ;

        //在当前类和父类中查找
        if( methodAnnotation==null ){
            methodAnnotation = findAnnotation( method.getDeclaringClass() , annotationType ) ;
        }

        return methodAnnotation ;
    }

}
