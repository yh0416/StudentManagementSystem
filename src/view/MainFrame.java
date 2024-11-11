package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import utils.DatabaseUtils;
import model.Student;

public class MainFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton;

    public MainFrame() {
        setTitle("Student Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建表格模型
        String[] columns = {"ID", "Name", "Email", "Age"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 使表格不可直接编辑
            }
        };
        table = new JTable(tableModel);

        // 创建按钮
        addButton = new JButton("Add New Student");
        editButton = new JButton("Edit Student");
        deleteButton = new JButton("Delete Student");
        refreshButton = new JButton("Refresh");

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // 设置布局
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // 添加事件监听器
        addButton.addActionListener(e -> openStudentForm(null));
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                Student student = getStudentFromSelectedRow();
                openStudentForm(student);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student to edit");
            }
        });
        deleteButton.addActionListener(e -> deleteStudent());
        refreshButton.addActionListener(e -> loadStudents());

        // 加载学生数据
        loadStudents();
    }

    private void loadStudents() {
        tableModel.setRowCount(0); // 清空表格
        try {
            Connection conn = DatabaseUtils.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("age")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage());
        }
    }

    private void openStudentForm(Student student) {
        StudentForm form = new StudentForm(this, student);
        form.setVisible(true);
    }

    private void deleteStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) table.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this student?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Connection conn = DatabaseUtils.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(
                            "DELETE FROM students WHERE id = ?");
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                    loadStudents(); // 刷新表格
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting student: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to delete");
        }
    }

    private Student getStudentFromSelectedRow() {
        int row = table.getSelectedRow();
        return new Student(
                (int) table.getValueAt(row, 0),
                (String) table.getValueAt(row, 1),
                (String) table.getValueAt(row, 2),
                (int) table.getValueAt(row, 3)
        );
    }

    public void refreshTable() {
        loadStudents();
    }
}