package com.fosung.framework.common.secure.dataccess;

import com.fosung.framework.common.secure.dataccess.exception.APIDataAccessException;
import com.fosung.framework.common.secure.dataccess.strategy.APIDataAccessAuthStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * 数据权限入口的默认实现，将请求转发到不同的策略类中
 * @Author : liupeng
 * @Date : 2018/8/23 11:22
 * @Modified By
 */
@Slf4j
public class APIDataAccessAuthServiceDefaultImpl implements APIDataAccessAuthService {

    @Autowired
    private ApplicationContext applicationContext ;

    @Override
    public void saveOrUpdateDataAccessAuth( Object record ) {
        //获取数据访问认证为委托类
        APIDataAccessPrincipal dataAccessPrincipal = APIDataAccessPrincipalHolder.getPrincipalHolder() ;
        //没有保存认证委托类、没有开启认证、没有指定认证策略类，默认不进行相关认证逻辑
        if( dataAccessPrincipal==null || dataAccessPrincipal.getApiDataAccess()==null ||
                !dataAccessPrincipal.getApiDataAccess().enable() ||
                dataAccessPrincipal.getApiDataAccess().strategyClass()==null ){
            return ;
        }

        // 获取认证策略类
        APIDataAccessAuthStrategy dataAccessAuthStrategy = getAPIDataAccessAuthStrategy( dataAccessPrincipal.getApiDataAccess().strategyClass() ) ;

        // 保存认证相关数据
        dataAccessAuthStrategy.saveOrUpdateDataAccessAuth( record , dataAccessPrincipal ) ;
    }

    /**
     * 是否有数据访问权限
     * @return
     */
    @Override
    public boolean isAccessAllowed( Object entity ) throws APIDataAccessException {

        APIDataAccessPrincipal dataAccessPrincipal = APIDataAccessPrincipalHolder.getPrincipalHolder() ;

        //没有保存认证委托类、没有开启认证、没有指定认证策略类，默认不进行相关认证逻辑
        if( dataAccessPrincipal==null || dataAccessPrincipal.getApiDataAccess()==null ||
                !dataAccessPrincipal.getApiDataAccess().enable() ||
                dataAccessPrincipal.getApiDataAccess().strategyClass()==null ){
            return true ;
        }

        APIDataAccessAuthStrategy dataAccessAuthStrategy = getAPIDataAccessAuthStrategy( dataAccessPrincipal.getApiDataAccess().strategyClass() ) ;

        boolean authResult = dataAccessAuthStrategy.isAccessAllowed( entity , dataAccessPrincipal ) ;

        log.info("执行数据权限认证验证逻辑, 结果:{}" , authResult);

        return authResult ;
    }

    /**
     * 格式化或填充查询条件
     * @param searchParams
     */
    @Override
    public Map<String,Object> fillAccessAuthQueryParam(Map<String,Object> searchParams) {
        APIDataAccessPrincipal dataAccessPrincipal = APIDataAccessPrincipalHolder.getPrincipalHolder() ;

        //没有保存认证委托类、没有开启认证、没有指定认证策略类，默认不进行相关认证逻辑
        if( dataAccessPrincipal==null || dataAccessPrincipal.getApiDataAccess()==null ||
                !dataAccessPrincipal.getApiDataAccess().enable() ||
                dataAccessPrincipal.getApiDataAccess().strategyClass()==null ){
            return searchParams ;
        }

        APIDataAccessAuthStrategy dataAccessAuthStrategy = getAPIDataAccessAuthStrategy( dataAccessPrincipal.getApiDataAccess().strategyClass() ) ;

        Map<String, Object> map = dataAccessAuthStrategy.fillAccessAuthQueryParam( searchParams , dataAccessPrincipal ) ;

        return map;
    }


    /**
     * 获取数据访问认证策略类
     * @param strategyClass
     * @return
     */
    public APIDataAccessAuthStrategy getAPIDataAccessAuthStrategy( Class<? extends APIDataAccessAuthStrategy> strategyClass ){
        APIDataAccessAuthStrategy dataAccessAuthStrategy = applicationContext.getBean( strategyClass ) ;

        return dataAccessAuthStrategy ;
    }

}
