package raucherAgent;

import static java.lang.Thread.sleep;

import java.util.List;

public class Main {
  public static void main(String[] args) throws InterruptedException {
    Tisch tisch = new Tisch();

    List<Agent> agentenListe = List.of(
        new Agent("Agent Smith 1", tisch),
        new Agent("Agent Smith 2", tisch)
    );

    List<Raucher> raucherListe = List.of(
        new Raucher("Trinity", Zutat.PAPIER, tisch),
        new Raucher("Neo", Zutat.TABAK, tisch),
        new Raucher("Morpheus", Zutat.STREICHHOLZ, tisch)
    );

    agentenListe.forEach(Agent::start);
    raucherListe.forEach(Raucher::start);

    sleep(5_000);
    agentenListe.forEach(Thread::interrupt);
    raucherListe.forEach(Thread::interrupt);
  }
}
