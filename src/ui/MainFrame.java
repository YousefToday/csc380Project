package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("Olympics DB Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Countries", new CountryPanel());
        tabs.addTab("Sports", new SportPanel());
        tabs.addTab("Games Editions", new GamesEditionPanel());
        tabs.addTab("Athletes", new AthletePanel());
        tabs.addTab("Events", new EventInGamePanel());
        tabs.addTab("Entries", new EntryPanel());
        tabs.addTab("Results", new ResultPanel());

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignore) {}
        new MainFrame().setVisible(true);
    }
}
