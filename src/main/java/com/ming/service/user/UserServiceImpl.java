package com.ming.service.user;

import com.ming.dao.BaseDao;
import com.ming.pojo.SmbmsRole;
import com.ming.pojo.SmbmsUser;
import com.ming.dao.user.UserDao;
import com.ming.dao.user.UserDaoImpl;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class UserServiceImpl implements UserService {

    //登录的方法
    //业务层都会调用dao层
    private UserDao userDao;

    //构造函数实例化Dao层
    public UserServiceImpl() {
        userDao = new UserDaoImpl();
    }

    @Override
    public SmbmsUser login(String userCode, String password) {
        //预先定义变量
        Connection connection = null;
        SmbmsUser usr = null;
        connection = BaseDao.getConnection();
        usr = userDao.getLoginUser(connection, userCode);
        return usr;
    }

    @Override
    //根据用户id来修改密码
    public boolean updatePw(int id, String password) {
        boolean flag = false;
        //预先定义变量
        Connection connection = null;
        connection = BaseDao.getConnection();
        if (userDao.updatePwd(connection, id, password) > 0) {
            flag = true;
        }
        return flag;
    }

    @Override
    //查询用户数量
    public int getUserCount(String username, int userRole) {
        Connection connection = BaseDao.getConnection();
        int userCount = userDao.getUserCount(connection, username, userRole);
        return userCount;
    }

    @Override
    //查询用户列表
    public List<SmbmsUser> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize) {
        Connection connection = BaseDao.getConnection();
        List<SmbmsUser> userList = userDao.getUserList(connection, queryUserName, queryUserRole, currentPageNo, pageSize);
        return userList;
    }

    @Override
    //获取角色表
    public List<SmbmsRole> getUserRoleList() {
        Connection connection = BaseDao.getConnection();
        List<SmbmsRole> roleList = userDao.getRoleList(connection);
        return roleList;
    }

    @Override
    //增加用户
    public boolean addUser(SmbmsUser user) {
        Connection connection = BaseDao.getConnection();
        //标志位
        boolean flag = false;
        flag = userDao.addUser(connection, user);
        return flag;
    }

    @Override
    //根据id删除用户
    public boolean deleteUserById(int id) {
        Connection connection = BaseDao.getConnection();
        //标志位
        boolean flag = false;
        flag = userDao.deleteUserById(connection, id);
        return flag;
    }

    @Override
    public SmbmsUser selectUserCodeExist(String userCode) {
        Connection connection = null;
        SmbmsUser user = null;
        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return user;
    }

    @Override
    public Boolean modify(SmbmsUser user) {
        Boolean flag = false;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);    //开启事务
            flag = userDao.modifyUserById(connection, user);//执行修改sql
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

    @Test
    public void test() {
        UserServiceImpl userService = new UserServiceImpl();
        SmbmsUser user = new SmbmsUser();
        user.setId(16);
        user.setUserCode("zhang3");
        user.setUserName("法外狂徒1");
        user.setUserPassword("1343142964");
        user.setGender(1);
        user.setBirthday(new Date(1998,6,7));
        user.setPhone("13996394614");
        user.setAddress("重庆市北碚区缙云山");
        user.setUserRole(1);
        user.setCreatedBy(1);
        user.setCreationDate(new Date(1998,6,7));
        user.setModifyBy(2);
        user.setModifyDate(new Date(1998,6,7));
        System.out.println(userService.modify(user));
    }
}
