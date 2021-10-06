package gla.folders.grouper;

import gla.Watchdog;
import gla.folders.Folder;
import gla.observer.Observer;
import gla.observer.Watcher;
import java.nio.file.Path;

/**
 * Group watcher.
 */
public abstract class WatcherGrouper extends Watcher {
  boolean running = false;
  private Folder defaultFolder;
  private Watcher watcherDefault;
  private Thread treadDefault;
  private Watcher watcherTheme;
  private Thread threadTheme = null;

  /**
   * Constructor.
   *
   * @param defaultFolder default folder to used
   * @param themeFolder   theme folder to used.
   */
  public WatcherGrouper(Folder defaultFolder, Folder themeFolder) {
    super((Path) null);
    setDefaultFolder(defaultFolder);
    setThemeWatcher(themeFolder);
  }

  /**
   * Constructor with grouper.
   *
   * @param grouper grouper use to have default and theme folder.
   */
  public WatcherGrouper(Grouper grouper) {
    this(grouper.getDefaultFolder(), grouper.getThemeFolder());
  }

  private static void stopWatcher(Watcher watcher, Thread thread) {
    try {
      watcher.stop();
      thread.join(Watchdog.WAITING_TIME);
    } catch (InterruptedException e) {
      thread.interrupt();
    }
  }

  @Override
  public Observer getObserver(String fileName) {
    throw new RuntimeException("Please no set this method");
  }

  /**
   * Getter observer in default folder.
   *
   * @param fileName name of file
   * @return Observer
   */
  public abstract Observer getObserverDefault(String fileName);

  /**
   * Getter observer in theme folder.
   *
   * @param fileName name of file
   * @return Observer
   */
  public abstract Observer getObserverTheme(String fileName);

  /**
   * setter theme to used (stop and theme watcher), if running.
   *
   * @param folder theme folder to link
   */
  public void setThemeWatcher(Folder folder) {
    stopThemeWatch();
    if (folder == null) {
      return;
    }

    watcherTheme = new Watcher(folder) {
      @Override
      public Observer getObserver(String fileName) {
        if (defaultFolder.contains(fileName)) {
          return null;
        }
        return getObserverTheme(fileName);
      }
    };
    threadTheme = new Thread(watcherTheme);

    startThemeWatchIfRunning();
  }

  @Override
  public void run() {
    running = true;

    treadDefault.start();
    startThemeWatchIfRunning();

    joinMainThread();
    joinThemeThread();
  }

  @Override
  public synchronized void stop() {
    stopWatcher(watcherDefault, treadDefault);
    stopThemeWatch();
    running = false;
  }

  private void startThemeWatchIfRunning() {
    if (running && threadTheme != null) {
      threadTheme.start();
    }
  }

  private void stopThemeWatch() {
    if (running && threadTheme != null) {
      stopWatcher(watcherTheme, threadTheme);
      threadTheme = null;
      watcherTheme = null;
    }
  }

  private void joinMainThread() {
    try {
      treadDefault.join();
    } catch (InterruptedException e) {
      treadDefault.interrupt();
    }
  }

  private void joinThemeThread() {
    if (threadTheme != null) {
      try {
        threadTheme.join();
      } catch (InterruptedException e) {
        threadTheme.interrupt();
      }
    }
  }

  private void setDefaultFolder(Folder defaultFolder) {
    this.defaultFolder = defaultFolder;
    watcherDefault = new Watcher(defaultFolder) {
      @Override
      public Observer getObserver(String fileName) {
        return getObserverDefault(fileName);
      }
    };
    treadDefault = new Thread(watcherDefault);
  }

}
