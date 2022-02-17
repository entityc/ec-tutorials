# Module 2: Building the Microservice

In this module we will go over setting up a base microservice with the entities we established in the previous module. It includes the following sessions:

1. Database
2. Baseline classes
3. Model classes
4. Repository Classes
5. Data Transfer Object Classes
6. Mapper Classes
7. Service Classes
8. Controller Classes
9. Microservice (shortcut)
10. User Authentication and Authorization

Each session will have a discussion about the topic.

## Session 1: Database

### Objective

Now that we have our entity model setup we can generate a database model.

### Discussion

Support for database model generation was actually available before the template engine existed and is actually built into the compiler. It supports creating only Postgresql, but supports "migration" SQL that represent the differences in your entity model as you creation new versions of it. We will cover versioning in detail in a future module. For now we will always generate the full model.

The database model generation is done with what is called a "transform". This is because it is written in Java code instead of template code and is built into the compiler.

To run the transform, you declare it in your `configuration` block as follows:

```
    transform Postgres {
        ...
    }
```

Inside this transform declaration, you will need to specify **two** outputs: `primary` and `schema`.

|Output Type|Description|
| ---------	| ---------	|
| `primary`|The primary output should be a path to a directory where the generated sql files will be placed. This is typically a directory such as `src/main/resources/db/migration`. As you create new versions of your entity model, the transform will place "migration" SQL files there. A "migration" SQL contains SQL to convert and existing database model to another, so for instance, if a column was added to an existing table, this SQL will include an `alter table` statement instead of creating the table from scratch again.|
| `schema`|This is a path to a directory that holds special schema (transform internal) files that this transform needs to store to keep track of changes in the entity model as it changes from version to version. This should be a directory that is under the same source code control as the rest of the project files.|

A common configuration would be as follows:

```
    output DatabaseMigrationResources {
        path "src/main/resources/db/migration"
    }

    output DatabaseSchema {
        path "schema"
    }

    transform Postgres {
        output primary DatabaseMigrationResources
        output schema DatabaseSchema
    }
```

As mentioned before, a future module will cover how to perform versioning of your entity model. For this module we will just create an initial version and work only with that version.

### Exercise

#### Step 1

Edit the `Configuration.edl` file in the `ec` folder. Add the above `output` and `transform` statements.

This will configure the transform to execute when we invoke the compiler.

#### Step 2

The Postgres transform requires that a domain be defined called `Database`. Edit `Space.edl` and add the following just **under** the `repository DomainRepo` definition:

```
    import Database from DomainRepo
```

This will import the base definition of the `Database` domain.

We will need to add to this base definition of the `Database` domain to set its namepace and also to rename an entity so it doesn't cause a problem with Postgres.

The just outside of the space block add:

```
domain Database (Tutorial) {

    namespace db.migration

    entity Module rename tutorial_module
}
```

#### Step 3

Now we are ready to run our run script and create the database SQL.

```
./run.sh
```

The SQL file will be created under `src/main/resources/db/migration` and will start with `V1__`. Take a look at the file and see how entites and attributes have been rendered into tables and columns.

## Session 2: Baseline Classes

### Objective

This session will go over installing some baseline classes needed by sessions after this one.

### Discussion

Most typical microservices have some custom exceptions and utility classes. These can easily be installed in our project directory by running a couple templates from our `ServerTemplates` repository: `exception/ExceptionTemplate` and `util/UtilsTemplate`. 

As with many templates, they depend on certain domains being defined. Fortunately those can also be imported like we did for the base `Database` domain.

### Exercise

#### Step 1

For this session and the following sessions we need to place generated files inside the `src/main` directory so we should define a new output for that.

Edit the `Configuration.edl` file in the `ec` folder. Under the definition for the `Project` output add the following:

```
    output ServerCode {
        path "src/main/java"
    }
```

This is the typical base path for locating Java classes in a server application. From this `java` directory subsequent child directories will follow the Java package structure.

#### Step 2

 Inside the `templates {}` block add the following (under the `template PomTemplate` declaration):

```
        template ExceptionTemplate in "exception" {
            output primary ServerCode
        }
        template UtilsTemplate in "util" {
            output primary ServerCode
        }
```

Notice the reference to the `ServerCode` output we defined in the previous step.

#### Step 3

