package com.ming.dao.bill;

import com.ming.dao.BaseDao;
import com.ming.pojo.SmbmsBill;
import com.mysql.cj.util.StringUtils;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillDaoImpl implements BillDao {
    @Override
    public boolean add(Connection connection, SmbmsBill bill) throws Exception {
        //首先定义一个变量显示是否添加成功
        boolean flag = false;
        if (connection != null && bill != null) {
            //首先要获取供应商的信息
            long providerId = bill.getProviderId();//供应商id
            String billCode = bill.getBillCode(); //订单编码
            String billName = bill.getProductName(); //订单名称
            String billDesc = bill.getProductDesc(); //订单描述
            String billUnit = bill.getProductUnit();   //订单计量
            BigDecimal billCount = bill.getProductCount(); //订单数
            BigDecimal billPrice = bill.getTotalPrice();    //订单价格
            long isPayment = bill.getIsPayment();    //是否支付
            long createdBy = bill.getCreatedBy();   //创建者
            java.util.Date creationDate = bill.getCreationDate(); //创建时间
            long modifyBy = bill.getModifyBy();     //更新者
            Date modifyDate = bill.getModifyDate();   //更新时间
            //定义sql语句
            //防止自增不连续
            String sql1 = "ALTER TABLE smbms_bill AUTO_INCREMENT = 1 ";
            String sql = "insert into smbms_bill values(null,?,?,?,?,?,?,?,?,?,?,?,?)";
            //定义参数集合
            Object[] params = {billCode, billName, billDesc, billUnit, billCount, billPrice, isPayment,
                    createdBy, creationDate, modifyBy, modifyDate, providerId};
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
    public List<SmbmsBill> getBillList(Connection connection, String queryProductName, String queryProviderId, String queryIsPayment, int currentPageNo, int pageSize) throws Exception {
        //首先定义一个list返回所有可能的结果
        List<SmbmsBill> res = new ArrayList<>();
        //ResultSet
        ResultSet resultSet = null;
        //如果连接不为空就开始
        if (connection != null) {
            //进行SQL拼接
            StringBuffer sql = new StringBuffer();
            sql.append("select b.*,p.proName as providerName from smbms_bill b, smbms_provider p where b.providerId = p.id");
            //用一个list存放参数
            ArrayList<Object> params = new ArrayList<>();
            //如果订单名不为空
            if (!StringUtils.isNullOrEmpty(queryProductName)) {
                sql.append(" and productName like ?");
                params.add("%" + queryProductName + "%");
            }
            //如果订单id不为空
            if (Integer.parseInt(queryProviderId) > 0) {
                sql.append(" and providerId = ?");
                params.add(queryProviderId);
            }
            //是否支付不为空
            if (Integer.parseInt(queryIsPayment) > 0) {
                sql.append(" and isPayment = ?");
                params.add(queryIsPayment);
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
                    SmbmsBill smbmsBill = new SmbmsBill();
                    smbmsBill.setId(resultSet.getInt("id"));
                    smbmsBill.setBillCode(resultSet.getString("billCode"));
                    smbmsBill.setProductName(resultSet.getString("productName"));
                    smbmsBill.setProductDesc(resultSet.getString("productDesc"));
                    smbmsBill.setProductUnit(resultSet.getString("productUnit"));
                    smbmsBill.setProductCount(resultSet.getDouble("productCount"));
                    smbmsBill.setTotalPrice(resultSet.getDouble("totalPrice"));
                    smbmsBill.setIsPayment(resultSet.getInt("isPayment"));
                    smbmsBill.setProviderId(resultSet.getInt("providerId"));
                    smbmsBill.setProviderName(resultSet.getString("providerName"));
                    smbmsBill.setCreationDate(resultSet.getTimestamp("creationDate"));
                    smbmsBill.setCreatedBy(resultSet.getInt("createdBy"));
                    res.add(smbmsBill);
                }
                BaseDao.closeResource(connection, null, resultSet);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return res;
    }

    @Override
    public boolean deleteBillById(Connection connection, String delId) throws Exception {
        //首先定义一个变量显示是否删除成功
        boolean flag = false;
        if (connection != null && delId != null) {
            //定义sql语句
            String sql = "delete from smbms_bill where id=?";
            //定义参数
            Object[] params = {delId};
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
    public SmbmsBill getBillById(Connection connection, String id) throws Exception {
        SmbmsBill bill = null;
        String sql = "select b.*,p.proName as providerName from smbms_bill b, smbms_provider p " +
                "where b.providerId = p.id and b.id=?";
        Object[] params = {id};
        if (connection != null) {
            try {
                ResultSet res = BaseDao.execute(connection, sql, params);
                if (res.next()) {
                    bill = new SmbmsBill();
                    bill.setId(res.getInt("id"));
                    bill.setBillCode(res.getString("billCode"));
                    bill.setProductName(res.getString("productName"));
                    bill.setProductDesc(res.getString("productDesc"));
                    bill.setProductUnit(res.getString("productUnit"));
                    bill.setProductCount(res.getDouble("productCount"));
                    bill.setTotalPrice(res.getDouble("totalPrice"));
                    bill.setIsPayment(res.getInt("isPayment"));
                    bill.setProviderId(res.getInt("providerId"));
                    bill.setProviderName(res.getString("providerName"));
                    bill.setModifyBy(res.getInt("modifyBy"));
                    bill.setModifyDate(res.getTimestamp("modifyDate"));
                }
                BaseDao.closeResource(connection, null, res);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return bill;
    }

    @Override
    public boolean modify(Connection connection, SmbmsBill bill) throws Exception {
        //首先定义一个变量显示是否添加成功
        boolean flag = false;
        if (connection != null) {
            //定义sql语句
            String sql = "update smbms_bill set productName=?," +
                    "productDesc=?,productUnit=?,productCount=?,totalPrice=?," +
                    "isPayment=?,providerId=?,modifyBy=?,modifyDate=? where id = ? ";
            Object[] params = {bill.getProductName(), bill.getProductDesc(),
                    bill.getProductUnit(), bill.getProductCount(), bill.getTotalPrice(), bill.getIsPayment(),
                    bill.getProviderId(), bill.getModifyBy(), bill.getModifyDate(), bill.getId()};
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
    public int getBillCountByProviderId(Connection connection, String providerId) throws Exception {
        int count = 0;
        ResultSet rs = null;
        if (null != connection) {
            String sql = "select count(1) as billCount from smbms_bill where providerId = ?";
            Object[] params = {providerId};
            rs = BaseDao.execute(connection, sql, params);
            if (rs.next()) {
                count = rs.getInt("billCount");
            }
            BaseDao.closeResource(connection, null, rs);
        }
        return count;
    }

    @Override
    public int getBillCount(Connection connection, String queryProductName, String queryProviderId, String queryIsPayment) {
        //先定义一个结果集
        ResultSet res = null;
        int count = 0;
        if (connection != null) {
            //进行SQL拼接
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_bill b, smbms_provider p where b.providerId = p.id");
            //用一个list存放参数
            ArrayList<Object> params = new ArrayList<>();
            //如果用户名不为空
            if (!StringUtils.isNullOrEmpty(queryProductName)) {
                sql.append(" and productName like ?");
                params.add("%" + queryProductName + "%");
            }
            //如果订单id不为空
            if (Integer.parseInt(queryProviderId) > 0) {
                sql.append(" and providerId = ?");
                params.add(queryProviderId);
            }
            //是否支付不为空
            if (Integer.parseInt(queryIsPayment) > 0) {
                sql.append(" and isPayment = ?");
                params.add(queryIsPayment);
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

    @Test
    public void test() throws Exception {
        String billCode = "BILL2016_001";
        String productName = "蛋炒饭";
        String productDesc = "食品";
        String productUnit = "份";
        String productCount = "2";
        String totalPrice = "100";
        String providerId = "1";
        String isPayment = "1";
        SmbmsBill bill = new SmbmsBill();
        bill.setBillCode(billCode);
        bill.setProductName(productName);
        bill.setProductDesc(productDesc);
        bill.setProductUnit(productUnit);
        bill.setProductCount(new BigDecimal(productCount).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setIsPayment(Integer.parseInt(isPayment));
        bill.setTotalPrice(new BigDecimal(totalPrice).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setProviderId(Integer.parseInt(providerId));
        bill.setCreatedBy(1);
        bill.setCreationDate(new Date());
        bill.setId(18);
        Connection connection = BaseDao.getConnection();
        deleteBillById(connection, "18");
    }
}
