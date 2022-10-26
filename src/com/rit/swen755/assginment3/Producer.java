package com.rit.swen755.assginment3;

public class Producer implements Runnable {

    private Resource resource;
    private String name;

    private int keepTrackOfProducedNumbers;
    public Producer(Resource resource) {
        this.resource = resource;
        this.keepTrackOfProducedNumbers = 0;
    }

    public Producer(Resource resource, String name) {
        this.resource = resource;
        this.name = name;
        this.keepTrackOfProducedNumbers = 0;
    }

    public boolean keepCheckOnQueue() {
         return resource.queue.size() == 0;
    }
    @Override
    public void run() {
        int item;
        try {
            while (true) {
                if (this.resource.continueProducing) {
                    for (Integer i = 1; i <= 5; i++) {
                        System.out.println("Produced Queue: " + resource.getQueue());
//                        if (resource.peek() == null || resource.peek() == 0)
//                            item = 1;
//                        else
//                            item = resource.peek()+1;
                        item = resource.put();
                        System.out.println(this.name + " produced: " + (item));
//                        resource.put(this.keepTrackOfProducedNumbers++);
                        Thread.sleep(2000);
                    }
                    this.resource.continueProducing = Boolean.FALSE;
                }

                Thread.sleep(1000);
                if (keepCheckOnQueue())
                    this.resource.continueProducing = Boolean.TRUE;
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
