package ui;

import operations.instructor.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class InstructorPanel extends JPanel {
    private final JTextField txtSearch = new JTextField(20);
    private final JButton btnSearch = new JButton("Search");
    private final JButton btnAdd = new JButton("Add");
    private final JButton btnEdit = new JButton("Edit");
    private final JButton btnDelete = new JButton("Delete");
    private final JTable table = new JTable();
    private final CoursePanel coursePanel;
    private final RegistrationPanel regPanel;

    public InstructorPanel(CoursePanel coursePanel, RegistrationPanel regPanel) {
        this.regPanel = regPanel;
        this.coursePanel = coursePanel;

        setLayout(new BorderLayout(6, 6));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.add(new JLabel("Search:")); top.add(txtSearch); top.add(btnSearch);
        add(top, BorderLayout.SOUTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10,10));
        bottom.add(btnAdd); bottom.add(btnEdit); bottom.add(btnDelete);
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
            DefaultTableModel tm = new SearchIns().run(txtSearch.getText());
            table.setModel(tm);
            table.setDefaultEditor(Object.class, null);
        } catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    private void onAdd() {
        JTextField fName = new JTextField(20);
        JTextField fEmail = new JTextField(20);
        JComboBox<LabeledId> cbDept = makeDeptCombo();
        JComboBox<LabeledId> cbOffice = makeOfficeCombo();

        JPanel p = new JPanel(new GridLayout(0,2,8,8));
        p.add(new JLabel("Name:"));   p.add(fName);
        p.add(new JLabel("Email:"));  p.add(fEmail);
        p.add(new JLabel("Dept:"));   p.add(cbDept);
        p.add(new JLabel("Office:")); p.add(cbOffice);

        int ok = JOptionPane.showConfirmDialog(this, p, "Add Instructor",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        String name = safe(fName.getText());
        String email = safe(fEmail.getText());
        LabeledId d = (LabeledId) cbDept.getSelectedItem();
        LabeledId o = (LabeledId) cbOffice.getSelectedItem();
        if (name.isEmpty()) { showWarn("Name is required."); return; }
        if (!email.contains("@")) { showWarn("Valid email required."); return; }
        if (d == null || d.getId() <= 0) { showWarn("Choose a department."); return; }
        if (o == null || o.getId() <= 0) { showWarn("Choose an office."); return; }

        try {
            int n = new InsertIns().run(name, email, d.getId(), o.getId());
            if (n == 1) { showInfo("Inserted."); refresh(); }
        } catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    private void onEdit() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) { showWarn("Select a row first."); return; }
        int r = table.convertRowIndexToModel(viewRow);

        int id = Integer.parseInt(table.getModel().getValueAt(r, 0).toString());
        String curName = table.getModel().getValueAt(r, 1).toString();
        String curEmail = table.getModel().getValueAt(r, 2).toString();
        String curDeptLabel = table.getModel().getValueAt(r, 3).toString();
        String curOfficeLabel = String.valueOf(table.getModel().getValueAt(r, 4));

        JTextField fName = new JTextField(curName, 20);
        JTextField fEmail = new JTextField(curEmail, 20);
        JComboBox<LabeledId> cbDept = makeDeptCombo();
        JComboBox<LabeledId> cbOffice = makeOfficeCombo();
        preselectByLabel(cbDept, curDeptLabel);
        preselectByLabel(cbOffice, curOfficeLabel);

        JPanel p = new JPanel(new GridLayout(0,2,8,8));
        p.add(new JLabel("Name:"));   p.add(fName);
        p.add(new JLabel("Email:"));  p.add(fEmail);
        p.add(new JLabel("Dept:"));   p.add(cbDept);
        p.add(new JLabel("Office:")); p.add(cbOffice);

        int ok = JOptionPane.showConfirmDialog(this, p, "Edit Instructor",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        String name = safe(fName.getText());
        String email = safe(fEmail.getText());
        LabeledId d = (LabeledId) cbDept.getSelectedItem();
        LabeledId o = (LabeledId) cbOffice.getSelectedItem();
        if (name.isEmpty()) { showWarn("Name is required."); return; }
        if (!email.contains("@")) { showWarn("Valid email required."); return; }
        if (d == null || d.getId() <= 0) { showWarn("Choose a department."); return; }
        if (o == null || o.getId() <= 0) { showWarn("Choose an office."); return; }

        try {
            int n = new UpdateIns().run(id, name, email, d.getId(), o.getId());
            if (n == 1) { showInfo("Updated."); refresh(); this.coursePanel.refresh(); this.regPanel.refresh(); } else showInfo("Row not found.");
        } catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    private void onDelete() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) { showWarn("Select a row first."); return; }
        int r = table.convertRowIndexToModel(viewRow);

        int id = Integer.parseInt(table.getModel().getValueAt(r, 0).toString());
        String name = table.getModel().getValueAt(r, 1).toString();

        int ok = JOptionPane.showConfirmDialog(this, "Delete instructor \"" + name + "\"?",
                "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            int n = new RemoveIns().run(id);
            if (n == 1) { showInfo("Deleted."); refresh(); } else showInfo("Row not found.");
        } catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    // ---- helpers (keep it simple) ----
    private JComboBox<LabeledId> makeDeptCombo() {
        DefaultComboBoxModel<LabeledId> m = new DefaultComboBoxModel<>();
        m.addElement(new LabeledId(-1, "-- Select --"));
        String sql = "SELECT DepartmentID, DepartmentName FROM Department ORDER BY DepartmentName";
        try (Connection c = db.DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) m.addElement(new LabeledId(rs.getInt(1), rs.getString(2)));
        } catch (SQLException ignore) {}
        return new JComboBox<>(m);
    }

    private JComboBox<LabeledId> makeOfficeCombo() {
        DefaultComboBoxModel<LabeledId> m = new DefaultComboBoxModel<>();
        m.addElement(new LabeledId(-1, "-- Select --"));
        String sql = "SELECT OfficeID, FloorNumber FROM Office ORDER BY FloorNumber";
        try (Connection c = db.DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) m.addElement(new LabeledId(rs.getInt(1), String.valueOf(rs.getInt(2))));
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
