package employee.management.system;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class SortingExample {
	public static void main(String[] args) {
		List<Task> tasks = List.of(new Task("Task A", "Urgent", LocalDateTime.now().plusDays(1), false, 1),
				new Task("Task B", "Low Priority", LocalDateTime.now().plusDays(5), true, 3));

		tasks.stream().sorted(Comparator.comparing(Task::priority)).forEach(t -> System.out.println(t.title()));
	}
}
