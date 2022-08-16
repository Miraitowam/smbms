package com.ming.dao.user;

import com.ming.pojo.SmbmsRole;
import com.ming.pojo.SmbmsUser;

import java.sql.Connection;
import java.util.List;

public interface UserDao {

    //得到登录的用户信息
    public SmbmsUser getLoginUser(Connection connection, String userCode);

    //修改当前的密码
    public int updatePwd(Connection connection, int id, String password);

    //查询用户总数
    public int getUserCount(Connection connection, String userName, int userRole);

    //获取用户列表
    public List<SmbmsUser> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize);

    //获取角色列表
    public List<SmbmsRole> getRoleList(Connection connection);

    //增加一个用户
    public boolean addUser(Connection connection, SmbmsUser user);

    //删除一个用户
    public boolean deleteUserById(Connection connection, int id);

    //修改一个用户信息
    public boolean modifyUserById(Connection connection, SmbmsUser user);
}
