package com.fosung.framework.web.mvc.config.secure.firewall;

import com.fosung.framework.common.config.AppSecureProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.RequestRejectedException;

import javax.servlet.http.HttpServletRequest;

/**
 * 基于url的防火墙检验
 * @Author : liupeng
 * @Date : 2018-10-16
 * @Modified By
 */
public class AppHttpUrlFirewall extends AppFirewallAdaptor {

    public AppHttpUrlFirewall(ApplicationContext applicationContext , AppSecureProperties appSecureProperties) {
        super( applicationContext , appSecureProperties ) ;
    }

    @Override
    public boolean isEnable(HttpServletRequest request) {
        return true ;
    }

    @Override
    public FirewalledRequest doWrapRequest(HttpServletRequest request) {

        if ( !isNormalized(request.getServletPath()) || !isNormalized(request.getPathInfo())) {
            throw new RequestRejectedException("url格式不正确: " + request.getServletPath()
                    + (request.getPathInfo() != null ? request.getPathInfo() : ""));
        }

        return emptyFirewalledRequest( request ) ;
    }

    /**
     * Checks whether a path is normalized (doesn't contain path traversal
     * sequences like "./", "/../" or "/.")
     *
     * @param path
     *            the path to test
     * @return true if the path doesn't contain any path-traversal character
     *         sequences.
     */
    private boolean isNormalized(String path) {
        if (path == null) {
            return true;
        }

        for (int j = path.length(); j > 0;) {
            int i = path.lastIndexOf('/', j - 1);
            int gap = j - i;

            if (gap == 2 && path.charAt(i + 1) == '.') {
                // ".", "/./" or "/."
                return false;
            } else if (gap == 3 && path.charAt(i + 1) == '.' && path.charAt(i + 2) == '.') {
                return false;
            }

            j = i;
        }

        return true;
    }

}
