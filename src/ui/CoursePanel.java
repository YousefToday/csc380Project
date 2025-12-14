package ui;

import operations.course.InsertCourse;
import operations.course.RemoveCourse;
import operations.course.SearchCourse;
import operations.course.UpdateCourse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CoursePanel extends JPanel {
    private final JTextField txtSearch = new JTextField(20);
    private final JButton btnSearch = new JButton("Search");
    private final JButton btnAdd = new JButton("Add");
    private final JButton btnEdit = new JButton("Edit");
    private final JButton btnDelete = new JButton("Delete");
    private final JTable table = new JTable();
    private final RegistrationPanel regPanel;

    public CoursePanel(RegistrationPanel regPanel) {
        this.regPanel = regPanel;

        setLayout(new BorderLayout(6, 6));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.add(new JLabel("Search:"));
        top.add(txtSearch);
        top.add(btnSearch);
        add(top, BorderLayout.SOUTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDelete);
        add(bottom, BorderLayout.NORTH);

        btnSearch.addActionListener(e -> refresh());
        txtSearch.addActionListener(e -> btnSearch.doClick());
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());

        refresh();
    }

    public void refresh() {
        try {
            DefaultTableModel tm = new SearchCourse().run(txtSearch.getText());
            table.setModel(tm);
            table.setDefaultEditor(Object.class, null);
        } catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    private void onAdd() {
        JTextField fCourse = new JTextField(20);
        JComboBox<LabeledId> cbInstructor = makeInstructorCombo();

        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.add(new JLabel("Course:"));     p.add(fCourse);
        p.add(new JLabel("Instructor:")); p.add(cbInstructor);

        int ok = JOptionPane.showConfirmDialog(this, p, "Add Course",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        String name = safe(fCourse.getText());
        LabeledId ins = (LabeledId) cbInstructor.getSelectedItem();
        if (name.isEmpty()) { showWarn("Course name is required."); return; }
        if (ins == null || ins.getId() <= 0) { showWarn("Choose an instructor."); return; }

        try {
            int n = new InsertCourse().run(name, ins.getId());
            if (n == 1) { showInfo("Inserted."); refresh(); this.regPanel.reloadCombos(); }
        } catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    private void onEdit() {
        int v = table.getSelectedRow();
        if (v < 0) { showWarn("Select a row first."); return; }
        int r = table.convertRowIndexToModel(v);

        int id = Integer.parseInt(table.getModel().getValueAt(r, 0).toString());
        String curCourse = table.getModel().getValueAt(r, 1).toString();
        String curInstructorLabel = table.getModel().getValueAt(r, 2).toString();

        JTextField fCourse = new JTextField(curCourse, 20);
        JComboBox<LabeledId> cbInstructor = makeInstructorCombo();
        preselectByLabel(cbInstructor, curInstructorLabel);

        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.add(new JLabel("Course:"));     p.add(fCourse);
        p.add(new JLabel("Instructor:")); p.add(cbInstructor);

        int ok = JOptionPane.showConfirmDialog(this, p, "Edit Course",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        String name = safe(fCourse.getText());
        LabeledId ins = (LabeledId) cbInstructor.getSelectedItem();
        if (name.isEmpty()) { showWarn("Course name is required."); return; }
        if (ins == null || ins.getId() <= 0) { showWarn("Choose an instructor."); return; }

        try {
            int n = new UpdateCourse().run(id, name, ins.getId());
            if (n == 1) { showInfo("Updated."); refresh(); this.regPanel.refresh(); } else showInfo("Row not found.");
        } catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    private void onDelete() {
        int v = table.getSelectedRow();
        if (v < 0) { showWarn("Select a row first."); return; }
        int r = table.convertRowIndexToModel(v);

        int id = Integer.parseInt(table.getModel().getValueAt(r, 0).toString());
        String name = table.getModel().getValueAt(r, 1).toString();

        int ok = JOptionPane.showConfirmDialog(this, "Delete course \"" + name + "\"?",
                "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            int n = new RemoveCourse().run(id);
            if (n == 1) { showInfo("Deleted."); refresh(); this.regPanel.reloadCombos(); } else showInfo("Row not found.");
        } catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    private JComboBox<LabeledId> makeInstructorCombo() {
        DefaultComboBoxModel<LabeledId> m = new DefaultComboBoxModel<>();
        m.addElement(new LabeledId(-1, "-- Select --"));
        String sql = "SELECT InstructorID, InstructorName FROM Instructor ORDER BY InstructorName";
        try (Connection c = db.DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) m.addElement(new LabeledId(rs.getInt(1), rs.getString(2)));
        } catch (SQLException ignore) {}
        return new JComboBox<>(m);
    }

    private void preselectByLabel(JComboBox<LabeledId> combo, String label) {
        ComboBoxModel<LabeledId> m = combo.getModel();
        for (int i = 0; i < m.getSize(); i++) {
            LabeledId it = m.getElementAt(i);
            if (it != null && label.equals(String.valueOf(it.getLabel()))) {
                combo.setSelectedIndex(i); return;
            }
        }
    }

    private String safe(String s){ return s==null? "" : s.trim(); }
    private void showInfo(String m){ JOptionPane.showMessageDialog(this,m,"Info",JOptionPane.INFORMATION_MESSAGE); }
    private void showWarn(String m){ JOptionPane.showMessageDialog(this,m,"Warning",JOptionPane.WARNING_MESSAGE); }
    private void showError(String m){ JOptionPane.showMessageDialog(this,m,"Error",JOptionPane.ERROR_MESSAGE); }
}
