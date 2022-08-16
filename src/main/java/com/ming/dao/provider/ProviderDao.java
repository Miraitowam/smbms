package com.ming.dao.provider;

import com.ming.pojo.SmbmsProvider;

import java.sql.Connection;
import java.util.List;

public interface ProviderDao {
    /**
     * 通过供应商名称、编码获取供应商列表-模糊查询-providerList
     *
     * @param connection
     * @param proName
     * @return
     * @throws Exception
     */
    public List<SmbmsProvider> getProviderList(Connection connection, String proName, String proCode, int currentPageNo, int pageSize) throws Exception;

    /**
     * 增加供应商
     *
     * @param connection
     * @param provider
     * @return
     * @throws Exception
     */
    public boolean add(Connection connection, SmbmsProvider provider) throws Exception;

    /**
     * 通过proId删除Provider
     *
     * @param delId
     * @return
     * @throws Exception
     */
    public int deleteProviderById(Connection connection, String delId) throws Exception;

    /**
     * 修改供应商信息
     *
     * @param connection
     * @param provider
     * @return
     * @throws Exception
     */
    public boolean modify(Connection connection, SmbmsProvider provider) throws Exception;

    /**
     * 通过proId获取Provider
     *
     * @param connection
     * @param id
     * @return
     * @throws Exception
     */
    public SmbmsProvider getProviderById(Connection connection, String id) throws Exception;

    //查询供应商总数
    public int getProviderCount(Connection connection, String queryProName, String queryProCode);

}
