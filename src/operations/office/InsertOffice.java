package operations.office;

import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertOffice {
    public int run(int floorNumber) throws SQLException {
        String sql = "INSERT INTO Office (FloorNumber) VALUES (?)";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, floorNumber);
            return ps.executeUpdate();
        }
    }
}
