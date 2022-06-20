package com.ch.epaper.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 * @author Song Wannian
 **/
public class LoginInterceptor implements HandlerInterceptor {
    private Log log = LogFactory.getLog(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        String uri = request.getRequestURI();

        log.info("url:"+uri);

        //判断当前请求地址是否登录地址
        if(uri.contains("login") || uri.contains("toLoginPage"))
        {
            //登录请求，直接放行
            return true;
        }
        else
        {
            //判断用户是否登录
            if(request.getSession().getAttribute("userName")!=null)
            {
                //说明已经登录，放行
                return true;
            }
            else
            {
                //没有登录，重定向到登录界面
                response.sendRedirect(request.getContextPath() + "/toLoginPage");
            }
        }

        //默认拦截
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,Exception ex) throws Exception {
    }
}
