package com.megvii.mapapi.annotation.aspect;

import com.megvii.exception.NotAuthException;
import com.megvii.mapapi.annotation.AdminAuthVerify;
import com.megvii.mapapi.properties.SystemOtherProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Component
@Aspect
@Slf4j
@Order(0)
public class AdminAuthAspect {
    @Autowired
    private SystemOtherProperties systemOtherProperties;

    @Pointcut(value = "@annotation(adminAuthVerify) || @within(adminAuthVerify)")
    public void doAdminAuthVerfiy(AdminAuthVerify adminAuthVerify) {
    }

    @Before(value = "doAdminAuthVerfiy(adminAuthVerify)",argNames = "adminAuthVerify")
    public void doBefore(AdminAuthVerify adminAuthVerify) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String auth = request.getParameter("auth");
//        log.info("鉴权："+auth);
        if (auth == null)
            throw new NotAuthException("无权限");
        else if (!systemOtherProperties.getAuth().equals(auth))
            throw new NotAuthException("无权限");
    }
}
