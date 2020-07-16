package com.eeepay.frame.interceptor;

import cn.hutool.crypto.SecureUtil;
import com.eeepay.frame.annotation.SignValidate;
import com.eeepay.frame.bean.AppDeviceInfo;
import com.eeepay.frame.bean.ResponseType;
import com.eeepay.frame.utils.GsonUtils;
import com.eeepay.frame.utils.StringUtils;
import com.eeepay.frame.utils.WebUtils;
import com.eeepay.modules.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.function.Function;

/**
 * @Title：agentApi2
 * @Description：客户端接口验证签名
 * @Author：zhangly
 * @Date：2019/5/13 10:49
 * @Version：1.0
 */
@Slf4j
@Component
public class ClientSignValidateInterceptor extends HandlerInterceptorAdapter {

    public static final String DEFAULT_SIGN_KEY = "46940880d9f79f27bb7f85ca67102bfdylkj@@agentapi2#$$^&pretty";
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    @Resource
    private SysConfigService sysConfigService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("请求路径: {}, 请求ip {}", request.getRequestURI(), WebUtils.getRealIp(request));
        log.info("请求param参数: {}", GsonUtils.toJson(request.getParameterMap()));
        log.info("请求头公共参数: {}", WebUtils.getAppDeviceInfo(request));
        threadLocal.set(System.currentTimeMillis());
//        return true;
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        SignValidate methodSignValidate = handlerMethod.getMethodAnnotation(SignValidate.class);
        // 1. 如果方法体上有，需要进行签名
        if (methodSignValidate != null) {
            // 则判断是否需要登陆校验
            return !methodSignValidate.needSign() || signValid(request, response);
        }
        // 2. 如果类上有这个注解，需要进行签名
        SignValidate classSignValidate = handlerMethod.getBeanType().getAnnotation(SignValidate.class);
        if (classSignValidate != null) {
            // 如果有判断时候需要登陆校验
            return !classSignValidate.needSign() || signValid(request, response);
        }
        return signValid(request, response);
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){
        log.info("请求执行时间 {} ms", System.currentTimeMillis() - threadLocal.get());
    }
    /**
     * 校验签名
     *
     * @param request
     * @param response
     * @return
     */
    private boolean signValid(HttpServletRequest request, HttpServletResponse response) {
        //获取请求体body中的所有参数
        Map<String, String> signMap = WebUtils.getReqBodyInfo(request);
        AppDeviceInfo appDeviceInfo = WebUtils.getAppDeviceInfo(request);
        if (appDeviceInfo == null) {
            WebUtils.setJsonDataResponse(response, ResponseType.SIGN_FAIL, 403);
            return false;
        }
        String sign = appDeviceInfo.getSign();
        if (StringUtils.isBlank(sign)) {
            WebUtils.setJsonDataResponse(response, ResponseType.SIGN_FAIL, 403);
            return false;
        }
        // 当前时间戳和登录token参与签名
        signMap.put("timestamp", appDeviceInfo.getTimestamp());
        String loginToken = WebUtils.getLoginToken(request);
        if (StringUtils.isNotBlank(loginToken)) {
            signMap.put("loginToken", loginToken);
        }
        //首字母ascii升序
        String signSrc = WebUtils.buildSignSrc(false, signMap, "sign");
        //获取系统签名密钥
        String key = sysConfigService.getSysConfigValueByKey("AGENT_API_SIGN_KEY", DEFAULT_SIGN_KEY, Function.identity());
        signSrc += "&key=" + key;
        log.info("---->签名源串：" + signSrc);
        String signMsg = SecureUtil.md5(signSrc);
        if (!sign.equalsIgnoreCase(signMsg)) {
            WebUtils.setJsonDataResponse(response, ResponseType.SIGN_FAIL, 403);
            return false;
        }
        return true;
    }

}