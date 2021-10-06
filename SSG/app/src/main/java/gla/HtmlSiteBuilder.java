package gla;

import gla.exceptions.files.WrongFileExtensionException;
import gla.files.html.HtmlFile;
import gla.files.markdown.MarkdownBuilder;
import gla.files.markdown.MarkdownFile;
import gla.files.staticfile.StaticBuilder;
import gla.files.staticfile.StaticFile;
import gla.files.toml.SiteToml;
import gla.folders.InputFolder;
import gla.folders.OutputFolder;
import gla.folders.contentfolder.ContentFolder;
import gla.folders.staticfolder.StaticFolder;
import gla.folders.templatefolder.TemplateFolder;
import gla.utils.Printer;
import gla.utils.UnusedFileDeleter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import sun.misc.Signal;

/**
 * Class aiming at building an html website from a directory containing markdown files.
 */
public class HtmlSiteBuilder {
  public static final int MAX_JOBS = Runtime.getRuntime().availableProcessors();
  private final InputFolder inputFolder;
  private final OutputFolder outputFolder;
  private final SiteToml siteToml;

  private boolean incremental = true;
  private ExecutorService executorService;


  /**
   * HtmlSiteBuilder constructor when both [--input-dir] and [--output-dir] are specified (Path
   * form).
   *
   * @param inputFolder the input directory
   * @param output      the output directory
   */
  public HtmlSiteBuilder(InputFolder inputFolder, OutputFolder output) {
    this.inputFolder = inputFolder;
    this.outputFolder = output;
    this.siteToml = inputFolder.getSiteToml();
    setJobs(MAX_JOBS);
  }

  /**
   * HtmlSiteBuilder constructor when both [--input-dir] and [--output-dir] are specified (String
   * form).
   *
   * @param inputFolder the input directory
   * @param output      the output directory
   * @throws IOException ""
   */
  public HtmlSiteBuilder(String inputFolder, String output)
      throws IOException {
    this(new InputFolder(Path.of(inputFolder)),
        new OutputFolder(Path.of(output)));
  }


  /**
   * Set to rebuild all.
   *
   * @param rebuild not incremental
   * @return Object to chain it
   */
  public HtmlSiteBuilder setRebuild(boolean rebuild) {
    incremental = !rebuild;
    return this;
  }

  /**
   * Set number of jobs use.
   *
   * @param jobs number of jobs to used.
   * @return Html site builder
   */
  public HtmlSiteBuilder setJobs(int jobs) {
    if (jobs <= 0) {
      jobs = MAX_JOBS;
    }
    executorService = Executors.newFixedThreadPool(jobs);
    return this;
  }

  /**
   * Getter input folder.
   *
   * @return input folder
   */
  public InputFolder getInputFolder() {
    return inputFolder;
  }

  /**
   * Getter output folder.
   *
   * @return output folder
   */
  public OutputFolder getOutputFolder() {
    return outputFolder;
  }

  /**
   * Getter template folder.
   *
   * @return template folder
   */
  public TemplateFolder getTemplateFolder() {
    return siteToml.getTemplateFolder();
  }


  /**
   * Getter static folder.
   *
   * @return static folder
   */
  public StaticFolder getStaticFolder() {
    return siteToml.getStaticFolder();
  }

  /**
   * Getter Content folder.
   *
   * @return content folder
   */
  public ContentFolder getContentFolder() {
    return getInputFolder().getContentFolder();
  }

  /**
   * Getter Watchdog.
   *
   * @return Watchdog
   */
  public Watchdog getWatchdog() {
    return new Watchdog(inputFolder, outputFolder);
  }

  /**
   * Render html files and copy static file from an InputFolder.
   *
   * @throws IOException ""
   * @throws WrongFileExtensionException ""
   * @throws InterruptedException ""
   */
  public void build()
      throws IOException, WrongFileExtensionException, InterruptedException {
    if (outputFolder.exist()) {
      deleteUnusedHtmlFile();
      deleteUnusedStaticFile();
    } else {
      outputFolder.create();
    }

    buildMarkdown();
    copyStaticFile();

    executorService.shutdown();

    // if ctrl-c
    Signal.handle(new Signal("INT"),
        signal -> {
          Printer.buildInterrupted();
          executorService.shutdownNow();
        });

    if (!executorService.awaitTermination(24, TimeUnit.HOURS)) {
      executorService.shutdownNow();
      throw new RuntimeException("Error build take more 24h.");
    } else {
      Printer.buildSiteEndingProcess(incremental, inputFolder, outputFolder);
    }
  }

  /**
   * build all markdown file.
   */
  private void buildMarkdown()
      throws WrongFileExtensionException, IOException {
    for (MarkdownFile markdownFile : getContentFolder().listMarkdownFile()) {
      executorService.submit(
          new MarkdownBuilder(
              markdownFile,
              getTemplateFolder(),
              getOutputFolder(),
              incremental)
      );
    }
  }

  /**
   * Copy all static folders to output folder.
   */
  private void copyStaticFile() throws IOException {
    List<StaticFile> staticFileList = getStaticFolder().listStaticFile();
    if (staticFileList.size() == 0) {
      return;
    } else if (getTemplateFolder().exist()) {
      outputFolder.getStaticFolder().create();
    }
    staticFileList.forEach(staticFile -> executorService.submit(
        new StaticBuilder(staticFile, getOutputFolder(), incremental))
    );
  }

  /**
   * Delete static file unused.
   */
  private void deleteUnusedStaticFile()
      throws IOException {
    StaticFolder staticOutputFolder = getOutputFolder().getStaticFolder();
    if (!staticOutputFolder.exist()) {
      return;
    } else if (!getStaticFolder().exist()) {
      getOutputFolder().getStaticFolder().delete();
      Printer.staticFolderHasBeenDeleted(outputFolder, inputFolder);
      return;
    }
    for (StaticFile staticFile : staticOutputFolder.listStaticFile()) {
      if (!getStaticFolder().contains(staticFile)) {
        executorService.submit(new UnusedFileDeleter(staticFile));
      }
    }
  }

  /**
   * Delete html unused.
   */
  private void deleteUnusedHtmlFile() {
    for (HtmlFile htmlFile : getOutputFolder().listHtmlFile()) {
      String nameMd = htmlFile.nameWithNewExtension(MarkdownFile.EXTENSION);
      if (!getContentFolder().contains(nameMd)) {
        executorService.submit(new UnusedFileDeleter(htmlFile));
      }
    }
  }
}