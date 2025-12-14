package operations.facility;

import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertDep {
    public int run(String departmentName) throws SQLException {
        if (departmentName == null || departmentName.trim().isEmpty())
            throw new IllegalArgumentException("Department name is required.");

        String sql = "INSERT INTO Department (DepartmentName) VALUES (?)";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, departmentName.trim());
            return ps.executeUpdate();
        }
    }
}
