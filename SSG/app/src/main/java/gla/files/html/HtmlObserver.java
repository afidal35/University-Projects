package gla.files.html;

import gla.exceptions.files.WrongFileExtensionException;
import gla.exceptions.html.NotSameNameException;
import gla.files.markdown.MarkdownFile;
import gla.observer.FileObserver;
import gla.utils.Printer;
import java.io.IOException;

/**
 * Html observer.
 */
public class HtmlObserver extends FileObserver {
  private final HtmlFile htmlFile;

  /**
   * Constructor html file observer markdown.
   *
   * @param htmlFile     html file to observe
   * @param markdownFile markdown product this html file
   * @throws NotSameNameException  error to set markdown file
   * @throws IOException error to parse markdown file
   * @throws WrongFileExtensionException error to parse markdown file.

   */
  public HtmlObserver(HtmlFile htmlFile,
                      MarkdownFile markdownFile)
      throws NotSameNameException, IOException, WrongFileExtensionException {
    super(htmlFile);
    this.htmlFile = htmlFile.setMarkdownFile(markdownFile);
  }

  /**
   * Create html file with is content.
   *
   * @param update if is an update.
   */
  private void createFile(boolean update) {
    try {
      if (htmlFile.markdownIsDraft() && htmlFile.exists()) {
        htmlFile.delete();
        Printer.fileIsDraft(htmlFile.getMarkdownFile());
      } else if (!htmlFile.markdownIsDraft()) {
        htmlFile.create();
        Printer.fileEndingProcess(htmlFile, update);
      }
    } catch (IOException e) {
      Printer.errorOnCreate(getFileInfo());
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onCreated() {
    createFile(false);
  }

  @Override
  public void onModified() {
    htmlFile.update();
    createFile(htmlFile.exists());
  }

  @Override
  public void onDeleted() {
    if (htmlFile.exists()) {
      try {
        htmlFile.delete();
        Printer.fileHasBeenDeleted(getFileInfo());
      } catch (IOException e) {
        Printer.errorOnDelete(getFileInfo());
        //throw new RuntimeException(e);
      }
    }
  }
}
