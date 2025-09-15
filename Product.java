import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Product {
        private int id;
        private String name;
        private double price;
        private int quantity;
        private String category;


    public Product(int id, String name, double price, int quantity, String category){
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.category = category;
        }
        public Product(String name, double price, int quantity, String category){
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.category = category;
    }

    public void savetodatabase(){
            try(Connection con = DBConnection.getConnection()){
                String mysql = "Insert into products(name, price, quantity, category) values(?,?,?,?)";
                PreparedStatement stm = con.prepareStatement(mysql, PreparedStatement.RETURN_GENERATED_KEYS);
                stm.setString(1, name);
                stm.setDouble(2, price);
                stm.setInt(3, quantity);
                stm.setString(4, category);
                stm.executeUpdate();
                ResultSet keys = stm.getGeneratedKeys();
                if(keys.next()){
                    this.id = keys.getInt(1);
                }
                System.out.println("Product saved successfully");
            }
            catch (SQLException e){
                e.printStackTrace();

            }
        }

        public void updateQuantityInDB(){
            if(this.id == 0){
                savetodatabase();
                return;
            }
            try(Connection con = DBConnection.getConnection()){
                String mysql = "Update products Set quantity = ? where product_id = ?";
                PreparedStatement stm = con.prepareStatement(mysql);
                stm.setInt(1, this.quantity);
                stm.setInt(2, this.id);
                stm.executeUpdate();
                System.out.println("Quantity updated in database for: " + this.name);
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }

        public static List<Product> allproducts(){
            List<Product> products = new ArrayList<>();
            try(Connection con = DBConnection.getConnection()){
                String mysql = "Select * from products";
                PreparedStatement stm = con.prepareStatement(mysql);
                ResultSet rs = stm.executeQuery();
                while (rs.next()){
                    Product p = new Product(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getString("category")
                    );
                    products.add(p);
                }

            }
            catch (SQLException e){
                e.printStackTrace();

            }
            return products;
        }

        public String toString() {
            return name + " ($" + price + ") - Stock: " + quantity;
        }

        public void decreaseQuantity(int amount) {
            if (amount > 0 && amount <= this.quantity) {
                this.quantity -= amount;
                updateQuantityInDB();
            }
        }
        public void increaseQuantity(int amount) {
            if (amount > 0) {
                this.quantity += amount;
                updateQuantityInDB();
            }
        }
        public Object[] toRow() {
            return new Object[]{id, name, price, quantity, category};
        }





    public String getName(){
            return name;
        }
        public double getPrice(){
            return price;
        }
        public int getQuantity(){
            return quantity;
        }
        public String getCategory(){
            return category;
        }
        public void setQuantity(int quantity){
            this.quantity = quantity;
        }
        public int getId(){
            return id;
        }
        public void setName(String name) {
            this.name = name;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public void setCategory(String category) {
            this.category = category;
        }





}
