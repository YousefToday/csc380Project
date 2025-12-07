package operations.course;

import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RemoveCourse {
    public int run(int courseId) throws SQLException {
        if (courseId <= 0) throw new IllegalArgumentException("Invalid Course ID.");

        String sql = "DELETE FROM Course WHERE CourseID = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            return ps.executeUpdate();
        }
    }
}
