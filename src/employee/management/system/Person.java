package employee.management.system;

import java.time.LocalDateTime;
import java.util.List;

sealed interface Person permits Employee {}

record Employee(String id, String name, Department department, List<Task> tasks) implements Person {}
record Task(String title, String description, LocalDateTime deadline, boolean completed, int priority) {}
record Department(String name, List<Employee> employees) {}

