package db;

import ui.LabeledId;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Vector;

public class DAO {

    public DefaultTableModel loadTable(String sql, Object... params) throws SQLException {
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            bind(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int cols = md.getColumnCount();

                Vector<String> colNames = new Vector<>();
                for (int i = 1; i <= cols; i++) colNames.add(md.getColumnLabel(i));

                Vector<Vector<Object>> data = new Vector<>();
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    for (int i = 1; i <= cols; i++) row.add(rs.getObject(i));
                    data.add(row);
                }

                return new DefaultTableModel(data, colNames) {
                    @Override public boolean isCellEditable(int row, int column) { return false; }
                };
            }
        }
    }

    public int execUpdate(String sql, Object... params) throws SQLException {
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        }
    }

    public void loadCombo(JComboBox<LabeledId> combo, String sql, Object... params) throws SQLException {
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            bind(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                DefaultComboBoxModel<LabeledId> model = new DefaultComboBoxModel<>();
                while (rs.next()) {
                    model.addElement(new LabeledId(rs.getInt(1), rs.getString(2)));
                }
                combo.setModel(model);
            }
        }
    }

    private void bind(PreparedStatement ps, Object... params) throws SQLException {
        if (params == null) return;
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    public static void selectById(JComboBox<LabeledId> combo, int id) {
        ComboBoxModel<LabeledId> m = combo.getModel();
        for (int i = 0; i < m.getSize(); i++) {
            LabeledId it = m.getElementAt(i);
            if (it != null && it.getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    public static Integer parseIntRequired(JTextField tf, String name) {
        String s = tf.getText().trim();
        if (s.isEmpty()) throw new IllegalArgumentException(name + " is required (manual id).");
        return Integer.parseInt(s);
    }

    public static Integer parseIntNullable(JTextField tf) {
        String s = tf.getText().trim();
        if (s.isEmpty()) return null;
        return Integer.parseInt(s);
    }

    public static Double parseDoubleNullable(JTextField tf) {
        String s = tf.getText().trim();
        if (s.isEmpty()) return null;
        return Double.parseDouble(s);
    }

    public static String textOrNull(JTextField tf) {
        String s = tf.getText().trim();
        return s.isEmpty() ? null : s;
    }

    public static String textRequired(JTextField tf, String name) {
        String s = tf.getText().trim();
        if (s.isEmpty()) throw new IllegalArgumentException(name + " is required.");
        return s;
    }
}
