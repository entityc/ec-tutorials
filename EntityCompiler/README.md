# Entity Compiler

If you are new to the Entity Compiler this is the place to start. It covers everything you need to know about
the compiler to not only build microservices but also to build components of microservices that you can reuse or
share with others so they can easily add new features to their microservices.

The tutorial is broken down into major parts called **Modules**. Within each Module there are **Sessions** where each session discusses one smaller concept. Each session also steps through an exercise to practice what was learned. Starting with Module 1 inside each session directory is a `solution` directory that has a fully working exercise.

## [Module 0: Tutorial Introduction](Module0)

The tutorial starts with an Introduction module that helps you to install the compiler then invoke it on some very basic input files.

## [Module 1: Language Basics](Module1)

This is where you will learn some basics of the entity language such as defining an entity with attributes as well as relationships to other entities. You will also be introduced to the template engine built into the compiler that allows you to programatically inspect and print out the entities, attributes and relationships you have defined in your model. It also discusses how to define and use enumerations (enums).

## [Module 2: Templates Intermediate](Module2)

Now that we have the basics out of the way we can dive into more useful template language constructs so we can learn to build more powerful templates.

## [Module 3: Templates Filters](Module3)

This module covers a very powerful part of the template language called **filters**. Filters are much like a unix pipe where you can easily chain many together making for a very compact syntax.

## [Module 4: Enterprise Concepts](Module4)

The inspiration for the Entity Compiler was that it span an entire enterprise application from inside a microservice all the way out to client applications so as to bind them all together as one cohesive system. This module will cover they key concepts that allow the compiler span the enterprise.

## [Module 5: Generating Source Code](Module5)

The real nuts and bolts of the compiler is its ability to generate code. This module goes into how to generate code that you can use in a microservice or in a client application.

## [Module 6: Importing From Repositories](Module6)

As you build up a collection of templates and entity models, you will quickly realize that its best to define these in a central repository that can be accessed by all segments of the enterprise application. This module covers how to go about doing this.

## [Module 7: Advanced Topics](Module7)

As your enterprise application goes larger, you will want to employ the more advanced techniques and features of the compiler. This module covers, for instance, how to write template code such that application features can be coded in a single template file even though the code they generate span any number of generated source files.
