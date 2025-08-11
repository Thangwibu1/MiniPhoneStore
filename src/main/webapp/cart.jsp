<%-- File: src/main/webapp/cart.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="templates/header.jsp" />

<div class="container">
  <h2>Giỏ Hàng của bạn</h2>
  <c:choose>
    <c:when test="${empty cartItems}">
      <p>Giỏ hàng của bạn đang trống.</p>
    </c:when>
    <c:otherwise>
      <table class="cart-table">
        <thead>
        <tr>
          <th>Sản phẩm</th>
          <th>Số lượng</th>
          <th>Đơn giá</th>
          <th>Thành tiền</th>
          <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${cartItems}" var="item">
          <tr>
            <td>${item.productName}</td>
            <td>${item.quantity}</td>
            <td>${String.format("%,.0f", item.price)} VNĐ</td>
            <td>${String.format("%,.0f", item.price * item.quantity)} VNĐ</td>
            <td>
              <form action="cart" method="post">
                <input type="hidden" name="action" value="remove">
                <input type="hidden" name="productId" value="${item.productId}">
                <button type="submit">Xóa</button>
              </form>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
      <h3>Tổng cộng: ${String.format("%,.0f", totalPrice)} VNĐ</h3>
    </c:otherwise>
  </c:choose>
</div>

<jsp:include page="templates/footer.jsp" />