package com.ming.service.bill;

import com.ming.dao.BaseDao;
import com.ming.dao.bill.BillDao;
import com.ming.dao.bill.BillDaoImpl;
import com.ming.pojo.SmbmsBill;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BillServiceImpl implements BillService {
    private BillDao billDao;

    public BillServiceImpl() {
        billDao = new BillDaoImpl();
    }

    @Override
    public boolean add(SmbmsBill bill) throws Exception {
        Connection connection = BaseDao.getConnection();
        //标志位
        boolean flag = false;
        flag = billDao.add(connection, bill);
        return flag;
    }

    @Override
    public List<SmbmsBill> getBillList(String queryProductName, String queryProviderId, String queryIsPayment, int currentPageNo, int pageSize) throws Exception {
        Connection connection = BaseDao.getConnection();
        List<SmbmsBill> billsList = billDao.getBillList(connection, queryProductName, queryProviderId, queryIsPayment, currentPageNo, pageSize);
        return billsList;
    }

    @Override
    public boolean deleteBillById(String delId) throws Exception {
        Connection connection = BaseDao.getConnection();
        //标志位
        boolean flag = false;
        flag = billDao.deleteBillById(connection, delId);
        return flag;
    }

    @Override
    public SmbmsBill getBillById(String id) throws Exception {
        Connection connection = BaseDao.getConnection();
        SmbmsBill bill = billDao.getBillById(connection, id);
        return bill;
    }

    @Override
    public boolean modify(SmbmsBill bill) throws Exception {
        Boolean flag = false;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);    //开启事务
            flag = billDao.modify(connection, bill);//执行修改sql
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            //说明修改失败，需要回滚
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    @Override
    public int getBillCount(String queryProductName, String queryProviderId, String queryIsPayment) {
        Connection connection = BaseDao.getConnection();
        int count = billDao.getBillCount(connection, queryProductName, queryProviderId, queryIsPayment);
        return count;
    }
}
