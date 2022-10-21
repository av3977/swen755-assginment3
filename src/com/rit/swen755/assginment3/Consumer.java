package com.rit.swen755.assginment3;

public class Consumer implements Runnable {
    private String name;
    private Resource resource;
    private int requestNumberOfItems;
    public Consumer(String name, Resource resource, int requestNumberOfItems) {
        this.name = name;
        this.resource = resource;
        this.requestNumberOfItems = requestNumberOfItems;
        System.out.println(this.name + " entered the store to request " + this.requestNumberOfItems + " items.");
    }

    @Override
    public void run() {
        try {
            Integer data = resource.get();
            while (true) {
                Thread.sleep(1000);
                if (data!=null) {
                    System.out.println(this.name + " processed data from resource: " + data);
                    this.requestNumberOfItems--;
                    if (this.requestNumberOfItems == 0)
                        break;
                } else {
//                    System.out.println(this.name + " is waiting... ");
                }
                data = resource.get();
            }

            System.out.println("Consumer " + this.name + " finished its job; terminating.");
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
