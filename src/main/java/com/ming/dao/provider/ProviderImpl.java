package com.ming.dao.provider;

import com.ming.dao.BaseDao;
import com.ming.pojo.SmbmsProvider;
import com.mysql.cj.util.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProviderImpl implements ProviderDao {
    @Override
    public List<SmbmsProvider> getProviderList(Connection connection, String proName, String proCode, int currentPageNo, int pageSize) throws Exception {
        //首先定义一个list返回所有可能的结果
        List<SmbmsProvider> res = new ArrayList<>();
        //ResultSet
        ResultSet resultSet = null;
        //如果连接不为空就开始
        if (connection != null) {
            //进行SQL拼接
            StringBuffer sql = new StringBuffer();
            sql.append("select * from smbms_provider where 1=1");
            //用一个list存放参数
            ArrayList<Object> params = new ArrayList<>();
            //如果用户名不为空
            if (!StringUtils.isNullOrEmpty(proName)) {
                sql.append(" and proName like ?");
                params.add("%" + proName + "%");
            }
            //如果用户类型不为空
            if (!StringUtils.isNullOrEmpty(proCode)) {
                sql.append(" and proCode like ?");
                params.add("%" + proCode + "%");
            }
            //进行分页
            sql.append(" order by creationDate desc limit ?,?");
            currentPageNo = (currentPageNo - 1) * pageSize;
            params.add(currentPageNo);
            params.add(pageSize);
            //把list转换为数组
            Object[] array = params.toArray();
            try {
                resultSet = BaseDao.execute(connection, sql.toString(), array);
                while (resultSet.next()) {
                    SmbmsProvider provider = new SmbmsProvider();
                    provider.setId(resultSet.getInt("id"));
                    provider.setProCode(resultSet.getString("proCode"));
                    provider.setProName(resultSet.getString("proName"));
                    provider.setProDesc(resultSet.getString("proDesc"));
                    provider.setProContact(resultSet.getString("proContact"));
                    provider.setProPhone(resultSet.getString("proPhone"));
                    provider.setProAddress(resultSet.getString("proAddress"));
                    provider.setProFax(resultSet.getString("proFax"));
                    provider.setCreationDate(resultSet.getTimestamp("creationDate"));
                    res.add(provider);
                }
                BaseDao.closeResource(connection, null, resultSet);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return res;
    }

    @Override
    public boolean add(Connection connection, SmbmsProvider provider) throws Exception {
        //首先定义一个变量显示是否添加成功
        boolean flag = false;
        if (connection != null && provider != null) {
            //首先要获取供应商的信息
            String proCode = provider.getProCode(); //供应商编码
            String proName = provider.getProName(); //供应商名称
            String proDesc = provider.getProDesc(); //描述
            String proContact = provider.getProContact();   //供应商联系人
            String proPhone = provider.getProPhone(); //供应商电话
            String proAddress = provider.getProAddress();    //供应商地址
            String proFax = provider.getProFax();    //供应商传真
            long createdBy = provider.getCreatedBy();   //创建者
            java.util.Date creationDate = provider.getCreationDate(); //创建时间
            long modifyBy = provider.getModifyBy();     //更新者
            Date modifyDate = provider.getModifyDate();   //更新时间
            //定义sql语句
            //防止自增不连续
            String sql1 = "ALTER TABLE smbms_provider AUTO_INCREMENT = 1 ";
            String sql = "insert into smbms_provider values (null,?,?,?,?,?,?,?,?,?,?,?)";
            //定义参数集合
            Object[] params = {proCode, proName, proDesc, proContact, proPhone, proAddress, proFax,
                    createdBy, creationDate, modifyDate, modifyBy};
            try {
                connection.setAutoCommit(false);
                //先执行防止自增不连续的
                BaseDao.executeQ(connection, sql1, new Object[]{});
                int row = BaseDao.executeQ(connection, sql, params);
                if (row > 0) flag = true;
                connection.commit();
            } catch (SQLException throwables) {
                try {
                    connection.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                throwables.printStackTrace();
            } finally {
                BaseDao.closeResource(connection, null, null);
            }
        }
        return flag;
    }

    @Override
    public int deleteProviderById(Connection connection, String delId) throws Exception {
        //首先定义一个变量显示是否删除成功
        int rows = -1;
        if (connection != null && delId != null) {
            //定义sql语句
            String sql = "delete from smbms_provider where id=?";
            //先要查询有没有其他没有付款的订单，要是有就不能删除
            String sql1 = "select COUNT(billCode) as count from smbms_bill,smbms_provider where smbms_provider.id = smbms_bill.providerId\n" +
                    "and smbms_bill.isPayment=1 and smbms_provider.id = ?";
            //定义参数
            Object[] params = {delId};
            //执行sql
            try {
                //先查询有没有欠费
                ResultSet execute = BaseDao.execute(connection, sql1, params);
                while (execute.next()) {
                    int count = execute.getInt("count");
                    if (count > 0) return count;
                }
                rows = BaseDao.executeQ(connection, sql, params);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return rows > 0 ? 0 : -1;
    }

    @Override
    public boolean modify(Connection connection, SmbmsProvider provider) throws Exception {
        //首先定义一个变量显示是否添加成功
        boolean flag = false;
        if (connection != null) {
            //定义sql语句
            String sql = "update smbms_provider set proName=?,proDesc=?,proContact=?," +
                    "proPhone=?,proAddress=?,proFax=?,modifyBy=?,modifyDate=? where id = ? ";
            Object[] params = {provider.getProName(), provider.getProDesc(), provider.getProContact(),
                    provider.getProPhone(), provider.getProAddress(), provider.getProFax(), provider.getModifyBy(),
                    provider.getModifyDate(), provider.getId()};
            //执行sql
            int rows = 0;
            try {
                rows = BaseDao.executeQ(connection, sql, params);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            if (rows > 0) flag = true;
        }
        return flag;
    }

    @Override
    public SmbmsProvider getProviderById(Connection connection, String id) throws Exception {
        SmbmsProvider provider = null;
        String sql = "select * from smbms_provider where id=?";
        Object[] params = {id};
        if (connection != null) {
            try {
                ResultSet res = BaseDao.execute(connection, sql, params);
                if (res.next()) {
                    provider = new SmbmsProvider();
                    provider.setId(res.getInt("id"));
                    provider.setProCode(res.getString("proCode"));
                    provider.setProName(res.getString("proName"));
                    provider.setProDesc(res.getString("proDesc"));
                    provider.setProContact(res.getString("proContact"));
                    provider.setProPhone(res.getString("proPhone"));
                    provider.setProAddress(res.getString("proAddress"));
                    provider.setProFax(res.getString("proFax"));
                    provider.setCreatedBy(res.getInt("createdBy"));
                    provider.setCreationDate(res.getTimestamp("creationDate"));
                    provider.setModifyBy(res.getInt("modifyBy"));
                    provider.setModifyDate(res.getTimestamp("modifyDate"));
                }
                BaseDao.closeResource(connection, null, res);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return provider;
    }

    @Override
    public int getProviderCount(Connection connection, String queryProName, String queryProCode) {
        //先定义一个结果集
        ResultSet res = null;
        int count = 0;
        if (connection != null) {
            //进行SQL拼接
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_provider where 1=1");
            //用一个list存放参数
            ArrayList<Object> params = new ArrayList<>();
            //如果用户名不为空
            if (!StringUtils.isNullOrEmpty(queryProName)) {
                sql.append(" and proName like ?");
                params.add("%" + queryProName + "%");
            }
            //如果用户类型不为空
            if (!StringUtils.isNullOrEmpty(queryProCode)) {
                sql.append(" and proCode like ?");
                params.add("%" + queryProCode + "%");
            }
            //把list转换为数组
            Object[] array = params.toArray();
            try {
                res = BaseDao.execute(connection, sql.toString(), array);
                if (res.next()) {
                    count = res.getInt("count");
                }
                BaseDao.closeResource(connection, null, res);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return count;
    }
}
