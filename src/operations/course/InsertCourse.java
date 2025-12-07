package operations.course;

import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertCourse {
    public int run(String courseName, int instructorId) throws SQLException {
        if (courseName == null || courseName.trim().isEmpty())
            throw new IllegalArgumentException("Course name is required.");
        if (instructorId <= 0)
            throw new IllegalArgumentException("Choose an instructor.");

        String sql = "INSERT INTO Course (CourseName, InstructorID) VALUES (?, ?)";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, courseName.trim()); // 1st ?
            ps.setInt(2, instructorId);         // 2nd ?
            return ps.executeUpdate();
        }
    }
}
