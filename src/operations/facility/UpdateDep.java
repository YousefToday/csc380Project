package operations.facility;

import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateDep {
    public int run(int departmentId, String departmentName) throws SQLException {
        if (departmentId <= 0) throw new IllegalArgumentException("Invalid Department ID.");
        if (departmentName == null || departmentName.trim().isEmpty())
            throw new IllegalArgumentException("Department name is required.");

        String sql = "UPDATE Department SET DepartmentName = ? WHERE DepartmentID = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, departmentName.trim());
            ps.setInt(2, departmentId);
            return ps.executeUpdate();
        }
    }
}
