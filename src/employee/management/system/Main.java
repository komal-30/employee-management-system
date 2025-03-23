package employee.management.system;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
	private static final Scanner scanner = new Scanner(System.in);
	private static final List<Employee> employees = new ArrayList<>();
	private static final List<Task> tasks = new ArrayList<>();

	public static void main(String[] args) {
		while (true) {
			System.out.println("\n====== Employee Task Manager ======");
			System.out.println("1. Add Employee");
			System.out.println("2. Assign Task");
			System.out.println("3. View Tasks");
			System.out.println("4. Filter Tasks (Pending)");
			System.out.println("5. Exit");
			System.out.print("Choose an option: ");

			int choice = scanner.nextInt();
			scanner.nextLine(); // consume newline

			switch (choice) {
			case 1 -> addEmployee();
			case 2 -> assignTask();
			case 3 -> viewTasks();
			case 4 -> filterPendingTasks();
			case 5 -> {
				System.out.println("Exiting program...");
				return;
			}
			default -> System.out.println("Invalid choice! Please try again.");
			}
		}
	}

	private static void addEmployee() {
		System.out.print("Enter Employee ID: ");
		String id = scanner.nextLine();
		System.out.print("Enter Employee Name: ");
		String name = scanner.nextLine();
		System.out.print("Enter Department: ");
		String deptName = scanner.nextLine();

		Department department = new Department(deptName, new ArrayList<>());
		Employee employee = new Employee(id, name, department, new ArrayList<>());
		employees.add(employee);
		System.out.println("Employee added successfully!");
	}

	private static void assignTask() {
		if (employees.isEmpty()) {
			System.out.println("No employees available! Add an employee first.");
			return;
		}

		System.out.print("Enter Task Title: ");
		String title = scanner.nextLine();
		System.out.print("Enter Task Description: ");
		String description = scanner.nextLine();
		System.out.print("Enter Task Deadline (yyyy-MM-dd HH:mm): ");
		String deadlineStr = scanner.nextLine();
		LocalDateTime deadline = LocalDateTime.parse(deadlineStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		System.out.print("Enter Task Priority (1-5): ");
		int priority = scanner.nextInt();
		scanner.nextLine(); // consume newline

		Task task = new Task(title, description, deadline, false, priority);
		tasks.add(task);

		// Assigning task to the first available employee for simplicity
		employees.get(0).tasks().add(task);
		System.out.println("Task assigned successfully!");
	}

	private static void viewTasks() {
		if (tasks.isEmpty()) {
			System.out.println("No tasks available.");
			return;
		}

		System.out.println("\n====== All Tasks ======");
		tasks.forEach(task -> System.out.println(task));
	}

	private static void filterPendingTasks() {
		List<Task> pendingTasks = tasks.stream().filter(task -> !task.completed()).collect(Collectors.toList());

		if (pendingTasks.isEmpty()) {
			System.out.println("No pending tasks.");
		} else {
			System.out.println("\n====== Pending Tasks ======");
			pendingTasks.forEach(task -> System.out.println(task));
		}
	}
}