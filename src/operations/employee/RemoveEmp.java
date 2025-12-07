package operations.employee;

import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RemoveEmp {
    public int run(int employeeId) throws SQLException {
        if (employeeId <= 0) throw new IllegalArgumentException("Invalid Employee ID.");

        String sql = "DELETE FROM Employee WHERE EmployeeID = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId); // 1st ?
            return ps.executeUpdate();
        }
    }
}
