package ru.effectivemobile.testtask;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CustomTimer extends TimerTask {

    double deposit = 100;
    double initialDeposit = deposit;

    @Override
    public void run() {
        deposit = deposit * 1.05;
        System.out.println(deposit);
        if (deposit > initialDeposit * 3.07){
            synchronized (Test.obj){
                Test.obj.notify();
            }
        }
    }


}

class Test {

    protected static Test obj;

    public static void main(String[] args) throws InterruptedException {

        obj = new Test();

        Timer timer = new Timer();

        CustomTimer customTimer = new CustomTimer();

        Date date = new Date();

        timer.schedule(customTimer, date, 100);

        System.out.println("Start");

        synchronized (obj) {
            obj.wait();
            timer.cancel();
            System.out.println(timer.purge());
        }
    }
}