Now we need to import the the domains that these templates require. Edit `Space.edl`, find the line that imports the `Database` domain from the `DomainRepo` and add our two required domains. The line should look like this after you have finished:

```
    import Database, Exception, Utils from DomainRepo
``` 

#### Step 4

Now its time to run our run script:

```
./run.sh
```

Under the `src/main/java` directory you should now see a directory tree starting with `org`. The exception classes are located in `org/entityc/tutorial/exception` and the utility classes in `org/entityc/tutorial/util`.

Notice how the base path (or package) is the same as our space's namespace. This is because the two domains we imported use a namespace that is extended from our space's namespace.

## Session 3: Model Classes

### Objective

In this session we will cover how to generate model classes.

### Discussion

Model classes are used extensively inside the microservice as an object representation of the data in the database. To generate these classes we simply use the `ModelTemplate`.

### Exercise

#### Step 1

Start by editing the `Configuration.edl` file in the `ec` folder. Inside the `templates {}` block add the following:

```
        template ModelTemplate in "model" {
            output primary ServerCode
        }
```

#### Step 2

Just like in the previous session, we need to add the domain that is required by the template we just added which is the `Model` domain. Edit `Space.edl` and on the line that imports domains from `DomainRepo` add `Model` so it looks like this:

```
    import Database, Exception, Utils, Model from DomainRepo
```

#### Step 3

Now its time to run our run script:

```
./run.sh
```

If you traverse from the `src/main/java/org` directory down you should see a `model` directory inside which will be a model class for each entity.

## Session 4: Repository Classes

### Objective

This session will go over how to generate Spring Boot repository classes.

### Discussion

These classes represent the first abstraction layer over the database and keeps layers above it from having to deal with SQL and only deal with Java objects that fetch and store data.

### Exercise

#### Step 1

Start by editing the `Configuration.edl` file in the `ec` folder. Inside the `templates {}` block add the following:

```
        template RepositoryPublisher in "repository" {
            output primary ServerCode
        }
```

Note how the name of this template ends with `Publisher`; this is because it uses the publisher constructions of the template language in its implementation.

#### Step 2

Just like in the previous sessions, we need to add the domain that is required by the template we just added which is the `Repository` domain. Edit `Space.edl` and on the line that imports domains from `DomainRepo` add `Repository` so it looks like this:

```
    import Database, Exception, Utils, Model, Repository from DomainRepo
```

#### Step 3

Now its time to run our run script:

```
./run.sh
```

If you traverse from the `src/main/java/org` directory down you should see a `repository` directory inside which will be a repository class for each entity.

## Session 5: Data Transfer Object (DTO) Classes

### Objective

This session will go over how to generate Data Transfer Object classes.

### Discussion

These classes are used for passing data to clients outside the microservice. Although they may carry some of the same data as Model classes, it can be different in value and/or structure. When an endpoint is called to get data, an object of this class is returned. Likewise for sending data from a client into the server, objects of this class are used to transfer that data.

### Exercise

#### Step 1

Start by editing the `Configuration.edl` file in the `ec` folder. Inside the `templates {}` block add the following:

```
        template DTOTemplate in "dto" {
            output primary ServerCode
        }
```

#### Step 2

Just like in the previous sessions, we need to add the domain (or domains) that are required by the template we just added which in this case is two domains: the `DTO` domain and the `JSONDTO` domain. Edit `Space.edl` and on the line that imports domains from `DomainRepo` add these two domains so it looks like this:

```
    import Database, Exception, Utils, Model, Repository, DTO, JSONDTO from DomainRepo
```

The reason we have to also use the `JSONDTO` domain is because we want to build endpoints that will be using JSON and the structure of the classes built using the `JSONDTO` domain lends itself better for this.

#### Step 3

Now its time to run our run script:

```
./run.sh
```

If you traverse from the `src/main/java/org` directory down you should see a `dto` directory inside which will be a DTO class for each entity.

## Session 6: Mapper Classes

### Objective

This session will go over how to generate classes used to map between Model objects and DTO objects.

### Discussion

Mapper classes simply create Model objects from DTO objects and vice versa by knowing how they are different. These will be needed when having to marshal and unmarshal data to and from clients using DTO objects instead of Model objects.

### Exercise

#### Step 1

Start by editing the `Configuration.edl` file in the `ec` folder. Inside the `templates {}` block add the following:

```
        template MapperTemplate in "mapper" {
            output primary ServerCode
        }
```

