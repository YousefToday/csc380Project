package operations.instructor;

import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RemoveIns {
    public int run(int instructorId) throws SQLException {
        if (instructorId <= 0) throw new IllegalArgumentException("Invalid Instructor ID.");

        String sql = "DELETE FROM Instructor WHERE InstructorID = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            return ps.executeUpdate();
        }
    }
}
