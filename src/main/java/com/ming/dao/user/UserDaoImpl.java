package com.ming.dao.user;

import com.ming.dao.BaseDao;
import com.ming.pojo.SmbmsRole;
import com.ming.pojo.SmbmsUser;
import com.mysql.cj.util.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//查询一个用户的全部信息
public class UserDaoImpl implements UserDao {
    @Override
    public SmbmsUser getLoginUser(Connection connection, String userCode) {
        SmbmsUser user = null;
        String sql = "select * from smbms_user where userCode = ?";
        Object[] params = {userCode};
        if (connection != null) {
            try {
                ResultSet res = BaseDao.execute(connection, sql, params);
                if (res.next()) {
                    user = new SmbmsUser();
                    user.setId(res.getInt("id"));
                    user.setUserCode(res.getString("userCode"));
                    user.setUserName(res.getString("userName"));
                    user.setUserPassword(res.getString("userPassword"));
                    user.setGender(res.getInt("gender"));
                    user.setBirthday(res.getDate("birthday"));
                    user.setPhone(res.getString("phone"));
                    user.setAddress(res.getString("address"));
                    user.setUserRole(res.getInt("userRole"));
                    user.setCreatedBy(res.getInt("createdBy"));
                    user.setCreationDate(res.getTimestamp("creationDate"));
                    user.setModifyBy(res.getInt("modifyBy"));
                    user.setModifyDate(res.getTimestamp("modifyDate"));
                }
                BaseDao.closeResource(connection, null, res);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return user;
    }

    @Override
    public int updatePwd(Connection connection, int id, String password) {
        PreparedStatement pstm = null;
        String sql = "update smbms_user set userPassword = ? where id = ?";
        //获得参数
        Object[] params = {password, id};
        int execute = 0;
        if (connection != null) {
            try {
                execute = BaseDao.executeQ(connection, sql, params);
                BaseDao.closeResource(connection, pstm, null);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return execute;
    }

    //根据用户名或者角色查询用户总数
    @Override
    public int getUserCount(Connection connection, String userName, int userRole) {
        //先定义一个结果集
        ResultSet res = null;
        int count = 0;
        if (connection != null) {
            //进行SQL拼接
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_user u ,smbms_role r where u.userRole = r.id");
            //用一个list存放参数
            ArrayList<Object> params = new ArrayList<>();
            //如果用户名不为空
            if (!StringUtils.isNullOrEmpty(userName)) {
                sql.append(" and u.userName like ?");
                params.add("%" + userName + "%");
            }
            //如果用户类型不为空
            if (userRole > 0) {
                sql.append(" and u.userRole like ?");
                params.add("%" + userRole + "%");
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

    //根据用户名或者用户角色查询用户
    @Override
    public List<SmbmsUser> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) {
        //首先定义一个list返回所有可能的结果
        List<SmbmsUser> res = new ArrayList<>();
        //ResultSet
        ResultSet resultSet = null;
        //如果连接不为空就开始
        if (connection != null) {
            //进行SQL拼接
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*,r.roleName from smbms_user u ,smbms_role r where u.userRole = r.id");
            //用一个list存放参数
            ArrayList<Object> params = new ArrayList<>();
            //如果用户名不为空
            if (!StringUtils.isNullOrEmpty(userName)) {
                sql.append(" and u.userName like ?");
                params.add("%" + userName + "%");
            }
            //如果用户类型不为空
            if (userRole > 0) {
                sql.append(" and u.userRole like ?");
                params.add("%" + userRole + "%");
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
                    SmbmsUser user = new SmbmsUser();
                    user.setId(resultSet.getInt("id"));
                    user.setUserCode(resultSet.getString("userCode"));
                    user.setUserName(resultSet.getString("userName"));
                    user.setUserPassword(resultSet.getString("userPassword"));
                    user.setGender(resultSet.getInt("gender"));
                    user.setBirthday(resultSet.getDate("birthday"));
                    user.setPhone(resultSet.getString("phone"));
                    user.setAddress(resultSet.getString("address"));
                    user.setUserRole(resultSet.getInt("userRole"));
                    user.setCreatedBy(resultSet.getInt("createdBy"));
                    user.setCreationDate(resultSet.getTimestamp("creationDate"));
                    user.setModifyBy(resultSet.getInt("modifyBy"));
                    user.setModifyDate(resultSet.getTimestamp("modifyDate"));
                    user.setUserRoleName(resultSet.getString("roleName"));
                    res.add(user);
                }
                BaseDao.closeResource(connection, null, resultSet);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return res;
    }

    //获取角色表
    @Override
    public List<SmbmsRole> getRoleList(Connection connection) {
        List<SmbmsRole> smbmsRoleList = new ArrayList<>();
        if (connection != null) {
            String sql = "select * from smbms_role";
            Object[] params = {};
            try {
                ResultSet resultSet = BaseDao.execute(connection, sql, params);
                //查询到有结果
                while (resultSet.next()) {
                    SmbmsRole role = new SmbmsRole();
                    int id = resultSet.getInt("id");
                    String roleName = resultSet.getString("roleName");
                    String roleCode = resultSet.getString("roleCode");
                    role.setId(id);
                    role.setRoleCode(roleCode);
                    role.setRoleName(roleName);
                    smbmsRoleList.add(role);
                }
                BaseDao.closeResource(connection, null, resultSet);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return smbmsRoleList;
    }

    @Override
    //添加用户
    public boolean addUser(Connection connection, SmbmsUser user) {
        //首先定义一个变量显示是否添加成功
        boolean flag = false;
        if (connection != null && user != null) {
            //首先要获取用户的信息
            String userCode = user.getUserCode(); //用户编码
            String userName = user.getUserName(); //用户名称
            String userPassword = user.getUserPassword(); //用户密码
            int gender = user.getGender();  //性别
            java.util.Date birthday = user.getBirthday();  //出生日期
            String phone = user.getPhone();   //电话
            String address = user.getAddress(); //地址
            Integer userRole = user.getUserRole();    //用户角色
            Integer createdBy = user.getCreatedBy();   //创建者
            java.util.Date creationDate = user.getCreationDate(); //创建时间
            Integer modifyBy = user.getModifyBy();     //更新者
            Date modifyDate = user.getModifyDate();   //更新时间
            //定义sql语句
            //防止自增不连续
            String sql1 = "ALTER TABLE smbms_user AUTO_INCREMENT = 1 ";
            String sql = "insert into smbms_user values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            //定义参数集合
            Object[] params = {null, userCode, userName, userPassword, gender, birthday, phone, address, userRole,
                    createdBy, creationDate, modifyBy, modifyDate};
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
    public boolean deleteUserById(Connection connection, int id) {
        //首先定义一个变量显示是否添加成功
        boolean flag = false;
        if (connection != null && id > 0) {
            //定义sql语句
            String sql = "delete from smbms_user where id = ?";
            //定义参数
            Object[] params = {id};
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
    public boolean modifyUserById(Connection connection, SmbmsUser user) {
        //首先定义一个变量显示是否添加成功
        boolean flag = false;
        if (connection != null) {
            //定义sql语句
            String sql = "update smbms_user set userName=?," +
                    "gender=?,birthday=?,phone=?,address=?,userRole=?,modifyBy=?,modifyDate=? where id = ? ";
            Object[] params = {user.getUserName(), user.getGender(), user.getBirthday(),
                    user.getPhone(), user.getAddress(), user.getUserRole(), user.getModifyBy(),
                    user.getModifyDate(), user.getId()};
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
}
