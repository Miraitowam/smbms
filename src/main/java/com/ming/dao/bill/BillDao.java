package com.ming.dao.bill;

import com.ming.pojo.SmbmsBill;

import java.sql.Connection;
import java.util.List;

public interface BillDao {
    /**
     * 增加订单
     *
     * @param connection
     * @param bill
     * @return
     * @throws Exception
     */
    public boolean add(Connection connection, SmbmsBill bill) throws Exception;

    /**
     * 通过查询条件获取供应商列表-模糊查询-getBillList
     *
     * @param connection
     * @param bill
     * @return
     * @throws Exception
     */
    public List<SmbmsBill> getBillList(Connection connection, String queryProductName, String queryProviderId, String queryIsPayment, int currentPageNo, int pageSize) throws Exception;

    /**
     * 通过delId删除Bill
     *
     * @param connection
     * @param delId
     * @return
     * @throws Exception
     */
    public boolean deleteBillById(Connection connection, String delId) throws Exception;


    /**
     * 通过billId获取Bill
     *
     * @param connection
     * @param id
     * @return
     * @throws Exception
     */
    public SmbmsBill getBillById(Connection connection, String id) throws Exception;

    /**
     * 修改订单信息
     *
     * @param connection
     * @param bill
     * @return
     * @throws Exception
     */
    public boolean modify(Connection connection, SmbmsBill bill) throws Exception;

    /**
     * 根据供应商ID查询订单数量
     *
     * @param connection
     * @param providerId
     * @return
     * @throws Exception
     */
    public int getBillCountByProviderId(Connection connection, String providerId) throws Exception;

    //查询订单总数
    public int getBillCount(Connection connection, String queryProductName, String queryProviderId, String queryIsPayment);
}
