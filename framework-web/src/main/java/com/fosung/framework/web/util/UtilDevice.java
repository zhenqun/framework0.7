package com.fosung.framework.web.util;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DevicePlatform;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.mobile.device.DeviceType;
import org.springframework.mobile.device.LiteDeviceResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Iterator;

/**
 * 发起http请求的设备
 * @Author : liupeng
 * @Date : 2018/7/27 14:36
 * @Modified By
 */
public abstract class UtilDevice {

    /**
     * 请求来源检测
     */
    public static final DeviceResolver DEVICERESOLVER = new LiteDeviceResolver();

    /**
     * 解析发起请求的设备类型
     *
     * @param request
     * @return
     */
    public static Device resolve(HttpServletRequest request) {
        return DEVICERESOLVER.resolveDevice(request);
    }

    /**
     * 解析发出请求的设备类型，并判断是否为允许的设备类型
     *
     * @param request
     * @param deviceTypes
     * @return
     */
    public static boolean isEnabled(HttpServletRequest request, DeviceType[] deviceTypes) {
        //deviceTypes为空，则不允许任何设备
        if (deviceTypes == null || deviceTypes.length == 0) {
            return false;
        }

        Device device = DEVICERESOLVER.resolveDevice(request);

        if (device == null) {
            device = PC_DEVICE;
        }

        //手机
        if (device.isMobile() && ArrayUtils.contains(deviceTypes, DeviceType.MOBILE)) {
            return true;
        }
        //PC
        if (device.isNormal() && ArrayUtils.contains(deviceTypes, DeviceType.NORMAL)) {
            return true;
        }
        //平板
        if (device.isTablet() && ArrayUtils.contains(deviceTypes, DeviceType.TABLET)) {
            return true;
        }

        return false;

    }

    /**
     * 解析发出请求的设备类型，并判断是否为允许的设备类型
     */
    public static boolean isEnabled(HttpServletRequest request, Collection<String> collection) {
        DeviceType[] deviceTypes = new DeviceType[collection.size()];
        int index = 0;
        Iterator<String> iterator = collection.iterator();
        while (iterator.hasNext()) {
            deviceTypes[index++] = Enum.valueOf(DeviceType.class, iterator.next());
        }
        return isEnabled(request, deviceTypes);
    }

    /**
     * pc设备
     */
    private static final Device PC_DEVICE = new Device() {
        @Override
        public boolean isNormal() {
            return true;
        }

        @Override
        public boolean isMobile() {
            return false;
        }

        @Override
        public boolean isTablet() {
            return false;
        }

        @Override
        public DevicePlatform getDevicePlatform() {
            return DevicePlatform.UNKNOWN;
        }
    };
}
