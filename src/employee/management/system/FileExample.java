package employee.management.system;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileExample {
	public static void main(String[] args) throws IOException {
		Path path = Paths.get("tasks.txt");
		List<String> taskData = List.of("Task A - Urgent", "Task B - Low Priority");
		Files.write(path, taskData);
	}
}
