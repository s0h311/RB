package raucherAgent;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Raucher extends Thread {

  public static final Logger LOGGER = LoggerFactory.getLogger(Raucher.class);
  private String zutat;
  private Object monitor;
  private String status;

  public Raucher(String name, Object monitor, String zutat) {
    super.setName(name);
    this.monitor = monitor;
    status = "will eine endlich rauchen";
    this.zutat = zutat;
  }

  @Override
  public void run() {
    try {
      rauchen();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public String legeAufDenTisch() {
    return zutat;
  }

  public void rauchen() throws InterruptedException {
    synchronized (monitor) {
      status = "kann endlich rauchen";
      LOGGER.info(getName() + " raucht jetzt");
      sleep(new Random().nextInt(1001));
      LOGGER.info(getName() + " ist zuende");
      monitor.notifyAll();
      status = "will eine endlich rauchen";
    }
  }

  public String getStatus() {
    return getName() + status;
  }
}
