package operations.facility;

import db.DB;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchDep {
    public DefaultTableModel run(String q) throws SQLException {
        DefaultTableModel tm = new DefaultTableModel(new Object[]{"ID", "Department"}, 0);
        String sql = "SELECT DepartmentID, DepartmentName " +
                "FROM Department " +
                "WHERE DepartmentName LIKE ? " +
                "ORDER BY DepartmentName";
        String p = (q == null || q.trim().isEmpty()) ? "%" : "%" + q.trim() + "%";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tm.addRow(new Object[]{ rs.getInt(1), rs.getString(2) });
            }
        }
        return tm;
    }
}
