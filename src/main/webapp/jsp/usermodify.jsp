<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/jsp/common/head.jsp" %>
<div class="right">
    <div class="location">
        <strong>你现在所在的位置是:</strong>
        <span>用户管理页面 >> 用户修改页面</span>
    </div>
    <div class="providerAdd">
        <form id="userForm" name="userForm" method="post" action="${pageContext.request.contextPath}/jsp/user.do">
            <input type="hidden" name="method" value="modify">
            <input type="hidden" name="uid" id="uid" value="${param.id}"/>
            <div>
                <label for="userName">用户名称：</label>
                <input type="text" name="userName" id="userName" value="${param.n}">
                <font color="red"></font>
            </div>
            <div>
                <label>用户性别：</label>
                <select name="gender" id="${param.g}">
                    <c:choose>
                        <c:when test="${param.g == 1}">
                            <option value="1" selected="selected">男</option>
                            <option value="2">女</option>
                        </c:when>
                        <c:otherwise>
                            <option value="1">男</option>
                            <option value="2" selected="selected">女</option>
                        </c:otherwise>
                    </c:choose>
                </select>
            </div>
            <div>
                <label for="data">出生日期：</label>
                <input type="text" Class="Wdate" id="birthday" name="birthday" value="${param.b }"
                       readonly="readonly" onclick="WdatePicker();">
                <font color="red"></font>
            </div>

            <div>
                <label for="userphone">用户电话：</label>
                <input type="text" name="phone" id="phone" value="${param.p}">
                <font color="red"></font>
            </div>
            <div>
                <label for="userAddress">用户地址：</label>
                <input type="text" name="address" id="address" value="${param.a}">
            </div>
            <div>
                <label>用户角色：</label>
                <!-- 列出所有的角色分类 -->
                <input type="hidden" value="${param.r}" id="rid"/>
                <select name="userRole" id="userRole">
                    <c:choose>
                        <c:when test="${param.r == 1}">
                            <option value="1" selected="selected">系统管理员</option>
                            <option value="2">经理</option>
                            <option value="3">普通员工</option>
                        </c:when>
                        <c:when test="${param.r == 2}">
                            <option value="1">系统管理员</option>
                            <option value="2" selected="selected">经理</option>
                            <option value="3">普通员工</option>
                        </c:when>
                        <c:otherwise>
                            <option value="1">系统管理员</option>
                            <option value="2">经理</option>
                            <option value="3" selected="selected">普通员工</option>
                        </c:otherwise>
                    </c:choose>
                </select>
                <font color="red"></font>
            </div>
            <div class="providerAddBtn">
                <input type="button" name="save" id="save" value="保存"/>
                <input type="button" id="back" name="back" value="返回"/>
            </div>
        </form>
    </div>
</div>
</section>
<%@include file="/jsp/common/foot.jsp" %>
<script type="text/javascript" src="${pageContext.request.contextPath }/js/usermodify.js"></script>
