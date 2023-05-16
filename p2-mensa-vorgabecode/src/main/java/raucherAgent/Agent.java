package raucherAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Agent extends Thread {

  Tisch tisch;

  public Agent(String name, Tisch tisch) {
    super.setName(name);
    this.tisch = tisch;
  }

  @Override
  public void run() {
    try {
      legeAufDenTisch();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void legeAufDenTisch() throws InterruptedException {
    while(!isInterrupted()) {
      int rand = new Random().nextInt(3);
      List<Zutat> zutaten = new ArrayList<>(List.of(Zutat.PAPIER, Zutat.STREICHHOLZ, Zutat.TABAK));
      zutaten.remove(rand);
      tisch.enter(zutaten);
    }
  }
}
