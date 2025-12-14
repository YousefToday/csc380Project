package ui;

import db.DAO;

import javax.swing.*;
import java.awt.*;

public class ResultPanel extends JPanel {
    private final DAO dao = new DAO();

    private JTable table;
    private JTextField id, rankPos, resultStatus, performanceValue, unit;
    private JComboBox<LabeledId> entry;

    public ResultPanel() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = gbc();

        id = new JTextField(12);
        entry = new JComboBox<>();
        rankPos = new JTextField(10);
        resultStatus = new JTextField(10);
        performanceValue = new JTextField(12);
        unit = new JTextField(10);

        row(form,g,0,"ID",id);
        row(form,g,1,"Entry",entry);
        row(form,g,2,"Rank",rankPos);
        row(form,g,3,"Result Status",resultStatus);
        row(form,g,4,"Performance Value",performanceValue);
        row(form,g,5,"Unit",unit);

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

            int entryId = Integer.parseInt(v(r,1));
            DAO.selectById(entry, entryId);

            rankPos.setText(v(r,2));
            resultStatus.setText(v(r,3));
            performanceValue.setText(v(r,4));
            unit.setText(v(r,5));
        });

        refresh();
    }

    private void refresh() {
        try {
            // Better label: show athlete + event for the entry
            dao.loadCombo(entry,
                    "SELECT e.id, CONCAT(a.full_name,' - ',ev.name,' ',ev.gender_category) " +
                            "FROM `entry` e " +
                            "JOIN `athlete` a ON e.athlete_id = a.id " +
                            "JOIN `event_in_game` ev ON e.event_id = ev.id " +
                            "ORDER BY e.id");

            table.setModel(dao.loadTable(
                    "SELECT id, entry_id, rank_pos, result_status, performance_value, unit FROM `result` ORDER BY id"));
        } catch (Exception ex) { err(ex); }
    }

    private void onAdd() {
        try {
            int rid = DAO.parseIntRequired(id, "Result ID");
            LabeledId en = (LabeledId) entry.getSelectedItem();
            if (en == null) throw new IllegalArgumentException("Entry is required.");

            Integer rp = DAO.parseIntNullable(rankPos);
            String rs = DAO.textRequired(resultStatus, "Result Status");
            Double pv = DAO.parseDoubleNullable(performanceValue);
            String un = DAO.textOrNull(unit);

            dao.execUpdate("INSERT INTO `result`(id,entry_id,rank_pos,result_status,performance_value,unit) VALUES (?,?,?,?,?,?)",
                    rid, en.getId(), rp, rs, pv, un);

            msg("Result added.");
            refresh(); clear();
        } catch (Exception ex) { err(ex); }
    }

    private void onUpdate() {
        try {
            int rid = DAO.parseIntRequired(id, "Result ID");
            LabeledId en = (LabeledId) entry.getSelectedItem();
            if (en == null) throw new IllegalArgumentException("Entry is required.");

            Integer rp = DAO.parseIntNullable(rankPos);
            String rs = DAO.textRequired(resultStatus, "Result Status");
            Double pv = DAO.parseDoubleNullable(performanceValue);
            String un = DAO.textOrNull(unit);

            int rows = dao.execUpdate(
                    "UPDATE `result` SET entry_id=?, rank_pos=?, result_status=?, performance_value=?, unit=? WHERE id=?",
                    en.getId(), rp, rs, pv, un, rid);

            msg(rows == 0 ? "No row updated." : "Result updated.");
            refresh();
        } catch (Exception ex) { err(ex); }
    }

    private void onDelete() {
        try {
            int rid = DAO.parseIntRequired(id, "Result ID");
            int ok = JOptionPane.showConfirmDialog(this, "Delete result " + rid + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;

            int rows = dao.execUpdate("DELETE FROM `result` WHERE id=?", rid);
            msg(rows == 0 ? "No row deleted." : "Result deleted.");
            refresh(); clear();
        } catch (Exception ex) { err(ex); }
    }

    private void clear() {
        id.setText(""); rankPos.setText(""); resultStatus.setText(""); performanceValue.setText(""); unit.setText("");
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
