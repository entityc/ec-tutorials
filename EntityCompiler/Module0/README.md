# Module 0: Tutorial Introduction

## Session 1: Installation

### Objective

This will guide you through the process of installing all the necessary components needed for running the exercises of the tutorial.

### Discussion

This git repository includes the JAR file containing the Entity Compiler - specifically a version that works with all tutorial code.

### Exercise

Installing requires the following steps:

#### Step 1

Clone this git repository to somewhere on your computer. We will refer to this install directory as `EC_TUTORIAL_HOME`.

#### Step 2

Next we need to update your shell startup configuration file. For bash this is `~/.bash_profile`. For zsh this is `~/.zshrc`. In this file you will need to add:
```
export EC_TUTORIAL_HOME=path_to_this_git_repo
export PATH=$PATH:$EC_TUTORIAL_HOME/bin
```
Where `path_to_this_git_repo` is the directory where you cloned this git repository. This could simply be `~/ec-tutorials` if you placed it in your home directory.

> If you already use the Entity Compiler and have `ec` in your PATH then you may want to use the specific version of the `ec.jar` contained in this repository since it has been tested to work with the tutorial code.

#### Step 3

Open a new shell (terminal) or source your shell configuration file. To ensure it is working type:

```
which ec
```

This should point to the `ec` script in the tutorial repository's `bin` directory.


#### Step 4

If you plan to use IntelliJ to edit the files of this tutorial, then you will want to install two IntelliJ plugins that implement code highlighting for Entity Compiler specific source files:

| Plugin | Description |
| ------	| -----------	|
| `Entity-Model-JetBrains-Plugin-0.1.zip`| Supports syntax highlighting for entity definition language files (`.edl`).|
| `Entity-Template-JetBrains-Plugin-0.1.zip`| Supports syntax highlighting for the template language files (`.eml`).|

These plugin files are located in the `bin` directory of this tutorial.

To install:

1. Open IntelliJ and go to **Preferences...**.
2. On the left side of the Preferences panel, select **Plugins**.
3. Just below the panel title bar is another bar that has a settings gear icon towards the right. Click on that gear icon and select **Install Plugin from Disk...**.
4. Now select the first plugin in the table above from the tutorial's `bin` directory and hit ok.
5. At this point, **do not restart IntellJ**, instead repeat the procedure for the other plugin above.
6. Even after the second plugin install, **do not restart IntelliJ** even when it prompts you.
7. Hit OK on the preferences panel to save and close it.
8. **Quit** IntelliJ and re-launch

The reason quitting and relaunching IntelliJ instead of restarting is because sometimes restarting from its prompts does not work properly.

## Session 2: Getting Started

### Objective

The first part of the tutorial just shows you how to get started in using the Entity Compiler for a project.

### Discussion

When using the Entity Compiler in a project it is best to create a directory to contain files specific to the Entity Compiler. In this tutorial the directory used will be called just `ec` and it will be placed just inside the root directory of the project or in its "source" (e.g., `src`) directory (in this case the tutorial session). 

Every project typically has at least the following elements contained in one or more files that the compiler will read:

|Element|File|Description|
| -----	| --- | ---------	|
| Space	| `Space.edl`	| For the purpose of this part of the tutorial a space denotes where you will be generating code. Every project needs to define a primary space.	| 
| Configuration	| `Configuration.edl`	| The configuration is where you configure how you want the compiler to transform entities to files via templates (or built in transforms). You can have multiple configurations based on what you want to do. For this tutorial we just use a single configuration.	| 

There is no requirement as to how you split those elements into files, you just need to make sure you tell the compiler to read all the files you have created. 

In this introduction you will be instructed to type in the full command line, but **later** tutorials will prepare a `run.sh` script for you to run to simplify it.

### Exercise

For this intro we are simply going to invoke the compiler - nothing will be generated.

#### Step 1

Change your working directory to the `Session2` directory.

#### Step 2

On the command line, type:
```
ec build Tutorial ec/Space.edl ec/Configuration.edl
```

If all went well you should see no errors, just simply returns.

The `-c Tutorial` part of the command line is in reference to the configuration declared in `Configuration.edl` called `Tutorial`. The other two arguments on the command line simply reference the two input files to the compiler that are located in the `ec` directory.

