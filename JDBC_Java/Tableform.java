    import java.sql.*;
    import javax.swing.*;
    import javax.swing.table.DefaultTableModel;

    public class Tableform {
        public static void main(String[] args) throws SQLException{
            try {
                Connection con = DBConnection.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM cust");

                DefaultTableModel td = new DefaultTableModel();

                td.addColumn("ID");
                td.addColumn("Name");
                td.addColumn("Salary");

                while(rs.next()){
                    td.addRow(new Object[]{ 
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("salary")
                    });
                }

                JTable table = new JTable(td);
                JScrollPane sp = new JScrollPane(table);

                JFrame frame = new JFrame("Customer Data");
                frame.add(sp);
                frame.setSize(400, 200);
                frame.setVisible(true);

            } catch (SQLException e) {
                System.out.println(e);
            }
        }
    }
