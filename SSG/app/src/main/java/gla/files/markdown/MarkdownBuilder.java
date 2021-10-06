package gla.files.markdown;

import gla.folders.OutputFolder;
import gla.folders.templatefolder.TemplateFolder;
import java.util.concurrent.Callable;

/**
 * To build markdown to html.
 */
public class MarkdownBuilder implements Callable<MarkdownFile> {
  private final MarkdownFile markdownFile;
  private final TemplateFolder templateFolder;
  private final OutputFolder outputFolder;
  private final boolean incremental;

  /**
   * Constructor.
   *
   * @param markdownFile   markdown file to build
   * @param templateFolder template folder used
   * @param outputFolder   output folder used to build html files
   * @param incremental    if is a build in incremental
   */
  public MarkdownBuilder(MarkdownFile markdownFile,
                         TemplateFolder templateFolder,
                         OutputFolder outputFolder,
                         boolean incremental) {
    this.markdownFile = markdownFile;
    this.templateFolder = templateFolder;
    this.outputFolder = outputFolder;
    this.incremental = incremental;
  }

  @Override
  public MarkdownFile call() throws Exception {
    markdownFile.parse()
        .setTemplateFolder(templateFolder)
        .getWatchable(outputFolder)
        .build(incremental);
    return markdownFile;
  }
}
