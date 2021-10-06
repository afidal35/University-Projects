package gla.watchdog;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gla.Watchdog;
import gla.exceptions.files.WrongFileExtensionException;
import gla.files.staticfile.StaticFile;
import gla.folders.InputFolder;
import gla.folders.OutputFolder;
import gla.folders.ThemeFolder;
import gla.folders.staticfolder.StaticFolder;
import gla.folders.staticfolder.StaticFolderGrouper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class WatchdogTestStaticFolder {
  @TempDir
  Path tmpPath;

  @ParameterizedTest
  @ValueSource(strings={"static_copy.css", "style.css"})
  void testModifyStatic(String staticName)
      throws IOException, WrongFileExtensionException, InterruptedException {
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    StaticFolderGrouper staticFolder = inputFolder.getSiteToml().getStaticFolder();
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);
    Path outputPathStatic = outputFolder.getStaticFolder()
        .getStaticFile(staticName).toPath();

    StaticFile staticFileInput = staticFolder.getStaticFile(staticName);
    String contentStatic = staticFileInput.getContent();
    assertEquals(contentStatic, Files.readString(outputPathStatic));

    WatchdogHelper.execWatchdog(watchdog,
        () -> assertDoesNotThrow(() -> Files.writeString(staticFileInput.toPath(),
            "test update", APPEND)));

    assertNotEquals(contentStatic, Files.readString(outputPathStatic));
    assertEquals(Files.readString(outputPathStatic), Files.readString(outputPathStatic));
  }

  @Test
  void testModifyDuplicateStaticThemeNoUpdate()
      throws IOException, WrongFileExtensionException, InterruptedException {
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    StaticFolderGrouper staticFolder = inputFolder.getSiteToml().getStaticFolder();
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);
    final String fileName = "static_copy.css";
    Path outputPathStatic = outputFolder.getStaticFolder().getStaticFile(fileName).toPath();

    StaticFile staticFileInput = inputFolder.getStaticFolder().getStaticFile(fileName);
    StaticFile staticFile = staticFolder.getStaticFileMemo(fileName);
    StaticFile staticFileTheme = inputFolder.getSiteToml().getThemeFolder()
        .getStaticFolder().getStaticFile(fileName);
    String contentStaticTheme = staticFileTheme.getContent();
    assertEquals(staticFile, staticFileInput);
    assertEquals(staticFileInput.getContent(), Files.readString(outputPathStatic));

    WatchdogHelper.execWatchdog(watchdog,
        () -> assertDoesNotThrow(() -> Files.writeString(staticFileTheme.toPath(),
            "test update", APPEND)));

    assertEquals(Files.readString(outputPathStatic), Files.readString(outputPathStatic));
    assertNotEquals(contentStaticTheme, staticFileTheme.getContent());
  }

  void helperAddFile(InputFolder inputFolder, StaticFolder dest)
      throws IOException, WrongFileExtensionException, InterruptedException {
    final String fileName = "newStatic.css";
    StaticFolderGrouper staticFolder = inputFolder.getSiteToml().getStaticFolder();
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);

    StaticFile staticFileOutput = outputFolder.getStaticFolder().getStaticFile(fileName);
    assertFalse(staticFolder.getStaticFile(fileName).exists());
    assertFalse(staticFileOutput.exists());

    WatchdogHelper.execWatchdog(watchdog,
        () -> WatchdogHelper.copyOtherResource(fileName, dest.toPath().resolve(fileName)));

    assertTrue(staticFolder.getStaticFile(fileName).exists());
    assertTrue(staticFileOutput.exists());
  }

  @Test
  void testAddDefaultStaticNoExist()
      throws IOException, WrongFileExtensionException, InterruptedException {
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    helperAddFile(inputFolder, inputFolder.getStaticFolder());
  }

  @Test
  void testAddStaticThemeNoExist()
      throws IOException, WrongFileExtensionException, InterruptedException {
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    ThemeFolder themeFolder = inputFolder.getTheme();
    helperAddFile(inputFolder, themeFolder.getStaticFolder());
  }

  @Test
  void testAddStaticThemeAlreadyExist()
      throws InterruptedException, IOException, WrongFileExtensionException {
    final String fileName = "newStatic.css";
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    ThemeFolder themeFolder = inputFolder.getTheme();
    WatchdogHelper.copyOtherResource(fileName,
        inputFolder.getStaticFolder().toPath().resolve(fileName));
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);

    StaticFile staticFileOutput = outputFolder.getStaticFolder().getStaticFile(fileName);
    String contentOutput = staticFileOutput.getContent();
    assertTrue(staticFileOutput.exists());

    StaticFile staticFileTheme = themeFolder.getStaticFolder().getStaticFile(fileName);
    StaticFile staticFileDefault = inputFolder.getStaticFolder().getStaticFile(fileName);
    assertFalse(staticFileTheme.exists());
    assertTrue(staticFileDefault.exists());
    assertEquals(staticFileOutput.getContent(), staticFileDefault.getContent());

    WatchdogHelper.execWatchdog(watchdog,
        () -> assertDoesNotThrow(() -> Files.writeString(staticFileTheme.toPath(),
            contentOutput + " -- test")));

    assertTrue(staticFileTheme.exists());
    assertTrue(staticFileOutput.exists());
    assertNotEquals(staticFileOutput.getContent(), staticFileTheme.getContent());
    assertEquals(staticFileOutput.getContent(), staticFileDefault.getContent());
  }

  @Test
  void testAddStaticDefaultAlreadyExist()
      throws InterruptedException, IOException, WrongFileExtensionException {
    final String fileName = "newStatic.css";
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    ThemeFolder themeFolder = inputFolder.getTheme();
    WatchdogHelper.copyOtherResource(fileName,
        themeFolder.getStaticFolder().toPath().resolve(fileName));
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);

    StaticFile staticFileOutput = outputFolder.getStaticFolder().getStaticFile(fileName);
    String contentOutput = staticFileOutput.getContent();
    assertTrue(staticFileOutput.exists());

    StaticFile staticFileTheme = themeFolder.getStaticFolder().getStaticFile(fileName);
    StaticFile staticFileDefault = inputFolder.getStaticFolder().getStaticFile(fileName);
    assertTrue(staticFileTheme.exists());
    assertFalse(staticFileDefault.exists());
    assertEquals(staticFileOutput.getContent(), staticFileTheme.getContent());

    WatchdogHelper.execWatchdog(watchdog,
        () -> assertDoesNotThrow(() -> Files.writeString(staticFileDefault.toPath(),
            contentOutput + " -- test")));

    assertTrue(staticFileTheme.exists());
    assertTrue(staticFileOutput.exists());
    assertNotEquals(staticFileOutput.getContent(), staticFileTheme.getContent());
    assertEquals(staticFileOutput.getContent(), staticFileDefault.getContent());
  }

  void helperRemoveFile(InputFolder inputFolder, StaticFolder dest)
      throws IOException, WrongFileExtensionException, InterruptedException {
    final String fileName = "newStatic.css";
    final Path pathFile = dest.toPath().resolve(fileName);
    WatchdogHelper.copyOtherResource(fileName, pathFile);
    StaticFolderGrouper staticFolder = inputFolder.getSiteToml().getStaticFolder();
    OutputFolder outputFolder = WatchdogHelper.getOutputFolder(tmpPath);
    Watchdog watchdog = WatchdogHelper.getWatchdogAndBuild(inputFolder, outputFolder);

    StaticFile staticFileOutput = outputFolder.getStaticFolder().getStaticFile(fileName);
    assertTrue(staticFolder.getStaticFile(fileName).exists());
    assertTrue(staticFileOutput.exists());

    WatchdogHelper.execWatchdog(watchdog, () -> assertDoesNotThrow( () -> Files.delete(pathFile)));

    assertFalse(staticFolder.getStaticFile(fileName).exists());
    assertFalse(staticFileOutput.exists());
  }

  @Test
  void testRemoveDefaultStaticNoExist()
      throws IOException, WrongFileExtensionException, InterruptedException {
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    helperRemoveFile(inputFolder, inputFolder.getStaticFolder());
  }

  @Test
  void testRemoveStaticThemeNoExist()
      throws IOException, WrongFileExtensionException, InterruptedException {
    InputFolder inputFolder = WatchdogHelper.getInputFolder(tmpPath);
    ThemeFolder themeFolder = inputFolder.getTheme();
    helperRemoveFile(inputFolder, themeFolder.getStaticFolder());
  }
}
