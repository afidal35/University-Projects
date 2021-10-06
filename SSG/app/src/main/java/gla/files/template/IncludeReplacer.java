package gla.files.template;

import gla.exceptions.files.WrongFileExtensionException;
import gla.exceptions.templates.TemplateNotFoundException;
import gla.folders.contentfolder.ContentFolder;
import gla.folders.templatefolder.TemplateFolder;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Replace all include and keep dependencies.
 */
public class IncludeReplacer {

  private static final Pattern PATTERN = Pattern.compile("\\{\\{\\s+include\\s+\"?(.*?)\"?\\s+}}");
  private final TemplateFolder templateFolder;
  private final Set<TemplateFile> dependency = new HashSet<>();
  private ContentFolder contentFolder;
  private String htmlContent;

  /**
   * Constructor Replace include field.
   *
   * @param templateFolder Template folder used
   * @param htmlContent    html content used
   */
  public IncludeReplacer(TemplateFolder templateFolder, String htmlContent) {
    this.templateFolder = templateFolder;
    this.htmlContent = htmlContent;
  }

  /**
   * set content folder to parse in include.
   *
   * @param contentFolder content folder.
   * @return this
   */
  public IncludeReplacer setContentFolder(ContentFolder contentFolder) {
    this.contentFolder = contentFolder;
    return this;
  }

  /**
   * To get matcher on pattern who match in html content.
   *
   * @return Macher
   */
  private Matcher getMatcher() {
    return PATTERN.matcher(htmlContent);
  }

  /**
   * To get html on content. This method load template file with `TemplatesSingleton`, and add this
   * template to dependency.
   *
   * @param fileName name of template file
   * @return html content to used
   * @throws IOException                     if IO error to load template file
   * @throws TemplateNotFoundException       if cannot found template
   */
  private String getTemplateContent(String fileName)
      throws WrongFileExtensionException, IOException {
    TemplateFile template = templateFolder.getTemplateMemo(fileName);
    if (contentFolder != null) {
      template.setContentFolder(contentFolder);
    }
    template.parse();
    dependency.add(template);
    return template.getContent();
  }

  /**
   * Replace all includes.
   *
   * @return String used by template call
   * @throws IOException                     if IO error to load template file
   * @throws TemplateNotFoundException       if cannot found template
   * @throws WrongFileExtensionException ""
   */
  public String replace()
      throws IOException, WrongFileExtensionException {
    for (Matcher matcher = getMatcher(); matcher.find(); matcher = getMatcher()) {
      String fileName = matcher.group(1);
      String contentInclude = getTemplateContent(fileName);
      htmlContent = matcher.replaceFirst(contentInclude);
    }
    return htmlContent;
  }

  /**
   * To get all dependency file used to in replace méthod.
   *
   * @return Set of template
   */
  public Set<TemplateFile> getDependency() {
    return dependency;
  }
}
