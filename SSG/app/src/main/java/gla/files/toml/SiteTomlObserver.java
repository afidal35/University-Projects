package gla.files.toml;

import gla.exceptions.files.WrongFileExtensionException;
import gla.folders.OutputFolder;
import gla.folders.staticfolder.StaticFolderGrouper;
import gla.folders.templatefolder.TemplateFolderGrouper;
import gla.observer.FileWatchable;
import gla.observer.Observer;
import gla.utils.Printer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Site toml watcher.
 */
public class SiteTomlObserver implements Observer {
  private final SiteToml siteToml;
  private final OutputFolder outputFolder;


  /**
   * Constructor watcher.
   *
   * @param siteToml siteToml use to construct.
   * @param outputFolder output folder
   * @throws FileNotFoundException ""
   */
  public SiteTomlObserver(SiteToml siteToml, OutputFolder outputFolder)
      throws FileNotFoundException {
    this.siteToml = siteToml;
    this.outputFolder = outputFolder;

  }

  @Override
  public void onCreated() {
    throw new RuntimeException("Cannot create site.toml in runtime");
  }

  @Override
  public void onModified() {
    try {
      String currentTemplate = siteToml.getThemeName();
      siteToml.updateTable();

      if (currentTemplate == null ? siteToml.hasTheme()
          : !siteToml.hasTheme() || !currentTemplate.equals(siteToml.getThemeName())) {
        if (siteToml.hasTheme()) {
          Printer.themeHasChanged(siteToml.getThemeName());
        } else {
          Printer.themeHasBeenRemoved();
        }
        updateStaticTheme();
        updateTemplateTheme();
        siteToml.updateUpdateTime();
      }
      siteToml.updateTheme();
    } catch (IOException e) {
      throw new RuntimeException(e); // TODO printer ???
    }
  }

  @Override
  public void onDeleted() {
    throw new RuntimeException("Site.toml has been delete");
    // TODO catch error and stop
  }


  private StaticFolderGrouper getStaticFolder() {
    return siteToml.getStaticFolder();
  }

  private TemplateFolderGrouper getTemplateFolder() {
    return siteToml.getTemplateFolder();
  }

  private void applyStaticFile(Consumer<FileWatchable> consumer) {
    getStaticFolder().listStaticFileTheme().stream()
        .map(staticFile -> {
          try {
            return staticFile.getWatchable(outputFolder);
          } catch (FileNotFoundException e) {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .forEach(consumer);
  }


  private void updateStaticTheme() {
    applyStaticFile(FileWatchable::notifyDelete);
    getStaticFolder().setTheme(siteToml.getThemeFolder());
    applyStaticFile(FileWatchable::notifyCreated);
  }

  private List<FileWatchable> listWatchableTemplate() {
    return getTemplateFolder().listTemplateFileTheme().stream()
        .map(template -> {
          try {
            return template.getWatchable();
          } catch (IOException | WrongFileExtensionException e) {
            throw new RuntimeException(e);
          }
        })
        .collect(Collectors.toList());
  }

  private void updateTemplateTheme() {
    TemplateFolderGrouper grouper = siteToml.getTemplateFolder();
    List<FileWatchable> oldThemes = listWatchableTemplate();

    grouper.setTheme(siteToml.getThemeFolder());
    oldThemes.forEach(FileWatchable::onDeleted);
  }
}
