package com.ming.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.ming.pojo.SmbmsRole;
import com.ming.pojo.SmbmsUser;
import com.ming.service.user.UserService;
import com.ming.service.user.UserServiceImpl;
import com.ming.util.Constants;
import com.ming.util.PageSupport;
import com.mysql.cj.util.StringUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //判断前端传过来的是什么方法
        String method = req.getParameter("method");
        System.out.println(method);
        if (method.equals("savepwd") && method != null) {
            this.updatePwd(req, resp);
        } else if (method.equals("pwdmodify") && method != null) {     //如果是验证旧密码
            this.pwdModify(req, resp);
        } else if (method.equals("query") && method != null) {     //如果传入的是查询语句
            this.query(req, resp);
        } else if (method != null && method.equals("add")) {      //如果传入的是增加语句
            this.addUser(req, resp);
        } else if (method != null && method.equals("ucexist")) {    //查询当前用户是否已经存在
            this.userCodeExist(req, resp);
        } else if (method != null && method.equals("deluser")) {    //删除用户
            this.deleteUserById(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //判断前端传过来的是什么方法
        String method = req.getParameter("method");
        if (method.equals("modify") && method != null) {
            this.modify(req, resp);
        }
    }

    //写一个通用的方法更改密码
    public void updatePwd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //从session中获取用户信息
        Object attribute = req.getSession().getAttribute(Constants.User_Session);
        String newPassword = req.getParameter("newpassword");
        //定义一个变量标志是否修改成功
        boolean flag = false;
        //如果不为空说明找到了
        if (attribute != null && !StringUtils.isNullOrEmpty(newPassword)) {       //要求新密码不为空才行
            UserService userService = new UserServiceImpl();
            //修改密码
            int id = (int) ((SmbmsUser) attribute).getId();
            flag = userService.updatePw(id, newPassword);
            if (flag) {     //修改成功
                req.setAttribute("message", "修改密码成功，请使用新密码登录");
                //密码修改成功，移除session
                req.getSession().removeAttribute(Constants.User_Session);
            } else {
                req.setAttribute("message", "密码修改失败");
            }
        } else {
            req.setAttribute("message", "新密码存在问题");
        }
        req.getRequestDispatcher("pwdmodify.jsp").forward(req, resp);
    }

    //写一个方法验证旧密码是否正确
    public void pwdModify(HttpServletRequest req, HttpServletResponse resp) {
        //从session中获取用户信息
        Object attribute = req.getSession().getAttribute(Constants.User_Session);
        String dldPassword = req.getParameter("dldPassword");
        //结果集Map
        Map<String, String> Map = new HashMap<>();
        if (attribute == null) {   //session失效了
            Map.put("result", "sessionerror");
        } else if (StringUtils.isNullOrEmpty(dldPassword)) {  //输入的密码为空
            Map.put("result", "error");
        } else {
            //获得密码
            String password = ((SmbmsUser) attribute).getUserPassword();    //session中用户的旧密码
            if (dldPassword.equals(password)) {
                Map.put("result", "true");
            } else {
                Map.put("result", "false");
            }
        }
        //获得流返回
        try {
            //设置方法的返回类型为json
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            writer.write(JSONArray.toJSONString(Map));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //写一个方法执行查询
    public void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //查询用户列表
        String queryName = req.getParameter("queryname");
        String queryUserRole = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        //第一次请求一定展示第一页，页面大小是固定的
        int pageSize = 5;
        int currentPage = 1;
        //获取用户列表
        UserServiceImpl userService = new UserServiceImpl();
        int queryNum = 0;  //下拉列表的数字
        if (queryName == null) {
            queryName = "";
        }
        if (queryUserRole != null && !queryUserRole.equals("")) {
            queryNum = Integer.parseInt(queryUserRole);     //给查询分页
        }
        if (pageIndex != null) {
            currentPage = Integer.parseInt(pageIndex);
        }
        //获取用户总数
        int totalCount = userService.getUserCount(queryName, queryNum);
        //总页数支持
        PageSupport pageSupport = new PageSupport();
        pageSupport.setPageSize(pageSize);
        pageSupport.setCurrentPageNo(currentPage);
        pageSupport.setTotalCount(totalCount);
        int totalPageCount = pageSupport.getTotalPageCount();   //返回一共能分多少页
        //控制首页和尾页
        //如果当前页面就要小于1了就只显示第一页
        if (currentPage < 1) {
            currentPage = 1;
        } else if (currentPage > totalPageCount) {   //大于最后一页
            currentPage = totalPageCount;
        }
        //获取用户列表展示
        List<SmbmsUser> userList = userService.getUserList(queryName, queryNum, currentPage, pageSize);
        req.setAttribute("userList", userList);
        //获取角色列表展示
        List<SmbmsRole> roleList = userService.getUserRoleList();
        req.setAttribute("roleList", roleList);
        //设置分页
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPageNo", currentPage);
        req.setAttribute("totalPageCount", totalPageCount);
        req.setAttribute("queryUserName", queryName);
        req.setAttribute("queryUserRole", queryUserRole);
        //返回前端
        req.getRequestDispatcher("userlist.jsp").forward(req, resp);
    }

    //写一个方法实现增加用户
    public void addUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //从前端得到页面的请求的参数即用户输入的值
        String id = req.getParameter("userid");
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");
        //创建一个用户对象
        SmbmsUser user = new SmbmsUser();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setAddress(address);
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        user.setId(Integer.parseInt(id));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreationDate(new Date());
        //查找当前正在登陆的用户的id
        user.setCreatedBy(((SmbmsUser) req.getSession().getAttribute(Constants.User_Session)).getId());
        UserServiceImpl userService = new UserServiceImpl();
        Boolean flag = userService.addUser(user);
        //如果添加成功，则页面转发，否则重新刷新，再次跳转到当前页面
        if (flag) {
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        } else {
            req.getRequestDispatcher("useradd.jsp").forward(req, resp);
        }
    }

    //判断用户是否已经存在
    private void userCodeExist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //先拿到用户的编码
        String userCode = req.getParameter("userCode");
        //用一个hashmap，暂存现在所有现存的用户编码
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (StringUtils.isNullOrEmpty(userCode)) {
            //如果输入的这个编码为空或者不存在，说明可用
            resultMap.put("userCode", "exist");
        } else {     //如果输入的编码不为空，则需要去找一下是否存在这个用户
            UserService userService = new UserServiceImpl();
            SmbmsUser user = userService.selectUserCodeExist(userCode);
            if (null != user) {
                resultMap.put("userCode", "exist");
            } else {
                resultMap.put("userCode", "notexist");
            }
        }
        //把resultMap转为json字符串以json的形式输出
        //配置上下文的输出类型
        resp.setContentType("application/json");
        //从response对象中获取往外输出的writer对象
        PrintWriter outPrintWriter = resp.getWriter();
        //把resultMap转为json字符串 输出
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();//刷新
        outPrintWriter.close();//关闭流
    }

    //根据用户id删除用户
    public void deleteUserById(HttpServletRequest req, HttpServletResponse resp) {
        String id = req.getParameter("uid");
        int delId = 0;
        delId = Integer.parseInt(id);
        //需要判断是否能删除成功
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (delId <= 0) {
            resultMap.put("delResult", "notexist");
        } else {
            UserService userService = new UserServiceImpl();
            if (userService.deleteUserById(delId)) {
                resultMap.put("delResult", "true");
            } else {
                resultMap.put("delResult", "false");
            }
        }
        //把resultMap转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = null;
        try {
            outPrintWriter = resp.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    //修改用户信息
    private void modify(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //需要拿到前端传递进来的参数
        String id = req.getParameter("uid");
        String userName = req.getParameter("userName");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        //创建一个user对象接收这些参数
        SmbmsUser user = new SmbmsUser();
        user.setUserName(userName);
        user.setGender(Integer.valueOf(gender));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            System.out.println("出问题");
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.valueOf(userRole));
        user.setModifyBy(((SmbmsUser) req.getSession().getAttribute(Constants.User_Session)).getId());
        user.setModifyDate(new Date());
        user.setId(Integer.valueOf(id));

        //调用service层
        UserServiceImpl userService = new UserServiceImpl();
        Boolean flag = userService.modify(user);
        System.out.println(flag);
        //判断是否修改成功来决定跳转到哪个页面
        if (flag) {
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        } else {
            req.getRequestDispatcher("usermodify.jsp").forward(req, resp);
        }
    }

}
