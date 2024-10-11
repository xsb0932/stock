public class DemoDeadLock {


    public static void main(String[] args) {
        DinnerThread dinnerThread = new DinnerThread();
        new Thread(dinnerThread,"Jack").start();
        new Thread(dinnerThread,"Rose").start();
    }
}

class DinnerThread implements Runnable{

    String lock1 = "第1根筷子";
    String lock2 = "第2根筷子";

    @Override
    public void run() {
        if (Thread.currentThread().getName().equals("Jack")){
            synchronized (lock1){
                System.out.println("Jack拿到"+lock1);
                synchronized (lock2){
                    System.out.println("Jack拿到"+lock2);
                    System.out.println(Thread.currentThread().getName()+ "开始夹东西吃");
                }
            }
        }else{
            synchronized (lock2){
                System.out.println("Rose拿到"+lock2);
                synchronized (lock1){
                    System.out.println("Rose拿到"+lock1);
                    System.out.println(Thread.currentThread().getName()+ "开始夹东西吃");
                }
            }
        }
    }
}
