# Phân Tích Chi Tiết Toàn Bộ Dự Án Web Bán Điện Thoại

### Giới thiệu

Tài liệu này là bản phân tích sâu và mở rộng về mã nguồn của dự án Web Bán Điện Thoại. Chúng ta sẽ bắt đầu bằng việc tìm hiểu các công nghệ nền tảng là **Hibernate** và **MongoDB**, sau đó sẽ "mổ xẻ" từng file code, từ cấu hình, lớp dữ liệu, lớp truy xuất, cho đến các bộ điều khiển và giao diện, để hiểu rõ cách chúng hoạt động và tương tác với nhau một cách chi tiết nhất.

---
## Phần 1: Tìm hiểu các Công nghệ Cốt lõi

Trước khi xem code, việc hiểu rõ "tại sao" chúng ta lại chọn những công nghệ này là cực kỳ quan trọng.

### 1.1. Hibernate là gì? - Người Phiên Dịch Thông Thái 👨‍🏫

Khi mới bắt đầu, khái niệm về Hibernate có thể hơi khó hiểu. Hãy hình dung một cách đơn giản nhất.

**Vấn đề cốt lõi:**

* **Bạn (Lập trình viên)**: Tư duy bằng ngôn ngữ "Hướng đối tượng". Bạn làm việc với các đối tượng Java như `Product`, `User`, mỗi đối tượng có các thuộc tính và phương thức.

* **Cơ sở dữ liệu (MSSQL)**: Lại "nói" một ngôn ngữ hoàn toàn khác, đó là ngôn ngữ "Quan hệ". Nó chỉ hiểu các bảng (tables), các hàng (rows) và các cột (columns).

Sự khác biệt này được gọi là **"Object-Relational Impedance Mismatch"** (Sự không tương xứng giữa Hướng đối tượng và Quan hệ). Nếu không có công cụ hỗ trợ, bạn sẽ phải tự tay viết rất nhiều code **JDBC** (Java Database Connectivity) để "phiên dịch" qua lại. Công việc này rất **nhàm chán, lặp đi lặp lại và cực kỳ dễ phát sinh lỗi**.

**Giải pháp - Hibernate xuất hiện!**
**Hibernate** là một framework **ORM (Object-Relational Mapping)**. Nó chính là "người phiên dịch" thông thái đứng giữa bạn và cơ sở dữ liệu. Bạn chỉ cần đưa cho nó một đối tượng `Product`, nó sẽ tự động "dịch" nó thành câu lệnh `INSERT` phù hợp. Khi bạn muốn lấy sản phẩm, bạn chỉ cần nói: `session.get(Product.class, 1)`, Hibernate sẽ tự dịch thành câu lệnh `SELECT` và tạo sẵn cho bạn một đối tượng `Product` hoàn chỉnh.

**Các thành phần chính của Hibernate trong dự án:**

* **Annotations (`@Entity`, `@Table`, `@Id`,...)**: Đây là những "chú thích" bạn đặt trong các lớp Model (`Product.java`). Chúng giống như những chỉ dẫn ngữ pháp cho người phiên dịch Hibernate.

* **`hibernate.cfg.xml` (File cấu hình)**: Giống như "sổ tay" của người phiên dịch, chứa các thông tin kết nối CSDL và các cấu hình khác.

* **`SessionFactory` (Nhà máy sản xuất Session)**: Đây là một đối tượng **"nặng ký"**, được **tạo một lần duy nhất** khi ứng dụng khởi động. Nó chứa tất cả thông tin đã được dịch sẵn và quản lý một "pool" kết nối CSDL.

* **`Session` (Phiên làm việc)**: Đây là một đối tượng **"nhẹ ký"**, đại diện cho một phiên làm việc ngắn hạn với CSDL để thực hiện các thao tác. Sau khi dùng xong, `Session` được đóng lại và kết nối được trả về pool.

### 1.2. MongoDB - Chiếc Hộp Công Cụ Linh Hoạt 🧰

