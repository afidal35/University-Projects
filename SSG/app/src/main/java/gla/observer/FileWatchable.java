package gla.observer;

import gla.files.FileInfo;
import gla.utils.Printer;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class for watchable file.
 */
public abstract class FileWatchable extends FileObserver implements Observable {
  private final Set<FileObserver> observers = new HashSet<>();

  public FileWatchable(FileInfo fileInfo) {
    super(fileInfo);
  }

  /**
   * Constructor.
   *
   * @param fileInfo   file info used
   * @param checkExist if you need to check existence
   * @throws FileNotFoundException if file not exist
   */
  public FileWatchable(FileInfo fileInfo, boolean checkExist) throws FileNotFoundException {
    super(fileInfo);
    if (checkExist) {
      fileInfo.throwIfNotExist();
    }
  }

  /**
   * notify observer file is in create or modify, to build the file.
   *
   * @param incremental if true notify modify est create
   */
  public void build(boolean incremental) {
    if (incremental) {
      notifyModified();
    } else {
      notifyCreated();
    }
  }


  /**
   * Add observer to notify.
   *
   * @param observer Observer to add
   */
  public synchronized void addObserver(FileObserver observer) {
    observers.add(observer);
  }


  /**
   * Remove observer to notify.
   *
   * @param observer Observer to remove
   */
  public synchronized void removeObserver(FileObserver observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyCreated() {
    getFileInfo().updateLastFileTime();
    observers.forEach(observer -> {
      observer.updateLastFileTime(this);
      observer.onCreated();
    });
  }


  @Override
  public void notifyModified() {
    getFileInfo().updateLastFileTime();
    new HashSet<>(observers)
        .forEach(observer -> {
          if (!this.hasBeenModifiedAfter(observer)) {
            Printer.fileAlreadyUpToDate(observer.getFileInfo());
            return;
          }
          observer.updateLastFileTime(this);
          observer.onModified();
        });
  }


  @Override
  public void notifyDelete() {
    getFileInfo().updateLastFileTimeNow();
    observers.forEach(observer -> {
      observer.updateLastFileTime(this);
      observer.onDeleted();
    });
  }


}
