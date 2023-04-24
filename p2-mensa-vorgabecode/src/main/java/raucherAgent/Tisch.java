package raucherAgent;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tisch extends Thread{

  public static final Logger LOGGER = LoggerFactory.getLogger(Raucher.class);
  private List<Agent> agenten;
  private List<Raucher> raucher;
  private Object monitor = new Object();
  private int anzahlRunde = 0;

  public Tisch() {
    init();
  }

  private void init() {
    agenten = List.of(
        new Agent("Agent Smith 1"),
        new Agent("Agent Smith 2")
    );

    raucher = List.of(
        new Raucher("Trinity", monitor),
        new Raucher("Morpheus", monitor),
        new Raucher("Neo", monitor)
    );
  }

  @Override
  public void run() {
    try {
      runde();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void runde() throws InterruptedException {
    while(!isInterrupted()) {
      anzahlRunde++;
      Random rand = new Random();
      init();

      synchronized (monitor) {
        LOGGER.error("====RUNDE %s BEGINNNT====".formatted(anzahlRunde));
        Agent nextAgent = agenten.get(rand.nextInt(2));
        Raucher nextRaucher = raucher.get(rand.nextInt(3));
        List<String> kartenDesAgents = nextAgent.legeAufDenTisch();
        LOGGER.error("%s zieht die Karten: %s und %s".formatted(nextAgent.getName(), kartenDesAgents.get(0), kartenDesAgents.get(1)));
        String karteDesRauchers = nextRaucher.legeAufDenTisch();
        LOGGER.error("%s zieht die Karte: %s".formatted(nextRaucher.getName(), karteDesRauchers));

        while(kartenDesAgents.contains(karteDesRauchers)) {
          nextRaucher = raucher.get(rand.nextInt(3));
          karteDesRauchers = nextRaucher.legeAufDenTisch();
          LOGGER.error("NOCHMAL: %s zieht die Karte: %s".formatted(nextRaucher.getName(), karteDesRauchers));
        }
        try {
          nextRaucher.start();
          monitor.wait();
        } catch (InterruptedException e) {
          throw new RuntimeException("Game Over");
        }
      }
    }
  }
}
