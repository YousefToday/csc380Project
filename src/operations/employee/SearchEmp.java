package operations.employee;

import db.DB;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchEmp {
    public DefaultTableModel run(String q) throws SQLException {
        DefaultTableModel tm = new DefaultTableModel(new Object[]{"ID", "Name", "Email" , "Speciality"}, 0);
        String sql = "SELECT EmployeeID, EmployeeName, Email, Speciality " +
                "FROM Employee " +
                "WHERE EmployeeName LIKE ? OR Email LIKE ? OR Speciality LIKE ?" +
                "ORDER BY EmployeeName";
        String p = (q == null || q.trim().isEmpty()) ? "%" : "%" + q.trim() + "%";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tm.addRow(new Object[]{ rs.getInt(1), rs.getString(2), rs.getString(3) , rs.getString(4) });
            }
        }
        return tm;
    }


}
