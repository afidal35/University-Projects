package gla;

import gla.files.toml.SiteToml;
import gla.folders.InputFolder;
import gla.folders.OutputFolder;
import gla.folders.contentfolder.ContentFolder;
import gla.folders.staticfolder.StaticFolder;
import gla.folders.templatefolder.TemplateFolder;
import gla.observer.Watcher;
import gla.utils.Printer;
import java.util.HashMap;
import java.util.Map;
import sun.misc.Signal;

/**
 * Watchdog class, observing file changes and re-building in consequences.
 */
public class Watchdog {
  public static final long WAITING_TIME = 1000;
  private final InputFolder inputFolder;
  private final OutputFolder outputFolder;
  private final SiteToml siteToml;
  private final ContentFolder contentFolder;
  private final TemplateFolder templateFolder;
  private final StaticFolder staticFolder;

  private final Map<Watcher, Thread> mapThread = new HashMap<>();

  /**
   * Constructor.
   *
   * @param inputFolder  input folder used.
   * @param outputFolder output folder used.
   */
  public Watchdog(InputFolder inputFolder,
                  OutputFolder outputFolder) {
    this.inputFolder = inputFolder;
    this.outputFolder = outputFolder;
    siteToml = inputFolder.getSiteToml();
    contentFolder = inputFolder.getContentFolder();
    templateFolder = siteToml.getTemplateFolder();
    staticFolder = siteToml.getStaticFolder();
  }


  /**
   * start watch a project.
   */
  public void start() {
    Printer.startWatching(inputFolder, outputFolder);
    startWatch(contentFolder.getWatcher(templateFolder, outputFolder));
    startWatch(staticFolder.getWatcher(outputFolder));
    startWatch(templateFolder.getWatcher());
    startWatch(siteToml.getWatcher(outputFolder));
    // if ctrl-c
    Signal.handle(new Signal("INT"), signal -> stop());
  }

  /**
   * Stop watching project.
   */
  public synchronized void stop() {
    mapThread.forEach((watcher, thread) -> {
      watcher.stop();
      try {
        thread.join(WAITING_TIME);
      } catch (InterruptedException e) {
        thread.interrupt();
      }
    });
    mapThread.clear();
    Printer.stopWatching(inputFolder, outputFolder);
  }

  /**
   * Stop watching project.
   *
   * @param waitingTime time to wait before stop
   */
  public void stop(long waitingTime) {
    try {
      Thread.sleep(waitingTime);
    } catch (InterruptedException e) {
      // ignore
    } finally {
      stop();
    }
  }

  /**
   * Start a watcher in a thread.
   *
   * @param watcher watcher to start.
   */
  private void startWatch(Watcher watcher) {
    Thread thread = new Thread(watcher);
    mapThread.put(watcher, thread);
    thread.start();
  }

}