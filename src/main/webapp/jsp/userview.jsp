<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/jsp/common/head.jsp"%>
 <div class="right">
        <div class="location">
            <strong>你现在所在的位置是:</strong>
            <span>用户管理页面 >> 用户信息查看页面</span>
        </div>
        <div class="providerView">
            <p><strong>用户编号：</strong><span>${param.userCode}</span></p>
            <p><strong>用户名称：</strong><span>${param.n}</span></p>
            <p><strong>用户性别：</strong>
            	<span>
            		<c:if test="${param.g == 1 }">男</c:if>
					<c:if test="${param.g == 2 }">女</c:if>
				</span>
			</p>
            <p><strong>出生日期：</strong><span>${param.b}</span></p>
            <p><strong>用户电话：</strong><span>${param.p}</span></p>
            <p><strong>用户地址：</strong><span>${param.a}</span></p>
            <p><strong>用户角色：</strong>
                <span>
                    <c:if test="${param.r == 1 }">系统管理员</c:if>
					<c:if test="${param.r == 2 }">经理</c:if>
					<c:if test="${param.r == 2 }">普通员工</c:if>
                </span>
            </p>
			<div class="providerAddBtn">
            	<input type="button" id="back" name="back" value="返回" >
            </div>
        </div>
    </div>
</section>
<%@include file="/jsp/common/foot.jsp" %>
<script type="text/javascript" src="${pageContext.request.contextPath }/js/userview.js"></script>