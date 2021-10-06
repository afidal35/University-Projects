package gla.watchdog;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import gla.HtmlSiteBuilder;
import gla.ResourcesHelper;
import gla.Watchdog;
import gla.exceptions.files.WrongFileExtensionException;
import gla.folders.InputFolder;
import gla.folders.OutputFolder;
import java.io.IOException;
import java.nio.file.Path;

public class WatchdogHelper {

  public final static String OS = System.getProperty("os.name").toLowerCase();
  public final static boolean IS_MAC = OS.contains("mac");
  public final static long WAITING_TIME_START = 500;
  public final static long WAITING_TIME_END = IS_MAC ? 10000 : 1000;
  private final static String resourceInput = "/watchdog/input/";
  private final static String resourceOther = "/watchdog/other/";

  public static InputFolder getInputFolder(Path tmpPath) throws IOException {
    Path path = ResourcesHelper.copyResourcesFolder(resourceInput, tmpPath);
    return new InputFolder(path);
  }

  public static OutputFolder getOutputFolder(Path tmpPath) {
    return new OutputFolder(tmpPath.resolve("_output"));
  }

  public static Watchdog getWatchdogAndBuild(InputFolder inputFolder, OutputFolder outputFolder)
      throws IOException, WrongFileExtensionException, InterruptedException {
    HtmlSiteBuilder builder = new HtmlSiteBuilder(inputFolder, outputFolder);
    builder.build();
    return builder.getWatchdog();
  }

  public static void copyOtherResource(String resource, Path dest) {
  assertDoesNotThrow(
      () -> ResourcesHelper.copyResourceFile(resourceOther + resource, dest)
  );
  }

  public static void execWatchdog(Watchdog watchdog, Runnable runnable)
      throws InterruptedException {
    watchdog.start();
    Thread.sleep(WAITING_TIME_START);

    runnable.run();

    watchdog.stop(WAITING_TIME_END);

  }
}
