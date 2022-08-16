package com.ming.service.bill;

import com.ming.pojo.SmbmsBill;

import java.util.List;

public interface BillService {
    /**
     * 增加订单
     *
     * @param bill
     * @return
     */
    public boolean add(SmbmsBill bill) throws Exception;

    /**
     * 通过条件获取订单列表-模糊查询-billList
     *
     * @param bill
     * @return
     */
    public List<SmbmsBill> getBillList(String queryProductName, String queryProviderId, String queryIsPayment, int currentPageNo, int pageSize) throws Exception;

    /**
     * 通过billId删除Bill
     *
     * @param delId
     * @return
     */
    public boolean deleteBillById(String delId) throws Exception;


    /**
     * 通过billId获取Bill
     *
     * @param id
     * @return
     */
    public SmbmsBill getBillById(String id) throws Exception;

    /**
     * 修改订单信息
     *
     * @param bill
     * @return
     */
    public boolean modify(SmbmsBill bill) throws Exception;

    //获得总数
    public int getBillCount(String queryProductName, String queryProviderId, String queryIsPayment);
}
