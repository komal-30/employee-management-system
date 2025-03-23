package employee.management.system;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/*
	 * 
	 * Sorting task priority - Will sort the task acc to prioirty 
 */
public class EmployeeManagementGUI {
	private JFrame frame;
	private JTable table;
	private DefaultTableModel tableModel;
	private EmployeeService employeeService;

	public EmployeeManagementGUI() {
		employeeService = new EmployeeService();
		frame = new JFrame("Employee Management System");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 500);
		frame.setLayout(new BorderLayout());

		// Table setup
		String[] columns = { "ID", "Name", "Department", "Tasks" };
		tableModel = new DefaultTableModel(columns, 0);
		table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		frame.add(scrollPane, BorderLayout.CENTER);

		// Buttons
		JPanel panel = new JPanel();
		JButton addButton = new JButton("Add Employee");
		JButton deleteButton = new JButton("Delete Employee");
		JButton refreshButton = new JButton("Refresh List");
		JButton manageTasksButton = new JButton("Manage Tasks");

		panel.add(addButton);
		panel.add(deleteButton);
		panel.add(refreshButton);
		panel.add(manageTasksButton);
		frame.add(panel, BorderLayout.SOUTH);

		// Button Actions
		addButton.addActionListener(e -> addEmployee());
		deleteButton.addActionListener(e -> deleteEmployee());
		refreshButton.addActionListener(e -> loadEmployees());
		manageTasksButton.addActionListener(e -> manageTasks());

		loadEmployees();
		frame.setVisible(true);
	}

	private void loadEmployees() {
		tableModel.setRowCount(0);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<List<Employee>> future = executor.submit(() -> employeeService.getAllEmployees());

		try {
			List<Employee> employees = future.get();
			for (Employee emp : employees) {
				String taskList = String.join(", ", emp.tasks().stream().map(Task::title).toList());
				tableModel.addRow(new Object[] { emp.id(), emp.name(), emp.department().name(), taskList });
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
		}
	}

	private void addEmployee() {
		String name = JOptionPane.showInputDialog("Enter Employee Name:");
		String departmentName = JOptionPane.showInputDialog("Enter Department Name:");
		Department department = new Department(departmentName, List.of());

		Employee newEmployee = new Employee("0", name, department, List.of());
		employeeService.addEmployee(newEmployee);
		loadEmployees();
	}

	private void deleteEmployee() {
		int row = table.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(frame, "Select an employee to delete");
			return;
		}
		String id = (String) tableModel.getValueAt(row, 0);
		employeeService.deleteEmployee(id);
		loadEmployees();
	}

	private void manageTasks() {
		int row = table.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(frame, "Select an employee to manage tasks");
			return;
		}
		String id = (String) tableModel.getValueAt(row, 0);
		new TaskManagementGUI(id, employeeService);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(EmployeeManagementGUI::new);
	}
}

class TaskManagementGUI {
	private JFrame frame;
	private DefaultListModel<String> taskListModel;
	private JList<String> taskList;
	private EmployeeService employeeService;
	private String employeeId;

	private Locale currentLocale = Locale.ENGLISH; // Always default to English
	private ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", currentLocale);

	public TaskManagementGUI(String employeeId, EmployeeService employeeService) {
		this.employeeId = employeeId;
		this.employeeService = employeeService;

		initializeGUI();
	}