#### Step 2

Just like in the previous sessions, we need to add the domain that is required by the template we just added which is the `DTOMapper` domain. Edit `Space.edl` and on the line that imports domains from `DomainRepo` add `DTOMapper` so it looks like this:

```
    import Database, Exception, Utils, Model, Repository, DTO, JSONDTO, DTOMapper from DomainRepo
```

#### Step 3

Now its time to run our run script:

```
./run.sh
```

If you traverse from the `src/main/java/org` directory down you should see a `mapper` directory inside which will be a mapper class for each entity.

## Session 7: Service Classes

### Objective

This session will cover how to generate the Spring Boot service classes.

### Discussion

Service classes represent a layer that provides some of the "business logic" to your application, providing services that are of a higher level and service the functionality requested by endpoints supported by the application.

For this application they will be used to do hierarchical object mapping from models to structured DTO representations. The `ServicePublisher` template that we will use can be configured as to how to do this mapping using views, specifically the views `APIGet` and `Reference`.

Inside the `DTO` domain, how you specify the `APIGet` view will determine how the mapping between model objects and DTO objects will be performed. First lets look at the portion of the `TutorialDto` class that describes its relationship to the `Module` entity:

```
public class TutorialDto
{
    ...
 
    // RELATIONSHIPS
    // The modules of a tutorial.
    private Set<ModuleDto> modules;

}
```

Although the DTO class is defined to have a set of modules, we don't have set it to anything. If an endpoint just wanted information about the tutorial, we could just leave `modules` set to null (and even have it excluded from the JSON object in the response).

However, if a GET endpoint wants to retrieve a tutorial object hierarchically, then we need to fill in the `modules` variable with a `Set` of child `ModuleDto` objects that belong to this tutorial object.

If we want to support the hierarchical option for the `Tutorial` entity, we need to configure the `APIGet` view as follows:

```
domain DTO (Tutorial) {
    entity Tutorial {
        view APIGet {
            relationships {
                include to-many entity Module as modules with view APIGet
            }
        }
    }
}
```

This will basically cause code in the `TutorialService` class to optionally (based on a `hierarchical` boolean parameter) populate the `modules` member variable with the tutorial's modules as `ModuleDTO` objects. The content of the `ModuleDTO` object will be defined by how its `APIGet` view is specified (since we say `... with view APIGet`).

If we want to have the hierarchical structure continue we would thus define `Module` in a similar way:

```
    entity Module {
        view APIGet {
            relationships {
                include to-one entity Tutorial with view Reference
                include to-many entity Session as sessions with view APIGet
            }
        }
    }
```

Notice here we specify its relationship to `Session` in the same way we did between `Tutorial` and `Module` above. Also note that we also define how we want to map the module's relationship back to its parent `Tutorial` here using `... with view Reference`. The `Reference` view is defined to only include the objects primary key since it is defined as follows:
```
    view Reference {
        include primarykey
        attributes { exclude }
        relationships { exclude }
    }
```

We are getting closer to having our microservice up and running. These service classes directly support the layer that implements our actual REST endpoints.

### Exercise

#### Step 1

Start by editing the `Configuration.edl` file in the `ec` folder. Inside the `templates {}` block add the following:

```
        template ServicePublisher in "service" {
            output primary ServerCode
        }
```

#### Step 2

Just like in the previous sessions, we need to add the domain that is required by the template we just added which is the `Service` domain. Edit `Space.edl` and on the line that imports domains from `DomainRepo` add `Service` so it looks like this:

```
    import Database, Exception, Utils, Model, Repository, DTO, JSONDTO, DTOMapper, Service from DomainRepo
```

#### Step 3

Now we need to configure how we map our tutorial data so its available to our endpoints in an optional hierarchical way.

At the same time, lets reorganize a bit and move our `domain` declarations to another file called `Domains.edl`.

The `Domains.edl` that you start out with will have our previously defined `Database` domain in it. Edit the `Domains.edl` file and add the `DTO` domain with our view configurations:

