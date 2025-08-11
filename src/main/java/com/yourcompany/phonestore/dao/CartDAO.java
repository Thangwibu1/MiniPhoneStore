package com.yourcompany.phonestore.dao;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.yourcompany.phonestore.model.CartItem;
import com.yourcompany.phonestore.util.MongoUtil;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private final MongoCollection<Document> cartCollection; // Collection để lưu giỏ hàng
    private final Gson gson = new Gson(); // Đối tượng để chuyển đổi giữa Java Object và JSON

    public CartDAO() {
        // Lấy database từ MongoUtil
        MongoDatabase database = MongoUtil.getDatabase();
        // Lấy collection 'carts'
        this.cartCollection = database.getCollection("carts");
    }

    // Lấy giỏ hàng của một user
    public List<CartItem> getCartItems(int userId) {
        // Tìm document trong collection có 'userId' tương ứng
        Document cartDoc = cartCollection.find(Filters.eq("userId", userId)).first();
        List<CartItem> items = new ArrayList<>();

        if (cartDoc != null) {
            // Lấy danh sách các item từ document
            List<Document> itemDocs = cartDoc.getList("items", Document.class);
            // Chuyển đổi từng Document item thành đối tượng CartItem
            for (Document itemDoc : itemDocs) {
                items.add(gson.fromJson(itemDoc.toJson(), CartItem.class));
            }
        }
        return items;
    }

    // Thêm một sản phẩm vào giỏ hàng
    public void addItemToCart(int userId, CartItem newItem) {
        // Tìm document giỏ hàng của user
        Document cartDoc = cartCollection.find(Filters.eq("userId", userId)).first();

        if (cartDoc == null) {
            // Nếu user chưa có giỏ hàng, tạo mới
            List<Document> items = new ArrayList<>();
            items.add(Document.parse(gson.toJson(newItem))); // Chuyển CartItem thành Document
            cartDoc = new Document("userId", userId).append("items", items);
            // Chèn document mới vào collection
            cartCollection.insertOne(cartDoc);
        } else {
            // Nếu user đã có giỏ hàng
            List<Document> items = cartDoc.getList("items", Document.class);
            boolean itemExists = false;
            // Kiểm tra xem sản phẩm đã có trong giỏ chưa
            for (Document itemDoc : items) {
                if (itemDoc.getInteger("productId") == newItem.getProductId()) {
                    // Nếu có, cập nhật số lượng
                    int newQuantity = itemDoc.getInteger("quantity") + newItem.getQuantity();
                    itemDoc.put("quantity", newQuantity);
                    itemExists = true;
                    break;
                }
            }
            // Nếu sản phẩm chưa có, thêm mới vào danh sách
            if (!itemExists) {
                items.add(Document.parse(gson.toJson(newItem)));
            }
            // Cập nhật lại document trong collection
            cartCollection.replaceOne(Filters.eq("userId", userId), cartDoc);
        }
    }

    // Xóa một sản phẩm khỏi giỏ hàng
    public void removeItemFromCart(int userId, int productId) {
        // Tìm giỏ hàng của user
        Document cartDoc = cartCollection.find(Filters.eq("userId", userId)).first();
        if (cartDoc != null) {
            List<Document> items = cartDoc.getList("items", Document.class);
            // Xóa item có productId tương ứng
            items.removeIf(itemDoc -> itemDoc.getInteger("productId") == productId);
            // Cập nhật lại document
            cartCollection.replaceOne(Filters.eq("userId", userId), cartDoc);
        }
    }
}
