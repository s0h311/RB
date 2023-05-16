package raucherAgent;

import java.util.ArrayList;
import java.util.List;

public class Tisch implements BoundedBuffer<Zutat> {

  private List<Zutat> zutaten;
  private int groesse = 2;

  public Tisch() {
    zutaten = new ArrayList<>();
  }

  @Override
  public synchronized void enter(List<Zutat> zutaten) throws InterruptedException {
    while (this.zutaten.size() == groesse) {
      this.wait();
    }
    System.err.println(AnsiColor.GREEN + "Es befinden sich %s Zutaten auf dem Tisch".formatted(this.zutaten.size()));
    this.zutaten = zutaten;
    System.err.println(AnsiColor.RED + "Einer der Agenten legt die Zutaten: %s und %s".formatted(zutaten.get(0).name(), zutaten.get(1).name()));
    this.notifyAll();
  }

  @Override
  public synchronized List<Zutat> remove(String name) throws InterruptedException {
    while (zutaten.size() == 0) {
      this.wait();
    }
    List<Zutat> oldList = zutaten;
    zutaten = new ArrayList<>();
    System.err.println(AnsiColor.YELLOW + "%s hat die Zutaten genommen".formatted(name));
    this.notifyAll();
    return oldList;
  }

  public synchronized List<Zutat> getZutaten() {
    return zutaten;
  }
}
