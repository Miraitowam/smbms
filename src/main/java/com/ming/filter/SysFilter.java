package com.ming.filter;

import com.ming.pojo.SmbmsUser;
import com.ming.util.Constants;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @description: 实现登录的优化
 * @return:
 * @Author: M
 * @create: 2022/7/14 20:39
 */

public class SysFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //过滤去从session中获取用户
        SmbmsUser user = (SmbmsUser) request.getSession().getAttribute(Constants.User_Session);
        if (user == null) {   //已经注销了或者未登录
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
