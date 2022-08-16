package com.ming.servlet.user;

import com.ming.pojo.SmbmsUser;
import com.ming.service.user.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.ming.util.Constants.User_Session;

/**
 * @description: 处理登录的请求
 * @return:
 * @Author: M
 * @create: 2022/7/14 19:47
 */

public class LoginServlet extends HttpServlet {
    //servlet:控制层，调用业务层代码

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //首先获取用户名和密码
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");
        //和数据库的密码进行对比
        UserServiceImpl userService = new UserServiceImpl();
        //查出要登录的人
        SmbmsUser user = userService.login(userCode, userPassword);
        if (user != null && userPassword.equals(user.getUserPassword())) {     //查到数据库确实有这个人密码也正确
            //将用户的信息放到session中
            req.getSession().setAttribute(User_Session, user);
            //跳转到主页
            resp.sendRedirect("jsp/frame.jsp");
        } else {     //查无此人
            //转发登录页面,顺带提示用户密码错误
            req.setAttribute("error", "用户密码不正确");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }
}
