package ui;

import operations.registration.AddRegistration;
import operations.registration.RegistrationList;
import operations.registration.RemoveRegistration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistrationPanel extends JPanel {
    private final JTextField txtSearch = new JTextField(20);
    private final JButton btnSearch = new JButton("Search");

    private final JComboBox<LabeledId> cbEmployee = new JComboBox<>();
    private final JComboBox<LabeledId> cbCourse = new JComboBox<>();

    private final JButton btnAdd = new JButton("Add");
    private final JButton btnRemove = new JButton("Remove");
    private final JTable table = new JTable();

    public RegistrationPanel() {
        setLayout(new BorderLayout(6, 6));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.add(new JLabel("Search:"));
        top.add(txtSearch);
        top.add(btnSearch);
        add(top, BorderLayout.SOUTH); // keep your layout

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.add(new JLabel("Employee:"));  bottom.add(cbEmployee);
        bottom.add(new JLabel("Course:"));    bottom.add(cbCourse);
        bottom.add(btnAdd);
        bottom.add(btnRemove);
        add(bottom, BorderLayout.NORTH); // keep your layout

        btnSearch.addActionListener(e -> refresh());
        txtSearch.addActionListener(e -> btnSearch.doClick());
        btnAdd.addActionListener(e -> onAdd());
        btnRemove.addActionListener(e -> onRemove());

        refresh(); // will also load combos now
    }

    private void reloadCombos() {
        cbEmployee.setModel(makeEmployeeCombo().getModel());
        cbCourse.setModel(makeCoursesWithInstructorCombo().getModel());
        if (cbEmployee.getItemCount() > 0) cbEmployee.setSelectedIndex(0);
        if (cbCourse.getItemCount() > 0) cbCourse.setSelectedIndex(0);
    }

    public void refresh() {
        reloadCombos();

        try {
            DefaultTableModel tm = new RegistrationList().run(txtSearch.getText());
            table.setModel(tm);
            table.setDefaultEditor(Object.class, null);
        } catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    private void onAdd() {
        LabeledId emp = (LabeledId) cbEmployee.getSelectedItem();
        LabeledId crs = (LabeledId) cbCourse.getSelectedItem();
        if (emp == null || emp.getId() <= 0) { showWarn("Choose an employee."); return; }
        if (crs == null || crs.getId() <= 0) { showWarn("Choose a course."); return; }

        try {
            int n = new AddRegistration().run(emp.getId(), crs.getId());
            if (n == 1) { showInfo("Added."); refresh(); }
        } catch (SQLException ex) {
            int code = ex.getErrorCode();
            if (code == 1062) showWarn("Already registered.");
            else if (code == 1452) showWarn("Invalid employee or course.");
            else showError("Database error: " + ex.getMessage());
        }
    }

    private void onRemove() {
        int v = table.getSelectedRow();
        if (v < 0) { showWarn("Select a row first."); return; }
        int r = table.convertRowIndexToModel(v);

        int empId = Integer.parseInt(table.getModel().getValueAt(r, 0).toString()); // EmployeeID
        int courseId = Integer.parseInt(table.getModel().getValueAt(r, 2).toString()); // CourseID
        String employeeName = table.getModel().getValueAt(r, 1).toString();
        String courseName = table.getModel().getValueAt(r, 3).toString();

        int ok = JOptionPane.showConfirmDialog(this,
                "Remove \"" + employeeName + "\" from \"" + courseName + "\"?",
                "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            int n = new RemoveRegistration().run(empId, courseId);
            if (n == 1) { showInfo("Removed."); refresh(); } else showInfo("Row not found.");
        } catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    // your combo makers kept as-is (no signature change)
    private static JComboBox<LabeledId> makeEmployeeCombo() {
        DefaultComboBoxModel<LabeledId> m = new DefaultComboBoxModel<>();
        m.addElement(new LabeledId(-1, "-- Select --"));
        String sql = "SELECT EmployeeID, EmployeeName FROM Employee ORDER BY EmployeeName";
        try (Connection c = db.DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) m.addElement(new LabeledId(rs.getInt(1), rs.getString(2)));
        } catch (SQLException ignore) {}
        return new JComboBox<>(m);
    }

    private static JComboBox<LabeledId> makeCoursesWithInstructorCombo() {
        DefaultComboBoxModel<LabeledId> m = new DefaultComboBoxModel<>();
        m.addElement(new LabeledId(-1, "-- Select --"));
        String sql = "SELECT c.CourseID, CONCAT(c.CourseName, ' — ', i.InstructorName) " +
                "FROM Course c JOIN Instructor i ON i.InstructorID = c.InstructorID " +
                "ORDER BY c.CourseName";
        try (Connection c = db.DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) m.addElement(new LabeledId(rs.getInt(1), rs.getString(2)));
        } catch (SQLException ignore) {}
        return new JComboBox<>(m);
    }

    private void showInfo(String m){ JOptionPane.showMessageDialog(this,m,"Info",JOptionPane.INFORMATION_MESSAGE); }
    private void showWarn(String m){ JOptionPane.showMessageDialog(this,m,"Warning",JOptionPane.WARNING_MESSAGE); }
    private void showError(String m){ JOptionPane.showMessageDialog(this,m,"Error",JOptionPane.ERROR_MESSAGE); }
}
