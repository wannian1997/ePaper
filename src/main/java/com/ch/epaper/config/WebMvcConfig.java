package com.ch.epaper.config;

import com.ch.epaper.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web Mvc配置类
 * @author songwannian
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 控制器配置
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry)
    {
        registry.addViewController("/toIndexPage").setViewName("/index");
        registry.addViewController("/").setViewName("/index");
    }

    /**
     * 拦截器配置
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        //注册Interceptor拦截器
        InterceptorRegistration registration = registry.addInterceptor(new LoginInterceptor());
        registration.addPathPatterns("/**");  //所有路径都被拦截
        registration.excludePathPatterns(   //添加不拦截路径
                "/toLoginPage", //登录页面
                "/login",  //登录请求
                "/**/*.html",  //html静态资源
                "/**/*.js",  //js静态资源
                "/**/*.css"  //css静态资源
        );
    }
}
