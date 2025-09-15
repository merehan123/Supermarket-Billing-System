import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class BillingSystem {
    private final Inventory inventory;
    private final Cart cart;
    private final Scanner scanner;
    private User current_user;

    public BillingSystem(){
        inventory = new Inventory();
        cart = new Cart(inventory);
        scanner = new Scanner(System.in);
    }

    // main loop to interact with the user
    public void start(){
        auser();
        while (true){
            System.out.println("welcome to my supermarket billing system!");
            System.out.println("1. Add product to your cart");
            System.out.println("2. Remove product from your cart");
            System.out.println("3. View your cart");
            System.out.println("4. Checkout");
            System.out.println("5. Exit");
            System.out.println("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice){
                case 1:
                    add_to_cart();
                    break;
                case 2:
                    remove_from_cart();
                    break;
                case 3:
                    cart.view_cart();
                    break;
                case 4:
                    checkout();
                    break;
                case 5:
                    System.out.println("Thank you for shopping!");
                    return;
                default:
                    System.out.println("Invalid choice try again");
            }
        }
    }

    private void auser(){
        System.out.println("Are you a new user? (yes or no): ");
        String response = scanner.nextLine().trim().toLowerCase();
        if(response.equals("yes")){
            register_user();
        }
        else{
            login_user();
        }
    }

    private void register_user(){
        String name, phone, address, password;
        while (true) {
            System.out.print("Enter your name (letters only): ");
            name = scanner.nextLine().trim();
            if (name.matches("[a-zA-Z ]+")) break;
            System.out.println("Invalid name! Please enter letters only.");
        }
        while (true) {
            System.out.print("Enter your phone number (at least 8 numbers): ");
            phone = scanner.nextLine().trim();
            if (phone.matches("\\d{8,}")) break;
            System.out.println("Invalid phone number! It should contain at least 8 digits.");
        }
        System.out.print("Enter your address: ");
        address = scanner.nextLine().trim();
        while (true) {
            System.out.print("Create a password: ");
            password = scanner.nextLine();
            if (!password.isEmpty()) break;
            System.out.println("Password cannot be empty! Please enter a valid password.");
        }

        User new_user = new User(name, phone, address, password);
        current_user = new_user;
        try(Connection con = DBConnection.getConnection()){
            PreparedStatement ps = con.prepareStatement("insert into users (name, phone, address, password) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, address);
            ps.setString(4, password);
            ps.executeUpdate();
            try(ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    new_user.setId(id);
                }
            }

        }
        catch (SQLException e){
            System.out.println("Error saving user: " + e.getMessage());
        }
        System.out.println("Registration successful! welcome " + name + "!");
    }

    private void login_user(){
        while (true){
            System.out.print("Enter your name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            try(Connection con = DBConnection.getConnection()){
                PreparedStatement ps = con.prepareStatement("select * from users where name = ? and password = ?");
                ps.setString(1, name);
                ps.setString(2, password);
                try(ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        current_user = new User(rs.getInt("user_id"), rs.getString("name"), rs.getString("phone"), rs.getString("address"), rs.getString("password"));
                        System.out.println("Login successful! Welcome back " + name + "!");
                        return;
                    } else {
                        System.out.println("Invalid login. Please try again.");
                    }
                }
            }
            catch (SQLException e){
                System.out.println("Error during login: " + e.getMessage());
            }
        }
    }

    // method to add an item to the cart
    private void add_to_cart(){
        System.out.println("Choose your category:");
        System.out.println("Vegetables");
        System.out.println("Fruits");
        System.out.println("Dairy");
        System.out.println("Sweets");
        System.out.print("Enter your category: ");
        String category = scanner.nextLine().trim().toLowerCase();
        inventory.showproductbycategory(category);
        System.out.print("Enter product name: ");
        String name = scanner.nextLine().toLowerCase();
        Product product = inventory.get_Product(name);
        if(product == null){
            System.out.println("Product not found in this category");
            return;
        }
        System.out.println("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();
        if (quantity > product.getQuantity()){
            System.out.println("Not enough stock available!");
            return;
        }
        cart.add_to_cart(product , quantity);
    }

    // method to remove an item from the cart
    private void remove_from_cart(){
        if(cart.getCart_items().isEmpty()){
            System.out.println("Cart is empty!");
            return;
        }
        System.out.println("Cart now contains:");
        for (Product p : cart.getCart_items()) {
            System.out.println(p.getName() + " - Quantity: " + p.getQuantity());
        }
        System.out.print("Enter product name to remove: ");
        String name = scanner.nextLine().trim().toLowerCase();
        System.out.print("Enter quantity to remove: ");
        int qty = scanner.nextInt();
        scanner.nextLine();
        cart.remove_from_cart(name, qty);


    }

    // method to complete the purchase and save a receipt
    private void checkout(){
        if (cart.getCart_items().isEmpty()){
            System.out.println("Cart is empty");
            return;
        }
        cart.view_cart();

        System.out.println("Do you want to pay cash or visa?");
        String paymentmethod = scanner.nextLine().trim().toLowerCase();
        if (paymentmethod.equals("visa")) {
            String visa_number;
            do {
                System.out.print("Enter your 16-digit Visa card number: ");
                visa_number = scanner.nextLine().trim();
            } while (!visa_number.matches("\\d{16}"));
            System.out.println("Visa card accepted.");
        }
        System.out.println("Confirm checkout?(yes or no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("no")){
            System.out.println("checkout canceled");
            cart.return_all_to_inventory();
            cart.getCart_items().clear();
            return;
        }
        List<Product> purchasedItems = new ArrayList<>(cart.getCart_items());
        show_receipt(paymentmethod, purchasedItems);
        cart.getCart_items().clear();
        System.out.println("Checkout successful!");


    }

    private void show_receipt(String paymentmethod, List<Product> purchasedItems){
        System.out.println("\n------- RECEIPT -------");
        System.out.println("Customer: " + current_user.getName());
        System.out.println("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("-----------------------");
        double total = 0;
        for (Product p : purchasedItems) {
            double lineTotal = p.getPrice() * p.getQuantity();
            System.out.println(p.getName() + " x " + p.getQuantity() + " --> $" + lineTotal);
            total += lineTotal;
        }
        System.out.println("-----------------------");
        System.out.println("TOTAL: $" + total);
        System.out.println("Payment method: " + paymentmethod);
        System.out.println("-----------------------\n");
    }

    // method to save receipt to a file
    private int save_receipt_db(String paymentmethod){
        if(current_user == null){
            System.out.println("Error, no user logged in");
            return -1;
        }
        int orderid = -1;
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            try(PreparedStatement ps = con.prepareStatement("insert into orders (user_id, order_date, total_price, payment_method) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                double total = cart.get_total();
                ps.setInt(1, current_user.getId());
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                ps.setDouble(3, total);
                ps.setString(4, paymentmethod);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if(keys.next()){
                        orderid = keys.getInt(1);
                    }
                }
            }
            for (Product p : cart.getCart_items()) {
                int qty = p.getQuantity();
                try(PreparedStatement psItem = con.prepareStatement("INSERT INTO order_items (order_id,product_id, quantity, price) VALUES (?, ?, ?,?)")) {
                    psItem.setInt(1, orderid);
                    psItem.setInt(2, p.getId());
                    psItem.setInt(3, qty);
                    psItem.setDouble(4, p.getPrice() * qty);
                    psItem.executeUpdate();
                }

            }
            con.commit();
            System.out.println("Checkout successful! Receipt saved");
        }
        catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("Error saving receipt: " + e.getMessage());
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return orderid;
    }

}
