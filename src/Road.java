import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Road {
    private final GasStation gasStation = new GasStation(this);

    protected GasStation getGasStation(){
        return gasStation;
    }

    public void start(){
        List<Machine> machines = new ArrayList<>();

        machines.add(new Car("Volvo", this));
        machines.add(new Car("Mercedes", this));
        machines.add(new Car("BMW", this));
        machines.add(new Car("Lada", this));
        machines.add(new Truck("MAZ", this));
        machines.add(new Truck("Volvo", this));
        machines.add(new Truck("Gazel", this));
        machines.add(new Bus("Volvo school bus", this));
        machines.add(new Bus("Ikarus", this));
        machines.add(new Bus("PAZ", this));

//        machines.add(new SuperEconomicalCar("Велосипед", this));

//        machines.add(new Bus("PAZ2", this));
//        machines.add(new Bus("PAZ3", this));
//        machines.add(new Bus("PAZ4", this));
//        machines.add(new Bus("PAZ5", this));
//        machines.add(new Bus("PAZ6", this));
//        machines.add(new Bus("PAZ7", this));
//        machines.add(new Bus("PAZ8", this));

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("_________________________________________________________________________");
                }
            }
        }).start();

        ExecutorService executorService = Executors.newCachedThreadPool();
        for(Machine m: machines){
            executorService.submit(m);
        }

        executorService.shutdown();
    }


}
