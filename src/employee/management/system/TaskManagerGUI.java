//package employee.management.system;
//
//import java.awt.FlowLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextArea;
//import javax.swing.JTextField;
//
//public class TaskManagerGUI extends JFrame {
//	private JTextField employeeIdField, nameField, departmentField;
//	private JButton addEmployeeButton;
//	private JTextArea displayArea;
//
//	public TaskManagerGUI() {
//		setTitle("Employee Task Manager");
//		setSize(500, 400);
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setLayout(new FlowLayout());
//
//		add(new JLabel("Employee ID:"));
//		employeeIdField = new JTextField(10);
//		add(employeeIdField);
//
//		add(new JLabel("Name:"));
//		nameField = new JTextField(15);
//		add(nameField);
//
//		add(new JLabel("Department:"));
//		departmentField = new JTextField(10);
//		add(departmentField);
//
//		addEmployeeButton = new JButton("Add Employee");
//		add(addEmployeeButton);
//
//		displayArea = new JTextArea(10, 40);
//		add(new JScrollPane(displayArea));
//
//		addEmployeeButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				String id = employeeIdField.getText();
//				String name = nameField.getText();
//				String department = departmentField.getText();
////				DatabaseHelper.addEmployee(id, name, department);
//				displayArea.append("Employee Added: " + name + " in " + department + "\n");
//			}
//		});
//
//		setVisible(true);
//	}
//
//	public static void main(String[] args) {
//		new TaskManagerGUI();
//	}
//}
