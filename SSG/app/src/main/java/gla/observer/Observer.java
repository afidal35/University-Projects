package gla.observer;

/**
 * Observer interface.
 */
public interface Observer {

  /**
   * Called on a created action.
   */
  void onCreated();

  /**
   * Called on a modified action.
   */
  void onModified();

  /**
   * Called on a deletion action.
   */
  void onDeleted();
}