Nếu Hibernate/MSSQL giống như một tủ hồ sơ được phân chia ngăn kéo rõ ràng, thì MongoDB giống như một chiếc hộp lớn nơi bạn có thể bỏ vào bất cứ loại tài liệu nào.

* **SQL (MSSQL)**: Yêu cầu bạn phải định nghĩa một **cấu trúc (schema) cứng nhắc** từ đầu.

* **NoSQL (MongoDB)**: Là **schemaless (phi cấu trúc)**. Nó lưu dữ liệu dưới dạng các **Document** (rất giống JSON), được nhóm vào trong các **Collection**.

**Tại sao lại dùng cho Giỏ hàng?**
Giỏ hàng là một ví dụ hoàn hảo cho sự linh hoạt của MongoDB. Cấu trúc giỏ hàng của mỗi người dùng có thể khác nhau và thay đổi liên tục. Việc lưu giỏ hàng của một user thành một document duy nhất sẽ hiệu quả và tự nhiên hơn nhiều so với việc phải tạo ra các bảng quan hệ phức tạp trong SQL.

```json
{
  "userId": 5,
  "items": [
    { "productId": 1, "productName": "iPhone 15", "quantity": 2, "price": 34990000 },
    { "productId": 8, "productName": "Xiaomi 14", "quantity": 1, "price": 22990000 }
  ]
}
```

---
## Phần 2: Phân Tích Chi Tiết Mã Nguồn và Luồng Hoạt Động

### 2.1. Các File Cấu Hình

#### **`pom.xml` - Quản Lý Dự Án và Thư Viện**

File này là "bảng kê nguyên vật liệu" cho dự án, giúp Maven quản lý các thư viện.

```xml
<dependencies>
    <!-- 1. jakarta.servlet-api: Cung cấp các lớp cốt lõi cho web như HttpServlet,
         HttpSession,... Scope 'provided' nghĩa là thư viện này sẽ được server (Tomcat)
         cung cấp sẵn, không cần đóng gói vào file .war. -->
    <dependency>
        <groupId>jakarta.servlet</groupId>
        <artifactId>jakarta.servlet-api</artifactId>
        <version>6.0.0</version>
        <scope>provided</scope>
    </dependency>

    <!-- 2. hibernate-core (cho ORM): Thư viện chính của Hibernate 6, tương thích
         với môi trường Jakarta EE. -->
    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>6.4.4.Final</version>
    </dependency>

    <!-- 3. jakarta.servlet.jsp.jstl: Thư viện JSTL để dùng các thẻ logic
         (<c:if>, <c:forEach>) trong file JSP. -->
    <dependency>
        <groupId>org.glassfish.web</groupId>
        <artifactId>jakarta.servlet.jsp.jstl</artifactId>
        <version>3.0.1</version>
    </dependency>

    <!-- 4. mssql-jdbc: Driver cho phép ứng dụng Java kết nối với MSSQL Server. -->
    <dependency>
        <groupId>com.microsoft.sqlserver</groupId>
        <artifactId>mssql-jdbc</artifactId>
        <version>9.4.1.jre8</version>
    </dependency>

    <!-- 5. mongodb-driver-sync: Driver cho phép ứng dụng Java giao tiếp với MongoDB. -->
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-sync</artifactId>
        <version>4.11.1</version>
    </dependency>
</dependencies>
```

#### **`web.xml` - Người Điều Phối Giao Thông**

File này là "bản đồ" của ứng dụng web, chỉ cho Tomcat biết phải làm gì với các yêu cầu.

