package gla.files.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gla.ResourcesHelper;
import gla.exceptions.files.WrongFileExtensionException;
import gla.folders.templatefolder.TemplateFolder;
import gla.observer.FileWatchable;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class TemplateFileWatchableTest {




  @Test
  void testTemplateWatchableOnCreation() throws IOException, WrongFileExtensionException {
    TemplateFolder templateFolder = ResourcesHelper.getTemplate("/template/IncludeOne");
    TemplateFile templateFile = templateFolder.getTemplate("default.html");
    FileWatchable watchable = templateFile.getWatchable();
    watchable.onCreated();

    assertNotNull(templateFile.toTemplateObj());
    assertNotNull(templateFile.toString());
    assertEquals(1, templateFile.getDependencies().size());
  }

  @Test
  void testTemplateWatchableOnModification() throws IOException, WrongFileExtensionException {
    TemplateFolder templateFolder = ResourcesHelper.getTemplate("/template/IncludeOne");
    TemplateFile templateFile = templateFolder.getTemplate("default.html");
    FileWatchable watchable = templateFile.getWatchable();
    watchable.onModified();

    assertNotNull(templateFile.toTemplateObj());
    assertNotNull(templateFile.toString());
    assertEquals(1, templateFile.getDependencies().size());
  }


  @Test
  void testTemplateWatchableOnDelete() throws IOException, WrongFileExtensionException {
    TemplateFolder templateFolder = ResourcesHelper.getTemplate("/template/IncludeOne");
    TemplateFile templateFile = templateFolder.getTemplate("default.html");
    FileWatchable watchable = templateFile.getWatchable();
    watchable.onDeleted();

    assertNotNull(templateFile.toTemplateObj());
    assertNotNull(templateFile.toString());
    assertEquals(1, templateFile.getDependencies().size());
  }

}
