# Module 1: Getting Started

Welcome to the tutorial! Before getting into the details we'll introduce the microservice we will be building and get our project setup.

## Session 1: Introduction

### Objective

This session will allow you to become familiar with the elements that make up a microservice and specifically the Tutorial microservice that we will be building in this tutorial.

### Discussion

#### Spring Boot Framework

A typical Spring Boot microservice has the following components:

- Database
- Model Classes
- Repository Classes
- Data Transfer Object (DTO) Classes
- Mapping to/from Data Transfer Objects
- Service Classes
- Controller Classes

> To get the most out of this module it is best if you are already familiar with Spring Boot ([https://spring.io/microservices](https://spring.io/microservices)).


#### Data Model

The microservice we will build in this module will be a **Tutorial** microservice. That is, it will be a microservice that is capable of hosting an entire tutorial such as the one you are reading right now. In fact it can host any number of tutorials such as this one.

The data model has the following entities specific for tutorials:

- Tutorial
- Module
- Session
- Exercise
- Step

We want this site to have users and be able to control access levels such as **administrator**, **instructor** or just **student**; therefore, we have also added the following:

- User
- Role (enum)

#### Markdown

Since tutorials are heavy on text, it was decided to base the text on Markdown. This allows it to be composed and edited with any text editor but also have some level of richness. As a result, some of the templates provided will make it easier to bring markdown support to the site.

#### Web App

This tutorial will also go into how to build a web-based Admin Console UI for administrators and instructors as well as a Student web site for simply viewing tutorials.

### Exercise

There is no exercise for the first session.

## Session 2: Project Setup

### Objective

This session will cover how to generate a Maven project file to be used to compile the Spring Boot application for this module.

### Discussion

A maven project file specifies not only information about the application being built but also its library dependencies.  The repository of templates used for this module contains a template to generate a Maven project file that has all the library dependencies needed for this project.

### Exercise

This exercise will simply setup our project with a maven project file using a provided template along with some specified information about our project.

#### Step 1

Start by editing the `Configuration.edl` file in the `ec` folder. It is already setup with an `output` and a path of where to place the resulting `pom.xml` project file.

It also has a `templates` block that already contains an import statement to import from the repository that is holding server templates. All we need to do is add the following template statement inside the `templates {}` block that will import the template that builds our project file:

```
        template PomTemplate in "pom" {
            output primary Project
        }
```

The `in "pom"` is needed because it resides in a sub-folder in the `ServerTemplates` repository.

#### Step 2

Now edit the `Space.edl` file in the `ec` folder. We will need to add some things there that are specific about our project that will be used by the `PomTemplate` when it builds our project file.

Just inside the `space Space {` block, add the following namespace declaration:

```
    namespace org.entityc.tutorial
```

This namespace will represent your project's **base namespace** from which all other namespaces will be extended from. This means that when you use other templates in later sessions, they will generate Java classes that use this as the base part of their package name. 

#### Step 3

Again we are going to add something to the `Space.edl` file. This time it is information about our project that we will annotate on our `Space` object that will be used by the `PomTemplate` template when generating the project file and potentially by other templates. The information is annotated using a `metadata` block that is essentially a JSON object.

Add the following under the `namespace` statement we added in the previous step:

```
    metadata {
        "microserviceIdentifier" : "ec-tutorial-service",
        "microserviceTitle" : "Entity Compiler Tutorial Service",
        "basePackage" : "org.entityc",
        "databaseName" : "ECTutorialService"
    }
```

#### Step 4

Now we are ready to generate our project file. The generated project file will be called `pom.xml` and placed right in our session folder.

Invoke the run script:

```
./run.sh
```

You should now see the `pom.xml` file. If you look inside the file you will notice some of the information in the `metadata` block are placed in there.

## Session 3: Entity Model

### Objective

In this session we will define the entity model for the Tutorial microservice.

### Discussion

We will put all entities into an entity module called `tutorial`. This just allows us to keep things organized and separate from other entities we will introduce in later sessions.

The model will be identical to how this tutorial is structured. The top most entity is `Tutorial` and represents an entire tutorial. A tutorial can have many modules, represented by the `Module` entity. A module can have mutiple sessions where a session is represented by the `Session` entity. A session can have an exercise, actually we will make the model so it can have multiple exercises. An exercise is represented by the `Exercise` entity. Finally, an exercise can have mutiple steps represented by the `Step` entity.

#### Tutorial Entity

The tutorial entity has a field `identifier` that is used to uniquely identifier this tutorial across all other tutorials with a human readable identifier. This could be like a reverse domain name if it was hosting a large number of tutorials.

It then has a `title` attribute to store a title that is presented to the user along with a `summary` attribute that provide a brief description of the tutorial and finally an `overview` that provides more detail about the tutorial.

And of course, it has a many relationship to the `Module` entity since it is broken down into modules.

#### Module Entity

This entity is intended to be numbered so it has a `number` attribute. This will be used when presenting to the user along with a title, just like with this tutorial.

The `title` attribute represents the module's title. Like the tutorial, it also has `summary` and `overview` attributes to provide different levels of description about the module.

For relationships, it has a `one` relationship back to its parent entity the `Tutorial` and a `many` relationship to its sessions.

#### Session Entity

As with modules, sessions are also numbered so the `Session` entity has a `number` field as well. This will also be used when presenting to the user along with the session's title.

The `title` attribute represents the session's title. Sessions have an Objective sub-section prepresented by the `objective` attribute and a Discussion sub-section represented by the `discussion` attribute.

For relationships, it has a `one` relationship to its parent `Module` entity and a `many` relationship to its exercise(s).

#### Exercise Entity

For this tutorial, each session has only at most one exercise, but it was thought to make it more flexible so the model is defined to allow multiple exercises per session. Thus the `Exercise` entity has a `number` field. If there is only 1 exercise object in a session, it can ignore the attribute and just not number them for that session.

An exercise can have some text to it to give a small overview of the exercise as represented by the `overview` attribute. It however does not have a title.

For relationships, it has a `one` relationship to its parent `Session` entity and a `many` relationship to its steps.

#### Step Entity

Steps are clearly numbered using the `number` attribute. Like exercises, steps don't have a title and simply just have an attribute called `instructions` that contains instructions for the tutorial student on how to carry out the step.

For relationships, it simply has a `one` relationship to its parent `Exercise` entity.

### Exercise

In this exercise, all the files are already setup to go, no editing necessary, and the steps are pretty straight forward.

#### Step 1

Simply look over the `Space.edl` file in the `ec` folder to see how the entites are defined.

#### Step 2

We can run the run script just to make sure we don't have any syntax errors with the entity definitions:

```
./run.sh
```

Just like in the last session, a `pom.xml` file will be generated and you shouldn't see any error messages.
