import com.rit.swen755.assginment3.Consumer;
import com.rit.swen755.assginment3.Producer;
import com.rit.swen755.assginment3.Resource;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ApplicationRunner {
    public static void main(String[] args) {
        try {
            Resource resource = new Resource();
            Random random = new Random();
            Consumer[] consumers = new Consumer[20];
            ExecutorService threadPool = Executors.newFixedThreadPool(10);
            Future producerStatus = threadPool.submit(new Producer(resource));

            for (int i = 0; i<20; i++)
                consumers[i] = new Consumer("Consumer-" + String.valueOf(i), resource, random.nextInt(5));

            System.out.println("--------Executing first 10 consumers--------");
            for (int i = 0; i<10; i++)
                threadPool.execute(consumers[i]);

//            System.out.println("--------Sleeping main thread for 20 seconds--------");
//            Thread.sleep(20000);
//            System.out.println("--------Scheduling next 5 consumers--------");
//            for (int i = 0; i<5; i++)
//                threadPool.execute(consumers[i+5]);

            producerStatus.get();

            threadPool.shutdown();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
