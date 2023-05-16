package raucherAgent;

import java.util.List;

public interface BoundedBuffer<E> {

  /* Lege ein Item in den Puffer */
  public void enter(List<E> item) throws InterruptedException;

  /* Entnimm dem Puffer das Item */
  public List<E> remove(String name) throws InterruptedException;
}
