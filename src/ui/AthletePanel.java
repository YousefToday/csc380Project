package ui;

import db.DAO;

import javax.swing.*;
import java.awt.*;

public class AthletePanel extends JPanel {
    private final DAO dao = new DAO();

    private JTable table;
    private JTextField id, fullName, gender, height, weight;
    private JComboBox<LabeledId> country;

    public AthletePanel() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = gbc();

        id = new JTextField(12);
        fullName = new JTextField(20);
        gender = new JTextField(10);
        height = new JTextField(10);
        weight = new JTextField(10);
        country = new JComboBox<>();

        row(form,g,0,"ID",id);
        row(form,g,1,"Full Name",fullName);
        row(form,g,2,"Gender",gender);
        row(form,g,3,"Height",height);
        row(form,g,4,"Weight",weight);
        row(form,g,5,"Country",country);

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
            fullName.setText(v(r,1));
            gender.setText(v(r,2));
            height.setText(v(r,3));
            weight.setText(v(r,4));

            int cid = Integer.parseInt(v(r,5));
            DAO.selectById(country, cid);
        });

        refresh();
    }

    private void refresh() {
        try {
            dao.loadCombo(country,
                    "SELECT id, CONCAT(name,' (',noc,')') FROM `country` ORDER BY name");
            table.setModel(dao.loadTable(
                    "SELECT id, full_name, gender, height, weight, country_id FROM `athlete` ORDER BY full_name"));
        } catch (Exception ex) { err(ex); }
    }

    private void onAdd() {
        try {
            int aid = DAO.parseIntRequired(id, "Athlete ID");
            String fn = DAO.textRequired(fullName, "Full Name");
            String g = DAO.textOrNull(gender);
            Double h = DAO.parseDoubleNullable(height);
            Double w = DAO.parseDoubleNullable(weight);

            LabeledId c = (LabeledId) country.getSelectedItem();
            if (c == null) throw new IllegalArgumentException("Country is required.");

            dao.execUpdate("INSERT INTO `athlete`(id,full_name,gender,height,weight,country_id) VALUES (?,?,?,?,?,?)",
                    aid, fn, g, h, w, c.getId());

            msg("Athlete added.");
            refresh(); clear();
        } catch (Exception ex) { err(ex); }
    }

    private void onUpdate() {
        try {
            int aid = DAO.parseIntRequired(id, "Athlete ID");
            String fn = DAO.textRequired(fullName, "Full Name");
            String g = DAO.textOrNull(gender);
            Double h = DAO.parseDoubleNullable(height);
            Double w = DAO.parseDoubleNullable(weight);

            LabeledId c = (LabeledId) country.getSelectedItem();
            if (c == null) throw new IllegalArgumentException("Country is required.");

            int rows = dao.execUpdate("UPDATE `athlete` SET full_name=?, gender=?, height=?, weight=?, country_id=? WHERE id=?",
                    fn, g, h, w, c.getId(), aid);

            msg(rows == 0 ? "No row updated." : "Athlete updated.");
            refresh();
        } catch (Exception ex) { err(ex); }
    }

    private void onDelete() {
        try {
            int aid = DAO.parseIntRequired(id, "Athlete ID");
            int ok = JOptionPane.showConfirmDialog(this, "Delete athlete " + aid + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;

            int rows = dao.execUpdate("DELETE FROM `athlete` WHERE id=?", aid);
            msg(rows == 0 ? "No row deleted." : "Athlete deleted.");
            refresh(); clear();
        } catch (Exception ex) { err(ex); }
    }

    private void clear() {
        id.setText(""); fullName.setText(""); gender.setText(""); height.setText(""); weight.setText("");
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
