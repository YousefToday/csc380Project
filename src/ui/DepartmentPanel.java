package ui;

import operations.department.InsertDep;
import operations.department.RemoveDep;
import operations.department.SearchDep;
import operations.department.UpdateDep;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class DepartmentPanel extends JPanel {
    private final JTextField txtSearch = new JTextField(20);
    private final JButton btnSearch = new JButton("Search");
    private final JButton btnAdd = new JButton("Add");
    private final JButton btnEdit = new JButton("Edit");
    private final JButton btnDelete = new JButton("Delete");
    private final JTable table = new JTable();

    public DepartmentPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.add(new JLabel("Search:"));
        top.add(txtSearch);
        top.add(btnSearch);
        add(top, BorderLayout.SOUTH);

        // Center: table
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom: actions
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDelete);
        add(bottom, BorderLayout.NORTH);

        // Actions
        btnSearch.addActionListener(e -> refresh());
        txtSearch.addActionListener(e -> btnSearch.doClick()); // Enter triggers search
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());

        // First load
        refresh();
    }

    private void refresh() {
        try {
            DefaultTableModel tm = new SearchDep().run(txtSearch.getText());
            table.setModel(tm);
            table.setDefaultEditor(Object.class, null); // read-only
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    private void onAdd() {
        String name = JOptionPane.showInputDialog(this, "Department name:", "Add Department",
                JOptionPane.PLAIN_MESSAGE);
        if (name == null) return; // cancelled
        name = name.trim();
        if (name.isEmpty()) { showWarn("Department name is required."); return; }
        try {
            int n = new InsertDep().run(name);
            if (n == 1) { showInfo("Inserted."); refresh(); }
            else { showInfo("Nothing inserted."); }
        } catch (IllegalArgumentException iae) {
            showWarn(iae.getMessage());
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    private void onEdit() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) { showWarn("Select a row first."); return; }
        int modelRow = table.convertRowIndexToModel(viewRow);

        int id = Integer.parseInt(table.getModel().getValueAt(modelRow, 0).toString()); // col 0 = ID
        String currentName = table.getModel().getValueAt(modelRow, 1).toString();

        String name = JOptionPane.showInputDialog(this, "Department name:", currentName);
        if (name == null) return;
        name = name.trim();
        if (name.isEmpty()) { showWarn("Department name is required."); return; }

        try {
            int n = new UpdateDep().run(id, name);
            if (n == 1) { showInfo("Updated."); refresh(); }
            else { showInfo("Row not found."); }
        } catch (IllegalArgumentException iae) {
            showWarn(iae.getMessage());
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    private void onDelete() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) { showWarn("Select a row first."); return; }
        int modelRow = table.convertRowIndexToModel(viewRow);

        int id = Integer.parseInt(table.getModel().getValueAt(modelRow, 0).toString());
        String name = table.getModel().getValueAt(modelRow, 1).toString();

        int ok = JOptionPane.showConfirmDialog(this,
                "Delete department \"" + name + "\"?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            int n = new RemoveDep().run(id);
            if (n == 1) { showInfo("Deleted."); refresh(); }
            else { showInfo("Row not found."); }
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    // tiny dialogs
    private void showInfo(String m) { JOptionPane.showMessageDialog(this, m, "Info", JOptionPane.INFORMATION_MESSAGE); }
    private void showWarn(String m) { JOptionPane.showMessageDialog(this, m, "Warning", JOptionPane.WARNING_MESSAGE); }
    private void showError(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
}