```xml
<!-- 1. Khai báo Servlet: Giới thiệu một Servlet cho Tomcat. -->
<servlet>
    <servlet-name>LoginServlet</servlet-name>
    <servlet-class>com.yourcompany.phonestore.controller.LoginServlet</servlet-class>
</servlet>

<!-- 2. Ánh xạ Servlet: Liên kết một URL với một Servlet. -->
<servlet-mapping>
    <servlet-name>LoginServlet</servlet-name>
    <url-pattern>/login</url-pattern>
</servlet-mapping>

<!-- 3. Khai báo Filter: Giới thiệu một lớp Filter (bộ lọc). -->
<filter>
    <filter-name>AuthFilter</filter-name>
    <filter-class>com.yourcompany.phonestore.controller.AuthFilter</filter-class>
</filter>

<!-- 4. Ánh xạ Filter: Yêu cầu Filter phải "chặn" và kiểm tra các request đến URL chỉ định. -->
<filter-mapping>
    <filter-name>AuthFilter</filter-name>
    <url-pattern>/cart</url-pattern>
    <url-pattern>/admin</url-pattern>
</filter-mapping>

<!-- 5. File chào mừng: Chỉ định trang mặc định khi truy cập vào thư mục gốc. -->
<welcome-file-list>
    <welcome-file>home</welcome-file>
</welcome-file-list>
```

### 2.2. Các Lớp Model và Utility

#### **`Product.java` & `User.java` - Bản Thiết Kế Đối Tượng**

Các lớp này được Hibernate dùng để ánh xạ với bảng trong CSDL.

```java
// File: Product.java
@Entity // 1. Báo cho Hibernate: "Đây là một lớp Entity."
@Table(name = "Products") // 2. "Ánh xạ lớp này với bảng 'Products'."
public class Product {
    @Id // 3. "Trường 'id' là khóa chính."
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 4. "Để CSDL tự động tạo giá trị."
    @Column(name = "id") // 5. "Ánh xạ trường này với cột 'id'."
    private int id;
    // ...
}
```

#### **`CartItem.java` - Đối Tượng Dữ Liệu Đơn Thuần**

Đây là một lớp POJO (Plain Old Java Object), chỉ dùng để chứa dữ liệu tạm thời.

```java
public class CartItem {
    private int productId;
    private String productName;
    private int quantity;
    private double price;
    // ...
}
```

#### **`HibernateUtil.java` - Nhà Máy Sản Xuất Kết Nối**

Lớp này quản lý đối tượng `SessionFactory` theo mẫu Singleton.

```java
public class HibernateUtil {
    // 1. Khai báo SessionFactory là static và final, đảm bảo chỉ có MỘT đối tượng duy nhất.
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // 2. Đọc file hibernate.cfg.xml.
            Configuration configuration = new Configuration().configure();
            // 3. Chỉ định rõ ràng các lớp Entity bằng code.
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Product.class);
            // 4. Xây dựng "nhà máy" SessionFactory.
            return configuration.buildSessionFactory();
        } catch (Throwable ex) { /* ... */ }
    }
    // 5. Cung cấp phương thức để các lớp khác có thể lấy được nhà máy.
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
```

#### **`MongoUtil.java` - Quản Lý Kết Nối MongoDB**

Lớp này quản lý kết nối đến MongoDB.

```java
public class MongoUtil {
    // Khối static được thực thi một lần duy nhất khi lớp được nạp.
    static {
        try {
            // Tạo kết nối và lấy về đối tượng database.
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("PhoneStoreMongo");
        } catch (Exception e) { /* ... */ }
    }
    // ...
}
```

### 2.3. Lớp DAO - Người Lao Động Thầm Lặng

#### **`UserDAO.java` - Thao Tác với Người Dùng**

```java
public class UserDAO {
    // Lưu một người dùng mới
    public void saveUser(User user) {
        // Mọi thao tác ghi đều nên được bao bọc trong một transaction.
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(user); // Ra lệnh cho Hibernate tạo câu lệnh INSERT
            transaction.commit(); // Xác nhận transaction
        } catch (Exception e) {
            transaction.rollback(); // Hủy bỏ thay đổi nếu có lỗi
        }
    }
    // Xác thực đăng nhập
    public User validate(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User U WHERE U.username = :username AND U.password = :password", User.class);
            // ... gán tham số ...
            return query.uniqueResult(); // Trả về một đối tượng duy nhất hoặc null.
        }
    }
}
```

#### **`ProductDAO.java` - Thao Tác với Sản Phẩm**

