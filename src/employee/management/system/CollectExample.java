package employee.management.system;

import java.util.stream.Collectors;

public class CollectExample {
    public static void main(String[] args) {
    	StreamExample streamExample = new StreamExample();
        var tasksByPriority = streamExample.tasks.stream()
            .collect(Collectors.groupingBy(Task::priority));

        var taskTitles = streamExample.tasks.stream()
            .map(Task::title)
            .collect(Collectors.toList());
    }

    static String getDepartmentCategory(Department dept) {
        return switch (dept.name()) {
            case "HR" -> "Human Resources";
            case "IT" -> "Technical Team";
            default -> "General";
        };
    }
}