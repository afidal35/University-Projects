package gla.observer;

/**
 * Observer interface.
 */
public interface Observable {
  /**
   * Notify Created to observers.
   */
  void notifyCreated();

  /**
   * Notify modified, if observer modify before.
   */
  void notifyModified();

  /**
   * Notify deleted to observer.
   */
  void notifyDelete();

}