```
domain DTO (Tutorial) {
    entity Tutorial {
        view APIGet {
            relationships {
                include to-many entity Module as modules with view APIGet
            }
        }
    }
    entity Module {
        view APIGet {
            relationships {
                include to-one entity Tutorial with view Reference
                include to-many entity Session as sessions with view APIGet
            }
        }
    }
    entity Session {
        view APIGet {
            relationships {
                include to-one entity Module with view Reference
                include to-many entity Exercise as exercises with view APIGet
            }
        }
    }
    entity Exercise {
        view APIGet {
            relationships {
                include to-one entity Session with view Reference
                include to-many entity Step as steps with view APIGet
            }
        }
    }
    entity Step {
        view APIGet {
            relationships {
                include to-one entity Exercise with view Reference
            }
        }
    }
}
```

#### Step 4

Now its time to run our run script:

```
./run.sh
```

If you traverse from the `src/main/java/org` directory down you should see a `service` directory inside which will be a service class for each entity. Inside a service class you should see a method named `dtoFromModel` that performs both a flat mapping of an object using the mapper classes and also an optional hierarchical mapping using the `dtoFromModel` methods of service classes for "child" entities.

## Session 8: Controller Classes

### Objective

This session will cover how to generate the Spring Boot controller classes.

### Discussion

The Spring Boot controller class is where REST endpoints are defined and implemented for your application. How endpoints are defined are controlled by the templates we use from our template library. We will break it down by the type (method) of REST call. Each method will be how it is done for each entity in your model. Note that all endpoint paths are usually prefixed by a project path - we will omit that here.

#### Create Methods

As the name implies a create method allows you to create an object on the server. These endpoints use the POST HTTP method. There are up to two create endpoints that the controller template will generate:

- Create
- Create With Parent

##### Create

This is the most basic create endpoint that simply creates a new object from a supplied JSON object. The endpoint is simply defined as:

	POST /<entityName>

Where `<entityName>` is the name of the entity of the created object, and where the body of the request contains the JSON object that supplies data for the created object.

##### Create With Parent

This is more of a convenient endpoint for creating an object that has a parent relationship with another entity. It is defined as:

	POST /<entityName>/<parentEntityName>/{id}

Where `<entityName>` is the name of the entity of the created object and `<parentEntityName>` is the name of the entity to which this entity has a parent relationship and `{id}` is the unique identifier of the parent object. For instance, for creating a module with respect to a specific tutorial, the endpoint would be:

	POST /module/tutorial/{id}

This basically means you do not have to specify the parent object's ID in the JSON and can instead just provide in the path.

#### Update Method

An update simply lets you modify an object that already exists on the server. The endpoint uses the PUT HTTP method and is defined as:

	PUT /<entityName>/{id}

Where `<entityName>` is the name of the entity of the object to be updated and `{id}` is the unique ID of the object being updated. The body of the request is a JSON object that contains the data with which to update the object.

#### Get Methods

There are usually multiple variations of methods that fetch data. The template library we use focuses on the following methods for an entity:

- Get by ID
- Get List
- Get List By Parent

##### Get by ID

It is sometimes necessary to get information about an object by its unique identifier (primary key) so this is supported as:

	GET /<entityName>/{id}

Where `<entityName>` is the name of the entity and `{id}` represents the ID of the object that will be supplied by the client when calling the endpoint. For instance, for the `Tutorial` entity, the endpoint would be `/tutorial/{id}`.

The data returned is the contents of the requested object formatted as JSON.

##### Get List

Maybe one of the more common endpoints is getting a list of all objects of an entity. This of course includes parameters to control paging. The endpoint is of the form:

	GET /<entityName>?start=<index>&limit=<number>

Where `<entityName>` is the name of the entity. The `start` parameter allows you to specify a starting index (`<index>`) of the results and `limit` allows you to limit the number (`<number>`) of results returned.

##### Get List by Parent

This is similar to the above but also allows you to specify the unique ID of a parent object such that all returned objects are those that have a parent relationship to that object. For instance, we could use this endpoint to get all the modules of a tutorial.

This endpoint also supports paging and is of the form:

	GET /<entityName>/<parentEntityName>/{id}?start=<index>&limit=<number>

Where `<entityName>` is the name of the entity of the returned objects, `<parentEntityName>` is the name of the parent entity to which all returned objects are with respect. The `start` and `limit` are the same as above.

For instance, to get the modules from a tutorial, the endpoint would looke like:

	GET /module/tutorial/{id}

#### Delete Methods

When it comes to deleting objects from the server there are two such endpoints supported by the template library:

- Delete by ID
- Delete by Parent

These endpoints use the DELETE HTTP method.

##### Delete by ID

