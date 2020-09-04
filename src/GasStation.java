import java.util.Deque;
import java.util.concurrent.*;


public class GasStation{

    private static final int STATIONSCOUNT = 3;

    //Очередь на заправку, синхронизирована, т.к. машины разбираются в 3 потока
    private final Deque<Machine> machineLine = new ConcurrentLinkedDeque<>();
    private final ExecutorService singleStationExecutorService = Executors.newFixedThreadPool(STATIONSCOUNT);
    private final Semaphore semaphore = new Semaphore(STATIONSCOUNT);
    private int threadsCount = 0;

    private Road road;

    public GasStation(Road road) {
        this.road = road;
    }

    public void visitStation(Machine machine){
        System.out.print(String.format("На заправку заехала машина %s:%s\n", machine.machineName, machine.uniqueName));

        machineLine.add(machine);
        System.out.print(String.format("Поставлена в очередь %s:%s\n", machine.machineName, machine.uniqueName));
        printMachinesLine();
        //Если машин в очереди больше, чем открытых колонок, и открытых колонок меньше 3х - открываем новую колонку
        if ((threadsCount < STATIONSCOUNT) && (machineLine.size() > threadsCount)){
            threadsCount++;
            System.out.print(String.format("Открыли колонку номер %s\n", threadsCount));
            singleStationExecutorService.submit(new SingleStation(semaphore));
        }
    }


    private void printMachinesLine(){
        synchronized (road) {
            for (Machine m : machineLine) {
                System.out.printf("[%s:%s]", m.machineName, m.uniqueName);
            }
            System.out.println();
        }
    }

    //Колонка на заправке
    private class SingleStation implements Runnable{
        private Semaphore semaphore;

        public SingleStation(Semaphore semaphore) {
            this.semaphore = semaphore;

        }

        @Override
        public void run() {
            try {
                /* Проверяем очередь с захватом семафора - тогда первая машина из очереди
                схватит первый освободившийся семафор */
                System.out.print(String.format("Колонка открыта:%s\n", Thread.currentThread().getName()));
                while (true) {
                    semaphore.acquire();
                    Machine machine = machineLine.pollFirst();
                    if (machine == null) {
                        semaphore.release();
                        continue;
                    }

                    System.out.print(String.format("Колонка %s %s:%s Перезаправляется\n", Thread.currentThread().getName(), machine.machineName, machine.uniqueName));
                    machine.refuel();
                    Thread.sleep(5000);

                    System.out.print(String.format("Колонка %s %s:%s перезаправилась и выехала на трасу\n", Thread.currentThread().getName(), machine.machineName, machine.uniqueName));

                    machine.refuelDone();
                    printMachinesLine();
                    semaphore.release();

                }

            } catch (InterruptedException e) {
                throw new RuntimeException("Неожиданное прерывание при захвате семафора", e);
            }

        }


    }

}
