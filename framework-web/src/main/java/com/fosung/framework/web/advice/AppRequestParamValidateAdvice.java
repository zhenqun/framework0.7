package com.fosung.framework.web.advice;

import com.fosung.framework.dao.util.UtilValidator;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 针对RequestMapping中方法的参数进行验证，验证失败后返回ValidationException异常
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
@Slf4j
@Component
@Aspect
public class AppRequestParamValidateAdvice {

    /**
     * 切点设置为带有@RequestMapping的方法
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void validRequestMappingMethodParam(){};

    /**
     * 切点设置为带有@PostMapping 的方法
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void validPostMappingMethodParam(){};

    /**
     * 切点设置为带有@GetMapping 的方法
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void validGetMappingMethodParam(){};

    @Before("validRequestMappingMethodParam()")
    public void beforeRequestMappingMethod(JoinPoint point){
        doValid( point ) ;
    }

    @Before("validPostMappingMethodParam()")
    public void beforePostMappingMethod(JoinPoint point){
        doValid( point ) ;
    }

    @Before("validGetMappingMethodParam()")
    public void beforeGetMappingMethod(JoinPoint point){
        doValid( point ) ;
    }

    private void doValid( JoinPoint point ){
        //获取的是代理后的对象
//        Object target = point.getThis() ;
        // 获得切入的方法
        Method method = ((MethodSignature)point.getSignature()).getMethod() ;

        //没有代理的对象进行参数的验证，避免获取类的名称中带有的其它字母
        List<String> invalidMessages = UtilValidator.validateParameters( point.getTarget() , method , point.getArgs() ) ;

        //验证结果为空，则抛出异常
        if( CollectionUtils.isNotEmpty( invalidMessages ) ){
            throw new ValidationException( "方法参数值验证错误:"+ Joiner.on(";").join( invalidMessages ) ) ;
        }
    }

}