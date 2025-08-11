package com.yourcompany.phonestore.controller;

import com.yourcompany.phonestore.dao.UserDAO;
import com.yourcompany.phonestore.model.User;
import jakarta.servlet.*; // Đã cập nhật
import jakarta.servlet.http.*; // Đã cập nhật
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;

    public void init() {
        userDAO = new UserDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Chuyển đến trang login.jsp khi có yêu cầu GET
        RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy username và password từ form đăng nhập
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Xác thực người dùng qua DAO
        User user = userDAO.validate(username, password);

        if (user != null) {
            // Nếu xác thực thành công, tạo một session mới
            HttpSession session = request.getSession();
            // Lưu đối tượng user vào session
            session.setAttribute("user", user);
            // Chuyển hướng về trang chủ
            response.sendRedirect("home");
        } else {
            // Nếu thất bại, gửi thông báo lỗi về trang đăng nhập
            request.setAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            dispatcher.forward(request, response);
        }
    }
}
