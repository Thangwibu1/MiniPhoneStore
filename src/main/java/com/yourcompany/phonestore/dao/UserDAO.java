package com.yourcompany.phonestore.dao;

import com.yourcompany.phonestore.model.User;
import com.yourcompany.phonestore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class UserDAO {

    // Phương thức để lưu một User mới vào CSDL
    public void saveUser(User user) {
        Transaction transaction = null; // Khởi tạo một transaction
        // Sử dụng try-with-resources để đảm bảo session được đóng tự động
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Bắt đầu một transaction
            transaction = session.beginTransaction();
            // Lưu đối tượng user
            session.save(user);
            // Commit transaction
            transaction.commit();
        } catch (Exception e) {
            // Nếu có lỗi, rollback lại transaction
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Phương thức để xác thực người dùng
    public User validate(String username, String password) {
        // Sử dụng try-with-resources
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Tạo câu truy vấn HQL (Hibernate Query Language)
            Query<User> query = session.createQuery("FROM User U WHERE U.username = :username AND U.password = :password", User.class);
            // Gán tham số cho câu truy vấn
            query.setParameter("username", username);
            query.setParameter("password", password); // Lưu ý: trong thực tế mật khẩu phải được hash
            // Trả về kết quả duy nhất, hoặc null nếu không tìm thấy
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Phương thức kiểm tra username đã tồn tại chưa
    public boolean isUsernameExists(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery("SELECT 1 FROM User U WHERE U.username = :username");
            query.setParameter("username", username);
            return (query.uniqueResult() != null);
        }
    }
}
