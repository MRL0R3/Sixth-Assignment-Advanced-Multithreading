package MonteCarloPI;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;



public class MonteCarloPi {

    static final long NUM_POINTS = 50_000_000L;
    static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        // Without Threads
        System.out.println("Single threaded calculation started: ");
        long startTime = System.nanoTime();
        double piWithoutThreads = estimatePiWithoutThreads(NUM_POINTS);
        long endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (single thread): " + piWithoutThreads);
        System.out.println("Time taken (single threads): " + (endTime - startTime) / 1_000_000 + " ms");

        // With Threads
        System.out.printf("Multi threaded calculation started: (your device has %d logical threads)\n", NUM_THREADS);
        startTime = System.nanoTime();
        double piWithThreads = estimatePiWithThreads(NUM_POINTS, NUM_THREADS);
        endTime = System.nanoTime();
        System.out.println("Monte Carlo Pi Approximation (Multi-threaded): " + piWithThreads);
        System.out.println("Time taken (Multi-threaded): " + (endTime - startTime) / 1_000_000 + " ms");


        System.out.println("\nRunning full benchmark and exporting to CSV...");
        runBenchmarkAndExport();

        // TODO: After completing the implementation, reflect on the questions in the description of this task in the README file
        //       and include your answers in your report file.
    }

    // Monte Carlo Pi Approximation without threads
    public static double estimatePiWithoutThreads(long numPoints) {
        // TODO: Implement this method to calculate Pi using a single thread

        long insideCircle = 0;

        for (long i = 0; i < numPoints; i++) {
            double x = Math.random();
            double y = Math.random();

            if (x * x + y * y <= 1.0) {
                insideCircle++;
            }
        }

        return 4.0 * insideCircle / numPoints;
    }

    public static void runBenchmarkAndExport() throws IOException, InterruptedException, ExecutionException {
        long[] testSizes = {1_000_000L, 5_000_000L, 10_000_000L, 25_000_000L, 50_000_000L};
        int threads = NUM_THREADS;

        try (FileWriter csvWriter = new FileWriter("benchmark.csv")) {
            // Clear and descriptive header
            csvWriter.append("Num Points,Single-Thread Time (ms),Multi-Thread Time (ms),PI (Single),PI (Multi)\n");

            for (long numPoints : testSizes) {
                System.out.printf("Benchmarking with %,d points...\n", numPoints);

                // Single-threaded
                long start = System.nanoTime();
                double piSingle = estimatePiWithoutThreads(numPoints);
                long end = System.nanoTime();
                long singleTime = (end - start) / 1_000_000;

                // Multi-threaded
                start = System.nanoTime();
                double piMulti = estimatePiWithThreads(numPoints, threads);
                end = System.nanoTime();
                long multiTime = (end - start) / 1_000_000;

                // Properly formatted for spreadsheet software
                csvWriter.append(String.format("%,d,%d,%d,%.8f,%.8f\n",
                        numPoints, singleTime, multiTime, piSingle, piMulti));
            }

            System.out.println("✅ Benchmark complete. Results saved to benchmark.csv");
        }
    }


    // Monte Carlo Pi Approximation with threads
    public static double estimatePiWithThreads(long numPoints, int numThreads) throws InterruptedException, ExecutionException {
        // TODO: Implement this method to calculate Pi using multiple threads

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // HINT: You may need to create a variable to *safely* keep track of points that fall inside the circle
        // HINT: Each thread should generate and process a subset of the total points

        // TODO: After submitting all tasks, shut down the executor to prevent new tasks
        // TODO: wait for the executor to be fully terminated
        // TODO: Calculate and return the final estimation of Pi

        long pointsPerThread = numPoints / numThreads;

        var futures = new ArrayList<Future<Long>>();

        for (int i = 0; i < numThreads; i++) {
            futures.add(executor.submit(() -> {
                long inside = 0;
                for (long j = 0; j < pointsPerThread; j++) {
                    double x = Math.random();
                    double y = Math.random();
                    if (x * x + y * y <= 1.0) {
                        inside++;
                    }
                }
                return inside;
            }));
        }

        // Wait for all threads and sum their results
        long totalInside = 0;
        for (var future : futures) {
            totalInside += future.get();
        }

        executor.shutdown();
        executor.awaitTermination(1, java.util.concurrent.TimeUnit.MINUTES);
            return 4.0 * totalInside / numPoints;
    }
}

