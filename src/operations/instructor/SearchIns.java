package operations.instructor;

import db.DB;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchIns {
    public DefaultTableModel run(String q) throws SQLException {
        DefaultTableModel tm = new DefaultTableModel(
                new Object[]{"ID", "Name", "Email", "Department", "Floor"}, 0
        );
        String sql = "SELECT i.InstructorID, i.InstructorName, i.Email, d.DepartmentName, o.FloorNumber " +
                "FROM Instructor i " +
                "JOIN Department d ON d.DepartmentID = i.DepartmentID " +
                "JOIN Office     o ON o.OfficeID     = i.OfficeID " +
                "WHERE i.InstructorName LIKE ? OR i.Email LIKE ? OR d.DepartmentName LIKE ? " +
                "ORDER BY i.InstructorName";
        String p = (q == null || q.trim().isEmpty()) ? "%" : "%" + q.trim() + "%";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tm.addRow(new Object[]{ rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getInt(5) });
                }
            }
        }
        return tm;
    }
}
