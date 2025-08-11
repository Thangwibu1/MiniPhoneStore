package com.yourcompany.phonestore.controller;

import com.yourcompany.phonestore.dao.ProductDAO;
import com.yourcompany.phonestore.model.Product;
import jakarta.servlet.*; // Đã cập nhật
import jakarta.servlet.http.*; // Đã cập nhật
import java.io.IOException;
import java.util.List;

public class HomeServlet extends HttpServlet {
    private ProductDAO productDAO;

    // Khởi tạo DAO khi servlet được tạo
    public void init() {
        productDAO = new ProductDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy danh sách tất cả sản phẩm từ DAO
        List<Product> productList = productDAO.getAllProducts();
        // Đặt danh sách sản phẩm vào request scope để JSP có thể truy cập
        request.setAttribute("productList", productList);
        // Chuyển hướng yêu cầu đến trang home.jsp
        RequestDispatcher dispatcher = request.getRequestDispatcher("home.jsp");
        dispatcher.forward(request, response);
    }
}
