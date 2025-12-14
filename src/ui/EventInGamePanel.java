package ui;

import db.DAO;

import javax.swing.*;
import java.awt.*;

public class EventInGamePanel extends JPanel {
    private final DAO dao = new DAO();

    private JTable table;
    private JTextField id, name, eventType, genderCategory;
    private JComboBox<LabeledId> edition, sport;

    public EventInGamePanel() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = gbc();

        id = new JTextField(12);
        name = new JTextField(20);
        eventType = new JTextField(12);
        genderCategory = new JTextField(12);
        edition = new JComboBox<>();
        sport = new JComboBox<>();

        row(form,g,0,"ID",id);
        row(form,g,1,"Name",name);
        row(form,g,2,"Event Type",eventType);
        row(form,g,3,"Gender Category",genderCategory);
        row(form,g,4,"Edition",edition);
        row(form,g,5,"Sport",sport);

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
            eventType.setText(v(r,2));
            genderCategory.setText(v(r,3));

            int ed = Integer.parseInt(v(r,4));
            int sp = Integer.parseInt(v(r,5));
            DAO.selectById(edition, ed);
            DAO.selectById(sport, sp);
        });

        refresh();
    }

    private void refresh() {
        try {
            dao.loadCombo(edition,
                    "SELECT id, CONCAT(year,' ',season) FROM `games_edition` ORDER BY year, season");
            dao.loadCombo(sport,
                    "SELECT id, name FROM `sport` ORDER BY name");

            table.setModel(dao.loadTable(
                    "SELECT id, name, event_type, gender_category, edition_id, sport_id " +
                            "FROM `event_in_game` ORDER BY id"));
        } catch (Exception ex) { err(ex); }
    }

    private void onAdd() {
        try {
            int eid = DAO.parseIntRequired(id, "Event ID");
            String n = DAO.textRequired(name, "Event Name");
            String et = DAO.textRequired(eventType, "Event Type");
            String gc = DAO.textRequired(genderCategory, "Gender Category");

            LabeledId ed = (LabeledId) edition.getSelectedItem();
            LabeledId sp = (LabeledId) sport.getSelectedItem();
            if (ed == null) throw new IllegalArgumentException("Edition is required.");
            if (sp == null) throw new IllegalArgumentException("Sport is required.");

            dao.execUpdate("INSERT INTO `event_in_game`(id,name,event_type,gender_category,edition_id,sport_id) VALUES (?,?,?,?,?,?)",
                    eid, n, et, gc, ed.getId(), sp.getId());

            msg("Event added.");
            refresh(); clear();
        } catch (Exception ex) { err(ex); }
    }

    private void onUpdate() {
        try {
            int eid = DAO.parseIntRequired(id, "Event ID");
            String n = DAO.textRequired(name, "Event Name");
            String et = DAO.textRequired(eventType, "Event Type");
            String gc = DAO.textRequired(genderCategory, "Gender Category");

            LabeledId ed = (LabeledId) edition.getSelectedItem();
            LabeledId sp = (LabeledId) sport.getSelectedItem();
            if (ed == null) throw new IllegalArgumentException("Edition is required.");
            if (sp == null) throw new IllegalArgumentException("Sport is required.");

            int rows = dao.execUpdate(
                    "UPDATE `event_in_game` SET name=?, event_type=?, gender_category=?, edition_id=?, sport_id=? WHERE id=?",
                    n, et, gc, ed.getId(), sp.getId(), eid);

            msg(rows == 0 ? "No row updated." : "Event updated.");
            refresh();
        } catch (Exception ex) { err(ex); }
    }

    private void onDelete() {
        try {
            int eid = DAO.parseIntRequired(id, "Event ID");
            int ok = JOptionPane.showConfirmDialog(this, "Delete event " + eid + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;

            int rows = dao.execUpdate("DELETE FROM `event_in_game` WHERE id=?", eid);
            msg(rows == 0 ? "No row deleted." : "Event deleted.");
            refresh(); clear();
        } catch (Exception ex) { err(ex); }
    }

    private void clear() {
        id.setText(""); name.setText(""); eventType.setText(""); genderCategory.setText("");
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
