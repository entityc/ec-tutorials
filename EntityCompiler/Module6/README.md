# Module 6: Importing From Repositories

In this module you will learn how to import model elements and templates into your local space from a git repository. This has two primary advantages:

- Allows these model elements and templates to be in a central place where they can be shared by other teams.
- The model elements and templates can be placed under revision control so your team can use a different version than another team.

The way in which model elements are imported is different than templates so we will have a separate section for each of these.

One thing they have in common however is how repositories are declared so we will start with a section on that.

## Session 1: Declaring a Repository

### Objective

Here we will discuss how to declare repositories that you will import from.

### Discussion

There are two types of repositories:

|Repository Type|Description|
| -------------	| ---------	|
| Local directory | This can be any directory on your local machine. |
| Github Repo	| This is a repository on Github.	| 

Declaring a repository should be done inside your `space {}` block.

For either type of repository we declare it as follows:

`repository` *name* `{`

`}`

Inside the `repository` declaration we can define the `type` of repository.

`type` *type*

Where *type* currently is either `local` for a local directory or `github` for a git repository on Github.

In the case of a **local directory**, this may look like:

```
repository LocalEntities {
    type local
    path "../library/entities"
}
```

The `path` is either an absolute path or a relative path. If you specify a relative path (such as above), it is relative to the directory where you invoke the compiler (essentially your "project" directory).

For a git repository on **Github**, there are more parameters you need to set:

|Parameter|Description|
| -------	| ---------	|
| `organization`|The name of the github organization owning the repository.|
| `name`| The name of the repository.	| 
| `path`| An optional path inside the git repository where you would like this repository declaration to be rooted.	| 
| `tag`|An optional tag from which to pull the files from the git repository. This is how you can implement revision control.|

For example:

```
repository EntitiesRUs {
    type github
    organization "entities-r-us"
    name "free-stuff"
    path "games/puzzle"
    tag "v2.3.4"
}
```

This defines a Github repository named `free-stuff` owned by a (fictitious) organization `entities-r-us` and for the purposes of our declared repository we only need to access in the `games/board` directory of the git repository. Also we want to only pull files tagged with `v2.3.4`.

### Exercise

We will skip having an exercise for this session but what you learned here will be applied for the other sessions of this module.

## Session 2: Importing Model Elements

### Objective

This session is about how to import model elements (such as entities, domains and units) from a git repository.

### Discussion

Now that we have declared a repository we can import from it.

When importing entities you need to add one or more import statements inside your `space {}` block. The import statement is as follows:

`import` *filename*[`,` *filename*] `from` *repositoryName*

Where *filename* is the name of a `.edl` file but **does not include the file extension**, and *repositoryName* is the name of a `repository` that we have declared.

For example:

```
import Chess from EntitiesRUs
```

Considering how `EntitiesRUs` is defined in the previous session, this will import a file called `Chess.edl` from the `entities-r-us` Github repository starting from its `games/board` directory and the version of the file that is tagged with `v2.3.4`.

> Most likely that file would have its entities defined inside a module but it's not required.

#### Domain Specialization

Domains can be imported like any of the other model elements, however, it is very likely that you will want to specialize one or more of them after import. One of the reasons for placing domain definitions in a central repository is so that you can standardize on its naming conventions, however, domains can also contain model specific elements in them. These model specific elements should **not** be included in the central repository and instead be included only in your local space and specialized on top of that standard definition. For instance, let's say that for a particular project you define a domain as:

```
domain Database {
    naming entity, attribute {
        method underscoreLowercase
        without units
    }
    entity HumanPlayer rename person {
        attributes {
            rename playerId to person_id
            rename experiencePoints to xp
        }
    }
    entity MagicSpell {
        attributes {
            int32 options replaces {
                (16) castEnergy
                (16) weight
            }
        }
    }
}
```

You can see it has three parts, one that defines naming and the other two that are specific to the entities in your local project. If we were to move this domain to a central repository we would only want to move the naming part:

```
domain Database {
    naming entity, attribute {
        method underscoreLowercase
        without units
    }
}
```

