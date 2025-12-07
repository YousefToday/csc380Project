package operations.employee;

import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertEmp {
    public int run(String name, String email , String speciality) throws SQLException {
        if (name == null || name.trim().isEmpty())   throw new IllegalArgumentException("Name is required.");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Email is required.");
        if (speciality == null || speciality.trim().isEmpty())throw new IllegalArgumentException("Speciality is required");

        String sql = "INSERT INTO Employee (EmployeeName, Email , Speciality) VALUES (?, ?, ?)";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, email.trim());
            ps.setString(3, speciality.trim());
            return ps.executeUpdate();
        }
    }
}
