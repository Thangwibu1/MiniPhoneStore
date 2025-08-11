package com.yourcompany.phonestore.controller;

import com.yourcompany.phonestore.dao.UserDAO;
import com.yourcompany.phonestore.model.User;
import jakarta.servlet.*; // Đã cập nhật
import jakarta.servlet.http.*; // Đã cập nhật
import java.io.IOException;

public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO;

    public void init() {
        userDAO = new UserDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Chuyển đến trang register.jsp khi có yêu cầu GET
        RequestDispatcher dispatcher = request.getRequestDispatcher("register.jsp");
        dispatcher.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy thông tin từ form đăng ký
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Kiểm tra xem username đã tồn tại chưa
        if (userDAO.isUsernameExists(username)) {
            request.setAttribute("errorMessage", "Tên đăng nhập đã tồn tại!");
            RequestDispatcher dispatcher = request.getRequestDispatcher("register.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Tạo đối tượng User mới
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password); // Cần hash mật khẩu ở đây
        newUser.setRole("user"); // Mặc định vai trò là 'user'

        // Lưu user mới vào CSDL
        userDAO.saveUser(newUser);

        // Chuyển hướng đến trang đăng nhập sau khi đăng ký thành công
        response.sendRedirect("login");
    }
}