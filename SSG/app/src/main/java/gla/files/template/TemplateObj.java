package gla.files.template;

import com.hubspot.jinjava.Jinjava;
import gla.exceptions.files.WrongFileExtensionException;
import gla.exceptions.markdown.NoMetaDataException;
import gla.exceptions.templates.TemplateNotFoundException;
import gla.files.html.HtmlObj;
import gla.files.markdown.MarkdownObj;
import gla.folders.contentfolder.ContentFolder;
import gla.folders.templatefolder.TemplateFolder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.checkerframework.checker.units.qual.A;

/**
 * Template object used bay Template file.
 */
public class TemplateObj {
  private static final Jinjava jinjava = new Jinjava();

  private final Set<TemplateFile> templateDependency;
  private final String content;


  private final List<String> listFile = new ArrayList<>();

  /**
   * Constructor Template object.
   * replace all dependency in construction.
   *
   * @param templateFolder template folder used
   * @param contentHtml    Html content used to start
   * @param contentFolder  content folder used
   * @throws IOException                 if IO error to load template file
   * @throws TemplateNotFoundException   if cannot found template
   * @throws WrongFileExtensionException if template have wrong extension
   */
  public TemplateObj(TemplateFolder templateFolder,
                     ContentFolder contentFolder,
                     String contentHtml)
      throws IOException, WrongFileExtensionException {
    if (contentFolder != null) {
      ListFileReplacer listFileReplacer = new ListFileReplacer(contentFolder, contentHtml);
      contentHtml = listFileReplacer.replaceListFile();
      listFile.addAll(listFileReplacer.listPathReplace());
    }

    IncludeReplacer includeReplacer = new IncludeReplacer(templateFolder, contentHtml)
        .setContentFolder(contentFolder);
    content = includeReplacer.replace();

    templateDependency = includeReplacer.getDependency();
  }

  public HtmlObj toHtml(MarkdownObj markdownObj) throws NoMetaDataException {
    String htmlString = jinjava.render(content, markdownObj.getContext());
    return new HtmlObj(htmlString);
  }

  /**
   * Get all template include inside this template. Used by incremental.
   *
   * @return Set Template
   */
  public Set<TemplateFile> getDependency() {
    return templateDependency;
  }

  /**
   * has list of files to replace.
   *
   * @return boolean
   */
  public boolean hasListFiles() {
    return !listFile.isEmpty();
  }

  /**
   * List of path replace with list files.
   *
   * @return list of name dir
   */
  public List<String> getListFile() {
    return listFile;
  }


  /**
   * Get template string (dependency is already replace).
   *
   * @return String
   */
  @Override
  public String toString() {
    return content;
  }
}
