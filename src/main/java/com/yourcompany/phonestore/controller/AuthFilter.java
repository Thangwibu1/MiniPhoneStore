package com.yourcompany.phonestore.controller;

import com.yourcompany.phonestore.model.User;
import jakarta.servlet.*; // Đã cập nhật
import jakarta.servlet.http.HttpServletRequest; // Đã cập nhật
import jakarta.servlet.http.HttpServletResponse; // Đã cập nhật
import jakarta.servlet.http.HttpSession; // Đã cập nhật
import java.io.IOException;

public class AuthFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false); // false: không tạo session mới nếu chưa có

        String requestURI = request.getRequestURI();

        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
        boolean isAdminPage = requestURI.startsWith(request.getContextPath() + "/admin");

        if (!isLoggedIn) {
            // Nếu chưa đăng nhập, chuyển về trang login
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            // Nếu đã đăng nhập
            User user = (User) session.getAttribute("user");
            if (isAdminPage && !"admin".equals(user.getRole())) {
                // Nếu truy cập trang admin mà không phải admin, báo lỗi 403 (Forbidden)
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            } else {
                // Nếu hợp lệ, cho phép request đi tiếp
                chain.doFilter(req, res);
            }
        }
    }
    public void init(FilterConfig filterConfig) {}
    public void destroy() {}
}
