import java.sql.Connection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;


public class Inventory {
    private final Map<String, Product> products;
    private Connection con = DBConnection.getConnection();


    public Inventory(){
        products = new HashMap<>();
        productsfromDB();
    }

    private void productsfromDB(){
        List<Product> productList = Product.allproducts();
        for(Product p : productList){
            products.put(p.getName().toLowerCase(), p);
        }
    }
    public List<Product> getProductsByCategory(String category) {
        List<Product> result = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                result.add(p);
            }
        }
        return result;
    }
    public void showproductbycategory(String category){
        System.out.println("Available products in " + category + ":");
        boolean found = false;
        for(Product p : products.values()){
            if(p.getCategory().equalsIgnoreCase(category)){
                System.out.println("--> " + p.getName() + " ($" + p.getPrice() + ") available: " + p.getQuantity());
                found = true;
            }
        }
        if(!found){
            System.out.println("No products found in this category");
        }
    }
    public void addProductQuantity(String productName, int amount) {
        Product product = get_Product(productName);
        if(amount <= 0){
            System.out.println("Invalid amount");
            return;
        }
        if (product != null) {
            int newQuantity = product.getQuantity() + amount;
            updateProductQuantity(product, newQuantity);
        } else {
            System.out.println("Product not found in inventory: " + productName);
        }
    }

    public void updateProductQuantity(Product product, int newQuantity) {
        product.setQuantity(newQuantity);
        product.updateQuantityInDB();
        String key = product.getName().toLowerCase();
        if(products.containsKey(key)) {
            products.put(key, product);
        } else {
            System.out.println("Warning: Product not found in inventory, adding it now.");
            products.put(key, product);
        }
    }
    public void refreshInventory() {
        products.clear();
        productsfromDB();
    }




    public Product get_Product(String name){
        return products.get(name.toLowerCase());
    }
}
