package com.ming.servlet.bill;

import com.alibaba.fastjson.JSONArray;
import com.ming.pojo.SmbmsBill;
import com.ming.pojo.SmbmsProvider;
import com.ming.pojo.SmbmsUser;
import com.ming.service.bill.BillService;
import com.ming.service.bill.BillServiceImpl;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BillServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        System.out.println(method);
        if (method != null && method.equals("query")) {
            this.query(req, resp);
        } else if (method != null && method.equals("add")) {
            this.add(req, resp);
        } else if (method != null && method.equals("view")) {
            this.getBillById(req, resp, "billview.jsp");
        } else if (method != null && method.equals("modify")) {
            this.getBillById(req, resp, "billmodify.jsp");
        } else if (method != null && method.equals("modifysave")) {
            this.modify(req, resp);
        } else if (method != null && method.equals("delbill")) {
            this.delBill(req, resp);
        } else if (method != null && method.equals("getproviderlist")) {
            this.getProviderlist(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

    }

    private void query(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<SmbmsProvider> providerList = new ArrayList();
        ProviderService providerService = new ProviderServiceImpl();
        try {
            providerList = providerService.getProviderList("", "", 1, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setAttribute("providerList", providerList);
        String queryProductName = request.getParameter("queryProductName");
        String queryProviderId = request.getParameter("queryProviderId");
        String queryIsPayment = request.getParameter("queryIsPayment");
        String pageIndex = request.getParameter("pageIndex");
        if (StringUtils.isNullOrEmpty(queryProductName)) {
            queryProductName = "";
        }
        if (StringUtils.isNullOrEmpty(queryProviderId)) {
            queryProviderId = "0";
        }
        if (StringUtils.isNullOrEmpty(queryIsPayment)) {
            queryIsPayment = "0";
        }
        //第一次请求一定展示第一页，页面大小是固定的
        int pageSize = 8;
        int currentPage = 1;
        //获取用户列表
        BillService billService = new BillServiceImpl();
        if (pageIndex != null) {
            currentPage = Integer.parseInt(pageIndex);
        }
        //获取用户总数
        int totalCount = billService.getBillCount(queryProductName, queryProviderId, queryIsPayment);
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
        List<SmbmsBill> billList = new ArrayList<>();
        try {
            billList = billService.getBillList(queryProductName, queryProviderId, queryIsPayment, currentPage, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("currentPageNo", currentPage);
        request.setAttribute("totalPageCount", totalPageCount);
        request.setAttribute("billList", billList);
        request.setAttribute("queryProductName", queryProductName);
        request.setAttribute("queryProviderId", queryProviderId);
        request.setAttribute("queryIsPayment", queryIsPayment);
        request.getRequestDispatcher("billlist.jsp").forward(request, response);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String billCode = request.getParameter("billCode");
        String productName = request.getParameter("productName");
        String productDesc = request.getParameter("productDesc");
        String productUnit = request.getParameter("productUnit");
        String productCount = request.getParameter("productCount");
        String totalPrice = request.getParameter("totalPrice");
        String providerId = request.getParameter("providerId");
        String isPayment = request.getParameter("isPayment");
        SmbmsBill bill = new SmbmsBill();
        bill.setBillCode(billCode);
        bill.setProductName(productName);
        bill.setProductDesc(productDesc);
        bill.setProductUnit(productUnit);
        bill.setProductCount(new BigDecimal(productCount).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setIsPayment(Integer.parseInt(isPayment));
        bill.setTotalPrice(new BigDecimal(totalPrice).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setProviderId(Integer.parseInt(providerId));
        bill.setCreatedBy(((SmbmsUser) request.getSession().getAttribute(Constants.User_Session)).getId());
        bill.setCreationDate(new Date());
        boolean flag = false;
        BillService billService = new BillServiceImpl();
        try {
            flag = billService.add(bill);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (flag) {
            response.sendRedirect(request.getContextPath() + "/jsp/bill.do?method=query");
        } else {
            request.getRequestDispatcher("billadd.jsp").forward(request, response);
        }
    }

    private void getBillById(HttpServletRequest request, HttpServletResponse response, String url) throws ServletException, IOException {
        String id = request.getParameter("billid");
        if (!StringUtils.isNullOrEmpty(id)) {
            BillService billService = new BillServiceImpl();
            SmbmsBill bill = null;
            try {
                bill = billService.getBillById(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            request.setAttribute("bill", bill);
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    private void modify(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String productName = request.getParameter("productName");
        String productDesc = request.getParameter("productDesc");
        String productUnit = request.getParameter("productUnit");
        String productCount = request.getParameter("productCount");
        String totalPrice = request.getParameter("totalPrice");
        String providerId = request.getParameter("providerId");
        String isPayment = request.getParameter("isPayment");
        SmbmsBill bill = new SmbmsBill();
        bill.setId(Integer.valueOf(id));
        bill.setProductName(productName);
        bill.setProductDesc(productDesc);
        bill.setProductUnit(productUnit);
        bill.setProductCount(new BigDecimal(productCount).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setIsPayment(Integer.parseInt(isPayment));
        bill.setTotalPrice(new BigDecimal(totalPrice).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setProviderId(Integer.parseInt(providerId));

        bill.setModifyBy(((SmbmsUser) request.getSession().getAttribute(Constants.User_Session)).getId());
        bill.setModifyDate(new Date());
        boolean flag = false;
        BillService billService = new BillServiceImpl();
        try {
            flag = billService.modify(bill);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (flag) {
            response.sendRedirect(request.getContextPath() + "/jsp/bill.do?method=query");
        } else {
            request.getRequestDispatcher("billmodify.jsp").forward(request, response);
        }
    }

    private void delBill(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("billid");
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (!StringUtils.isNullOrEmpty(id)) {
            BillService billService = new BillServiceImpl();
            boolean flag = false;
            try {
                flag = billService.deleteBillById(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (flag) {//删除成功
                resultMap.put("delResult", "true");
            } else {//删除失败
                resultMap.put("delResult", "false");
            }
        } else {
            resultMap.put("delResult", "notexit");
        }
        //把resultMap转换成json对象输出
        response.setContentType("application/json");
        PrintWriter outPrintWriter = response.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    private void getProviderlist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<SmbmsProvider> providerList = new ArrayList<>();
        ProviderService providerService = new ProviderServiceImpl();
        try {
            providerList = providerService.getProviderList("", "", 1, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //把providerList转换成json对象输出
        response.setContentType("application/json");
        PrintWriter outPrintWriter = response.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(providerList));
        outPrintWriter.flush();
        outPrintWriter.close();
    }
}