	private void initializeGUI() {
		frame = new JFrame(messages.getString("taskManagementTitle"));
		frame.setSize(500, 400);
		frame.setLayout(new BorderLayout());

		taskListModel = new DefaultListModel<>();
		taskList = new JList<>(taskListModel);
		JScrollPane scrollPane = new JScrollPane(taskList);
		frame.add(scrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		JButton addButton = new JButton(messages.getString("addTask"));
		JButton deleteButton = new JButton(messages.getString("deleteTask"));

		JButton switchToFrench = new JButton("Français");
		switchToFrench.addActionListener(e -> changeLanguage(Locale.FRENCH));

		panel.add(addButton);
		panel.add(deleteButton);
		panel.add(switchToFrench);
		frame.add(panel, BorderLayout.SOUTH);

		// Filter Pending Task
		JButton filterPendingTasksButton = new JButton("Filter Pending Tasks");
		filterPendingTasksButton.addActionListener(e -> {
			String employeeId = JOptionPane.showInputDialog("Enter Employee ID:");
			if (employeeId != null && !employeeId.trim().isEmpty()) {
//				employeeService.filterPendingTasks(employeeId); // Call the filtering method
				showPendingTasksWindow(employeeId);
			}
		});
		panel.add(filterPendingTasksButton);

		addButton.addActionListener(e -> {
			String employeeId = JOptionPane.showInputDialog("Enter Employee ID:");
			if (employeeId != null && !employeeId.trim().isEmpty()) {
				String title = JOptionPane.showInputDialog("Enter Task Title:");
				String description = JOptionPane.showInputDialog("Enter Task Description:");
				String deadlineStr = JOptionPane.showInputDialog("Enter Deadline (yyyy-MM-dd HH:mm):");
				String priorityStr = JOptionPane.showInputDialog("Enter Task Priority (1-5):");

				try {
					LocalDateTime deadline = LocalDateTime.parse(deadlineStr,
							DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
					int priority = Integer.parseInt(priorityStr);
					Task newTask = new Task(title, description, deadline, false, priority);

					employeeService.addTaskToEmployee(employeeId, newTask);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(frame, "Invalid input. Please try again.", "Error",
							JOptionPane.ERROR_MESSAGE);
					System.out.println(ex.toString());
				}
			}
		});

		deleteButton.addActionListener(e -> {
			String taskTitle = JOptionPane.showInputDialog("Enter Task Title to delete:");
//			String taskIdStr = JOptionPane.showInputDialog("Enter Task ID to delete:");
			String taskDesc = JOptionPane.showInputDialog("Enter Task Description to delete:");

			if (taskTitle != null && !taskTitle.trim().isEmpty() && taskDesc != null && !taskDesc.trim().isEmpty()) {
				try {
//					int taskId = Integer.parseInt(taskIdStr);
					deleteTask(e, taskTitle, taskDesc);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(frame, "Invalid Task ID format. Please enter a number.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		loadTasks();
		frame.setVisible(true);
	}

	private void changeLanguage(Locale locale) {
		currentLocale = locale;
		messages = ResourceBundle.getBundle("MessagesBundle", currentLocale);

		// Refresh UI elements
		frame.setTitle(messages.getString("taskManagementTitle"));

		// Reinitialize buttons and labels
		frame.getContentPane().removeAll();
		initializeGUI();
		frame.revalidate();
		frame.repaint();
	}

	public void loadTasks() {
		taskListModel.clear();
		Employee employee = employeeService.getEmployeeById(employeeId);
		if (employee != null) {
			for (Task task : employee.tasks()) {
				String taskPriority = employeeService.classifyTaskPriority(task);
				String taskDetails = String.format("<html><b>%s</b><br>%s: %s<br>%s: %s<br>%s: %s<br>%s: %s</html>",
						task.title(), messages.getString("description"), task.description(),
						messages.getString("deadline"),
						task.deadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
						messages.getString("priority"), taskPriority, messages.getString("status"),
						task.completed() ? messages.getString("completed") : messages.getString("pending"));
				taskListModel.addElement(
						"----------------------------------------------------------------------------------------");
				taskListModel.addElement(taskDetails);
			}
		}
	}

	private void deleteTask(java.awt.event.ActionEvent evt, String taskTitle, String taskDesc) {
		employeeService.removeTaskFromEmployee(taskTitle, taskDesc);
		employeeService.logTaskAction("Deleted", "Task Desc: " + taskDesc);
		loadTasks();
	}

	// New Method to Show Pending Tasks in a Separate Window
	public void showPendingTasksWindow(String employeeId) {
		JFrame pendingTasksFrame = new JFrame("Pending Tasks");
		pendingTasksFrame.setSize(500, 400);
		pendingTasksFrame.setLayout(new BorderLayout());

		DefaultListModel<String> pendingTaskListModel = new DefaultListModel<>();
		JList<String> pendingTaskList = new JList<>(pendingTaskListModel);
		JScrollPane scrollPane = new JScrollPane(pendingTaskList);
		pendingTasksFrame.add(scrollPane, BorderLayout.CENTER);

		Employee employee = employeeService.getEmployeeById(employeeId);
		if (employee != null) {
			List<Task> pendingTasks = employee.tasks().stream().filter(Predicate.not(Task::completed))
					.sorted(Comparator.comparing(Task::priority)).collect(Collectors.toList());

			for (Task task : pendingTasks) {
				String taskPriority = employeeService.classifyTaskPriority(task);
				String taskDetails = String.format("<html><b>%s</b><br>%s: %s<br>%s: %s<br>%s: %s<br>%s: %s</html>",
						task.title(), messages.getString("description"), task.description(),
						messages.getString("deadline"),
						task.deadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
						messages.getString("priority"), taskPriority, messages.getString("status"),
						messages.getString("pending"));

				pendingTaskListModel.addElement("------------------------------------------------------------");
				pendingTaskListModel.addElement(taskDetails);
			}
		}

		pendingTasksFrame.setVisible(true);
	}

}
