package operations.course;

import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateCourse {
    public int run(int courseId, String courseName, int instructorId) throws SQLException {
        if (courseId <= 0) throw new IllegalArgumentException("Invalid Course ID.");
        if (courseName == null || courseName.trim().isEmpty())
            throw new IllegalArgumentException("Course name is required.");
        if (instructorId <= 0)
            throw new IllegalArgumentException("Choose an instructor.");

        String sql = "UPDATE Course SET CourseName = ?, InstructorID = ? WHERE CourseID = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, courseName.trim());
            ps.setInt(2, instructorId);
            ps.setInt(3, courseId);
            return ps.executeUpdate();
        }
    }
}
