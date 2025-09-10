import java.sql.*;
import java.util.Scanner;

public class ColorInsert {
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection();
             Scanner sc = new Scanner(System.in)) {

            // Ask for model_id
            System.out.print("Enter Model ID: ");
            int modelId = sc.nextInt();
            sc.nextLine(); // clear buffer

            // ✅ Fetch model name
            String checkModel = "SELECT m.model_name, b.brand_name FROM CarModels m JOIN CarBrands b ON m.brand_id = b.brand_id WHERE m.model_id = ?";
            PreparedStatement psCheck = con.prepareStatement(checkModel);
            psCheck.setInt(1, modelId);
            ResultSet rsCheck = psCheck.executeQuery();

            if (rsCheck.next()) {
                String modelName = rsCheck.getString("model_name");
                String brandName = rsCheck.getString("brand_name");
                System.out.println(brandName + " | Model: " + modelName);
            } else {
                System.out.println("❌ No model found with ID: " + modelId);
                return;
            }

            // ---------- Add Colors ----------
            String choice = "yes";
            while (choice.equalsIgnoreCase("yes")) {
                System.out.print("Enter Color Name: ");
                String colorName = sc.nextLine();

                // Check if color already exists for this model
                String checkColor = "SELECT color_id FROM Colors WHERE model_id = ? AND color_name = ?";
                PreparedStatement psColorCheck = con.prepareStatement(checkColor);
                psColorCheck.setInt(1, modelId);
                psColorCheck.setString(2, colorName);
                ResultSet rsColorCheck = psColorCheck.executeQuery();

                if (rsColorCheck.next()) {
                    System.out.println("Color '" + colorName + "' already exists for this model.");
                } else {
                    String insertColor = "INSERT INTO Colors (model_id, color_name) VALUES (?, ?)";
                    PreparedStatement psInsert = con.prepareStatement(insertColor, Statement.RETURN_GENERATED_KEYS);
                    psInsert.setInt(1, modelId);
                    psInsert.setString(2, colorName);
                    psInsert.executeUpdate();

                    ResultSet rsColor = psInsert.getGeneratedKeys();
                    if (rsColor.next()) {
                        int colorId = rsColor.getInt(1);
                        System.out.println("Inserted the Color");
                    }
                }

                System.out.print("Do you want to add another color? (yes/no): ");
                choice = sc.nextLine();
            }

        } catch (SQLException e) {
            System.out.println("❌ Database Error: " + e.getMessage());
        }
    }
}