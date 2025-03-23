package employee.management.system;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.JList;

public class EmployeeService {

	private DefaultListModel<String> taskListModel = new DefaultListModel<>();
	private JList<String> taskList = new JList<>(taskListModel);
	// private static final Path LOG_FILE = Paths.get("task_log.txt");
	private static final Path LOG_FILE = Paths.get(
			"C:\\Users\\KOMAL\\eclipse-workspace\\employee.management.system\\src\\employee\\management\\system\\task_log.txt");
	private Locale currentLocale = Locale.ENGLISH; // Default locale
	private ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", currentLocale);

	public void setLocale(Locale locale) {
		this.currentLocale = locale;
		this.messages = ResourceBundle.getBundle("MessagesBundle", currentLocale);
	}

	public List<Employee> getAllEmployees() {
		List<Employee> employees = new ArrayList<>();
		try (Connection conn = DatabaseConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(
						"SELECT e.id, e.name, d.name FROM employees e JOIN departments d ON e.department_id = d.id")) {

			while (rs.next()) {
				String id = rs.getString("id");
				String name = rs.getString("e.name");
				String departmentName = rs.getString("d.name");
				Department department = new Department(departmentName, List.of());
				employees.add(new Employee(id, name, department, getTasksForEmployee(id)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employees;
	}

	public void addEmployee(Employee employee) {
		try (Connection conn = DatabaseConnection.getConnection()) {
			conn.setAutoCommit(false);

			int departmentId;
			try (PreparedStatement deptStmt = conn.prepareStatement(
					"INSERT INTO departments (name) SELECT ? WHERE NOT EXISTS (SELECT 1 FROM departments WHERE name = ?)",
					Statement.RETURN_GENERATED_KEYS)) {
				deptStmt.setString(1, employee.department().name());
				deptStmt.setString(2, employee.department().name());
				deptStmt.executeUpdate();
			}

			try (PreparedStatement getDeptIdStmt = conn.prepareStatement("SELECT id FROM departments WHERE name = ?")) {
				getDeptIdStmt.setString(1, employee.department().name());
				ResultSet rs = getDeptIdStmt.executeQuery();
				if (rs.next()) {
					departmentId = rs.getInt("id");
				} else {
					conn.rollback();
					return;
				}
			}

			int employeeId;
			try (PreparedStatement empStmt = conn.prepareStatement(
					"INSERT INTO employees (name, department_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
				empStmt.setString(1, employee.name());
				empStmt.setInt(2, departmentId);
				empStmt.executeUpdate();
				ResultSet rs = empStmt.getGeneratedKeys();
				if (rs.next()) {
					employeeId = rs.getInt(1);
				} else {
					conn.rollback();
					return;
				}
			}

			for (Task task : employee.tasks()) {
				try (PreparedStatement taskStmt = conn.prepareStatement(
						"INSERT INTO tasks (employee_id, title, description, deadline, completed, priority) VALUES (?, ?, ?, ?, ?, ?)");) {
					taskStmt.setInt(1, employeeId);
					taskStmt.setString(2, task.title());
					taskStmt.setString(3, task.description());
					taskStmt.setTimestamp(4, Timestamp.valueOf(task.deadline()));
					taskStmt.setBoolean(5, task.completed());
					taskStmt.setInt(6, task.priority());
					taskStmt.executeUpdate();
				}
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteEmployee(String id) {
		try (Connection conn = DatabaseConnection.getConnection()) {
			conn.setAutoCommit(false);
			try (PreparedStatement taskStmt = conn.prepareStatement("DELETE FROM tasks WHERE employee_id = ?")) {
				taskStmt.setString(1, id);
				taskStmt.executeUpdate();
			}
			try (PreparedStatement empStmt = conn.prepareStatement("DELETE FROM employees WHERE id = ?")) {
				empStmt.setString(1, id);
				empStmt.executeUpdate();
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Employee getEmployeeById(String id) {
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(
						"SELECT e.id, e.name, d.name FROM employees e JOIN departments d ON e.department_id = d.id WHERE e.id = ?")) {
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String name = rs.getString("name");
				String departmentName = rs.getString("name");
				Department department = new Department(departmentName, List.of());
				return new Employee(id, name, department, getTasksForEmployee(id));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Task> getTasksForEmployee(String employeeId) {
		List<Task> tasks = new ArrayList<>();
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(
						"SELECT title, description, deadline, completed, priority FROM tasks WHERE employee_id = ?")) {
			stmt.setString(1, employeeId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				tasks.add(new Task(rs.getString("title"), rs.getString("description"),
						rs.getTimestamp("deadline").toLocalDateTime(), rs.getBoolean("completed"),
						rs.getInt("priority")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tasks;
	}

	public void addTaskToEmployee(String employeeId, Task task) throws SQLException {
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(
						"INSERT INTO tasks (employee_id, title, description, deadline, completed, priority) VALUES (?, ?, ?, ?, ?, ?)")) {
			stmt.setInt(1, Integer.parseInt(employeeId));
			stmt.setString(2, task.title());
			stmt.setString(3, task.description());
			stmt.setTimestamp(4, Timestamp.valueOf(task.deadline()));
			stmt.setBoolean(5, task.completed());
			stmt.setInt(6, task.priority());
			stmt.executeUpdate();
		}
	}

	public void removeTaskFromEmployee(String taskTitle, String taskDesc) {
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn
						.prepareStatement("DELETE FROM tasks WHERE title = ? AND description = ?")) {
			stmt.setString(1, taskTitle);
			stmt.setString(2, taskDesc);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void logTaskAction(String action, String taskTitle) {
		String logEntry = String.format("%s: %s%n", action, taskTitle);
		try {
			Files.write(LOG_FILE, logEntry.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
					StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String classifyTaskPriority(Task task) {
		return switch (task.priority()) {
		case 1, 2 -> "Low Priority";
		case 3 -> "Medium Priority";
		case 4, 5 -> "High Priority";
		default -> "Unknown Priority";
		};
	}

	public void filterPendingTasks(String employeeId) {
		taskListModel.clear();
		Employee employee = getEmployeeById(employeeId);
		if (employee != null) {
			List<Task> pendingTasks = employee.tasks().stream().filter(Predicate.not(Task::completed))
					.sorted(Comparator.comparing(Task::priority)).collect(Collectors.toList());

			for (Task task : pendingTasks) {
				String taskDetails = String.format("<html><b>%s</b><br>%s: %s<br>%s: %s<br>%s: %d<br>%s: %s</html>",
						task.title(), messages.getString("description"), task.description(),
						messages.getString("deadline"),
						task.deadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
						messages.getString("priority"), task.priority(), messages.getString("status"),
						messages.getString("pending"));
				taskListModel.addElement(taskDetails);
			}
		}
	}

}
