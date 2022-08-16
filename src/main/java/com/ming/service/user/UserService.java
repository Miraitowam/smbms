package com.ming.service.user;

import com.ming.dao.BaseDao;
import com.ming.pojo.SmbmsRole;
import com.ming.pojo.SmbmsUser;

import java.util.List;

public interface UserService {
    //用户登录
    public SmbmsUser login(String userCode, String password);

    //根据userId修改密码
    public boolean updatePw(int id, String password);

    //查询记录数
    public int getUserCount(String username, int userRole);

    //根据用户名用户类型查询用户角色
    public List<SmbmsUser> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize);

    //获取角色表
    public List<SmbmsRole> getUserRoleList();

    //添加用户
    public boolean addUser(SmbmsUser user);

    //删除用户
    public boolean deleteUserById(int id);

    //判断用户是否已经在数据库中了（不能添加重复的数据）
    public SmbmsUser selectUserCodeExist(String userCode);

    //修改用户信息
    public Boolean modify(SmbmsUser user);
}
