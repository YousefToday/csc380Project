package operations.instructor;

import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateIns {
        public int run(int instructorId, String name, String email, int departmentId, int officeId) throws SQLException {
            if (instructorId <= 0)                         throw new IllegalArgumentException("Invalid Instructor ID.");
            if (name == null || name.trim().isEmpty())     throw new IllegalArgumentException("Name is required.");
            if (email == null || email.trim().isEmpty())   throw new IllegalArgumentException("Email is required.");
            if (!email.contains("@"))                      throw new IllegalArgumentException("Invalid email.");
            if (departmentId <= 0)                         throw new IllegalArgumentException("Choose a department.");
            if (officeId <= 0)                             throw new IllegalArgumentException("Choose an office.");

            String sql = "UPDATE Instructor SET InstructorName = ?, Email = ?, DepartmentID = ?, OfficeID = ? WHERE InstructorID = ?";
            try (Connection c = DB.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, name.trim());     // 1st ?
                ps.setString(2, email.trim());    // 2nd ?
                ps.setInt(3, departmentId);       // 3rd ?
                ps.setInt(4, officeId);           // 4th ?
                ps.setInt(5, instructorId);       // 5th ?
                return ps.executeUpdate();
            }
        }
}
