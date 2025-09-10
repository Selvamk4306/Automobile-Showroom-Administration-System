import java.sql.*;
import java.util.Scanner;

public class CarSelection {
    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection();
             Scanner sc = new Scanner(System.in)) {

            // ---------- Step 1: Show Brands ----------
            System.out.println("Available Brands:");
            String sqlBrands = "SELECT brand_id, brand_name FROM CarBrands ORDER BY brand_id ASC";
            Statement stBrand = con.createStatement();
            ResultSet rsBrand = stBrand.executeQuery(sqlBrands);
            while (rsBrand.next()) {
                System.out.println(rsBrand.getInt("brand_id") + ". " + rsBrand.getString("brand_name"));
            }

            System.out.print("Select Brand ID: ");
            int brandId = sc.nextInt();
            sc.nextLine();

            // ---------- Step 2: Show Models ----------
            String sqlModels = "SELECT model_id, model_name FROM CarModels WHERE brand_id = ?";
            PreparedStatement psModels = con.prepareStatement(sqlModels);
            psModels.setInt(1, brandId);
            ResultSet rsModels = psModels.executeQuery();

            System.out.println("\nAvailable Models:");
            while (rsModels.next()) {
                System.out.println(rsModels.getInt("model_id") + ". " + rsModels.getString("model_name"));
            }

            System.out.print("Select Model ID: ");
            int modelId = sc.nextInt();
            sc.nextLine();

            // ---------- Step 3: Show Variants ----------
            String sqlVariants = """
                SELECT v.variant_id, v.variant_name, v.price
                FROM CarVariants v
                WHERE v.model_id = ?
                """;
            PreparedStatement psVariants = con.prepareStatement(sqlVariants);
            psVariants.setInt(1, modelId);
            ResultSet rsVariants = psVariants.executeQuery();

            System.out.println("\nAvailable Variants:");
            while (rsVariants.next()) {
                System.out.println(rsVariants.getInt("variant_id") + ". " +
                        rsVariants.getString("variant_name") + " (₹" +
                        rsVariants.getDouble("price") + ")");
            }

            System.out.print("Select Variant ID: ");
            int variantId = sc.nextInt();
            sc.nextLine();

            // ---------- Step 4: Show Colors (linked to model_id) ----------
            String sqlColors = """
                SELECT c.color_id, c.color_name
                FROM Colors c
                WHERE c.model_id = ?
                ORDER BY c.color_id ASC
                """;
            PreparedStatement psColors = con.prepareStatement(sqlColors);
            psColors.setInt(1, modelId);   // ✅ use modelId since colors are linked to models
            ResultSet rsColors = psColors.executeQuery();

            System.out.println("\nAvailable Colors:");
            boolean hasColors = false;
            while (rsColors.next()) {
                hasColors = true;
                System.out.println(rsColors.getInt("color_id") + ". " + rsColors.getString("color_name"));
            }
            if (!hasColors) {
                System.out.println("⚠️ No colors found for this model!");
            }

            System.out.print("Select Color ID: ");
            int colorId = sc.nextInt();
            sc.nextLine();

            // ---------- Final Selection ----------
            String sqlFinal = """
                SELECT b.brand_name, m.model_name, v.variant_name, c.color_name, v.price
                FROM CarBrands b
                JOIN CarModels m ON b.brand_id = m.brand_id
                JOIN CarVariants v ON m.model_id = v.model_id
                JOIN Colors c ON m.model_id = c.model_id
                WHERE v.variant_id = ? AND c.color_id = ?
                """;
            PreparedStatement psFinal = con.prepareStatement(sqlFinal);
            psFinal.setInt(1, variantId);
            psFinal.setInt(2, colorId);
            ResultSet rsFinal = psFinal.executeQuery();

            if (rsFinal.next()) {
                System.out.println("\n✅ Final Selection: " +
                        rsFinal.getString("brand_name") + ", " +
                        rsFinal.getString("model_name") + ", " +
                        rsFinal.getString("variant_name") + ", (" +
                        rsFinal.getString("color_name") + ") - Price: ₹" +
                        rsFinal.getDouble("price"));
            } else {
                System.out.println("❌ Selection not found.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Database Error: " + e.getMessage());
        }
    }
}