```java
public class ProductDAO {
    public List<Product> getAllProducts() {
        // Khối try-with-resources đảm bảo session được đóng tự động.
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // "from Product" là HQL, "Product" là tên LỚP JAVA.
            return session.createQuery("from Product", Product.class).list();
        }
    }
}
```

#### **`CartDAO.java` - Thao Tác với Giỏ Hàng MongoDB**

```java
public class CartDAO {
    public void addItemToCart(int userId, CartItem newItem) {
        // Tìm document giỏ hàng của user trong collection 'carts'.
        Document cartDoc = cartCollection.find(Filters.eq("userId", userId)).first();
        if (cartDoc == null) {
            // Nếu chưa có, tạo một document mới và chèn vào.
            // ...
            cartCollection.insertOne(cartDoc);
        } else {
            // Nếu đã có, cập nhật danh sách items và thay thế document cũ.
            // ...
            cartCollection.replaceOne(Filters.eq("userId", userId), cartDoc);
        }
    }
}
```

### 2.4. Lớp Controller (Servlet) và Filter

#### **`AuthFilter.java` - Người Gác Cổng**

Bảo vệ các trang yêu cầu đăng nhập.

```java
public class AuthFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        // Lấy session hiện tại, không tạo mới.
        HttpSession session = request.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
        if (!isLoggedIn) {
            // Nếu chưa đăng nhập, chuyển hướng về trang login.
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            // Nếu đã đăng nhập, cho phép yêu cầu đi tiếp.
            chain.doFilter(req, res);
        }
    }
}
```

#### **`HomeServlet.java` - Hiển thị Trang Chủ**

```java
public class HomeServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        // 1. Gọi DAO để lấy danh sách sản phẩm.
        List<Product> productList = productDAO.getAllProducts();
        // 2. Đặt danh sách vào request scope.
        request.setAttribute("productList", productList);
        // 3. Chuyển tiếp (forward) request đến home.jsp.
        RequestDispatcher dispatcher = request.getRequestDispatcher("home.jsp");
        dispatcher.forward(request, response);
    }
}
```

#### **`LoginServlet.java` - Quản lý Phiên Đăng Nhập**

```java
protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    // ... lấy username, password ...
    User user = userDAO.validate(username, password);
    if (user != null) { // Nếu đăng nhập thành công
        // 1. Tạo hoặc lấy HttpSession.
        HttpSession session = request.getSession();
        // 2. Lưu đối tượng User vào session.
        session.setAttribute("user", user);
        // 3. Chuyển hướng trình duyệt (redirect).
        response.sendRedirect("home");
    }
}
```

### 2.5. Lớp View (JSP) - Giao Diện Người Dùng

#### **`templates/header.jsp` & `footer.jsp` - Tái sử dụng Giao diện**

Các file này chứa các phần chung của trang web.

```jsp
<!-- File: header.jsp -->
<header>
    <nav>
        <!-- Thẻ <c:choose> của JSTL hoạt động như một khối switch-case. -->
        <c:choose>
            <!-- ${not empty sessionScope.user} kiểm tra xem trong session có "user" không. -->
            <c:when test="${not empty sessionScope.user}">
                <span>Chào, ${sessionScope.user.username}!</span>
            </c:when>
            <c:otherwise>
                <a href="/login">Đăng Nhập</a>
            </c:otherwise>
        </c:choose>
    </nav>
</header>
<main> <!-- Thẻ main được mở ở đây -->
```

#### **`home.jsp` - Hiển thị Danh sách Sản phẩm**

Sử dụng `<jsp:include>` để ghép header và footer vào.

```jsp
<jsp:include page="templates/header.jsp" />
<div class="product-grid">
    <!-- Thẻ <c:forEach> của JSTL lặp qua danh sách 'productList'. -->
    <c:forEach items="${productList}" var="product">
        <div class="product-card">
            <!-- ${product.name} là Expression Language (EL), tự động gọi product.getName(). -->
            <h3>${product.name}</h3>
        </div>
    </c:forEach>
</div>
<jsp:include page="templates/footer.jsp" />
```

