# 📦 Smart Inventory System

A professional desktop-based inventory management application built with **Java Swing** and **MySQL**.

---

## 🖥️ Screenshots

### Login Page
![Login](screenshots/1_login.png)

### Dashboard
![Dashboard](screenshots/2_dashboard.png)

### Add Product
![Add Product](screenshots/3_add_product.png)

### View Products
![View Products](screenshots/4_view_products.png)

### Update Stock
![Update Stock](screenshots/5_update_stock.png)

### Delete Product
![Delete Product](screenshots/6_delete_product.png)

### Search Product
![Search Product](screenshots/7_search_product.png)

### Sales Entry
![Sales Entry](screenshots/8_sales_entry.png)

### Sales Chart
![Sales Chart](screenshots/9_sales_chart.png)

---

## ✨ Features

- 🔐 **Secure Login** — Username and password authentication
- 📊 **Live Dashboard** — Real-time stats showing total products, low stock alerts, total sales and categories
- ➕ **Add Product** — Add new products with validation and duplicate check
- 📋 **View Products** — View all products with automatic low stock row highlighting
- 🔄 **Update Stock** — Update product price and quantity by product ID
- 🗑️ **Delete Product** — Delete product with confirmation dialog
- 🔍 **Search Product** — Search and view complete product details by ID
- 💰 **Sales Entry** — Record sales with live product preview and auto total calculation
- 📈 **Sales Chart** — Visual bar chart showing revenue breakdown by product
- ⚠️ **Low Stock Alerts** — Automatic popup alerts when stock falls below 5 units
- 🎨 **Premium UI** — Custom painted components, animations, and consistent theme

---

## 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| Java Swing | UI / Frontend |
| MySQL 8.0 | Database |
| JDBC | Database connectivity |
| JFreeChart | Sales bar chart |
| IntelliJ IDEA | IDE |

---

## 📁 Project Structure

```
Smart-Inventory-System/
├── src/
│   ├── Login.java
│   ├── Dashboard.java
│   ├── DBConnection.java
│   ├── AddProduct.java
│   ├── ViewProducts.java
│   ├── UpdateProduct.java
│   ├── DeleteProduct.java
│   ├── SearchProduct.java
│   ├── SalesEntry.java
│   ├── SalesChart.java
│   └── FancyDialog.java
├── images/
├── screenshots/
└── README.md
```

---

## 🗄️ Database Setup

**Step 1** — Open MySQL Workbench and run:

```sql
CREATE DATABASE inventory_db;
USE inventory_db;
```

**Step 2** — Create tables:

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL
);

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    price DOUBLE,
    quantity INT
);

CREATE TABLE sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100),
    qty INT,
    total DOUBLE
);
```

**Step 3** — Add default admin user:

```sql
INSERT INTO users(username, password) 
VALUES('admin', 'admin123');
```

---

## ⚙️ How to Run

**Step 1** — Clone the repository:
```bash
git clone https://github.com/HiteshKumar360/Smart-Inventory-System.git
```

**Step 2** — Open in **IntelliJ IDEA**

**Step 3** — Add JAR dependencies:
- `mysql-connector-j-9.x.jar`
- `jfreechart-1.x.jar`
- `jcommon-1.x.jar`

**Step 4** — Update `DBConnection.java`:
```java
String url  = "jdbc:mysql://localhost:3306/inventory_db";
String user = "root";
String pass = "yourpassword";
```

**Step 5** — Run `Login.java`

---

## 🔑 Default Login Credentials

| Username | Password |
|---|---|
| admin | admin123 |

---

## 📌 System Requirements

| Requirement | Version |
|---|---|
| Java JDK | 17 or higher |
| MySQL | 8.0 or higher |
| IntelliJ IDEA | Any recent version |
| OS | Windows 10/11 |

---

## 🎨 UI Highlights

- **Dark navy sidebar** with twinkling star animation
- **White main content** area with soft gradient
- **Custom FancyDialog** replacing all plain system dialogs
- **Hover animations** on all cards and buttons
- **Color coded** status — green OK, red Low Stock
- **Live stats bar** with real time database counts
- **Consistent accent colors** per screen

---

## 👨‍💻 Developer

**Hitesh Kumar**

- GitHub: [@HiteshKumar360](https://github.com/HiteshKumar360)
- Email: kumhit871@gmail.com

---

## 📄 License

This project is developed for educational purposes.