Then in our local project we would specialize this using the syntax:

`domain` *name* `(`*specialization*`) {}`

So for the above example we would define a specialization in our local project for the two entities we specialize for this domain:

```
domain Database (Tutorial) {

    entity HumanPlayer rename person {
        attributes {
            rename playerId to person_id
            rename experiencePoints to xp
        }
    }
    entity MagicSpell {
        attributes {
            int32 options replaces {
                (16) castEnergy
                (16) weight
            }
        }
    }
}
```

This specialized domain definition assumes you import the original domain definition, it then merges the two together for your project.

### Exercise

Here we will use the same github repository that manages this tutorial from which to import model elements.

We will start with the solution of Module 5 Session 3 and transform it to use repositories and imports. The `repo` folder is already populated with the files you will need to import.

#### Step 1

Edit `Spaces.edl`. Delete the three modules and their entities. We will import them from our `repo` folder instead. 

Inside the `space Space {}` block we'll first define a repository for importing the modules and entities. Add the following code:

```
    repository EntityRepository {
        type github
        organization "entityc"
        name "ec-tutorials"
        path "Module7/Session2/repo/entities"
        tag "e1.0.0"
    }
```

Notice the `path` takes us not just to the `repo` folder but also to the `entities` subfolder inside it.

> Instead of prefixing our tag with a `v` for version, we will use `e` for entity (version). This way we can have tags for entity versions independent of the other model elements.

Now we want to import the files. Each module was placed into its own `.edl` file so we will import three files as follows:

```
    import Graphics, Accessories, User from EntityRepository
```

#### Step 2

Edit `Domains.edl`. Delete **all but** the `Database` domain. For the `database` domain, just delete the top `naming` part of it so it looks like this:

```
domain Database (Tutorial) {

    entity HumanPlayer rename person {
        attributes {
            rename playerId to person_id
            rename experiencePoints to xp
        }
    }
    entity MagicSpell {
        attributes {
            int32 options replaces {
                (16) castEnergy
                (16) weight
            }
        }
    }
}
```

The parts that we deleted will instead be imported. Edit `Spaces.edl` again and define the following repository:

```
    repository DomainRepository {
        type github
        organization "entityc"
        name "ec-tutorials"
        path "Module7/Session2/repo/domains"
        tag "d1.0.0"
    }
```

This time we are pointing to the `domains` subfolder of the `repo` folder.

> Again, note that our `tag` starts with a `d` for domain (version).

Each of the domains have been placed into their own files so we will import each one as follows:

```
    import Database, Model, DTO, API from DomainRepository
```

#### Step 3

Since we will be importing all of what is contained in the `Units.edl` file you can delete it. Then edit `Spaces.edl` and we will define a repository for it as follows:

```
    repository StandardsRepository {
        type github
        organization "entityc"
        name "ec-tutorials"
        path "Module7/Session2/repo/standards"
        tag "s1.0.0"
    }
```

Again, it points to a `standards` subfolder of the `repo` folder. This repository can be used for more than units, it can also be used to store language definitions.

> Again, note that our `tag` starts with a `s` for standards (version).

Below this we can import the units as follows:

```
    import Units from StandardsRepository
```

Since we are no longer going to read in our local `Units.edl` file, we should remove it from the `run.sh` script, it should now look like this:

```
ec -c Tutorial ec/Space.edl ec/Configuration.edl ec/Domains.edl -tp ec/templates
```

#### Step 4

Now its time to run:

```
./run.sh
```

The output should be identical to that of Module 5 Session 3.

#### Step 5

Now let's import different versions of our model elements to see how the output changes.

Starting with the `EntityRepository` repository, change its `tag` to `e1.1.0`. 

Now re-run the compiler:

```
./run.sh
```

You should now see the addition of the `color` attribute in output related to the `MagicSpell` entity.

#### Step 6

Next in the `DomainRepository` definition change its `tag` to `d1.1.0` and run the compiler:

```
./run.sh
```

You should now notice that the naming of classes has changed.

#### Step 7

