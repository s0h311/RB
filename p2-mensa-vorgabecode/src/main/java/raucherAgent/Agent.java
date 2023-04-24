package raucherAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Agent extends Thread {

  public Agent(String name) {
    super.setName(name);
  }

  @Override
  public void run() {

  }

  public List<String> legeAufDenTisch() {
    int rand = new Random().nextInt(3);
    List<String> zutaten = new ArrayList<>(List.of("tabak", "papier", "streichholz"));
    zutaten.remove(rand);
    return zutaten;
  }
}
