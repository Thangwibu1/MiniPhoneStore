package com.yourcompany.phonestore.controller;

import jakarta.servlet.*; // Đã cập nhật
import jakarta.servlet.http.*; // Đã cập nhật
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy session hiện tại
        HttpSession session = request.getSession(false);
        if (session != null) {
            // Hủy session
            session.invalidate();
        }
        // Chuyển hướng về trang chủ
        response.sendRedirect("home");
    }
}
