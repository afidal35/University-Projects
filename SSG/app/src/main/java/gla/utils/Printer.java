package gla.utils;

import gla.files.FileInfo;
import gla.files.markdown.MarkdownFile;
import gla.files.staticfile.StaticFile;
import gla.folders.InputFolder;
import gla.folders.OutputFolder;
import java.util.Date;

/**
 * Class aiming at displaying messages to the client.
 */
public class Printer {

  /**
   * Displays a message when an html file build is done.
   *
   * @param file   HtmlFile file being watched
   * @param update an update or not
   */
  public static void fileEndingProcess(FileInfo file, boolean update) {
    String mode = update ? "updated" : "rendered";
    System.out.println("File [" + file.toPath() + "] has been correctly " + mode + ".");
  }

  /**
   * Displays a message when a file have been deleted.
   *
   * @param file FileInfo file being deleted
   */
  public static void fileHasBeenDeleted(FileInfo file) {
    System.out.println("File [" + file.toPath()
        + "] has been deleted since his dependency no longer exists inside the input directory.");
  }

  /**
   * Displays a message when a file fail to be deleted.
   *
   * @param file FileInfo file trying to be deleted
   */
  public static void errorOnDelete(FileInfo file) {
    System.err.println("File [" + file.toPath() + "] has not been deleted due to an error.");
  }

  /**
   * Displays a message when a file fail to be created.
   *
   * @param file FileInfo file trying to be created
   */
  public static void errorOnCreate(FileInfo file) {
    System.err.println("File [" + file.toPath() + "] has not been created due to an error.");
  }

  /**
   * Displays a message when the static folder has been deleted.
   *
   * @param from  deleted FROM
   * @param input InputFolder
   */
  public static void staticFolderHasBeenDeleted(OutputFolder from, InputFolder input) {
    System.out.println(
        "Static folder [static] has been deleted from [" + from.toPath()
            + "] since it does not exists inside " + "[" + input.toPath() + "] anymore.");
  }

  /**
   * Displays a message when updating a static file.
   *
   * @param update boolean to know if it is an update or a new file being copied
   * @param file   FileInfo file being watched
   */
  public static void staticFileProcess(boolean update, StaticFile file) {
    System.out.println(
        "Static file [" + file.toPath() + "] has been correctly " + (update ? "updated" : "copied")
            + ".");
  }

  /**
   * Displays a message when a file hasn't been modified.
   */
  public static void siteAlreadyUpToDate() {
    System.out.println("Your site is already up to date, nothing to do.");
  }

  /**
   * Displays a message when a file hasn't been modified.
   *
   * @param file File info
   */
  public static void fileAlreadyUpToDate(FileInfo file) {
    System.out.println("File [" + file.toPath() + "] is already up to date.");
  }

  /**
   * Displays a message when a site has been built correctly.
   *
   * @param from built FROM
   * @param to   built TO
   */
  private static void siteHasBeenBuilt(InputFolder from, OutputFolder to) {
    System.out.println(
        "Your site has been correctly built from [" + from.toPath() + "] to "
            + "[" + to.toPath() + "].");
  }

  /**
   * Displays a message when a site has been updated correctly.
   *
   * @param from built FROM
   */
  private static void siteHasBeenUpdated(InputFolder from) {
    System.out.println("Your site has been correctly updated from [" + from.toPath() + "].");
  }

  /**
   * Displays a message when the site building is done.
   *
   * @param update boolean to know if it is an update or a new build
   * @param from   built FROM
   * @param to     built TO
   */
  public static void buildSiteEndingProcess(boolean update, InputFolder from, OutputFolder to) {
    if (update) {
      siteHasBeenUpdated(from);
    } else {
      siteHasBeenBuilt(from, to);
    }
  }

  /**
   * Displays a message when a markdown file is a draft and not taken into account.
   *
   * @param md the Markdown file
   */
  public static void fileIsDraft(MarkdownFile md) {
    System.out.println("File [" + md.toPath() + "] is a draft and thus not taken into account.");
  }

  /**
   * Displays a message when a build is interrupted by a kill signal (ctrl - c).
   */
  public static void buildInterrupted() {
    System.out.println("Building process has been interrupted !");
  }

  /**
   * Displays a message when Watchdog has started.
   *
   * @param input the input folder being watched
   * @param output the output folder waiting for changes
   */
  public static void startWatching(InputFolder input, OutputFolder output) {
    System.out.println("\nWatchdog has started, watching changes between [" + input.toPath()
            + "] and [" + output.toPath() + "].\n");
  }

  /**
   * Displays a message when Watchdog has stopped.
   *
   * @param input the input folder that has been watched
   * @param output the output folder that has been waiting for changes
   */
  public static void stopWatching(InputFolder input, OutputFolder output) {
    System.out.println("\nWatchdog has stopped, changes between [" + input.toPath() + "] and ["
            + output.toPath() + "] are not being watched anymore.\n");
  }

  /**
   * Displays a message when Watchdog detects a change on a certain file.
   *
   * @param file the file which has changed
   */
  public static void detectedChangeOn(FileInfo file) {
    System.out.println("Watchdog has detected that [" + file.toPath() + "] has changed.");
  }

  /**
   * Displays a message when Watchdog detects a file deletion.
   *
   * @param file the file which has been deleted
   */
  public static void detectedDeleteOn(FileInfo file) {
    System.out.println("Watchdog has detected that [" + file.toPath() + "] has been deleted.");
  }

  /**
   * Displays a message when Watchdog detects a file creation.
   *
   * @param file the file which has been created
   */
  public static void detectedCreationOn(FileInfo file) {
    System.out.println("Watchdog has detected that [" + file.toPath() + "] has been created.");
  }

  /**
   * Displays a message when theme has changed.
   *
   * @param name new
   */
  public static void themeHasChanged(String name) {
    System.out.println("Theme has change to : " + name);
  }

  /**
   * Displays a message when a theme is removed.
   */
  public static void themeHasBeenRemoved() {
    System.out.println("Theme removed.");
  }

  /**
   * Displays a message when the server is starting.
   *
   * @param port server port
   * @param loopBackAddress loopback address
   */
  public static void startingServer(int port, String loopBackAddress) {
    System.out.println("Server has started.");
    System.out.println("Listening for connections on port : " + port + " ...\n");
    System.out.println("Server launched at : " + loopBackAddress + port + ".");
    System.err.println("Ctrl + C for exit !");
  }

  /**
   * Displays a message when the connection is opened.
   */
  public static void connectionOpened() {
    System.out.println("Connection opened - (" + new Date() + ").");
  }

  /**
   * Displays a message when the connection is closed.
   */
  public static void connectionClosed() {
    System.out.println("Server connection closed.");
  }

  /**
   * Displays a message when a connection error happen.
   */
  public static void connectionError() {
    System.err.println("Server connection error.");
  }

  /**
   * Displays a message for method support.
   */
  public static void methodNotSupported() {
    System.err.println("Method not supported : only GET or HEAD.");
  }

}