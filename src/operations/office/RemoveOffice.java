package operations.office;

import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RemoveOffice {
    public int run(int officeId) throws SQLException {
        if (officeId <= 0) throw new IllegalArgumentException("Invalid Office ID.");

        String sql = "DELETE FROM Office WHERE OfficeID = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, officeId);
            return ps.executeUpdate();
        }
    }
}
