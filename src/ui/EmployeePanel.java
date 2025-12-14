package ui;

import operations.employee.InsertEmp;
import operations.employee.RemoveEmp;
import operations.employee.SearchEmp;
import operations.employee.UpdateEmp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class EmployeePanel extends JPanel {
    private final JTextField txtSearch = new JTextField(20);
    private final JButton btnSearch = new JButton("Search");
    private final JButton btnAdd = new JButton("Add");
    private final JButton btnEdit = new JButton("Edit");
    private final JButton btnDelete = new JButton("Delete");
    private final JTable table = new JTable();
    private final RegistrationPanel regPanel;

    public EmployeePanel(RegistrationPanel regPanel) {
        this.regPanel = regPanel;

        setLayout(new BorderLayout(6, 6));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.add(new JLabel("Search:")); top.add(txtSearch); top.add(btnSearch);
        add(top, BorderLayout.SOUTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.add(btnAdd); bottom.add(btnEdit); bottom.add(btnDelete);
        add(bottom, BorderLayout.NORTH);

        btnSearch.addActionListener(e -> refresh());
        txtSearch.addActionListener(e -> btnSearch.doClick());
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());

        refresh();
    }

    private void refresh() {
        try {
            DefaultTableModel tm = new SearchEmp().run(txtSearch.getText());
            table.setModel(tm);
            table.setDefaultEditor(Object.class, null);
        } catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    private void onAdd() {
        JTextField fName = new JTextField(20);
        JTextField fEmail = new JTextField(20);
        JTextField fSpec = new JTextField(20);

        JPanel p = new JPanel(new GridLayout(0,2,8,8));
        p.add(new JLabel("Name:"));  p.add(fName);
        p.add(new JLabel("Email:")); p.add(fEmail);
        p.add(new JLabel("Speciality:")); p.add(fSpec);

        int ok = JOptionPane.showConfirmDialog(this, p, "Add Employee",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        String name = safe(fName.getText());
        String email = safe(fEmail.getText());
        String spec = safe(fSpec.getText());

        if (name.isEmpty()) { showWarn("Name is required."); return; }
        if (spec.isEmpty()) { showWarn("Speciality is required."); return; }
        if (!email.contains("@")) { showWarn("Valid email required."); return; }

        try {
            int n = new InsertEmp().run(name, email , spec);
            if (n == 1) { showInfo("Inserted."); refresh(); this.regPanel.reloadCombos(); }
        } catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    private void onEdit() {
        int v = table.getSelectedRow();
        if (v < 0) { showWarn("Select a row first."); return; }
        int r = table.convertRowIndexToModel(v);

        int id = Integer.parseInt(table.getModel().getValueAt(r, 0).toString());
        String curName = table.getModel().getValueAt(r, 1).toString();
        String curEmail = table.getModel().getValueAt(r, 2).toString();
        String curSpec = table.getModel().getValueAt(r, 3).toString();

        JTextField fName = new JTextField(curName, 20);
        JTextField fEmail = new JTextField(curEmail, 20);
        JTextField fSpec = new JTextField(curSpec, 20);


        JPanel p = new JPanel(new GridLayout(0,2,8,8));
        p.add(new JLabel("Name:"));  p.add(fName);
        p.add(new JLabel("Email:")); p.add(fEmail);
        p.add(new JLabel("Speciality:")); p.add(fSpec);

        int ok = JOptionPane.showConfirmDialog(this, p, "Edit Employee",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;

        String name = safe(fName.getText());
        String email = safe(fEmail.getText());
        String spec = safe(fSpec.getText());

        if (name.isEmpty()) { showWarn("Name is required."); return; }
        if (!email.contains("@")) { showWarn("Valid email required."); return; }
        if (spec.isEmpty()) { showWarn("Speciality is required."); return; }

        try {
            int n = new UpdateEmp().run(id, name, email , spec);
            if (n == 1) { showInfo("Updated."); refresh(); this.regPanel.refresh(); } else showInfo("Row not found.");
        } catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    private void onDelete() {
        int v = table.getSelectedRow();
        if (v < 0) { showWarn("Select a row first."); return; }
        int r = table.convertRowIndexToModel(v);

        int id = Integer.parseInt(table.getModel().getValueAt(r, 0).toString());
        String name = table.getModel().getValueAt(r, 1).toString();

        int ok = JOptionPane.showConfirmDialog(this, "Delete employee \"" + name + "\"?",
                "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            int n = new RemoveEmp().run(id);
            if (n == 1) { showInfo("Deleted."); refresh(); this.regPanel.reloadCombos(); } else showInfo("Row not found.");
        } catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    private String safe(String s){ return s==null? "" : s.trim(); }
    private void showInfo(String m){ JOptionPane.showMessageDialog(this,m,"Info",JOptionPane.INFORMATION_MESSAGE); }
    private void showWarn(String m){ JOptionPane.showMessageDialog(this,m,"Warning",JOptionPane.WARNING_MESSAGE); }
    private void showError(String m){ JOptionPane.showMessageDialog(this,m,"Error",JOptionPane.ERROR_MESSAGE); }
}
