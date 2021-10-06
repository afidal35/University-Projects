package gla.commands;

import gla.folders.InputFolder;
import picocli.CommandLine;

/**
 * Class regroup [@Option] and [@Parameters] which are exclusive.
 */
class Md2HtmlOptionExclusive {

  @CommandLine.Parameters(
      index = "0",
      paramLabel = "<file1.html>",
      description = "Convert one or multiple markdown files into html files (10 maximum)."
  )
  String file;

  @CommandLine.Option(
      names = {"-i", "--input-dir"},
      description = "Build from the specified directory.",
      defaultValue = InputFolder.DEFAULT_FOLDER_NAME
  )
  String input;

}
