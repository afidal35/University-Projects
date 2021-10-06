SSG - Static Site Generator - Group H
======================
[![pipeline](https://gitlab.com/SawHad/GLA-H/badges/master/pipeline.svg)]()
[![coverage](https://gitlab.com/SawHad/GLA-H/badges/master/coverage.svg)]()
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

SSG is THE static site generator written in Java. 
SSG takes a directory with content and templates and renders them into a full html website.

#### Supported languages:
- [CommonMark](https://commonmark.org) 

## Table of content
[[_TOC_]]

## Prerequisite

SSG runs mainly on unix and requires only a [Java JDK](https://adoptopenjdk.net) version 14 or higher to be installed.  
To check, run:

```shell
java --version
```
## Installation

Download the latest release of `ssg` [here](https://gitlab.com/SawHad/GLA-H/-/releases).  
Extract the project binary
```shell
$ tar -xvf ssg-x.x.x.tar
```
Add the project to system's PATH environment temporarily
```shell
$ cd ssg-x.x.x/bin
$ export PATH=$PATH:$(pwd)
```
Change permissions of the script `ssg`
```shell
$ chmod +x ssg
```
Now the script `ssg` can be executed anywhere till you restart your session   
Test by running :
```shell
ssg version
```

### Usage

#### Translating one file to html5

**index.md**
```markdown
# This is title
```
Translate the file
```shell
ssg build index.html
```
Your file will be translated into the default `_output` directory.
```
cat _output/index.html
```
The above command should display the content of the translated version of `index.md`

#### Translating a directory to html5
Create a base directory, let's name it `minimal`.  
Create a `minimal/site.toml` file that contains the project basic configuration.  

**minimal/site.toml**
```toml
[general]
title = "A Minimal Site"
author = "The SSG Group H Team"
``` 
Create a `minimal/content` directory with the files to be translated. 

**minimal/content/index.md**
```markdown
# Index

This is the home page of the Minimal site.

Your static site generator should support [[MarkdownMarkup]].
```
Build the project.  
```shell
ssg build
```
`--jobs=<number-of-jobs>` option can be added to specify the number of simultaneous files to build.

Test and launch with `serve`
```shell
ssg serve
```
Open your web browser to the specified address, usually [http://127.0.0.1:8080](http://127.0.0.1:8080)

#### Watchdog
Watch for file changes and automatically rebuild the project.
```shell
ssg build --watch
```

#### For more usage, run the command
```shell
ssg help
```

## Documentation
[Go to the documentation](https://ndione24.gitlab.io/gla-docs/index.html)

## Useful links

* [Issue tracker](https://gitlab.com/SawHad/GLA-H/-/issues)
* [Source code](https://gitlab.com/SawHad/GLA-H)

## Releases
All releases are listed [here](https://gitlab.com/SawHad/GLA-H/-/releases).

## Authors

* Alex Fidalgo - [@alexfdg](https://gitlab.com/alexfdg)

* Ny Andrianina Mamy Razafintsialonina  - [@nyandrianinamamy](https://gitlab.com/nyandrianinamamy)

* Jeremy Damour - [@jeremy.damour](https://gitlab.com/jeremy.damour)

* Mouhamed Ndione - [@Ndione24](https://gitlab.com/Ndione24)

* Sutra Suhana - [@SutraSuhana](https://gitlab.com/SutraSuhana)

* Sawssen Hadded - [@SawHad](https://gitlab.com/SawHad)
