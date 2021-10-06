package gla.files.template;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gla.ResourcesHelper;
import gla.exceptions.files.FileNotParseException;
import gla.exceptions.files.WrongFileExtensionException;
import gla.exceptions.markdown.NoMetaDataException;
import gla.exceptions.templates.TemplateNotFoundException;
import gla.files.FileInfo;
import gla.files.html.HtmlObj;
import gla.files.markdown.MarkdownFile;
import gla.files.markdown.MarkdownObj;
import gla.folders.contentfolder.ContentFolder;
import gla.folders.templatefolder.TemplateFolder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TemplateFileTest {

  @TempDir
  Path dir_tmp;

  @Test
  void testWhenTemplateNameIsNotFound() {
    TemplateFolder templateFolder = new TemplateFolder(dir_tmp);
    assertThrows(TemplateNotFoundException.class,
        () -> templateFolder.getTemplate("Notexist.html"));
  }

  @Test
  void templateNameHasWrongExtension() {
    TemplateFolder templateFolder = new TemplateFolder(dir_tmp);
    assertThrows(WrongFileExtensionException.class,
        () -> templateFolder.getTemplate("template.bad"));

  }


  @Test
  void templateCannotCreateOrDeleteTemplateFile() throws WrongFileExtensionException, IOException {
    TemplateFolder templateFolder = new TemplateFolder(dir_tmp);
    TemplateFile templateFile = new TemplateFile(templateFolder, "template.html", false);
    assertThrows(RuntimeException.class, templateFile::create);
    assertThrows(RuntimeException.class, templateFile::delete);

  }

  @Test
  void compareTemplateFile() throws IOException, WrongFileExtensionException {
    TemplateFolder templateFolder = new TemplateFolder(dir_tmp);
    templateFolder.create();
    Path path1 = Files.createFile(templateFolder.toPath().resolve("template1.html"));
    Path path2 = Files.createFile(templateFolder.toPath().resolve("template2.html"));
    TemplateFile templateFile1 = templateFolder.getTemplateMemo("template1.html");
    TemplateFile templateFile2 = templateFolder.getTemplateMemo("template2.html");

    assertEquals(path1, templateFile1.toPath());
    assertEquals(path2, templateFile2.toPath());
    assertEquals(templateFile1, templateFile1);
    assertNotEquals(templateFile1, templateFile2);

    assertEquals(templateFolder, templateFile1.getTemplateFolder());
    assertNotEquals(templateFile1, path1);
    assertNotNull(templateFile1.toString());
  }



  private void helperTestTemplate(String pathResource, String pathTemplate)
      throws IOException, FileNotParseException, NoMetaDataException, WrongFileExtensionException {
    Path inputPath = ResourcesHelper.getResourcePath(pathResource);
    TemplateFolder templateFolder = new TemplateFolder(inputPath);
    ContentFolder contentFolder = new ContentFolder(inputPath);

    MarkdownFile markdownFile = contentFolder.getMarkdown("markdown.md");
    MarkdownObj markdownObj = markdownFile.toMarkdownObj();

    TemplateFile templateFile = templateFolder.getTemplate(pathTemplate);
    TemplateObj templateObj = templateFile.toTemplateObj();
    HtmlObj htmlExpected = ResourcesHelper.getResourceHtml(pathResource + "/output/expected.html");
    assertEquals(htmlExpected, templateObj.toHtml(markdownObj));
  }

  public void helperTestTemplate(String pathResource)
      throws IOException, NoMetaDataException, FileNotParseException,
      WrongFileExtensionException {
    Path inputPath = ResourcesHelper.getResourcePath(pathResource);
    TemplateFolder templateFolder = new TemplateFolder(inputPath);
    ContentFolder contentFolder = new ContentFolder(inputPath);

    MarkdownFile markdownFile = contentFolder.getMarkdown("markdown.md");
    MarkdownObj markdownObj = markdownFile.toMarkdownObj();

    TemplateFile templateFile = new TemplateFile(templateFolder, "default.html", true);
    TemplateObj templateObj = templateFile.toTemplateObj();
    HtmlObj htmlExpected = ResourcesHelper.getResourceHtml(pathResource + "/output/expected.html");
    assertEquals(htmlExpected, templateObj.toHtml(markdownObj));
  }


  @Test
  public void templateFileContainsOneKeywordInclude() {
    assertDoesNotThrow(() -> helperTestTemplate("/template/IncludeOne"));
  }

  @Test
  public void templateFileContainsTwoKeywordsInclude() {
    assertDoesNotThrow(() -> helperTestTemplate("/template/TwoKeywordInclude"));
  }


  @Test
  public void templateContainsRecursiveKeywordInclude() {
    assertDoesNotThrow(() -> helperTestTemplate("/template/TwoKeywordInclude"));
  }


  @Test
  public void theExtensionOfTheIncludedFileIsWrong() {
    Path inputPath = ResourcesHelper.getResourcePath("/template/wrongExtensionException");
    TemplateFolder templateFolder = new TemplateFolder(inputPath);
    assertThrows(WrongFileExtensionException.class,
        () -> templateFolder.getTemplate("default.html"));
  }


  @Test
  public void templateContainsVariableContent() {
    assertDoesNotThrow(() -> helperTestTemplate("/template/variableContent"));
  }


  @Test
  public void templateContainsTwoVariablesContent() {
    assertDoesNotThrow(() -> helperTestTemplate("/template/twoVariablesContent"));
  }


  @Test
  public void templateContainsOnlyVariablesThatAreSpecifiedInTheMetadata() {
    assertDoesNotThrow(() ->
        helperTestTemplate("/template/specifiedVariablesInMetadata/"));
  }


  @Test
  public void templateContainsVariablesThatAreSpecifiedInTheMetadataAndTheVariableContent() {
    assertDoesNotThrow(() ->
        helperTestTemplate("/template/metadataVariablesAndContentVariable"));
  }


  @Test
  public void variableSpecifyingInTemplateFileIsNotExistInMetadata() {
    assertDoesNotThrow(() ->
        helperTestTemplate("/template/variableSpecifyingNotExist"));

  }


  @Test
  public void WhenSpecifyingTheGoodTemplateName() {
    assertDoesNotThrow(() ->
        helperTestTemplate("/template/goodTemplatePath", "my_template.html"));

  }

  @Test
  public void templateContainsCommentsToBeDeleted() {
    assertDoesNotThrow(() ->
        helperTestTemplate("/template/commentsInTemplateFile/",
            "template_with_comments.html"));
  }

  @Test
  public void templateContainsLoopCondition() {
    assertDoesNotThrow( () ->
    helperTestTemplate("/template/loopExpression"));
  }

  @Test
  public void templateContainsAsingleIfCondition(){
    assertDoesNotThrow( () ->
        helperTestTemplate("/template/loopExpression"));
  }

  @Test
  public void templateContainsAsingleIfElseCondition() {
    assertDoesNotThrow( () ->
    helperTestTemplate("/template/ifElseCondition"));
  }

  @Test
  public void templateContainsConditionSuchAsIfElifElse() {
    assertDoesNotThrow( () ->
    helperTestTemplate("/template/ifElifElseConditions"));
  }


  @Test
  public void testTemplateWhenMetadataContentKeyList() {
    assertDoesNotThrow( () ->
    helperTestTemplate("/template/keyListInMetadata/"));
  }

  void helperTestDep(TemplateFolder templateFolder, String templateName, Set<String> expectedDep)
      throws IOException, WrongFileExtensionException {
    TemplateFile templateFile = templateFolder.getTemplate(templateName);
    Set<String> actualDep = templateFile.getDependencies().stream()
        .map(FileInfo::getFileName)
        .collect(Collectors.toSet());

    assertEquals(expectedDep, actualDep);
  }

  @Test
  public void testIncludeDependencies() throws IOException, WrongFileExtensionException {
    Path inputPath = ResourcesHelper.getResourcePath("/template/includeDependencies");
    TemplateFolder templateFolder = new TemplateFolder(inputPath);

    helperTestDep(templateFolder, "default.html",
        new HashSet<>(Arrays.asList("menu1.html", "menu2.html")));

    helperTestDep(templateFolder, "menu1.html",
        new HashSet<>(Arrays.asList("menu2.html", "menu3.html")));

    helperTestDep(templateFolder, "menu2.html",
        new HashSet<>(Collections.singletonList("menu3.html")));
  }
}