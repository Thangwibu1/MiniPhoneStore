<%-- File: src/main/webapp/home.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="templates/header.jsp" />

<div class="container">
    <h1>Sản phẩm nổi bật</h1>
    <div class="product-grid">
        <c:forEach items="${productList}" var="product">
            <div class="product-card">
                <img src="${product.imageUrl}" alt="${product.name}">
                <h3>${product.name}</h3>
                <p class="price">${String.format("%,.0f", product.price)} VNĐ</p>
                <form action="cart" method="post">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="productId" value="${product.id}">
                    <input type="number" name="quantity" value="1" min="1">
                    <button type="submit">Thêm vào giỏ</button>
                </form>
            </div>
        </c:forEach>
    </div>
</div>

<jsp:include page="templates/footer.jsp" />