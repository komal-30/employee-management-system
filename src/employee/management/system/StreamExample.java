package employee.management.system;

import java.time.LocalDateTime;
import java.util.List;

public class StreamExample {
	List<Task> tasks = List.of(
            new Task("Task A", "Urgent Task", LocalDateTime.now().plusDays(1), false, 1),
            new Task("Task B", "Low Priority", LocalDateTime.now().plusDays(5), true, 3)
        );
    public static void main(String[] args) {
    	StreamExample streamExample = new StreamExample();
        long pendingCount = streamExample.tasks.stream().filter(t -> !t.completed()).count();
        System.out.println("Pending tasks: " + pendingCount);
    }
}

