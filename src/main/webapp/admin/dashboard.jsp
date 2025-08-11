<%-- File: src/main/webapp/admin/dashboard.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="../templates/header.jsp" />

<div class="container">
  <h1>Trang Quản Trị</h1>
  <p>Chào mừng Admin, ${sessionScope.user.username}!</p>
  <p>Đây là khu vực quản lý. Bạn có thể thêm các chức năng quản lý người dùng, sản phẩm, đơn hàng tại đây.</p>
</div>

<jsp:include page="../templates/footer.jsp" />