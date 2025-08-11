// File: com/yourcompany/phonestore/util/HibernateUtil.java
package com.yourcompany.phonestore.util;

import com.yourcompany.phonestore.model.Product; // Import lớp Product
import com.yourcompany.phonestore.model.User;    // Import lớp User
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    // SessionFactory là một đối tượng nặng, chỉ nên được tạo một lần duy nhất
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        System.out.println(">>> [CANARY TEST] DANG CHAY CODE TRONG HIBERNATEUTIL MOI NHAT <<<");
        try {
            // Tạo SessionFactory từ file hibernate.cfg.xml
            // VÀ thêm các lớp Entity một cách tường minh bằng code
            Configuration configuration = new Configuration().configure(); // Đọc file hibernate.cfg.xml

            // === DÒNG QUAN TRỌNG NHẤT ĐỂ SỬA LỖI ===
            // Thêm các lớp có annotation @Entity vào cấu hình một cách trực tiếp
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Product.class);
            // =======================================

            return configuration.buildSessionFactory();

        } catch (Throwable ex) {
            // Ghi lại lỗi nếu có
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // Phương thức public để các lớp khác có thể lấy được SessionFactory
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    // Đóng các cache và connection pools
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
