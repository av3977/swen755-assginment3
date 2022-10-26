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
    public static Resource resource = new Resource();
    public static List<Thread> producerThreads = new ArrayList<>();

    public static void main(String[] args) {
        try {
            BlockingQueue jobQueue = new LinkedBlockingQueue<>();
            Consumer[] consumers = new Consumer[30];
            Thread[] consumersThreads = new Thread[30];
            ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(20, 25, 60L, SECONDS, jobQueue);

            // start 1 producer by default.
            Thread producerThread = new Thread(new Producer(resource, "Producer-"+producerThreads.size()));
            producerThread.setPriority(Thread.MAX_PRIORITY);
            producerThreads.add(producerThread);
            poolExecutor.submit(producerThread);
            System.out.println("Number of Producer threads: " + producerThreads.size());
//            Thread.sleep(2000);

            for (int i = 0; i<30; i++) {
                int randomNumber = ThreadLocalRandom.current().nextInt(1, 9 + 1);
                consumers[i] = new Consumer("Consumer-" + String.valueOf(i), resource, randomNumber, System.currentTimeMillis());
                consumersThreads[i] = new Thread(consumers[i]);
//                consumersThreads[i].setPriority(randomNumber); // Priority scheduling.
                consumersThreads[i].setPriority(10 - randomNumber); // --> SJF [Shortest job first]
            }
            int initialConsumerProcess = 7;
            int secondConsumerBatch = 7;
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("--------Executing first " +initialConsumerProcess+ " consumers--------");
            for (int i = 0; i<initialConsumerProcess; i++) {
                poolExecutor.execute(consumersThreads[i]);
                consumers[i].setScheduled(true);
            }


            Timer starvationCheck = new Timer(20000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    for (int i = 0; i<consumers.length;i++) {
                        if (consumers[i].isScheduled() && consumers[i].isStarving()) {
                            System.out.println(consumers[i].getName() + " is STARVING.......");
                        }
                    }
                }
            });
            starvationCheck.setRepeats(true); // execute multiple times
            starvationCheck.start();
            // seen 8 threads until here
            Timer timer = new Timer(10000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    // FixMe: java.util.concurrent.RejectedExecutionException
                    int scaleUp = scaleUpProducers(jobQueue, poolExecutor.getActiveCount());
                    if (scaleUp >= producerThreads.size()) {
                        System.out.println("Scaling up by: " + scaleUp);
                        Thread producerThread = new Thread(new Producer(resource, "Producer-"+producerThreads.size()+1));
                        producerThread.setPriority(Thread.MAX_PRIORITY);
                        // scaling up with multiple producer when consumers are more in demand
                        if (poolExecutor.getActiveCount() >= 10) {
                            poolExecutor.setCorePoolSize(15);
                            poolExecutor.setMaximumPoolSize(20);
                        }
                        for (int i = 0; i<scaleUp; i++) {
                            poolExecutor.submit(producerThread);
                            producerThreads.add(producerThread);
                        }
                        System.out.println("Number of Producer threads: " + producerThreads.size());
                    }
                }
            });
            starvationCheck.setRepeats(true); // execute multiple times
            starvationCheck.start();

            Timer checkOverProduction = new Timer(10000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                   if (checkOverProduction(poolExecutor.getActiveCount())) {
                       if (producerThreads.size() > 1) {
                           System.out.println("---------OVER PRODUCTION SCENARIO--------");
                           poolExecutor.remove(producerThreads.remove(producerThreads.size()-1));
                       }
                   } else {
                       System.out.println("---------ENOUGH PRODUCTION SCENARIO--------" + producerThreads.size());
                   }
                }
            });
            checkOverProduction.setRepeats(true); // execute multiple times
            checkOverProduction.start();

            Thread.sleep(20000);
            System.out.println("--------Scheduling next " +secondConsumerBatch+ " consumers--------");
            System.out.println("Job Queue: " + poolExecutor.getActiveCount());

            // Core pool: 10
            // Max pool: 15
            // room outside the store: 5 -- since they are not in the store, there priority doesn't matter.

            // 1 + 7 = 8 (initially)
            // wait for 20 seconds
            // 10 more consumers to the store

            // 10-8 = 2

            // room: 2 + 5 = 7
            // 10 - 7 = -3 (java.util.concurrent.RejectedExecutionException)

            for (int i = 0; i<secondConsumerBatch; i++) {
                poolExecutor.execute(consumersThreads[i+initialConsumerProcess]);
                consumers[i+initialConsumerProcess].setScheduled(true);
            }


            poolExecutor.shutdown();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int scaleUpProducers(BlockingQueue queue, int activeProcess) {
        int activeJobCount = Math.max(queue.size(), activeProcess);
        System.out.println("Scale up queue: "+ activeJobCount);
        if (activeJobCount >= 8 && activeJobCount < 12) {
            return 1;
        } else if (activeJobCount >= 12 && activeJobCount < 16) {
            return 2;
        } else if (activeJobCount >= 16 && activeJobCount < 20) {
            return 3;
        } else if (activeJobCount >= 20 && activeJobCount < 24) {
            return 4;
        } else if (activeJobCount >= 24) {
            return 5;
        }
        return 0;
    }

    private static boolean checkOverProduction(int activeProcesses) {
        System.out.println("Resource queue size: " + resource.getQueue());
        if (resource.getQueue().size() > 10 )
            return true;
        if (producerThreads.size() > 1 && activeProcesses < 10)
            return true;
        return false;
    }
}
