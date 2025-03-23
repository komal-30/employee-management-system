package employee.management.system;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcurrencyExample {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        Callable<String> taskProcessor = () -> {
            Thread.sleep(1000);
            return "Task Processed";
        };

        Future<String> result = executor.submit(taskProcessor);
        executor.shutdown();
    }
}