This is the most straight forward one, simply deleting an object by its unique ID. The endpoint is of the form:

	DELETE /<entityName>/{id}

Where `<entityName>` is the name of the entity belonging to the object being deleted and `{id}` is the unique ID of the object being deleted.

##### Delete by Parent

Sometimes you may want to delete all objects that belong to a parent object. For instance, deleting all steps of an exercise. This endpoint takes the following form:

	DELETE /<entityName>/<parentEntityName>/{id}

Where `<entityName>` is the name of the entity belonging to the objects being deleted, `<parentEntityName>` is the name of the parent entity to this entity and `{id}` is the unique ID of the parent object to which all the objects to delete belong.

### Exercise

#### Step 1

Start by editing the `Configuration.edl` file in the `ec` folder. Inside the `templates {}` block add the following:

```
        template ControllerPublisher in "controller" {
            output primary ServerCode
        }
```

#### Step 2

Just like in the previous sessions, we need to add the domain that is required by the template we just added which is the `Controller` domain. Edit `Space.edl` and on the line that imports domains from `DomainRepo` add `Controller` so it looks like this:

```
    import Database, Exception, Utils, Model, Repository, DTO, JSONDTO, DTOMapper, Service, Controller from DomainRepo
```

#### Step 3

When we imported the `Controller` domain in the previous step it also included the definition of a domain called `APIPath` that is responsible for definiing both the base path (from `namespace`) and the naming method when using entity or attribute names in an endpoint path.

The default naming for this `APIPath` domain is `dashesLowercase` which is to make all characters lowercase and insert dashes between words. We could override this for our project if we want but we will just leave that as the default.

The base path for our endpoints is controlled via the `namespace` of this `APIPath` domain and there is no default so we must specialize the `APIPath` domain to set it.

Edit the `Domains.edl` file and add the following:

```
domain APIPath (Tutorial) {
    namespace api.ectutorials
}
```

This will make the base path for all our endpoints to be `/api/ectutorials`

#### Step 4

Now its time to run our run script:

```
./run.sh
```

If you traverse from the `src/main/java/org` directory down you should see a `controller` directory inside which will be a controller class for each entity. Inside a controller class you should see a method for each endpoint supported.

## Session 9: Microservice (shortcut)

### Objective

This session will go over how to basically shortcut many of the sessions above using a template that encompasses the essential parts of a microservice.

### Discussion

It was important to go over each of the parts that make up a Spring Boot microservice so you know how each work and are configured for your application. Now that we have covered them we can change our project to use a single template called simply `Microservice.edl` that basically imports the individual templates discussed in the previous sessions.

This greatly simplifies configuration but still requires the same domain import and specialization to customize template behavior to suit your project.

Basically inside your `config` statement and inside its `templates {}` block, you can replace most of the templates with the following:

```
        template Microservice {
            output primary ServerCode
        }
```

Another advantage to using this template is that in the future if the template library decides to change the partitioning or add new elements to the core microservice, it won't affect the applications that use this template.

> Of course, you can control the impact to changes by importing templates from a repository by a repository tag.

This template actually does include a template we have not used before: `SpringBootApplicationPublisher`. This template creates a required class for a Spring Boot application that represents the application. It will expect to find a metadata property defined on the application's `space` object called `microserviceName` that is the name of your application.

### Exercise

In this exercise we will simply covert our configuration to use this template.

#### Step 1

Edit the `Configuration.edl` file in the `ec` folder. Replace the large `templates {}` block with:

```
    templates {
        import from ServerTemplates

        template PomTemplate in "pom" {
            output primary Project
        }

        template Microservice {
            output primary ServerCode
        }
    }
```

##### Step 2

Edit the `Space.edl` file and add the `microserviceName` metadata property. We can set it to the same we set for the database name. Inside the `metadata` block of our `Space` object add the following line:

```
        "microserviceName" : "ECTutorialService",
```

#### Step 3

Now its time to run our run script:

```
./run.sh
```

If you traverse from the `src/main/java/org` directory down you should see a new class called `ECTutorialServiceApplication.java`. Notice how its name is derived from the `microserviceName` value we set in the previous step.

#### Step 4

Now that we have generated an entire basic microservice, we can build it to make sure there are no issues.

Since we also generated the Maven project file, we just need to invoke maven as follows:

```
mvn clean package spring-boot:repackage
```

For convenience, this has been placed into a script called `build.sh` so you can just:

