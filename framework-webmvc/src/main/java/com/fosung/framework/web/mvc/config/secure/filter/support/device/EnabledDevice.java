package com.fosung.framework.web.mvc.config.secure.filter.support.device;

import org.springframework.mobile.device.*;

/**
 * 允许的设备
 * @Author : liupeng
 * @Date : 2019-01-05
 * @Modified By
 */
public interface EnabledDevice {
    /**
     * 获取允许的设备
     * @return
     */
    DeviceType[] getEnabledDeviceTypes() ;

    /**
     * 设置允许的设备
     * @param enabledDeviceTypes
     */
    void setEnabledDeviceTypes(DeviceType[] enabledDeviceTypes) ;

}
