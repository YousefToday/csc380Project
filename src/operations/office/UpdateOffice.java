package operations.office;

import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateOffice {
    public int run(int officeId, int floorNumber) throws SQLException {
        if (officeId <= 0) throw new IllegalArgumentException("Invalid Office ID.");

        String sql = "UPDATE Office SET FloorNumber = ? WHERE OfficeID = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, floorNumber);
            ps.setInt(2, officeId);
            return ps.executeUpdate();
        }
    }
}