```
./build.sh
```

You should see a lot of output from Maven. Ultimately you should see the following at the bottom of the output:

```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

This means its ready to deploy and run on a server machine. However, keep in mind that at this point it has no user authentication so the endpoints are potentially accessible by anyone. In the next sessions we will add more functionailty to the microservice, such as user authentication.

## Session 10: User Authentication and Authorization

### Objective

In this session we will discuss how to add authentication (the requirement to login) and authorization (having levels of access to data).

### Discussion

The template library we are using combines authentication and authorization into a group called Security. This involves both a top level template `SecurityTemplate` and a domain `Security`.

This tutorial will not cover the details of implementing security feature in Spring Boot and mainly cover how to enable features of the security templates that generate the security code. First we will cover Authentication features followed by Authorization.

#### Authentication

The requirement to have an account and login to a website with that account is an important part of many web sites. We will add this to our tutorial website. The Security templates we use go beyond the basic authentication and use one that is based on JWT.

The first thing you need to do is to create an entity that will represent your user. We will then use a new domain called `Security` that will be used to annotate our user entity with tags to instruct the `SecurityTemplate` to generate authentication code for our entity. The tagging gives us freedom in naming the user entity and its attributes as we like, then tag (in the `Security`) domain with specific tags.

For authentication, our user entity must at least have a field that represents a username and a field that represents a password. Of course, the password field will be used to store an encoded password. The username field should be one that is considered unique across all other users and be something they choose and can entered in a login screen. You can also define a field that represents an account enable which would allow an administrator to disable or suspend a user account. The user entity we will use for this microservice is defined as:

```
    entity User {

        D "Represents a user in the system."

        primarykey uuid userId

        attributes {
            string firstName {
                D "The user's first (given) name."
            }
            string lastName {
                D "The user's last (family) name."
            }
            string email {
                D "The user's Email address that is also their username."
            }
            string encodedPassword {
                D "The user's password encoded so not in plain text."
            }
            boolean enabled = true {
                D "If set the user is allowed to login, otherwise they cannot log in."
            }
            creation date createdOn { D "When the user account was created." }
            modification date modifiedOn { D "When the user account was last modified." }
        }
    }
```

As a typical user entity might have, it has fields to store their first and last names. Also note that this user entity is defined to have an email field which we can use as their username.

Now that we the user entity defined we can tag fields with respect to the `Security` domain by specializing the `Security` domain for our project so that the generated code with be specific to our project.

Actually before we add tags to the entity attributes we need to add a tag to the entity itself to tell the templates that this is our "user" entity. This would look like this:

```
domain Security (Tutorial) {

    entity User {
        T "user"
    }
}
```

Now the templates will know the `User` entity is the user entity. The name of our entity could be anything.

Next we should add a tag to the attribute that represents the username. This would looke like:

```
        attributes {
            email {
                T "login:username"
            }
        }
```

Likewise for the password attribute (also inside the same `attributes {}` block):

```
            encodedPassword {
                T "login:password"
            }
```

Finally if there is an attribute that represents an account enable, it can be tagged as follows:

```
            enabled {
                T "login:enabled"
            }
```

This will synthesize the code necessary for creating user authentication.

#### Authorization

Authorization is the control of access to specific resources on the server. To do this we first define our roles, then we apply those roles to the resource to which we want to control access.

##### Defining Roles

The template library we are using allows us to define roles with an `enum`. For this tutorial we will define it as:

```
    enum Role {
        D "Represents a level of security in the system."
        Student       = 1 { D "Is only allowed to view tutorials." }
        Instructor    = 2 { D "Is allowed to view, modify and create new tutorials." }
        Administrator = 3 { D "Is allowed to do what the Instructor can do but also change the role of users." }
    }
```

The name of the enum and its items can be anything. We will use tags in the `Security` domain to indicate this is the "role" enum to be used for security code generation.

```
domain Security (Tutorial) {
    enum Role {
        T "role"
    }
}
```

Inside our specialization of the `Security` domain we simply declare the enum `Role` and inside that block add the tag `"role"`.

Additionally we should mark an item in that enum that should serve as the default role - that is the role someone gets when they register in the system.

This can be done as follows:

```
domain Security (Tutorial) {
    enum Role {
        T "role"

        Student {
            T "role:default"
        }
    }
}
```

Tagging the `Student` item with `"role:default"` means **new** users will be given that role.

Since users have roles, we need to update the `User` entity so it can keep track of them. We can do this as the following:

```
    entity User {
        ...
        attributes {
            many Role roles { D "The roles assigned to a user." }
            ...
        }
        ...
    }
