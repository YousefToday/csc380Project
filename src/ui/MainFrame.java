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

        RegistrationPanel regPanel = new RegistrationPanel();
        CoursePanel coursePanel = new CoursePanel(regPanel);
        InstructorPanel instucPanel = new InstructorPanel(coursePanel, regPanel);
        OfficePanel officePanel = new OfficePanel(instucPanel);
        DepartmentPanel depPanel = new DepartmentPanel(instucPanel);
        EmployeePanel employeePanel = new EmployeePanel(regPanel);

        tabs.addTab("Departments", depPanel);
        tabs.addTab("Offices", officePanel);
        tabs.addTab("Instructors", instucPanel);
        tabs.addTab("Courses", coursePanel);
        tabs.addTab("Employees", employeePanel);
        tabs.addTab("Registration", regPanel);

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignore) {}
        new MainFrame().setVisible(true);
    }
}
