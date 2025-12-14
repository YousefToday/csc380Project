package operations.facility;

import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RemoveDep {
    public int run(int departmentId) throws SQLException {
        if (departmentId <= 0) throw new IllegalArgumentException("Invalid Department ID.");

        String sql = "DELETE FROM Department WHERE DepartmentID = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            return ps.executeUpdate();
        }
    }
}
