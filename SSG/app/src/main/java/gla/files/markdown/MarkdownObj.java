package gla.files.markdown;

import gla.exceptions.files.WrongFileExtensionException;
import gla.exceptions.markdown.NoMetaDataException;
import gla.exceptions.markdown.NoTemplateFileSetException;
import gla.files.html.HtmlObj;
import gla.files.template.TemplateFile;
import gla.files.toml.TomlObject;
import gla.folders.contentfolder.ContentFolder;
import gla.folders.templatefolder.TemplateFolder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.tomlj.TomlTable;

/**
 * Represent Markdown object.
 */
public class MarkdownObj {
  private final String content;
  private final MetaData metaData;
  private TemplateFile templateFile = null;

  /**
   * Constructor markdown object.
   *
   * @param content content used
   */
  public MarkdownObj(String content) {
    this.metaData = new MetaData(content);
    this.content = content.substring(metaData.end());

  }

  /**
   * Get html object simple.
   *
   * @return HtmlObj Simple
   */
  public HtmlObj toHtmlObjSimple() {
    Parser parser = Parser.builder().build();
    Node document = parser.parse(content);
    HtmlRenderer renderer = HtmlRenderer.builder().build();
    return new HtmlObj(renderer.render(document));
  }

  /**
   * Getter Html with template. Null if is a draft or if template file not set.
   *
   * @return HtmlObj Template create null if is Draft
   * @throws NoMetaDataException        If no metada in metadata
   * @throws NoTemplateFileSetException If no template file is set
   */
  public HtmlObj toHtmlObjTemplate()
      throws NoMetaDataException, NoTemplateFileSetException {
    if (templateFile == null) {
      throw new NoTemplateFileSetException();
    } else if (isDraft()) {
      return null;
    }
    return templateFile.toTemplateObj().toHtml(this);
  }

  /**
   * Get HtmlObject use template if have metadata else use simple.
   *
   * @return Html object
   */
  public HtmlObj toHtmlObj() {
    try {
      return toHtmlObjTemplate();
    } catch (NoMetaDataException | NoTemplateFileSetException ignore) {
      return toHtmlObjSimple();
    }
  }

  /**
   * Getter template file.
   *
   * @return template file
   */
  public TemplateFile getTemplateFile() {
    return templateFile;
  }

  /**
   * Set Template file to used.
   *
   * @param templateFolder template folder to get template
   * @param contentFolder content folder to list file
   * @throws NoMetaDataException         if No Metadata in object.
   * @throws IOException                 IO error to parse template
   * @throws WrongFileExtensionException template have not a good extension
   */
  public void setTemplateFile(TemplateFolder templateFolder, ContentFolder contentFolder)
      throws NoMetaDataException, IOException, WrongFileExtensionException {
    templateFile = (templateFolder == null || !templateFolder.exist())
        ? null : templateFolder.getTemplateMemo(getTemplateName())
                  .setContentFolder(contentFolder);
  }

  /**
   * Set Template file to used. (use a null content folder)
   *
   * @param templateFolder template folder to get template
   * @throws NoMetaDataException         if No Metadata in object.
   * @throws IOException                 IO error to parse template
   * @throws WrongFileExtensionException template have not a good extension
   */
  public void setTemplateFile(TemplateFolder templateFolder)
      throws NoMetaDataException, IOException, WrongFileExtensionException {
    setTemplateFile(templateFolder, null);
  }

  /**
   * If contains metadata.
   *
   * @return boolean
   */
  public boolean containsMetadata() {
    return metaData.end() != 0;
  }

  /**
   * Getter MetaData.
   *
   * @return Metadata
   * @throws NoMetaDataException if no Metadata in file
   */
  public MetaData getMetaData() throws NoMetaDataException {
    if (!containsMetadata()) {
      throw new NoMetaDataException();
    }
    return metaData;
  }

  /**
   * Get map with metadata and content.
   *
   * @return final binding map
   * @throws NoMetaDataException if not metadata in file
   */
  public Map<String, Object> getContext() throws NoMetaDataException {
    Map<String, Object> bindings = new HashMap<>();
    bindings.put("metadata", getMetaData().toTomlObject().toMap());
    bindings.put("content", toHtmlObjSimple().getRawContent());
    return bindings;
  }

  /**
   * Getter template if not found in metadata return `DEFAULT_TEMPLATE`.
   * null if No Metadata
   *
   * @return String template name.
   * @throws NoMetaDataException ""
   */
  public String getTemplateName() throws NoMetaDataException {
    TomlObject tomlObject = getMetaData().toTomlObject();
    return tomlObject.get("template", TemplateFile.DEFAULT_FILE_NAME);
  }

  /**
   * Getter Draft Metadata (default=false).
   *
   * @return Boolean draft.
   */
  public boolean isDraft() {
    try {
      TomlTable table = getMetaData().toTomlObject().toTable();
      return table.getBoolean("draft", () -> false);
    } catch (NoMetaDataException e) {
      return false;
    }
  }

  @Override
  public String toString() {
    return content;
  }


}
