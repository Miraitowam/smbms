package com.ming.servlet.provider;

import com.alibaba.fastjson.JSONArray;
import com.ming.pojo.SmbmsProvider;
import com.ming.pojo.SmbmsUser;
import com.ming.service.provider.ProviderService;
import com.ming.service.provider.ProviderServiceImpl;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ProviderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //判断前端传过来的是什么方法
        String method = req.getParameter("method");
        System.out.println(method);
        if (method != null && method.equals("query")) {
            this.query(req, resp);
        } else if (method != null && method.equals("add")) {
            this.add(req, resp);
        } else if (method != null && method.equals("view")) {
            this.getProviderById(req, resp, "providerview.jsp");
        } else if (method != null && method.equals("modify")) {
            this.getProviderById(req, resp, "providermodify.jsp");
        } else if (method != null && method.equals("modifysave")) {
            this.modify(req, resp);
        } else if (method != null && method.equals("delprovider")) {
            this.delProvider(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    private void query(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String queryProName = request.getParameter("queryProName");
        String queryProCode = request.getParameter("queryProCode");
        String pageIndex = request.getParameter("pageIndex");
        //第一次请求一定展示第一页，页面大小是固定的
        int pageSize = 5;
        int currentPage = 1;
        //获取用户列表
        ProviderService providerService = new ProviderServiceImpl();
        if (queryProName == null) {
            queryProName = "";
        }
        if (queryProCode == null) {
            queryProCode = "";    //给查询分页
        }
        if (pageIndex != null) {
            currentPage = Integer.parseInt(pageIndex);
        }
        //获取用户总数
        int totalCount = providerService.getProviderCount(queryProName, queryProCode);
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
        if (StringUtils.isNullOrEmpty(queryProName)) {
            queryProName = "";
        }
        if (StringUtils.isNullOrEmpty(queryProCode)) {
            queryProCode = "";
        }
        List<SmbmsProvider> providerList = new ArrayList<>();
        try {
            providerList = providerService.getProviderList(queryProName, queryProCode, currentPage, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("currentPageNo", currentPage);
        request.setAttribute("totalPageCount", totalPageCount);
        request.setAttribute("providerList", providerList);
        request.setAttribute("queryProName", queryProName);
        request.setAttribute("queryProCode", queryProCode);
        request.getRequestDispatcher("providerlist.jsp").forward(request, response);
    }

    private void modify(HttpServletRequest req, HttpServletResponse resp) {
        String proName = req.getParameter("proName");
        String proContact = req.getParameter("proContact");
        String proPhone = req.getParameter("proPhone");
        String proAddress = req.getParameter("proAddress");
        String proFax = req.getParameter("proFax");
        String proDesc = req.getParameter("proDesc");
        String id = req.getParameter("id");
        SmbmsProvider provider = new SmbmsProvider();
        provider.setProName(proName);
        provider.setId(Integer.valueOf(id));
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProFax(proFax);
        provider.setProAddress(proAddress);
        provider.setProDesc(proDesc);
        provider.setModifyBy(((SmbmsUser) req.getSession().getAttribute(Constants.User_Session)).getId());
        provider.setModifyDate(new Date());
        boolean flag = false;
        ProviderService providerService = new ProviderServiceImpl();
        try {
            flag = providerService.modify(provider);
            if (flag) {
                resp.sendRedirect(req.getContextPath() + "/jsp/provider.do?method=query");
            } else {
                req.getRequestDispatcher("providermodify.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProviderById(HttpServletRequest req, HttpServletResponse resp, String url) {
        String id = req.getParameter("proid");
        if (!StringUtils.isNullOrEmpty(id)) {
            ProviderService providerService = new ProviderServiceImpl();
            SmbmsProvider provider = null;
            try {
                provider = providerService.getProviderById(id);
                req.setAttribute("provider", provider);
                req.getRequestDispatcher(url).forward(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void add(HttpServletRequest req, HttpServletResponse resp) {
        String proCode = req.getParameter("proCode");
        String proName = req.getParameter("proName");
        String proContact = req.getParameter("proContact");
        String proPhone = req.getParameter("proPhone");
        String proAddress = req.getParameter("proAddress");
        String proFax = req.getParameter("proFax");
        String proDesc = req.getParameter("proDesc");

        SmbmsProvider provider = new SmbmsProvider();
        provider.setProCode(proCode);
        provider.setProName(proName);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProFax(proFax);
        provider.setProAddress(proAddress);
        provider.setProDesc(proDesc);
        provider.setCreatedBy(((SmbmsUser) req.getSession().getAttribute(Constants.User_Session)).getId());
        provider.setCreationDate(new Date());
        boolean flag = false;
        ProviderService providerService = new ProviderServiceImpl();
        try {
            flag = providerService.add(provider);
            if (flag) {
                resp.sendRedirect(req.getContextPath() + "/jsp/provider.do?method=query");
            } else {
                req.getRequestDispatcher("provideradd.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void delProvider(HttpServletRequest req, HttpServletResponse resp) {
        String id = req.getParameter("proid");
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (!StringUtils.isNullOrEmpty(id)) {
            ProviderService providerService = new ProviderServiceImpl();
            int flag = 0;
            try {
                flag = providerService.deleteProviderById(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (flag == 0) {//删除成功
                resultMap.put("delResult", "true");
            } else if (flag == -1) {//删除失败
                resultMap.put("delResult", "false");
            } else if (flag > 0) {//该供应商下有订单，不能删除，返回订单数
                resultMap.put("delResult", String.valueOf(flag));
            }
        } else {
            resultMap.put("delResult", "notexit");
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
}