```

We define it such that a user can have `many` roles.

##### Assigning Roles to Resources

Now that we have our roles setup we can use them to indicate how specific roles have access to specific entites and/or attribute and specifically read and write access.

Before getting to the code, lets just make a table of the permissions we want for our roles. For now we will just think about permissions on an entity basis:

|Entity|Student|Instructor|Administrator|
| ----	| -----	| --------	| -----------	|
| `User` | read / write (self only\*) | read / write (self only\*) | read / write |
| `Tutorial` | read | read/write | read |
| `Module` | read | read/write | read |
| `Session` | read | read/write | read |
| `Exercise` | read | read/write | read |
| `Step` | read | read/write | read |
| `Language`†| read | read | read/write |

\* A user can write only some fields of their account. † We will cover language in the next session.

We will define a user to have multiple roles, so an administrator can make themselves be both an administrator and an instructor.

Before we configure each entity we first need to establish tags for each role, then use those tags to configure the entities with those tags. We will establish role tags as follows:

```
domain Security (Tutorial) {

    enum Role {
        T "role"
        Student       { T "role:default" }
        Instructor    { T "role:instructor" }
        Administrator { T "role:admin" }
    }

    ...
}
```

As we explained previously, the enum itself is tagged with `role` to establish that this is the enum used for roles. The `Student` item we establish as our default using the `role:default` tag. The next two items have tags that start with `role:` but end with something we determine. For the `Instructor` item we go with a tag of `role:instructor` and for the `Administrator` item we go with `role:admin`. We will use these tags to form larger tags when tagging specifc entities and/or their attributes to control access via that role.

So for our `User` entity, to set its access as shown in the table above, we would tag as follows:

```
domain Security (Tutorial) {

    ...

    entity User {
        T "user"
        T "access:write:user"
        T "access:write:role:admin"
        T "access:read:role:admin"
        ...
    }
}
```

The format of the tag is basically: `access:` `read` or `write` `:` *role_tag*

Where *role_tag* is one that we defined for our role enum items. For instance `access:write:role:admin` means write access for the `Administrator` role. There is also a special *role_tag* of `user` that means the logged in (or principle) user. This can only be used on the entity that is the "user" (tagged with `user`) entity. Basically here we are giving a user write access to their own user object. As we will show later we can further limit this by attribute so they cannot change things that we only want an admin to be able to change.

The other entites are pretty straight forward:

```
    entity Tutorial {
        T "access:write:role:admin"
    }
    entity Module {
        T "access:write:role:instructor"
    }
    entity Session {
        T "access:write:role:instructor"
    }
    entity Exercise {
        T "access:write:role:instructor"
    }
    entity Step {
        T "access:write:role:instructor"
    }
    entity Language {
        T "access:write:role:admin"
    }

```

Normally the access for each attribute of an entity are inherited from the entity, so if you give write access to an entity you are also giving write access to all its attributes - that is unless you specifically assign access to an individual attribute to override its inherited access.

Now lets refine the access to the `User` entity attributes by tagging the attributes in the same way we have been doing for entities:

```
    entity User {
        T "user"
        T "access:write:user"
        T "access:write:role:admin"
        T "access:read:role:admin"

        attributes {
            roles {
                T "access:write:role:admin"
            }
            email {
                T "login:username"
            }
            enabled {
                T "login:enabled"
                T "access:write:role:admin"
            }
            encodedPassword {
                T "login:password"
            }
        }
    }
```

Anytime an attribute is given a specific access, it overrides what it inherited from the entity. That is, if we assign write access to a role, all other roles that would have been inherited from the entity are discarded and overriden.

Notice how the `roles` and `enabled`  attributes are tagged with `access:write:role:admin`. This means that they will only be accessible by the admin.

##### Entity Relationships to User Entity

There are times when it is useful to keep track of which user created something or last modified an object. This is done by defining a relationship between an entity and the "user" entity (the entity tagged with `user`). But there needs to be code to actually set the relationship field when the entity object is created/modified. The security templates support generating this code automatically by the way the relationships are tagged:

| Tag | Description |
| ---	| -----------	|
| `user:created`| When placed on a relationship **to** the "user" entity, code will be synthesized such that when objects of the **from** entity are created this relationship is set to the logged in user's "user" object.|
| `user:modified`| When placed on a relationship **to** the "user" entity, code will be synthesized such that when objects of the **from** entity are modified this relationship is set to the logged in user's "user" object.|

For instance:

```
    createdBy {
        T "user:created"
    }
