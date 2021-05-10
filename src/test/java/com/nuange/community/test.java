package com.nuange.community;

public class test {
    int x = 0;

    public class Runner implements Runnable {

        @Override
        public void run() {
            int c = 0;
            for (int i = 0; i < 4; i++) {
                c = x;
                System.out.println(c + "," + Thread.currentThread().getName());
                x = c + 2;
            }
        }
    }

    public static void main(String[] args) {
        new test().go();
    }

    public void go() {
        Runnable runner = new Runner();
        new Thread(runner).start();
        new Thread(runner).start();
    }
}
