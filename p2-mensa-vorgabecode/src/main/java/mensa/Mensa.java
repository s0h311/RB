package mensa;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raucherAgent.Raucher;

/**
 * Mensa.java: Erzeuge Kassen und fuege sie einer Kassenliste hinzu. Erzeuge
 * Studenten-Threads und starte diese. Nach Ende der Laufzeit werden alle
 * Studententhreads unterbrochen.
 */
public class Mensa {

	public static final Logger LOGGER = LoggerFactory.getLogger(Raucher.class);
	private static final int ANZAHL_STUDENTEN = 100;
	private static final int ANZAHL_KASSEN = 3;

	public LinkedList<Kasse> kassenliste;

	private LinkedList<Student> studentenListe;

	public ReentrantLock auswahlMutex = new ReentrantLock(true); // Faire Warteschlange vor dieser Kasse!

	public void starteSimulation() {
		Kasse aktKasse;
		Student aktStudent;
		int i;

		kassenliste = new LinkedList<Kasse>();
		studentenListe = new LinkedList<Student>();

		/* Kassen erzeugen */
		for (i = 0; i < ANZAHL_KASSEN; i++) {
			aktKasse = new Kasse(i);
			kassenliste.add(aktKasse);
		}
		/* Studenten erzeugen */
		for (i = 0; i < ANZAHL_STUDENTEN; i++) {
			aktStudent = new Student(String.format("Studi-%2d", i), this);
			studentenListe.add(aktStudent);
			aktStudent.start();
		}

		/* Laufzeit abwarten */
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		for (i = 0; i < ANZAHL_STUDENTEN; i++) {
			aktStudent = studentenListe.get(i);
			aktStudent.interrupt();
		}

		System.err.println("-------------------- THE END -------------------");
	}

	public void showScore() {
		/* Anzeige der Anzahl an Studis pro Kasse in einer Zeile */
		System.err.print(" --- Kassenliste ");
		for (int i = 0; i < ANZAHL_KASSEN; i++) {
			System.err.print(String.format("  Kasse %s: %2d", kassenliste.get(i).getKassenName(),
					kassenliste.get(i).getAnzahlStudenten()));
		}
		System.err.println();
	}

	public static void main(String args[]) {
		new Mensa().starteSimulation();
	}
}
