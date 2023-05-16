package raucherAgent;

import java.util.Random;

public class Raucher extends Thread {

  private Zutat zutat;
  private Tisch tisch;

  public Raucher(String name, Zutat zutat, Tisch tisch) {
    super.setName(name);
    this.zutat = zutat;
    this.tisch = tisch;
  }

  @Override
  public void run() {
    try {
      nimmDieZutaten();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void nimmDieZutaten() throws InterruptedException {
    while(!isInterrupted()) {
      if(!tisch.getZutaten().contains(zutat)) {
        tisch.remove(getName());
        System.err.println(AnsiColor.BLUE + getName() + " raucht jetzt");
        rauchen();
      }
    }
  }

  public void rauchen() throws InterruptedException {
    sleep(new Random().nextInt(1001));
    System.err.println(AnsiColor.BLUE + getName() + " ist zuende");
  }
}
