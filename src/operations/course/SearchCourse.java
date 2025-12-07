package operations.course;

import db.DB;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchCourse {
    public DefaultTableModel run(String q) throws SQLException {
        DefaultTableModel tm = new DefaultTableModel(
                new Object[]{"ID", "Course", "Instructor"}, 0
        );
        String sql = "SELECT c.CourseID, c.CourseName, i.InstructorName " +
                "FROM Course c " +
                "JOIN Instructor i ON i.InstructorID = c.InstructorID " +
                "WHERE c.CourseName LIKE ? OR i.InstructorName LIKE ? " +
                "ORDER BY c.CourseName, i.InstructorName";
        String p = (q == null || q.trim().isEmpty()) ? "%" : "%" + q.trim() + "%";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p);
            ps.setString(2, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tm.addRow(new Object[]{ rs.getInt(1), rs.getString(2), rs.getString(3) });
            }
        }
        return tm;
    }

}
