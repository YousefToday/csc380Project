package operations.registration;

import db.DB;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistrationList {
    public DefaultTableModel run(String q) throws SQLException {
        DefaultTableModel tm = new DefaultTableModel(
                new Object[]{"EmployeeID", "Employee", "CourseID", "Course", "Instructor"}, 0
        );
        String sql = "SELECT r.EmployeeID, e.EmployeeName, r.CourseID, c.CourseName, i.InstructorName " +
                "FROM Registration r " +
                "JOIN Employee   e ON e.EmployeeID   = r.EmployeeID " +
                "JOIN Course     c ON c.CourseID     = r.CourseID " +
                "JOIN Instructor i ON i.InstructorID = c.InstructorID " +
                "WHERE e.EmployeeName LIKE ? OR c.CourseName LIKE ? OR i.InstructorName LIKE ? " +
                "ORDER BY e.EmployeeName, c.CourseName";
        String p = (q == null || q.trim().isEmpty()) ? "%" : "%" + q.trim() + "%";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tm.addRow(new Object[]{ rs.getInt(1), rs.getString(2),
                            rs.getInt(3), rs.getString(4), rs.getString(5) });
                }
            }
        }
        return tm;
    }

}
