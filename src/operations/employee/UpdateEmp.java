package operations.employee;

import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateEmp {
    public int run(int employeeId, String name, String email , String speciality) throws SQLException {
        if (employeeId <= 0)                        throw new IllegalArgumentException("Invalid Employee ID.");
        if (name == null || name.trim().isEmpty())  throw new IllegalArgumentException("Name is required.");
        if (email == null || !email.contains("@"))throw new IllegalArgumentException("Email is required.");
        if (speciality == null || speciality.trim().isEmpty()) throw new IllegalArgumentException("Speciality is required.");

        String sql = "UPDATE Employee SET EmployeeName = ?, Email = ? , Speciality = ? WHERE EmployeeID = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, email.trim());
            ps.setString(3, speciality.trim());
            ps.setInt(4, employeeId);
            return ps.executeUpdate();
        }
    }
}
