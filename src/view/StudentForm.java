package view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import model.Student;
import utils.DatabaseUtils;

public class StudentForm extends JDialog {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField ageField;
    private JButton saveButton;
    private JButton cancelButton;
    private Student student;
    private MainFrame mainFrame;

    public StudentForm(MainFrame parent, Student student) {
        super(parent, student == null ? "Add New Student" : "Edit Student", true);
        this.student = student;
        this.mainFrame = parent;

        setSize(300, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // 创建表单面板
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Age:"));
        ageField = new JTextField();
        formPanel.add(ageField);

        // 如果是编辑模式，填充现有数据
        if (student != null) {
            nameField.setText(student.getName());
            emailField.setText(student.getEmail());
            ageField.setText(String.valueOf(student.getAge()));
        }

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // 添加到主面板
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // 添加事件监听器
        saveButton.addActionListener(e -> saveStudent());
        cancelButton.addActionListener(e -> dispose());
    }

    private void saveStudent() {
        if (!validateInput()) {
            return;
        }

        String name = nameField.getText();
        String email = emailField.getText();
        int age = Integer.parseInt(ageField.getText());

        try {
            Connection conn = DatabaseUtils.getConnection();
            PreparedStatement pstmt;

            if (student == null) {
                // 插入新学生
                pstmt = conn.prepareStatement(
                        "INSERT INTO students (name, email, age) VALUES (?, ?, ?)");
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setInt(3, age);
            } else {
                // 更新现有学生
                pstmt = conn.prepareStatement(
                        "UPDATE students SET name = ?, email = ?, age = ? WHERE id = ?");
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setInt(3, age);
                pstmt.setInt(4, student.getId());
            }

            pstmt.executeUpdate();
            mainFrame.refreshTable();
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving student: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name");
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an email");
            return false;
        }
        try {
            int age = Integer.parseInt(ageField.getText().trim());
            if (age < 0 || age > 150) {
                JOptionPane.showMessageDialog(this, "Please enter a valid age");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age");
            return false;
        }
        return true;
    }
}