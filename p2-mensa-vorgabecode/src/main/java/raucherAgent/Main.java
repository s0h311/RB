package raucherAgent;

public class Main {
  public static void main(String[] args) throws InterruptedException {
    Tisch t = new Tisch();
    t.start();

    Thread.sleep(6_000);
    t.interrupt();
  }
}
