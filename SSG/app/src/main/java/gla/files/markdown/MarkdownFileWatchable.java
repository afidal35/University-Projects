package gla.files.markdown;

import gla.exceptions.files.FileNotParseException;
import gla.exceptions.files.WrongFileExtensionException;
import gla.exceptions.html.NotSameNameException;
import gla.files.html.HtmlFile;
import gla.files.template.TemplateFile;
import gla.folders.OutputFolder;
import gla.observer.FileWatchable;
import gla.utils.Printer;
import java.io.IOException;

/**
 * Class use to build html file on build or watcher.
 * Observable: call by build site / notify html file
 * Observer: notify by template
 */
public class MarkdownFileWatchable extends FileWatchable {
  private final MarkdownFile markdownFile;
  private FileWatchable templateWatchable = null;

  /**
   * Constructor of watcher.
   *
   * @param markdownFile markdown file to watch
   * @param outputFolder output folder to make html file
   * @throws IOException                 error to load markdown file
   * @throws WrongFileExtensionException error to load template file to use
   */
  public MarkdownFileWatchable(MarkdownFile markdownFile, OutputFolder outputFolder)
      throws WrongFileExtensionException, IOException {
    super(markdownFile, true);
    this.markdownFile = markdownFile.parse();
    try {
      HtmlFile htmlFile = markdownFile.toHtmlFile(outputFolder);
      addObserver(htmlFile.toObserver(markdownFile));
    } catch (NotSameNameException | FileNotParseException e) {
      throw new RuntimeException(e);
    }
    updateTemplateWatchable();
  }

  /**
   * Update template watcher notify modification.
   */
  void updateTemplateWatchable() {
    if (templateWatchable != null) {
      templateWatchable.removeObserver(this);
    }

    setNewTemplateWatchable();

    if (templateWatchable != null) {
      templateWatchable.addObserver(this);
    }
  }

  /**
   * Set new template watchable.
   */
  private void setNewTemplateWatchable() {
    try {
      TemplateFile newTemplateFile = markdownFile.toMarkdownObj().getTemplateFile();
      templateWatchable = newTemplateFile == null
          ? null : newTemplateFile.getWatchable();
    } catch (FileNotParseException | IOException | WrongFileExtensionException e) {
      throw new RuntimeException(e);
    }
  }


  /**
   * Update markdown.
   */
  private void updateMarkdownInfo() {
    try {
      markdownFile.parse(true);
      updateTemplateWatchable();
    } catch (WrongFileExtensionException e) {
      System.err.println(e.getMessage() + " -- " + e); // TODO add printer error to build template
    } catch (IOException e) {
      throw new RuntimeException(e); // TODO printer ???
    }
  }

  @Override
  public void onCreated() {
    Printer.detectedCreationOn(getFileInfo());
    notifyCreated();
  }

  @Override
  public void onModified() {
    Printer.detectedChangeOn(getFileInfo());
    updateMarkdownInfo();
    notifyModified();
  }

  @Override
  public void onDeleted() {
    Printer.detectedDeleteOn(getFileInfo());
    notifyDelete();
  }

}
