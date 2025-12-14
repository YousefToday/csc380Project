package ui;

import db.DAO;

import javax.swing.*;
import java.awt.*;

public class EntryPanel extends JPanel {
    private final DAO dao = new DAO();

    private JTable table;
    private JTextField id, status;
    private JComboBox<LabeledId> athlete, event;

    public EntryPanel() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = gbc();

        id = new JTextField(12);
        status = new JTextField(15);
        athlete = new JComboBox<>();
        event = new JComboBox<>();

        row(form,g,0,"ID",id);
        row(form,g,1,"Entry Status",status);
        row(form,g,2,"Athlete",athlete);
        row(form,g,3,"Event",event);

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
            status.setText(v(r,1));

            int aid = Integer.parseInt(v(r,2));
            int eid = Integer.parseInt(v(r,3));
            DAO.selectById(athlete, aid);
            DAO.selectById(event, eid);
        });

        refresh();
    }

    private void refresh() {
        try {
            dao.loadCombo(athlete,
                    "SELECT id, full_name FROM `athlete` ORDER BY full_name");
            dao.loadCombo(event,
                    "SELECT id, CONCAT(name,' - ',gender_category) FROM `event_in_game` ORDER BY id");

            table.setModel(dao.loadTable(
                    "SELECT id, entry_status, athlete_id, event_id FROM `entry` ORDER BY id"));
        } catch (Exception ex) { err(ex); }
    }

    private void onAdd() {
        try {
            int enid = DAO.parseIntRequired(id, "Entry ID");
            String st = DAO.textRequired(status, "Entry Status");

            LabeledId a = (LabeledId) athlete.getSelectedItem();
            LabeledId e = (LabeledId) event.getSelectedItem();
            if (a == null) throw new IllegalArgumentException("Athlete is required.");
            if (e == null) throw new IllegalArgumentException("Event is required.");

            dao.execUpdate("INSERT INTO `entry`(id,entry_status,athlete_id,event_id) VALUES (?,?,?,?)",
                    enid, st, a.getId(), e.getId());

            msg("Entry added.");
            refresh(); clear();
        } catch (Exception ex) { err(ex); }
    }

    private void onUpdate() {
        try {
            int enid = DAO.parseIntRequired(id, "Entry ID");
            String st = DAO.textRequired(status, "Entry Status");

            LabeledId a = (LabeledId) athlete.getSelectedItem();
            LabeledId e = (LabeledId) event.getSelectedItem();
            if (a == null) throw new IllegalArgumentException("Athlete is required.");
            if (e == null) throw new IllegalArgumentException("Event is required.");

            int rows = dao.execUpdate(
                    "UPDATE `entry` SET entry_status=?, athlete_id=?, event_id=? WHERE id=?",
                    st, a.getId(), e.getId(), enid);

            msg(rows == 0 ? "No row updated." : "Entry updated.");
            refresh();
        } catch (Exception ex) { err(ex); }
    }

    private void onDelete() {
        try {
            int enid = DAO.parseIntRequired(id, "Entry ID");
            int ok = JOptionPane.showConfirmDialog(this, "Delete entry " + enid + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;

            int rows = dao.execUpdate("DELETE FROM `entry` WHERE id=?", enid);
            msg(rows == 0 ? "No row deleted." : "Entry deleted.");
            refresh(); clear();
        } catch (Exception ex) { err(ex); }
    }

    private void clear() {
        id.setText(""); status.setText("");
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
