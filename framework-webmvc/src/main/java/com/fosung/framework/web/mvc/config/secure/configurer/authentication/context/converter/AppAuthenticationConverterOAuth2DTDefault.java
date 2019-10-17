package com.fosung.framework.web.mvc.config.secure.configurer.authentication.context.converter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fosung.framework.common.json.JsonMapper;
import com.fosung.framework.common.secure.auth.AppUserDetailsDefault;
import com.fosung.framework.common.support.dao.entity.AppJpaIdEntity;
import com.fosung.framework.common.util.UtilAuthentication;
import com.fosung.framework.common.util.UtilString;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mzlion.easyokhttp.HttpClient;
import com.mzlion.easyokhttp.request.TextBodyRequest;
import com.mzlion.easyokhttp.response.HttpResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.util.*;

/**
 * 基于灯塔sso登录的认证对象转换器
 * @Author : liupeng
 * @Date : 2019-01-06
 * @Modified By
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class AppAuthenticationConverterOAuth2DTDefault extends AppAuthenticationConverterOAuth2 {

    private static final HttpClient httpClient = HttpClient.Instance ;

    @Getter
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String USER_ATTRIBUTE_TELEPHONE  = "telephone" ;

    public static final String USER_ATTRIBUTE_LOGO  = "logo" ;

    public static final String USER_ATTRIBUTE_HASH  = "hash" ;

    //简项库地址
    @Value("${app.dt.urls.simpleData:http://10.254.23.41:8962/simpledata}")
    @Getter
    @Setter
    private String dtSimpleDataUrl ;

    /**
     * 获取用户属性值
     * @param oauth2User
     * @return
     */
    public String getUserAttribute( OAuth2User oauth2User , String attributeKey ){
        Object userHash = oauth2User.getAttributes().get( attributeKey ) ;

        return userHash!=null ? userHash.toString() : null ;
    }

    @Override
    public Object loadUserId(OAuth2User oauth2User) {
        // 使用hash作为灯塔用户的id
        return getUserAttribute( oauth2User , USER_ATTRIBUTE_HASH );
    }

    /**
     * 执行其它的属性转换
     * @param appUserDetailsDefault
     * @param oauth2User
     */
    @Override
    public void convertMore(AppUserDetailsDefault appUserDetailsDefault , OAuth2User oauth2User ){
        // 设置用户手机号信息
        appUserDetailsDefault.setTelephone( getUserAttribute( oauth2User , USER_ATTRIBUTE_TELEPHONE ) );
        appUserDetailsDefault.setAvatar( getUserAttribute( oauth2User , USER_ATTRIBUTE_LOGO ) );

        this.loadUserOrg( appUserDetailsDefault , oauth2User ) ;
    }

    /**
     * 装载用户组织机构信息
     * @param appUserDetailsDefault
     * @param oauth2User
     */
    public void loadUserOrg(AppUserDetailsDefault appUserDetailsDefault , OAuth2User oauth2User ){
        String userHash = getUserAttribute( oauth2User , USER_ATTRIBUTE_HASH ) ;
        if( UtilString.isBlank( userHash ) ){
            return ;
        }

        TextBodyRequest textBodyRequest = httpClient.textBody(dtSimpleDataUrl + "/queryUserInfoByHash")
                .json(Lists.newArrayList(userHash)) ;

        // 发起请求
        HttpResponse httpResponse = textBodyRequest.execute() ;

        if( !httpResponse.isSuccess() ){
            log.error("请求失败：{}" , JsonMapper.toJSONString( textBodyRequest )); ;
            return ;
        }

        String resultText = httpResponse.asString() ;
        if(UtilString.isBlank( resultText )){
            return ;
        }

        List<TmpDTUser> resultList = null;
        try {
            resultList = (List<TmpDTUser>)objectMapper.readValue(resultText, getCollectionType(ArrayList.class, TmpDTUser.class) );
        } catch (IOException e) {
            e.printStackTrace();
        }

        TmpDTUser dtUser = resultList != null && resultList.size() > 0 ? resultList.get(0) : null;
        if( dtUser==null ){
            log.info("根据用户hash= {} 查询的用户详情为空" , userHash);
            return ;
        }

        // 设置组织机构信息
        appUserDetailsDefault.setOrgId( dtUser.getOrgId() ) ;
        appUserDetailsDefault.setOrgCode( dtUser.getOrgCode() ) ;
        appUserDetailsDefault.setOrgName( dtUser.getOrgName() ) ;
        // 设置个人信息
        appUserDetailsDefault.setUserRealName( dtUser.getUserName() ) ;
    }

    /**
     * 获取泛型的Collection Type
     * @param collectionClass 泛型的Collection
     * @param elementClasses 元素类
     * @return JavaType Java类型
     * @since 1.0
     */
    public JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    @Override
    public Collection<GrantedAuthority> loadUserAuthorities(OAuth2User oauth2User , AppUserDetailsDefault appUserDetailsDefault) {
        Object roles = oauth2User.getAttributes().get("roles") ;
        if( roles==null || !(roles instanceof List) ){
            return Lists.newArrayList() ;
        }

        // 抽取返回的用户角色列表，只返回当前clientId对应的权限信息
        Set<String> handledRecords = Sets.newHashSet() ;
        Set<String> userRoles = Sets.newHashSet() ;
        ((List<Object>)roles).stream().filter( role-> role instanceof Map).forEach(role->{
            Map roleMap = (Map)role ;
            // 对已经处理的记录进行去重
            if( handledRecords.contains( role.hashCode()+"" ) ){
                return;
            }
            // 添加hash编码
            handledRecords.add( role.hashCode()+"" ) ;

            // 包含对应的clientId
            if( this.getClientIdRegistrations().containsKey( roleMap.get("clientId") ) ){
                Object roleCode = roleMap.get("roleName") ;
                if( roleCode != null ){
                    userRoles.add( roleCode.toString() ) ;
                }
                //log.info( "权限编码->"+roleMap.hashCode() );
                // 添加用户权限
                appUserDetailsDefault.getUserPermissions().add( roleMap ) ;
            }
        });

        log.info("登录用户: {} , 包含角色: {} , 包含权限: {}" , oauth2User.getName() , UtilString.joinByComma( userRoles ) ,
                JsonMapper.toJSONString( appUserDetailsDefault.getUserPermissions() ));

        return UtilAuthentication.convertRoleToGrantedAuthority( userRoles );
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class TmpDTUser extends AppJpaIdEntity {

        private String orgId ;

        private String orgCode ;

        private String orgName ;

        private String hash ;

        private String userId ;

        private String userName ;

        private String telephone ;

        @JsonProperty("param1")
        private String realName ;

        private Set<String> roleCodes ;
    }

}
