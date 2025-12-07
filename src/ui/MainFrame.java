package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("CSC380 Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Departments", new DepartmentPanel());
        tabs.addTab("Instructors", new InstructorPanel());
        tabs.addTab("Offices", new OfficePanel());
        tabs.addTab("Courses", new CoursePanel());
        tabs.addTab("Employees", new EmployeePanel());
        tabs.addTab("Registration", new RegistrationPanel());
        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);

    }
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignore) {}
        new MainFrame().setVisible(true);
    }
}
