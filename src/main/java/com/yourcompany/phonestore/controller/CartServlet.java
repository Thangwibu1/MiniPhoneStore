package com.yourcompany.phonestore.controller;

import com.yourcompany.phonestore.dao.CartDAO;
import com.yourcompany.phonestore.dao.ProductDAO;
import com.yourcompany.phonestore.model.CartItem;
import com.yourcompany.phonestore.model.Product;
import com.yourcompany.phonestore.model.User;
import jakarta.servlet.*; // Đã cập nhật
import jakarta.servlet.http.*; // Đã cập nhật
import java.io.IOException;
import java.util.List;

public class CartServlet extends HttpServlet {
    private CartDAO cartDAO;
    private ProductDAO productDAO;

    public void init() {
        cartDAO = new CartDAO();
        productDAO = new ProductDAO();
    }

    // Hiển thị giỏ hàng
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // Lấy các item trong giỏ hàng của user từ MongoDB
        List<CartItem> cartItems = cartDAO.getCartItems(user.getId());

        // Tính tổng tiền
        double totalPrice = cartItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();

        // Gửi danh sách item và tổng tiền đến trang cart.jsp
        request.setAttribute("cartItems", cartItems);
        request.setAttribute("totalPrice", totalPrice);
        RequestDispatcher dispatcher = request.getRequestDispatcher("cart.jsp");
        dispatcher.forward(request, response);
    }

    // Xử lý thêm/xóa sản phẩm
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // Lấy hành động (add/remove) từ request
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            // Lấy id và số lượng sản phẩm cần thêm
            int productId = Integer.parseInt(request.getParameter("productId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            // Lấy thông tin sản phẩm từ CSDL
            Product product = productDAO.getProductById(productId);
            if (product != null) {
                // Tạo một CartItem mới
                CartItem newItem = new CartItem(product.getId(), product.getName(), quantity, product.getPrice());
                // Thêm vào giỏ hàng trong MongoDB
                cartDAO.addItemToCart(user.getId(), newItem);
            }
        } else if ("remove".equals(action)) {
            // Lấy id sản phẩm cần xóa
            int productId = Integer.parseInt(request.getParameter("productId"));
            // Xóa khỏi giỏ hàng trong MongoDB
            cartDAO.removeItemFromCart(user.getId(), productId);
        }

        // Chuyển hướng lại về trang giỏ hàng
        response.sendRedirect("cart");
    }
}

