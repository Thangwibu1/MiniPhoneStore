package com.yourcompany.phonestore.dao;

import com.yourcompany.phonestore.model.Product;
import com.yourcompany.phonestore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

public class ProductDAO {

    // Lấy tất cả sản phẩm
    public List<Product> getAllProducts() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Truy vấn để lấy tất cả các đối tượng Product
            return session.createQuery("from Product", Product.class).list();
        }
    }

    // Lấy sản phẩm theo ID
    public Product getProductById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Dùng phương thức get của session để lấy đối tượng theo khóa chính
            return session.get(Product.class, id);
        }
    }
}
