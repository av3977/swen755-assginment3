import com.rit.swen755.assginment3.Consumer;
import com.rit.swen755.assginment3.Producer;
import com.rit.swen755.assginment3.Resource;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ApplicationRunner {
    public static int numberOfProducers = 0;
    public static void main(String[] args) {
        try {
            Resource resource = new Resource();
            BlockingQueue jobQueue = new LinkedBlockingQueue<>();
            Consumer[] consumers = new Consumer[30];
            Thread[] consumersThreads = new Thread[30];
            List<Future> producerThreads = new ArrayList<>();
            ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(10, 15, 60L, SECONDS, jobQueue);

            // start 1 producer by default.
            Thread producerThread = new Thread(new Producer(resource, "Producer-"+ApplicationRunner.numberOfProducers));
            producerThread.setPriority(Thread.MAX_PRIORITY);
            producerThreads.add(poolExecutor.submit(producerThread));
            ApplicationRunner.numberOfProducers++;

            for (int i = 0; i<30; i++) {
                int randomNumber = ThreadLocalRandom.current().nextInt(1, 9 + 1);
                consumers[i] = new Consumer("Consumer-" + String.valueOf(i), resource, randomNumber, System.currentTimeMillis());
                consumersThreads[i] = new Thread(consumers[i]);
                consumersThreads[i].setPriority(randomNumber);
            }
            int initialConsumerProcess = 7;

            System.out.println("------------- -------------------------------------------------------------------");
            System.out.println("--------Executing first " +initialConsumerProcess+ " consumers--------");
            for (int i = 0; i<initialConsumerProcess; i++) {
                poolExecutor.execute(consumersThreads[i]);
            }

            Timer timer = new Timer(3000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    // FixMe: java.util.concurrent.RejectedExecutionException
                    int scaleUp = scaleUpProducers(jobQueue);
                    System.out.println("Scaling up by: " + scaleUp);
                    Thread producerThread = new Thread(new Producer(resource, "Producer-"+ApplicationRunner.numberOfProducers));
                    producerThread.setPriority(Thread.MAX_PRIORITY);
                    // scaling up with multiple producer when consumers are more in demand
                    for (int i = 0; i<scaleUp; i++) {
                        producerThreads.add(poolExecutor.submit(producerThread));
                        ApplicationRunner.numberOfProducers++;
                    }
                }
            });
            timer.setRepeats(true); // execute multiple times
            timer.start();


            Timer starvationMonitor = new Timer(9000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                   for (int i = 0;i<jobQueue.size()-1;i++)
                       if (isStarving(consumers[i]))
                           System.out.println(consumers[i].getName() + " IS STARVING AND REQUESTING FOR " + consumers[i].getRequestNumberOfItems());
                }
            });
            starvationMonitor.setRepeats(true); // execute multiple times
            starvationMonitor.start();

            System.out.println("--------Sleeping main thread for 20 seconds--------");
            Thread.sleep(20000);
            System.out.println("--------Scheduling next 5 consumers--------");
            for (int i = 0; i<5; i++)
                poolExecutor.execute(consumersThreads[i+initialConsumerProcess]);
            poolExecutor.shutdown();
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

    private static boolean isStarving(Consumer consumer) {
        return consumer.isStarving();
    }
}
