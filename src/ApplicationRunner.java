import com.rit.swen755.assginment3.Consumer;
import com.rit.swen755.assginment3.Producer;
import com.rit.swen755.assginment3.Resource;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ApplicationRunner {
    public static int numberOfProducers = 0;
    public static void main(String[] args) {
        try {
            Resource resource = new Resource();
            BlockingQueue jobQueue = new LinkedBlockingQueue<>();
            Consumer[] consumers = new Consumer[30];
            List<Future> producerThreads = new ArrayList<>();
//            ExecutorService threadPool = Executors.newFixedThreadPool(10);
            ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(10, 15, 60L, SECONDS, jobQueue);

            // start 1 producer by default.
            producerThreads.add(poolExecutor.submit(new Producer(resource, "Producer-"+ApplicationRunner.numberOfProducers)));
            ApplicationRunner.numberOfProducers++;

            for (int i = 0; i<30; i++) {
                int randomNumber = ThreadLocalRandom.current().nextInt(2, 5 + 1);
                consumers[i] = new Consumer("Consumer-" + String.valueOf(i), resource, randomNumber);
            }

            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("--------Executing first 10 consumers--------");
            for (int i = 0; i<7; i++) {
                poolExecutor.execute(consumers[i]);
            }

            Timer timer = new Timer(3000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    // FixMe: java.util.concurrent.RejectedExecutionException
                    int scaleUp = scaleUpProducers(jobQueue);
                    System.out.println("Scaling up by: " + scaleUp);
                    // scaling up with multiple producer when consumers are more in demand
                    for (int i = 0; i<scaleUp; i++) {
                        producerThreads.add(poolExecutor.submit(new Producer(resource, "Producer-"+ApplicationRunner.numberOfProducers)));
                        ApplicationRunner.numberOfProducers++;
                    }
                }
            });
            timer.setRepeats(true); // execute multiple times
            timer.start();

            System.out.println("Thread pool: " + poolExecutor);
            System.out.println("All jobs: " + jobQueue.size());

            System.out.println("--------Sleeping main thread for 20 seconds--------");
            Thread.sleep(20000);
            System.out.println("--------Scheduling next 5 consumers--------");
            for (int i = 0; i<5; i++)
                poolExecutor.execute(consumers[i+7]);
            poolExecutor.shutdown();
//            threadPool.shutdown();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int scaleUpProducers(BlockingQueue queue) {
        System.out.println("Scale up queue: "+ queue.size());
        if (queue.size() >= 6 && queue.size() < 10) {
            return 1;
        } else if (queue.size() >= 10 && queue.size() < 14) {
            return 2;
        } else if (queue.size() >= 14 && queue.size() < 18) {
            return 3;
        } else if (queue.size() >= 18 && queue.size() < 22) {
            return 4;
        } else if (queue.size() >= 22) {
            return 5;
        }
        return 0;
    }
}
