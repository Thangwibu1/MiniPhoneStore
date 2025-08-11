package com.yourcompany.phonestore.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoUtil {
    private static MongoClient mongoClient; // Đối tượng client kết nối tới MongoDB
    private static MongoDatabase database; // Đối tượng database

    // Khối static để khởi tạo kết nối ngay khi lớp được nạp
    static {
        try {
            // Tạo kết nối tới MongoDB server đang chạy trên localhost, cổng 27017
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            // Lấy database có tên là 'PhoneStoreMongo'
            database = mongoClient.getDatabase("PhoneStoreMongo");
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB." + e);
        }
    }

    // Phương thức public để các lớp khác có thể lấy được đối tượng database
    public static MongoDatabase getDatabase() {
        return database;
    }

    // Phương thức để đóng kết nối khi ứng dụng dừng
    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
