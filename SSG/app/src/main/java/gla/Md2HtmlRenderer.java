package gla;

import gla.exceptions.files.FileNotParseException;
import gla.exceptions.files.IndexFileNotFoundException;
import gla.exceptions.files.SiteTomlNotFoundException;
import gla.exceptions.files.WrongFileExtensionException;
import gla.exceptions.files.WrongFileNameException;
import gla.exceptions.templates.TemplateException;
import gla.files.FileHelpers;
import gla.files.html.HtmlFile;
import gla.files.markdown.MarkdownFile;
import gla.folders.InputFolder;
import gla.folders.OutputFolder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;

/**
 * Class aiming at parsing markdown files and rendering them to html files.
 */
public class Md2HtmlRenderer {

  private final OutputFolder output;

  public Md2HtmlRenderer(Path output) {
    this.output = new OutputFolder(output);
  }

  /**
   * Construct an HtmlFile object from a specified path.
   *
   * @param path the path specified by the user
   * @return HtmlFile object with his markdown related one
   * @throws FileNotFoundException       if the markdown file does not exists
   * @throws WrongFileNameException      if the file format is wrong
   * @throws WrongFileExtensionException if the extension does not correspond
   */
  public HtmlFile getHtmlFile(Path path) throws IOException, WrongFileExtensionException {
    String extension = FileHelpers.getFileExtension(path.getFileName().toString());
    switch (extension) {
      case "html" -> {
        String markdownPath = path.getParent() + "/" + FileHelpers
            .substituteFileExtension(path.getFileName().toString(), "md");
        MarkdownFile markdown = new MarkdownFile(Path.of(markdownPath), true);
        try {
          return markdown.toHtmlFile(output);
        } catch (FileNotParseException e) {
          throw new RuntimeException(e);
        }
      }
      case "" -> throw new WrongFileNameException(path, "file.html");
      default -> throw new WrongFileExtensionException(extension, "html");
    }
  }

  /**
   * Render an html file from a specified path.
   *
   * @param path the specified path of the html file to convert
   * @throws IOException               ""
   * @throws WrongFileExtensionException ""
   */
  public void renderHtmlFile(String path)
      throws IOException, WrongFileExtensionException {
    HtmlFile file = getHtmlFile(Path.of(path));
    try {
      InputFolder input = new InputFolder(FileHelpers.getGrandParent(Path.of(path)));
      output.create();
      // file.dump(input.templatesExist()); //FIXME
    } catch (IndexFileNotFoundException | SiteTomlNotFoundException ignore) {
      // Ignore if there is no [index.md] or [site.toml] files inside the InputFolder when
      // building single files since they are not directly dependent in this case.
    }
  }

  /**
   * Render multiple html files.
   *
   * @param paths file paths to render
   * @throws TemplateException         ""
   * @throws IOException               ""
   * @throws InstanceNotFoundException ""
   * @throws InstanceAlreadyExistsException ""
   * @throws WrongFileExtensionException ""
   */
  public void renderHtmlFiles(String[] paths)
      throws TemplateException,
      IOException,
      InstanceNotFoundException,
      InstanceAlreadyExistsException, WrongFileExtensionException {
    for (String path : paths) {
      renderHtmlFile(path);
    }
  }

}
