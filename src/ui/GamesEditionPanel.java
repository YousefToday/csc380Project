package ui;

import db.DAO;

import javax.swing.*;
import java.awt.*;

public class GamesEditionPanel extends JPanel {
    private final DAO dao = new DAO();

    private JTable table;
    private JTextField id, year, season;
    private JComboBox<LabeledId> hostCountry;

    public GamesEditionPanel() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = gbc();

        id = new JTextField(12);
        year = new JTextField(12);
        season = new JTextField(12); // keep simple like source (you can use combo later)
        hostCountry = new JComboBox<>();

        row(form,g,0,"ID",id);
        row(form,g,1,"Year",year);
        row(form,g,2,"Season",season);
        row(form,g,3,"Host Country",hostCountry);

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
            year.setText(v(r,1));
            season.setText(v(r,2));

            int hostId = Integer.parseInt(v(r,3));
            DAO.selectById(hostCountry, hostId);
        });

        refresh();
    }

    private void refresh() {
        try {
            dao.loadCombo(hostCountry,
                    "SELECT id, CONCAT(name,' (',noc,')') FROM `country` ORDER BY name");
            table.setModel(dao.loadTable(
                    "SELECT id, year, season, host_country_id FROM `games_edition` ORDER BY year, season"));
        } catch (Exception ex) { err(ex); }
    }

    private void onAdd() {
        try {
            int gid = DAO.parseIntRequired(id, "Edition ID");
            int y = Integer.parseInt(DAO.textRequired(year, "Year"));
            String s = DAO.textRequired(season, "Season");

            LabeledId host = (LabeledId) hostCountry.getSelectedItem();
            if (host == null) throw new IllegalArgumentException("Host country is required.");

            dao.execUpdate("INSERT INTO `games_edition`(id,year,season,host_country_id) VALUES (?,?,?,?)",
                    gid, y, s, host.getId());

            msg("Games Edition added.");
            refresh(); clear();
        } catch (Exception ex) { err(ex); }
    }

    private void onUpdate() {
        try {
            int gid = DAO.parseIntRequired(id, "Edition ID");
            int y = Integer.parseInt(DAO.textRequired(year, "Year"));
            String s = DAO.textRequired(season, "Season");

            LabeledId host = (LabeledId) hostCountry.getSelectedItem();
            if (host == null) throw new IllegalArgumentException("Host country is required.");

            int rows = dao.execUpdate("UPDATE `games_edition` SET year=?, season=?, host_country_id=? WHERE id=?",
                    y, s, host.getId(), gid);

            msg(rows == 0 ? "No row updated." : "Games Edition updated.");
            refresh();
        } catch (Exception ex) { err(ex); }
    }

    private void onDelete() {
        try {
            int gid = DAO.parseIntRequired(id, "Edition ID");
            int ok = JOptionPane.showConfirmDialog(this, "Delete edition " + gid + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;

            int rows = dao.execUpdate("DELETE FROM `games_edition` WHERE id=?", gid);
            msg(rows == 0 ? "No row deleted." : "Games Edition deleted.");
            refresh(); clear();
        } catch (Exception ex) { err(ex); }
    }

    private void clear() {
        id.setText(""); year.setText(""); season.setText("");
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
