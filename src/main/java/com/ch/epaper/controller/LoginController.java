package com.ch.epaper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;


/**
 * @author songwannian
 */
@Controller
public class LoginController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/toLoginPage")
    public String toLoginPage(Model model,String errorMsg) {
        //跳转至登录页面
        System.out.println(errorMsg);
        model.addAttribute("errorMsg",errorMsg);
        return "login";
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(Model model, HttpServletRequest request, String username, String password)
    {
        //验证登录信息
        if (username.equals("songwannian") && password.equals("123456"))
        {
            //验证成功，记录Session信息
            request.getSession().setAttribute("userName", username);

            //重定向到首页
            return "index";
        }
        else
        {
            model.addAttribute("errorMsg", "账号或密码错误！");
        }

        //跳转至登录页面
        return "forward:toLoginPage?errorMsg=账号或密码错误！";
    }

    /**
    * 登出
    **/
    @RequestMapping(value = "/logout")
    public String logout(HttpServletRequest request)
    {
        //销毁session对象
        request.getSession().invalidate();

        //重定向到登录页面
        return "redirect:toLoginPage";
    }

}
