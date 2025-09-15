import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Cart {
    private final List<Product> cart_items;
    private final Inventory inventory;
    private Connection con = DBConnection.getConnection();


    public Cart(Inventory inventory){
        this.cart_items = new ArrayList<>();
        this.inventory = inventory;
    }

    // method to add a product to the cart
    public void add_to_cart(Product product, int quantity){
        if(quantity > product.getQuantity()){
            System.out.println("Not enough quantity, available only " + product.getQuantity());
            return;
        }
        product.setQuantity(product.getQuantity() - quantity);
        product.updateQuantityInDB();
        for (Product p : cart_items){
            if(p.getName().equalsIgnoreCase(product.getName())){
                p.setQuantity(p.getQuantity() + quantity);
                System.out.println(quantity + " " + product.getName() + " added to cart");
                return;
            }
        }
        Product cartproduct = new Product(product.getId(), product.getName(), product.getPrice(), quantity, product.getCategory());
        cart_items.add(cartproduct);
        System.out.println(quantity + " " + product.getName() + " added to cart");

    }

    // method to remove a product from the cart
    public void remove_from_cart(String product_name, int quantityRemove) {
        product_name = product_name.trim().toLowerCase();

        Iterator<Product> iterator = cart_items.iterator();
        while (iterator.hasNext()) {
            Product p = iterator.next();

            if (p.getName().equalsIgnoreCase(product_name)) {
                if(quantityRemove <= 0){
                    System.out.println("Invalid input please enter a positive quantity number: ");
                    return;
                }
                Product invProduct = inventory.get_Product(product_name);


                if (quantityRemove >= p.getQuantity() ){
                    int removed = p.getQuantity();
                    invProduct.setQuantity(invProduct.getQuantity() + removed);
                    invProduct.updateQuantityInDB();
                    iterator.remove();
                    System.out.println(removed + " " + product_name + " removed from cart");

                }
                else{
                    p.setQuantity(p.getQuantity() - quantityRemove);
                    invProduct.setQuantity(invProduct.getQuantity() + quantityRemove);
                    invProduct.updateQuantityInDB();
                    System.out.println(quantityRemove + " " + product_name + " removed from cart");
                }
                return;

            }
        }
        System.out.println("Product not found in cart");
    }


    //method to display cart contents
    public void view_cart(){
        if(cart_items.isEmpty()){
            System.out.println("Cart is empty");
            return;
        }
        double total = 0;
        System.out.println("Cart contents:");
        System.out.println("--------------------------");
        System.out.printf("%-15s %-10s %-10s %-10s\n", "product", "quantity", "price", "total");
        System.out.println("--------------------------");

        for(Product p : cart_items){
            double subtotal = p.getPrice() * p.getQuantity();
            System.out.printf("%-15s %-10d %-10.2f %-10.2f\n", p.getName(), p.getQuantity(), p.getPrice(), subtotal);
            total += subtotal;
        }
        System.out.println("--------------------------");
        System.out.println("Total: $" + total);
    }

    public void return_all_to_inventory() {
        for (Product p : cart_items) {
            Product invProduct = inventory.get_Product(p.getName());
            invProduct.setQuantity(invProduct.getQuantity() + p.getQuantity());
            invProduct.updateQuantityInDB();
        }
        cart_items.clear();
    }


    public double get_total(){
        return cart_items.stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();

    }

    public List<Product> getCart_items(){
        return cart_items;
    }
}
