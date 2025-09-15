# 🛒 Supermarket Billing System (Java + MySQL)

## 🎯 Overview
This project is a **Supermarket Billing & Management System** built using **Java** and **MySQL**.  
It allows managing **products** and **users**, saving all data securely in a relational database instead of memory.

---

## 🧩 Modules

### 1️⃣ DBConnection
- Responsible for establishing a connection to the MySQL database.
- Provides a `getConnection()` method used across the system.

---

### 2️⃣ Product
Represents a supermarket product.  
**Fields:** `id`, `name`, `price`, `quantity`, `category`  

**Main Methods:**
- `savetodatabase()` → Save a new product into the DB.
- `updateQuantityInDB()` → Update stock quantity.
- `allproducts()` → Retrieve all products.
- `decreaseQuantity(int amount)` / `increaseQuantity(int amount)` → Manage stock levels.
- `toRow()` → Convert product to table row format (useful for GUI).

---

### 3️⃣ User
Represents a system user (customer).  
**Fields:** `id`, `name`, `phone`, `address`, `password`  

**Main Methods:**
- `savetodatabase()` → Add new user.
- `updateInDatabase()` → Update existing user.
- `deleteFromDatabase()` → Remove a user.
- `toString()` → Display user info in a readable format.

---

## ⚙️ Features
- Written in **Java (OOP-based)**.
- Connected to **MySQL Database** via **JDBC**.
- Supports **CRUD operations** (Create, Read, Update, Delete).
- Well-structured classes for easy maintenance and scalability.

---

## 🚀 Future Improvements
- Add a **GUI (Java Swing/JavaFX)** for easier interaction.
- Implement **Billing/Invoice system** to link users with purchased products.
- Add **Authentication/Login** system.
- Generate **Reports** (e.g., total sales, most sold products).

---

