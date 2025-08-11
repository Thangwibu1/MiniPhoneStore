<%-- File: src/main/webapp/templates/header.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Phone Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <nav>
        <a href="${pageContext.request.contextPath}/home">Trang Chủ</a>
        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <span>Chào, ${sessionScope.user.username}!</span>
                <a href="${pageContext.request.contextPath}/cart">Giỏ Hàng</a>
                <c:if test="${sessionScope.user.role == 'admin'}">
                    <a href="${pageContext.request.contextPath}/admin">Trang Admin</a>
                </c:if>
                <a href="${pageContext.request.contextPath}/logout">Đăng Xuất</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login">Đăng Nhập</a>
                <a href="${pageContext.request.contextPath}/register">Đăng Ký</a>
            </c:otherwise>
        </c:choose>
    </nav>
</header>
<main>