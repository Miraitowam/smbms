package com.ming.service.provider;

import com.ming.dao.BaseDao;
import com.ming.dao.provider.ProviderDao;
import com.ming.dao.provider.ProviderImpl;
import com.ming.pojo.SmbmsProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProviderServiceImpl implements ProviderService {
    private ProviderDao providerDao;

    //构造函数实例化
    public ProviderServiceImpl() {
        providerDao = new ProviderImpl();
    }

    @Override
    public List<SmbmsProvider> getProviderList(String proName, String proCode, int currentPageNo, int pageSize) throws Exception {
        Connection connection = BaseDao.getConnection();
        List<SmbmsProvider> providerList = providerDao.getProviderList(connection, proName, proCode, currentPageNo, pageSize);
        return providerList;
    }

    @Override
    public boolean add(SmbmsProvider provider) throws Exception {
        Connection connection = BaseDao.getConnection();
        //标志位
        boolean flag = false;
        flag = providerDao.add(connection, provider);
        return flag;
    }

    @Override
    public int deleteProviderById(String delId) throws Exception {
        Connection connection = BaseDao.getConnection();
        //标志位
        int flag = providerDao.deleteProviderById(connection, delId);
        return flag;
    }

    @Override
    public boolean modify(SmbmsProvider provider) throws Exception {
        Boolean flag = false;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);    //开启事务
            flag = providerDao.modify(connection, provider);//执行修改sql
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
    public SmbmsProvider getProviderById(String id) throws Exception {
        Connection connection = BaseDao.getConnection();
        SmbmsProvider provider = providerDao.getProviderById(connection, id);
        return provider;
    }

    @Override
    public int getProviderCount(String queryProName, String queryProCode) {
        Connection connection = BaseDao.getConnection();
        int count = providerDao.getProviderCount(connection, queryProName, queryProCode);
        return count;
    }
}