---
## Phần 3: Luồng Hoạt Động Tổng Thể - Hành Trình của một Yêu Cầu 🗺️

Hãy cùng nhau đi qua một kịch bản hoàn chỉnh để thấy cách tất cả các mảnh ghép hoạt động cùng nhau.

**Kịch bản:** Một người dùng mới truy cập trang web, đăng ký, đăng nhập, thêm đồ vào giỏ hàng và xem giỏ hàng.

**Bước 0: Khởi động Server**
1.  Tomcat khởi động và triển khai ứng dụng.
2.  Tomcat đọc `web.xml`.
3.  Khi có yêu cầu đầu tiên, `HibernateUtil` và `MongoUtil` được nạp, `SessionFactory` được tạo, kết nối MongoDB được thiết lập.
    *➡️ **Hệ thống đã sẵn sàng.**

**Bước 1: Người dùng truy cập Trang chủ**
1.  **Browser**: Gửi `GET` đến `/`.
2.  **Tomcat**: Dựa vào `welcome-file`, chuyển hướng nội bộ đến `/home`.
3.  **`HomeServlet.doGet()`**: Gọi `ProductDAO` để lấy `List<Product>`.
4.  **`ProductDAO`**: Dùng Hibernate Session để thực thi `createQuery("from Product")`.
5.  **`HomeServlet.doGet()`**: Đặt danh sách vào `request` và `forward` đến `home.jsp`.
6.  **`home.jsp`**: Dùng JSTL để lặp và hiển thị sản phẩm.
    *➡️ **Người dùng thấy trang chủ.**

**Bước 2: Người dùng Đăng ký**
1.  **Browser**: Gửi `POST` đến `/register`.
2.  **`RegisterServlet.doPost()`**: Gọi `UserDAO` để lưu người dùng mới.
3.  **`UserDAO`**: Dùng Hibernate Session để `save(newUser)`.
4.  **`RegisterServlet.doPost()`**: `redirect` về trang `/login`.
    *➡️ **Người dùng được chuyển đến trang đăng nhập.**

**Bước 3: Người dùng Đăng nhập**
1.  **Browser**: Gửi `POST` đến `/login`.
2.  **`LoginServlet.doPost()`**: Gọi `UserDAO` để xác thực.
3.  **`UserDAO`**: Dùng Hibernate Session để truy vấn tìm user.
4.  **`LoginServlet.doPost()`**: Nếu thành công, tạo `HttpSession` và lưu đối tượng `User` vào đó. Sau đó `redirect` về `/home`.
    *➡️ **Người dùng thấy lại trang chủ, nhưng đã ở trạng thái đăng nhập.**

**Bước 4: Người dùng Thêm sản phẩm vào Giỏ hàng**
1.  **Browser**: Gửi `POST` đến `/cart`.
2.  **`AuthFilter.doFilter()`**: Kiểm tra session, thấy đã đăng nhập, cho phép yêu cầu đi tiếp.
3.  **`CartServlet.doPost()`**: Lấy thông tin, gọi `CartDAO`.
4.  **`CartDAO`**: Kết nối MongoDB, cập nhật hoặc tạo mới document giỏ hàng.
5.  **`CartServlet.doPost()`**: `redirect` về `/cart`.
    *➡️ **Người dùng được chuyển đến trang giỏ hàng.**

**Bước 5: Người dùng Xem Giỏ hàng**
1.  **Browser**: Gửi `GET` đến `/cart`.
2.  **`AuthFilter`**: Cho phép đi qua.
3.  **`CartServlet.doGet()`**: Gọi `CartDAO` để lấy danh sách item.
4.  **`CartDAO`**: Đọc document từ MongoDB.
5.  **`CartServlet.doGet()`**: Đặt danh sách vào `request` và `forward` đến `cart.jsp`.
6.  **`cart.jsp`**: Hiển thị giỏ hàng.
    *➡️ **Người dùng thấy giỏ hàng của mình.**

```
</markdo