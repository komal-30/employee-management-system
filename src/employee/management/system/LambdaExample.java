package employee.management.system;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LambdaExample {
    public static void main(String[] args) {
        Consumer<Employee> printEmployee = emp -> System.out.println("Employee: " + emp.name());
        Predicate<Task> isPending = task -> !task.completed();
        Supplier<Task> taskSupplier = () -> new Task("New Task", "Generated Task", LocalDateTime.now().plusDays(2), false, 3);
        Function<Task, String> taskDescription = task -> "Task: " + task.title() + " - Priority: " + task.priority();
    }
}