import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Car extends Thread implements Comparable<Car>{

  public static final Logger LOGGER = LoggerFactory.getLogger(Car.class);
  public int totalTime = 0;
  public int roundsCompleted = 0;
  public final String carName;
  public final int roundsNeeded;

  public Car(int carNumber,int roundsNeeded) {
    this.roundsNeeded = roundsNeeded;
    carName = "CAR_" + carNumber;
  }

  @Override
  public void run() {
    while (roundsCompleted < roundsNeeded) drive();
  }

  public void drive() {
    int time = new Random().nextInt(101);
    totalTime += time;
    roundsCompleted++;
    try {
      sleep(time);
    } catch (InterruptedException e) {
      LOGGER.warn(e.getMessage()); //Removing the interrupted flag
    }
  }

  public int getTotalTime() {
    return totalTime;
  }

  @Override
  public int compareTo(Car other) {
    return Integer.compare(getTotalTime(), other.getTotalTime());
  }

  @Override
  public String toString() {
    return carName + " in " + totalTime + " ms";
  }
}
