import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection c = DB.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT 1");
            ResultSet rs = ps.executeQuery();
            System.out.println(rs.next() ? "DB CONNECTED" : "DB FAIL");
            rs.close(); ps.close(); c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}