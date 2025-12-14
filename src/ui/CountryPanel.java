package ui;

import db.DAO;

import javax.swing.*;
import java.awt.*;

public class CountryPanel extends JPanel {
    private final DAO dao = new DAO();

    private JTable table;
    private JTextField id, name, noc, capital;

    public CountryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = gbc();

        id = new JTextField(12);
        name = new JTextField(18);
        noc = new JTextField(8);
        capital = new JTextField(18);

        row(form, g, 0, "ID", id);
        row(form, g, 1, "Name", name);
        row(form, g, 2, "NOC", noc);
        row(form, g, 3, "Capital", capital);

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

        JPanel south = new JPanel(new BorderLayout(10, 10));
        south.add(form, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int r = table.getSelectedRow();
            if (r < 0) return;
            id.setText(v(r,0));
            name.setText(v(r,1));
            noc.setText(v(r,2));
            capital.setText(v(r,3));
        });

        refresh();
    }

    private void refresh() {
        try {
            table.setModel(dao.loadTable("SELECT id, name, noc, capital FROM `country` ORDER BY name"));
        } catch (Exception ex) { err(ex); }
    }

    private void onAdd() {
        try {
            int cid = DAO.parseIntRequired(id, "Country ID");
            String cname = DAO.textRequired(name, "Country Name");
            String cnoc = DAO.textOrNull(noc);
            String ccap = DAO.textOrNull(capital);

            dao.execUpdate("INSERT INTO `country`(id,name,noc,capital) VALUES (?,?,?,?)",
                    cid, cname, cnoc, ccap);

            msg("Country added.");
            refresh(); clear();
        } catch (Exception ex) { err(ex); }
    }

    private void onUpdate() {
        try {
            int cid = DAO.parseIntRequired(id, "Country ID");
            String cname = DAO.textRequired(name, "Country Name");
            String cnoc = DAO.textOrNull(noc);
            String ccap = DAO.textOrNull(capital);

            int rows = dao.execUpdate("UPDATE `country` SET name=?, noc=?, capital=? WHERE id=?",
                    cname, cnoc, ccap, cid);

            msg(rows == 0 ? "No row updated." : "Country updated.");
            refresh();
        } catch (Exception ex) { err(ex); }
    }

    private void onDelete() {
        try {
            int cid = DAO.parseIntRequired(id, "Country ID");
            int ok = JOptionPane.showConfirmDialog(this, "Delete country " + cid + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;

            int rows = dao.execUpdate("DELETE FROM `country` WHERE id=?", cid);
            msg(rows == 0 ? "No row deleted." : "Country deleted.");
            refresh(); clear();
        } catch (Exception ex) { err(ex); }
    }

    private void clear() {
        id.setText(""); name.setText(""); noc.setText(""); capital.setText("");
        table.clearSelection();
    }

    private String v(int r, int c) {
        Object x = table.getValueAt(r,c);
        return x == null ? "" : x.toString();
    }

    private static GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4);
        g.fill = GridBagConstraints.HORIZONTAL;
        return g;
    }

    private static void row(JPanel p, GridBagConstraints g, int y, String label, JComponent field) {
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
