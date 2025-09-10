import java.sql.*;
import java.util.Scanner;

public class ModelInsert {
    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection();
             Scanner sc = new Scanner(System.in)) {

            // ---------- Show Available Brands ----------
            System.out.println("📌 Available Brands:");
            String fetchBrands = "SELECT brand_id, brand_name FROM CarBrands ORDER BY brand_id  ASC";
            Statement st = con.createStatement();
            ResultSet rsBrands = st.executeQuery(fetchBrands);
            
            while (rsBrands.next()) {
                System.out.println(rsBrands.getInt("brand_id") + " - " + rsBrands.getString("brand_name"));
            }

            
            // Ask for existing brand_id
            System.out.print("Enter Brand ID: ");
            int brandId = sc.nextInt();
            sc.nextLine(); // clear buffer

            // ✅ Fetch brand name before proceeding
            String checkBrand = "SELECT brand_name FROM CarBrands WHERE brand_id = ?";
            PreparedStatement psCheck = con.prepareStatement(checkBrand);
            psCheck.setInt(1, brandId);
            ResultSet rsCheck = psCheck.executeQuery();

            if (rsCheck.next()) {
                String brandName = rsCheck.getString("brand_name");
                System.out.println("Brand - " + brandName);
            } else {
                System.out.println("❌ No brand found with ID: " + brandId);
                return; // stop program
            }

            System.out.println("\n📌 Available Models for this Brand:");
            String fetchModels = "SELECT model_name FROM CarModels WHERE brand_id = ? ORDER BY model_id ASC";
            PreparedStatement psFetchModels = con.prepareStatement(fetchModels);
            psFetchModels.setInt(1, brandId);
            ResultSet rsModels = psFetchModels.executeQuery();

            boolean hasModels = false;
            while (rsModels.next()) {
                hasModels = true;
                System.out.println(rsModels.getString("model_name"));
            }
            if (!hasModels) {
                System.out.println("⚠️ No models found yet for this brand.");
            }

            // ---------- Check if Model Already Exists ----------
            System.out.print("Enter Car Model: ");
            String modelName = sc.nextLine();

            String checkModel = "SELECT model_id FROM CarModels WHERE brand_id = ? AND model_name = ?";
            PreparedStatement psModelCheck = con.prepareStatement(checkModel);
            psModelCheck.setInt(1, brandId);
            psModelCheck.setString(2, modelName);
            ResultSet rsModelCheck = psModelCheck.executeQuery();

            int modelId = 0;
            if (rsModelCheck.next()) {
                // ✅ Model already exists
                modelId = rsModelCheck.getInt("model_id");
                System.out.println("Model '" + modelName + "' already exists with model_id = " + modelId);
            } else {
                // ✅ Insert new model
                String insertModel = "INSERT INTO CarModels (brand_id, model_name) VALUES (?, ?)";
                PreparedStatement ps = con.prepareStatement(insertModel, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, brandId);
                ps.setString(2, modelName);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    modelId = rs.getInt(1);
                    System.out.println("Inserted Model ");
                }
            }

            // ---------- Insert Variants ----------
            System.out.print("Do you want to add a variant for this model? (yes/no): ");
            String choice = sc.nextLine();

            while (choice.equalsIgnoreCase("yes")) {
                System.out.print("Enter Variant Name: ");
                String variantName = sc.nextLine();
                System.out.print("Enter Variant Price: ");
                double price = sc.nextDouble();
                sc.nextLine(); // clear buffer

                String insertVariant = "INSERT INTO CarVariants (model_id, variant_name, price) VALUES (?, ?, ?)";
                PreparedStatement psVar = con.prepareStatement(insertVariant);
                psVar.setInt(1, modelId);
                psVar.setString(2, variantName);
                psVar.setDouble(3, price);
                psVar.executeUpdate();

                System.out.println("✅ Inserted Variant ");

                System.out.print("Do you want to add another variant? (yes/no): ");
                choice = sc.nextLine();
            }

        } catch (SQLException e) {
            System.out.println("❌ Database Error: " + e.getMessage());
        }
    }
}