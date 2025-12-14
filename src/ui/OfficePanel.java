package ui;

import operations.office.InsertOffice;
import operations.office.RemoveOffice;
import operations.office.SearchOffice;
import operations.office.UpdateOffice;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class OfficePanel extends JPanel {
    private final JTextField txtSearch = new JTextField(20);
    private final JButton btnSearch = new JButton("Search");
    private final JButton btnAdd = new JButton("Add");
    private final JButton btnEdit = new JButton("Edit");
    private final JButton btnDelete = new JButton("Delete");
    private final JTable table = new JTable();
    private final InstructorPanel instucPanel;

    public OfficePanel(InstructorPanel instucPanel) {
        this.instucPanel = instucPanel;
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

    private void refresh() {
        try {
            DefaultTableModel tm = new SearchOffice().run(txtSearch.getText());
            table.setModel(tm);
            table.setDefaultEditor(Object.class, null);
        } catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    private void onAdd() {
        String s = JOptionPane.showInputDialog(this, "Floor number:", "Add Office", JOptionPane.PLAIN_MESSAGE);
        if (s == null) return;
        s = s.trim();
        if (s.isEmpty()) { showWarn("Floor is required."); return; }
        try {
            int floor = Integer.parseInt(s);
            int n = new InsertOffice().run(floor);
            if (n == 1) { showInfo("Inserted."); refresh(); }
        } catch (NumberFormatException nfe) { showWarn("Please enter an integer."); }
        catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    private void onEdit() {
        int v = table.getSelectedRow();
        if (v < 0) { showWarn("Select a row first."); return; }
        int r = table.convertRowIndexToModel(v);

        int id = Integer.parseInt(table.getModel().getValueAt(r, 0).toString());
        String cur = table.getModel().getValueAt(r, 1).toString();

        String s = JOptionPane.showInputDialog(this, "Floor number:", cur);
        if (s == null) return;
        s = s.trim();
        if (s.isEmpty()) { showWarn("Floor is required."); return; }

        try {
            int floor = Integer.parseInt(s);
            int n = new UpdateOffice().run(id, floor);
            if (n == 1) { showInfo("Updated."); refresh(); this.instucPanel.refresh(); } else showInfo("Row not found.");
        } catch (NumberFormatException nfe) { showWarn("Please enter an integer."); }
        catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    private void onDelete() {
        int v = table.getSelectedRow();
        if (v < 0) { showWarn("Select a row first."); return; }
        int r = table.convertRowIndexToModel(v);

        int id = Integer.parseInt(table.getModel().getValueAt(r, 0).toString());
        String floor = table.getModel().getValueAt(r, 1).toString();

        int ok = JOptionPane.showConfirmDialog(this, "Delete office on floor " + floor + "?", "Confirm",
                JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            int n = new RemoveOffice().run(id);
            if (n == 1) { showInfo("Deleted."); refresh(); } else showInfo("Row not found.");
        } catch (SQLException ex) { showError("Database error: " + ex.getMessage()); }
    }

    private void showInfo(String m){ JOptionPane.showMessageDialog(this,m,"Info",JOptionPane.INFORMATION_MESSAGE); }
    private void showWarn(String m){ JOptionPane.showMessageDialog(this,m,"Warning",JOptionPane.WARNING_MESSAGE); }
    private void showError(String m){ JOptionPane.showMessageDialog(this,m,"Error",JOptionPane.ERROR_MESSAGE); }
}
