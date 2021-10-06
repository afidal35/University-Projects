package gla.observer;


import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import gla.folders.Folder;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * Watcher use in directory or on file.
 */
public abstract class Watcher implements Runnable {
  private final Path path;
  private WatchService watchService = null;
  private WatchKey key;

  /**
   * Folder constructor.
   *
   * @param path Path of folder
   */
  public Watcher(Path path) {
    this.path = path;
  }

  /**
   * Folder constructor.
   *
   * @param folder folder to watch
   */
  public Watcher(Folder folder) {
    this(folder.toPath());
  }

  /**
   * Path to watch.
   *
   * @return path
   */
  protected Path toPath() {
    return path;
  }

  /**
   * Getter observable to run on event.
   *
   * @param fileName name of file notify by event
   * @return Observable
   */
  public abstract Observer getObserver(String fileName);

  private Observer getObserver(WatchEvent<?> event) {
    @SuppressWarnings("unchecked")
    WatchEvent<Path> ev = (WatchEvent<Path>) event;
    String fileName = ev.context().getFileName().toString();
    return getObserver(fileName);
  }

  private void notify(WatchEvent<?> event) {
    Observer observ = getObserver(event);
    if (observ == null) {
      return;
    }

    switch (event.kind().name()) {
      case "ENTRY_CREATE":
        observ.onCreated();
        break;

      case "ENTRY_MODIFY":
        observ.onModified();
        break;

      case "ENTRY_DELETE":
        observ.onDeleted();
        break;

      default:
        break;
    }
  }

  /**
   * Stop watching service.
   */
  public synchronized void stop() {
    try {
      if (watchService != null) {
        if (key != null) {
          key.cancel();
        }
        watchService.close();
        watchService = null;
      }
    } catch (IOException e) {
      // ignore
    }
  }

  /**
   * Run watching service.
   */
  @Override
  public void run() {
    if (path == null) {
      throw new RuntimeException("Cannot watch a path null");
    }
    try {
      watchService = FileSystems.getDefault().newWatchService();
      path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

      while (watchService != null && (key = watchService.take()) != null) {
        for (WatchEvent<?> event : key.pollEvents()) {
          notify(event);
        }
        if (!key.reset()) {
          break;
        }
      }
    } catch (InterruptedException | ClosedWatchServiceException e) {
      // ignore
    } catch (IOException e) {
      System.err.println(e.getMessage() + "-" + e);
    } finally {
      stop();
    }
  }
}
