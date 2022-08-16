package com.ming.service.provider;

import com.ming.pojo.SmbmsProvider;

import java.sql.Connection;
import java.util.List;

public interface ProviderService {
    /**
     * 通过供应商名称、编码获取供应商列表-模糊查询-providerList
     *
     * @param proName
     * @return
     */
    public List<SmbmsProvider> getProviderList(String proName, String proCode, int currentPageNo, int pageSize) throws Exception;

    /**
     * 增加供应商
     *
     * @param provider
     * @return
     */
    public boolean add(SmbmsProvider provider) throws Exception;

    /**
     * 通过proId删除Provider
     *
     * @param delId
     * @return
     */
    public int deleteProviderById(String delId) throws Exception;

    /**
     * 修改供应商信息
     *
     * @param provider
     * @return
     */
    public boolean modify(SmbmsProvider provider) throws Exception;

    /**
     * 通过proId获取Provider
     *
     * @param id
     * @return
     */
    public SmbmsProvider getProviderById(String id) throws Exception;

    //获得总数
    //查询记录数
    public int getProviderCount(String queryProName, String queryProCode);
}
