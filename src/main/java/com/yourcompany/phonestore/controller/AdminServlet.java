package com.yourcompany.phonestore.controller;

import jakarta.servlet.RequestDispatcher; // Đã cập nhật
import jakarta.servlet.ServletException; // Đã cập nhật
import jakarta.servlet.http.HttpServlet; // Đã cập nhật
import jakarta.servlet.http.HttpServletRequest; // Đã cập nhật
import jakarta.servlet.http.HttpServletResponse; // Đã cập nhật
import java.io.IOException;

public class AdminServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Đây là nơi để lấy dữ liệu cho trang admin (ví dụ: danh sách user, thống kê,...)
        // Hiện tại chỉ chuyển hướng đến trang dashboard
        RequestDispatcher dispatcher = request.getRequestDispatcher("admin/dashboard.jsp");
        dispatcher.forward(request, response);
    }
}