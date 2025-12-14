package ui;

import db.DAO;

import javax.swing.*;
import java.awt.*;

public class SportPanel extends JPanel {
    private final DAO dao = new DAO();

    private JTable table;
    private JTextField id, name, category;

    public SportPanel() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = CountryPanel_gbc();

        id = new JTextField(12);
        name = new JTextField(18);
        category = new JTextField(18);

        CountryPanel_row(form,g,0,"ID",id);
        CountryPanel_row(form,g,1,"Name",name);
        CountryPanel_row(form,g,2,"Category",category);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton addBtn = new JButton("Add");
        JButton updBtn = new JButton("Update");
        JButton delBtn = new JButton("Delete");
        JButton clrBtn = new JButton("Clear");
        JButton refBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> onAdd());
        updBtn.addActionListener(e -> onUpdate());
        delBtn.addActionListener(e -> onDelete());
        clrBtn.addActionListener(e -> clear());
        refBtn.addActionListener(e -> refresh());

        buttons.add(addBtn); buttons.add(updBtn); buttons.add(delBtn); buttons.add(clrBtn); buttons.add(refBtn);

        JPanel south = new JPanel(new BorderLayout(10,10));
        south.add(form, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int r = table.getSelectedRow();
            if (r < 0) return;
            id.setText(v(r,0));
            name.setText(v(r,1));
            category.setText(v(r,2));
        });

        refresh();
    }

    private void refresh() {
        try {
            table.setModel(dao.loadTable("SELECT id, name, category FROM `sport` ORDER BY name"));
        } catch (Exception ex) { err(ex); }
    }

    private void onAdd() {
        try {
            int sid = DAO.parseIntRequired(id, "Sport ID");
            String sname = DAO.textRequired(name, "Sport Name");
            String scat = DAO.textOrNull(category);

            dao.execUpdate("INSERT INTO `sport`(id,name,category) VALUES (?,?,?)", sid, sname, scat);
            msg("Sport added.");
            refresh(); clear();
        } catch (Exception ex) { err(ex); }
    }

    private void onUpdate() {
        try {
            int sid = DAO.parseIntRequired(id, "Sport ID");
            String sname = DAO.textRequired(name, "Sport Name");
            String scat = DAO.textOrNull(category);

            int rows = dao.execUpdate("UPDATE `sport` SET name=?, category=? WHERE id=?",
                    sname, scat, sid);

            msg(rows == 0 ? "No row updated." : "Sport updated.");
            refresh();
        } catch (Exception ex) { err(ex); }
    }

    private void onDelete() {
        try {
            int sid = DAO.parseIntRequired(id, "Sport ID");
            int ok = JOptionPane.showConfirmDialog(this, "Delete sport " + sid + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;

            int rows = dao.execUpdate("DELETE FROM `sport` WHERE id=?", sid);
            msg(rows == 0 ? "No row deleted." : "Sport deleted.");
            refresh(); clear();
        } catch (Exception ex) { err(ex); }
    }

    private void clear() {
        id.setText(""); name.setText(""); category.setText("");
        table.clearSelection();
    }

    private String v(int r, int c) {
        Object x = table.getValueAt(r,c);
        return x == null ? "" : x.toString();
    }

    // Local copies to keep file standalone and same style
    private static GridBagConstraints CountryPanel_gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4);
        g.fill = GridBagConstraints.HORIZONTAL;
        return g;
    }
    private static void CountryPanel_row(JPanel p, GridBagConstraints g, int y, String label, JComponent field) {
        g.gridx=0; g.gridy=y; g.weightx=0;
        p.add(new JLabel(label), g);
        g.gridx=1; g.gridy=y; g.weightx=1;
        p.add(field, g);
    }

    private void msg(String s) { JOptionPane.showMessageDialog(this, s); }
    private void err(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
