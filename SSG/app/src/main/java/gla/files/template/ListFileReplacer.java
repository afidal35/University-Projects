package gla.files.template;

import gla.exceptions.files.WrongFileExtensionException;
import gla.folders.contentfolder.ContentFolder;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replace all loop for by.
 */
public class ListFileReplacer {

  private static final Pattern PATTERN = Pattern.compile("list_files\\(\"(.*?)\",\\s(.*?)\\)");
  private final ContentFolder contentFolder;
  private String htmlContent;

  private final List<String> listPathReplace = new ArrayList<>();

  /**
   * Constructor Replacer include field.
   *
   * @param contentFolder content folder used to get files
   * @param htmlContent html content used
   */
  public ListFileReplacer(ContentFolder contentFolder, String htmlContent) {
    this.contentFolder = contentFolder;
    this.htmlContent = htmlContent;
  }

  /**
   * has list of files to replace.
   *
   * @return boolean
   */
  public boolean hasListFiles() {
    return !listPathReplace.isEmpty();
  }

  /**
   * List of path replace with list files.
   *
   * @return list of name dir
   */
  public List<String> listPathReplace() {
    return listPathReplace;
  }

  /**
   * To get matcher on pattern who match in html content.
   *
   * @return Matcher
   */
  public Matcher getMatcher() {
    return PATTERN.matcher(htmlContent);
  }

  /**
   * To get String[] in String.
   *
   * @param tab String[] used
   * @return String variable
   */
  public String getTabString(String[] tab) {
    StringBuilder res = new StringBuilder();
    res.append("[");
    for (int i = 0; i < tab.length; i++) {
      if (i > 0) {
        res.append(", ");
      }
      res.append('\"');
      res.append(tab[i]);
      res.append('\"');
    }
    res.append(']');
    return res.toString();
  }

  /**
   * Replace list_files by a String variable.
   *
   * @return new content with a String variable instead of "list_files"
   * @throws FileNotFoundException ""
   */
  public String replaceListFile()
      throws FileNotFoundException {
    for (Matcher matcher = getMatcher(); matcher.find(); matcher = getMatcher()) {
      String path = matcher.group(1);
      boolean rec = Boolean.parseBoolean(matcher.group(2));
      listPathReplace.add(path);

      List<String> res = contentFolder.listFilesName(path, rec);
      String tabString = getTabString(res.toArray(String[]::new));
      htmlContent = matcher.replaceAll(tabString);
    }
    return htmlContent;
  }

}