```

This will generate code such that when a new object is created, the `createdBy` field will automatically be set to the logged in user (the one doing the create). Likewise for:

```
    lastModifiedBy {
        T "user:modified"
    }
```

This will generate code such that when the object is updated, this `lastModifiedBy` field is automatically set to the logged in user (the one doing the modification).

### Exercise

#### Step 1

For the first step, lets define our user entity and role enum. Edit `Space.edl` and at the bottom we will place these two model elements inside a module called `platform`:

```
module platform
{
    enum Role {
        D "Represents a level of security in the system."
        Student       = 1 { D "Is only allowed to view tutorials." }
        Instructor    = 2 { D "Is allowed to view, modify and create new tutorials." }
        Administrator = 3 { D "Is allowed to do what the Instructor can do but also change the role of users." }
    }

    entity User {

        D "Represents a user in the system."

        primarykey uuid userId

        attributes {
            many Role roles { D "The roles assigned to a user." }
            string firstName {
                D "The user's first (given) name."
            }
            string lastName {
                D "The user's last (family) name."
            }
            string email {
                D "The user's Email address that is also their username."
            }
            string encodedPassword {
                D "The user's password encoded so not in plain text."
            }
            boolean enabled = true {
                D "If set the user is allowed to login, otherwise they cannot log in."
            }
            creation date createdOn { D "When the user account was created." }
            modification date modifiedOn { D "When the user account was last modified." }
        }
    }
}
```

With these in place we can add tags from the `Security` domain.

#### Step 2

Edit `Domains.edl` and at the bottom you will see an empty specialization of the `Security` domain:

```
domain Security (Tutorial) {

}
```

Inside this specialization we will first add tags to the role enum. Add the following inside the specialization block:

```
    enum Role {
        T "role"
        Student       { T "role:default" }
        Instructor    { T "role:instructor" }
        Administrator { T "role:admin" }
    }
```

These tags will set us up for applying them on our model entities to configure authorization.

Now, just under this we can add tags to our user entity by inserting the following:

```
    entity User {
        T "user"
        T "access:write:user"
        T "access:write:role:admin"
        T "access:read:role:admin"
        attributes {
            roles {
                T "access:write:role:admin"
            }
            email {
                T "login:username"
                T "email"
            }
            enabled {
                T "login:enabled"
                T "access:write:role:admin"
            }
            encodedPassword {
                T "login:password"
            }
        }
    }
```

The tagging here covers both the Authorization for the user entity but also Authentication for the system by establishing this entity as the "user" entity along with its username and password attributes that are needed for login.

Next we can add the remaining Authorization related tags for the other entities in our model:

```
    entity Tutorial {
        T "access:write:role:admin"
        relationships {
            createdUser {
                T "user:created"
            }
        }
    }
    entity Module {
        T "access:write:role:instructor"
    }
    entity Session {
        T "access:write:role:instructor"
    }
    entity Exercise {
        T "access:write:role:instructor"
    }
    entity Step {
        T "access:write:role:instructor"
    }
```

This also adds the tag `user:created` to the `createdUser` relationship of the `Tutorial` entity so that that field will automatically be set to the logged in user when a tutorial is created.

#### Step 3

Now we are ready to configure the `SecurityTemplate` to run. Edit `Configuration.edl` and add the following inside the `templates {}` block of the configuration:

```
        template SecurityTemplate in "security" {
            output primary ServerCode
        }
```

The `SecurityTemplate` not only installs many source files associated with authentication and authorization but also authors code to many of the other templates to build the support it needs for those features.

#### Step 4

Now we are ready to run the compiler to generate new code:

```
./run.sh
```

Now if you traverse from the `src/main/java/org` directory down you should see a new directory called `security` that contains a lot of source files associated with security. As well, many of the previously generated classes will have new code in them that support security functions.

#### Step 5

To make sure code generation was done correctly we should do a maven build:

```
./build.sh
```

You should see a line near the bottom of the output like this:

```
[INFO] BUILD SUCCESS
```
