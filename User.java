import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User{
    private int id;
    private String name;
    private String phone;
    private String address;
    private String password;


    public User(int id, String name, String phone, String address, String password){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.password = password;
    }
    public User(String name, String phone, String address, String password) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.password = password;
    }

    public void savetodatabase(){
        try(Connection con = DBConnection.getConnection()){
            String mysql = "insert into users(name, phone, address, password) values(?,?,?,?)";
            PreparedStatement stm = con.prepareStatement(mysql, PreparedStatement.RETURN_GENERATED_KEYS);
            stm.setString(1, name);
            stm.setString(2, phone);
            stm.setString(3, address);
            stm.setString(4, password);
            stm.executeUpdate();
            ResultSet keys = stm.getGeneratedKeys();
            if (keys.next()) {
                this.id = keys.getInt(1);
            }
            System.out.println("User saved successfully");
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void updateInDatabase() {
        if (this.id == 0) {
            System.out.println("User not found in DB, saving as new...");
            savetodatabase();
            return;
        }
        String sql = "UPDATE users SET name=?, phone=?, address=?, password=? WHERE user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {

            stm.setString(1, name);
            stm.setString(2, phone);
            stm.setString(3, address);
            stm.setString(4, password);
            stm.setInt(5, id);
            stm.executeUpdate();

            System.out.println("User updated successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteFromDatabase() {
        if (this.id == 0) {
            System.out.println("User not found in DB");
            return;
        }

        String sql = "DELETE FROM users WHERE user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {

            stm.setInt(1, id);
            stm.executeUpdate();
            System.out.println("User deleted successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }
    public String getAddress() {
        return address;
    }
    public String getPassword() {
        return password;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String toString() {
        return "User{id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                "}";
    }
}


