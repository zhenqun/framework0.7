package com.fosung.framework.common.dto;

import com.fosung.framework.common.dto.support.DTOCallbackHandler;
import com.fosung.framework.common.dto.support.DTOCallbackHandlerAdaptor;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * DTO回调处理类
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
@Slf4j
public class DTOCallbackHandlerDelegate extends DTOCallbackHandlerAdaptor {

    @Getter
    protected List<DTOCallbackHandler> dtoCallbackHandlers = Lists.newArrayList() ;

    //存储不同线程的回调处理类
    private DTOCallbackHandler targetDTOCallbackHandler ;

    /**
     * 是否在其它DTOCallbackHandler之前执行被代理的DTOCallbackHandler
     */
    @Setter
    @Getter
    private boolean executeTargetDTOCallbackHandlerFirst = false ;

    public DTOCallbackHandlerDelegate(){
        // 清除敏感字段
        addDTOCallbackHandlerTask( new DTOCallbackHandlerClearBaseEntityFields() ) ;

        // 对数字处理的handler
        addDTOCallbackHandlerTask( new DTOCallbackHandlerWithNumber() ) ;

        // 对日期处理的handler
        addDTOCallbackHandlerTask( new DTOCallbackHandlerWithDate() ) ;

        // 对运行时枚举字段进行处理
        addDTOCallbackHandlerTask( new DTOCallbackHandlerWithRuntimeDict() ) ;
    }

    /**
     * 添加dto回调的处理函数
     * @param dtoCallbackHandlers
     */
    public void addDTOCallbackHandlerTask(DTOCallbackHandler... dtoCallbackHandlers){
        for (DTOCallbackHandler dtoCallbackHandler : dtoCallbackHandlers) {
            Assert.isTrue(dtoCallbackHandler!=null , "dtoCallbackHandler不能为空") ;

            Assert.isTrue( !hasDTOCallbackHandler( dtoCallbackHandler.getClass() ) , "已经包含 " +
                    dtoCallbackHandler.getClass().getName()+" , 不能重复添加" );

            this.dtoCallbackHandlers.add( dtoCallbackHandler ) ;
        }
    }

    /**
     * 是否包含指定类型的 DTOCallbackHandler
     * @param dtoCallbackHandlerClass
     * @return
     */
    public boolean hasDTOCallbackHandler(Class<? extends DTOCallbackHandler> dtoCallbackHandlerClass ){
        // 判断是否包含指定的 DTOCallbackHandler
        for (DTOCallbackHandler dtoCallbackHandler : dtoCallbackHandlers) {
            if( dtoCallbackHandler.getClass().getName().equalsIgnoreCase( dtoCallbackHandlerClass.getName() ) ){
                return true ;
            }
        }
        return false ;
    }

    /**
     * 获取指定类型的 DTOCallbackHandler
     * @param dtoCallbackHandlerClass
     * @param <T>
     * @return
     */
    public <T extends DTOCallbackHandler> T getDTOCallbackHandler(Class<T> dtoCallbackHandlerClass ){
        for (DTOCallbackHandler dtoCallbackHandler : dtoCallbackHandlers) {
            if( dtoCallbackHandler.getClass().getName().equalsIgnoreCase( dtoCallbackHandlerClass.getName() ) ){
                return (T)dtoCallbackHandler ;
            }
        }
        return null ;
    }

    /**
     * 设置需要最后执行的DTOCallbackHandler
     * @param dtoCallbackHandler
     */
    public void setTargetDTOCallbackHandler(DTOCallbackHandler dtoCallbackHandler){
        targetDTOCallbackHandler = dtoCallbackHandler ;
    }

    @Override
    public <T> void preHandle(List<Map<String, Object>> objectList , Class<?> itemClass) {
        super.preHandle(objectList , itemClass) ;

        //针对处理过程类调用处理前的函数
        for (DTOCallbackHandler dtoCallbackHandler : this.dtoCallbackHandlers) {
            if( dtoCallbackHandler != null && dtoCallbackHandler instanceof DTOCallbackHandlerAdaptor){
                ((DTOCallbackHandlerAdaptor)dtoCallbackHandler).preHandle( objectList , itemClass ) ;
            }
        }

        //针对当前类调用处理前的函数
        if( targetDTOCallbackHandler != null && targetDTOCallbackHandler instanceof DTOCallbackHandlerAdaptor){
            ((DTOCallbackHandlerAdaptor)targetDTOCallbackHandler).preHandle( objectList , itemClass ) ;
        }

    }

    @Override
    public <T> void postHandle(List<Map<String, Object>> dtoMapList , Class<?> itemClass) {
        super.postHandle(dtoMapList , itemClass ) ;

        //针对处理过程类调用完成处理的函数
        for (DTOCallbackHandler dtoCallbackHandler : this.dtoCallbackHandlers) {
            if( dtoCallbackHandler != null && dtoCallbackHandler instanceof DTOCallbackHandlerAdaptor){
                ((DTOCallbackHandlerAdaptor)dtoCallbackHandler).postHandle( dtoMapList , itemClass ) ;
            }
        }

        //dto转换完成之后，进行统一的处理
        if( targetDTOCallbackHandler != null && targetDTOCallbackHandler instanceof DTOCallbackHandlerAdaptor){
            ((DTOCallbackHandlerAdaptor)targetDTOCallbackHandler).postHandle( dtoMapList , itemClass ) ;
        }

    }

    @Override
    public void doHandler(Map<String, Object> dtoMap , Class<?> itemClass) {

        log.debug("执行dto回调处理函数") ;

        // 判断是否需要在统一的处理类之前进行处理
        if(isExecuteTargetDTOCallbackHandlerFirst() && targetDTOCallbackHandler!=null){
            targetDTOCallbackHandler.doHandler(dtoMap , itemClass) ;
        }

        for (DTOCallbackHandler dtoCallbackHandler : this.dtoCallbackHandlers) {
            dtoCallbackHandler.doHandler(dtoMap , itemClass) ;
        }

        // 判断是否在统一的处理类之后进行处理
        if(! isExecuteTargetDTOCallbackHandlerFirst() && targetDTOCallbackHandler!=null){
            targetDTOCallbackHandler.doHandler(dtoMap , itemClass) ;
        }

    }

}
