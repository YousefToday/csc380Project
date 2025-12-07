package operations.registration;

import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddRegistration {
    public int run(int employeeId, int courseId) throws SQLException {
        if (employeeId <= 0) throw new IllegalArgumentException("Choose an employee.");
        if (courseId <= 0)   throw new IllegalArgumentException("Choose a course.");

        String sql = "INSERT INTO Registration (EmployeeID, CourseID) VALUES (?, ?)";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId); // 1st ?
            ps.setInt(2, courseId);   // 2nd ?
            return ps.executeUpdate();
        }
    }
}