Here we will upgrade to a new version of our entities. In this new version we added a new attribute to the `HumanPlayer` entity declared as `height in centimeters`. Since we introduced the use of a new unit, the new `centimeters` unit was also added in our `standards` repository so we need to upgrade to the new version of that repository.

In the `EntityRepository` definition, change its `tag` to `e1.2.0` and in the `StandardsRepository` change its `tag` to `s1.1.0`. Now run the compiler:

```
./run.sh
```

You should now see the new `HumanPlayer` attribute of `height` in the output along with its unit for places that include the unit in the name.

## Session 3: Importing Templates

### Objective

In this session we will go over how to import templates from a repository.

### Discussion

As with model elements, being able to import templates from a central repository has great advantages, primarily being able to share with other teams in how to generate code for various parts of an enterprise application. This encourages consistancy and reuse.

As we have shown in a previous module, you define a template with a `template` statement inside a `templates {...}` block as follows:

`templates {
    template` *name* `{`...`}
 }`

In the past sessions we have not specified anything other than `template` statements inside the `templates` block. This is where we can specify a repository from which to import the templates. For each repository you want to import from, you should have a separate `templates` block. Just inside the `templates` block you should have an import statement:

`import from` *repositoryName*

Where *repositoryName* is one of our declared repositories.

The syntax for the `template` statement has been expanded to support the ability to change the path and filename of the template file from the repository. The full syntax is:

`template` *name*  [`in` `"`*filename*`"`]

Where *filename* is the name of the file (excluding the `.eml` file extension) that will be imported. If the *filename* is not specified, it will default to the template name. The *filename* can also be a relative path that is basically appended to the `path` property of the `repository` referenced by *repositoryName*. This allows your repository to point to a templates directory but then on an import be able to access subfolders.

An example import might look like the following:

```
templates {
    import from TemplatesRepository
    template DtoTemplate in "server/DtoTemplate" {
    }
}
```

This will import a file named `DtoTemplate.eml` from a subfolder called `server` relative to the `path` specified by the `TemplatesRepository`.

### Exercise

In the last session we moved all the model elements into the repository and imported them from there. Here we will continue on and do the same for the templates.

> We will still import our model elements from Session 2's `repo` directory and only create a new `repo` directory in Session 3 for the templates.

#### Step 1

The first step is to simply delete our `templates` directory since we will be importing all of them from our repo.

Now we need to declare a `repository` to point to the `repo` folder of this session. Edit `Space.edl` and below the other declarations add:

```
    repository TemplatesRepository {
        type github
        organization "entityc"
        name "ec-tutorials"
        path "Module7/Session3/repo/templates"
        tag "t1.0.0"
    }
```

#### Step 2

Now we need to alter our existing template declarations to import from this repository. Edit `Configuration.edl`. For each `template` declaration add an import, the file should look like this:

```
configuration Tutorial
{
    templates {
        import from TemplatesRepository
        template DatabaseTemplate {}
        template DTOTemplate in "data/DTOTemplate" {}
        template ModelTemplate in "data/ModelTemplate" {}
        template APITemplate {}
    }
}
```

Notice how for the `DTOTemplate` and `ModelTemplate` their files were stored in a subdirectory called `data` so we had to use the `in` keyword to specify the path and filename. The other two templates use the default behavior of just loading the template file based on their name.

#### Step 3

Now that we are going to import the templates we don't need to specify a template path on the command line. Edit `run.sh` and remove the `-tp` option. It should now look like:

```
ec -c Tutorial ec/Space.edl ec/Configuration.edl ec/Domains.edl
```

> If you plan to also have some local templates you can continue using this command line option.

#### Step 4

It's time to run:

```
./run.sh
```

The output should look the same as the previous session (with the repository tags being the same).

#### Step 5

As in the last session, lets change the tag in our repository definition to use upgraded templates. Edit `Space.edl` and for `TemplatesRepository` change its `tag` to `t1.1.0`.

Run the compiler again:

```
./run.sh
```

The difference in the template code is just minor, the output should basically have the same information but slight changes in the formatting.