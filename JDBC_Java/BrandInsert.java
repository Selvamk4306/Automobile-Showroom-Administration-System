import java.sql.*;
import java.util.Scanner;

public class BrandInsert {
    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection();
             Scanner sc = new Scanner(System.in)) {

            System.out.print("Enter Car Brand: ");
            String brandName = sc.nextLine();

            String insertBrand = "INSERT INTO CarBrands (brand_name) VALUES (?)";
            PreparedStatement ps = con.prepareStatement(insertBrand, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, brandName);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                System.out.println("Inserted the Brand ");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
