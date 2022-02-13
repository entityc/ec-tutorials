# Module 5: Generating Source Code

Finally we are ready to generate real source code. We could have done it in previous modules but without learning some of the important features in the previous module (Enterprise Concepts) it would not have been practical.

The first thing we are going to learn in this module is how to create files from our templates. Up to now we have been simply sending all output to a log output (standard output). This worked great for tutorial purposes but now that we will be generating real working code, it needs to be written to source files.

The first type of source file we will generate in this module is known as a "model" class. A model class is one that is typically used internally within an application. Its main function is to represent objects from persistent storage or from some external source and it is in program variables (in RAM basically) for fast processing. The classes may have some logic as well but its limited to what pertains to that entity.

## Session 1: Creating Files

### Objective

The purpose of this section is to show how to create files and write to them from a template.

### Discussion

A single template can generate any number of files. The template language has a statement that allows you to open a file for writing and another statement for closing. To open a file, simply do:

`$[file `*directory*` `*filename*` `*extension*`]`

|Where:||
| ----	|---- |
| *directory*| A string containing a directory name or path relative to the assigned root directory of this template running instance (we'll cover that below). If there is no directory just specify `""`.|
| *filename* | A string representing the filename to be created.|
| *extension* | A string containing the file extension for the file being created.|

For example:

```
$[file "modelClasses" "Player" "java"]
```

The *directory* and *filename* often are not hardcoded strings as above but instead constructed from the model in some way.  For example, if we were creating a file for each entity we would instead do something like this:

```
$[foreach entity in space.entities]
    $[let className = entity|domain|name]
    $[file "" className "java"]
...
```

Here we are using the entity and domain to construct the class name then using that to name the file.

Often times the path of the file should represent the namespace (or package in the case of Java). This can be obtained from the domain assigned to the template. For instance, if we define a `Model` domain as follows:

```
domain Model {
    namespace com.example.model
    ...
}
```

We can use that namespace to construct the path. But we need to replace the `.`s with a path delimiter, that's where the `path` filter is helpful. If we use `domain.namespace|path` that will give us a path like `com/example/model`. So the above `$[file]` statement could be written as:

```
$[domain Model]
...
$[file domain.namespace|path className "java"]
...
```

Where `domain` in our *directory* expression is the default domain set at the top of the template.

The absolute file path will depend on how you configure the template to run. In your `configuration {}` block when you setup your template, you can specify an output directory.  For example:

```
configuration Tutorial {
    output Source {
        path "src"
    }
    template ModelTemplate {
        output primary Source
    }
}
```

So this will make our paths start with `src/`. But that is still not an absolute path! It's not a good practice to hardcode absolute paths in project files used by many team members so you have to just make sure you run the Entity Compiler from the "project" directory where `src` is located. This way each team member will run from their own project absolute path directory.

For the `path` you assign to a template, its a good practice to have it advance you to the directory where your namespace or package starts from. That will make the template independent of your particular project directory. We will learn in the next module how templates can be shared on a central repository for multiple projects to use so its important to allow different projects to control the path up to the namespace or package base directory.

All template output after the `$[file ...]` statement will be written to the file **except** when a `$[capture]`, `$[log]`, or any statement that consumes template output is executed.

The template stops outputting to the file when it encounters a `$[/file]` statement.

Let's use a simple text file to illustrate how template output ends up in the file (or doesn't):

```
$[file "doc" "Readme" "txt"]
This will go to our text file.
    $[capture compilerName]
Entity Compiler
    $[/capture]
The ${compilerName} is executing this template.
    $[log info]
This should NOT go to the file.
    $[/log]
That's it.
$[/file]
This is ignored since we closed the file.
```

The `doc/Readme.txt` file should contain:

```
This will go to our text file.
The Entity Compiler is executing this template.
That's it.
```

> You **cannot** nest `$[file]` statements.

### Exercise

The next session will have an exercise that demonstrates how to use the `$[file]` statement.

## Session 2: Generating Model Class

### Objective

Finally, generating code. We will learn what we've learned up to now to create Java source files for our entities.

### Discussion

Let's start by discussing how we want to create our Java class. Then we'll know how to setup our template. Often times you can start with a hand coded simple example of what you want to generate, then use that as your starting point for the template.

#### Basic Class Declaration

```
package com.example.model;

public class Player
{

}

```

At the top level we simply have a package and class declaration as above.

These are easily "templified" as:

```
package ${domain.namespace};

public class ${entity|domain|name}
{

}
```

But we need some more template code to make this work so it would look more like this:

```
$[language java]
$[domain Model]

$[foreach entity in space.entities]
    $[let className = entity|domain|name]
    $[file domain.namespace|path className "java"]
package ${domain.namespace};

import java.util.UUID;

        $[if entity.hasDescription]
// ${entity.description}
        $[/if]
public class ${className}
{

}
    $[/file]
$[/foreach]
```

At the top of our template we set the `language` and `domain` which is standard for most templates.

Next, since we want this template to generate a source file for each of our entities, we start a `$[foreach]` to iterate through the entities in our space. Since we are going to use the name of our class in a couple of places we just set it to a variable `className`. For this we pass the entity through the domain first before getting the name to give the domain the opportunity to change the name (`entity|domain|name`).

Next we open the file for our entity source file using the `className` variable. The first line is to declare our package - this is the namespace of our domain and since Java uses the same namespace format we simply need to use `domain.namespace`. Next we may need to include the import of other classes especially if we use them as types such as `UUID` that is used for our primary key.

Here we decided to include as a comment the description for the entity if it exists. We use an `$[if]` statement on the boolean `entity.hasDescription` to see if a description has been set on the entity before we commit to adding the comment. Then the description is accessed simply as `entity.description`.

For the class declaration we use the variable we made above `className`.

Finally we close the file with `$[/file]` and the entity loop with `$[/foreach]`.

#### Member Variables

Next we will add our member variables. These basically come from three sources: primary key, attributes and relationships.

##### Primary Key

This usually looks something like this:

```
    private UUID id;
```

Since our template should work for any data type for the primary key we need to use template code for both the type and variable name. It should look like this:

```
  $[if entity.hasPrimaryKey]
    private ${entity.primaryKeyAttribute.type|language} ${entity.primaryKeyAttribute|domain|name};
  $[/if]
```

Since not every entity may have a primary key, we use the `$[if entity.hasPrimaryKey]` statement around the variable declaration. The attribute for the primary key is `entity.primaryKeyAttribute` and from that we add `.type` to get its data type. Since we want the language specific name of the type, we use the `|language` filter. At the top of our template we specified the language as `java`  and in our `Configuration.edl` file the `java` language is defined such that the data type of `uuid` is the class `UUID`. Next the member variable name - we start with the same attribute of `entity.primaryKeyAttribute` but this time we pass it through our domain then get its name (`|domain|name`). This gives the domain an opportunity to rename it, which it does because it is configured to name all primary key attributes as `id`.

##### Attributes

Now for all the other attributes, they are similar to our primary key attribute except that we need to iterate through all of them so we first need to setup a `$[foreach]` then convert the attribute to a member variable declaration inside that:

```
  $[foreach attribute in entity.attributes]

      $[if attribute.hasDescription]
    // ${attribute.description}
      $[/if]
    private ${attribute.type|language} ${attribute|domain|name};
  $[/foreach]
```

Like we did for the entity name, we check to see if it has a description and if so we create a comment with the description in it. The rest is pretty much like our primary key attribute.

##### Relationships

Relationships can render very differently depending on how the class is going to be used. In some cases it may render into list variables or just a single reference to another class, or it could just contain variables that have the unique identifier of an external class object.

In the example we are using for this session, the purpose of the class is to be similar to how entities are stored in a database. This means that relationships to single other classes ("to-one" relationships) will be implemented by just storing the primary key of the other class object. Relationships to multiple other classes ("to-many" relationships) will not be implemented in our rendered class.

The first thing we need to do is setup a `$[foreach]` loop to iterate through all the relationships of an entity. Then look only for "to-one" relationships and create a member variable this matches its "to" entity's primary key.

```
  $[foreach relationship in entity.relationships]
      $[if relationship.to.isOne]

          $[if relationship.hasDescription]
      // ${relationship.description}
          $[/if]
    private ${relationship.to.entity.primaryKeyAttribute.type|language} ${relationship|domain|name};
      $[/if]
  $[/foreach]
```

First we use an `$[if]` statement to see if the "to" part of the relationship is to a single object with `relationship.to.isOne`.

Again, the same is done to create a comment from the relationship's description.

Then we access the primary key attribute of the "to" entity with `relationship.to.entity.primaryKeyAttribute`. Like above we use the `type` and filter through the language to get the type name in the target language. The variable name we will use is the relationship name (not the name of the primary key) since the relationship name is how it is represented by this entity.

#### Set and Get Methods

In keeping with a common access pattern, the member variables are declared as private and access is provided via set and get methods. This looks like this:

```
    public UUID getId() {
        return id;
    }

    public void setId(UUID value) {
        this.id = value;
    }
```

##### Primary Key

As before, we need to only add the set and get methods if the entity has a primary key so we will need a conditional block for that.

```
  $[if entity.hasPrimaryKey]

  $[/if]
```
Next we need to templify the type and name of the variable. For the get and set methods it would look like:

```
  $[if entity.hasPrimaryKey]
      $[let pkAttr = entity.primaryKeyAttribute]
    public ${pkAttr.type|language} get${pkAttr|domain|name|capitalize}() {
        return ${pkAttr|domain|name};
    }

    public void set${pkAttr|domain|name|capitalize}(${pkAttr.type|language} value) {
        this.${pkAttr|domain|name} = value;
    }
  $[/if]
```

Notice that since we are going to use the primary key attribute so much we just assign it to a variable called `pkAttr`.

For the get method we return a type that is just how we declared the member variable type ( `pkAttr.type|language`) by using the `language` filter. For the method name, since attributes are lowercase, we have to capitalize it since it comes after `get`, thus we use the `|capitalize` filter.

##### Attributes

Attributes of an entity are much the same, but we need a `$[foreach]` loop to iterate through the attributes:

```
  $[foreach attribute in entity.attributes]

    public ${attribute.type|language} get${attribute|domain|name|capitalize}() {
        return ${attribute|domain|name};
    }

    public void set${attribute|domain|name|capitalize}(${attribute.type|language} value) {
        this.${attribute|domain|name} = value;
    }
  $[/foreach]
```

##### Relationships

Just as when we declared the member variables for relationships, we also iterate through all relationships and only look at the "to-one" relationships:

```
  $[foreach relationship in entity.relationships]
      $[if relationship.to.isOne]
          $[let toEntityPk = relationship.to.entity.primaryKeyAttribute]

    public ${toEntityPk.type|language} get${relationship|domain|name|capitalize}() {
        return ${relationship|domain|name};
    }

    public void set${relationship|domain|name|capitalize}(${toEntityPk.type|language} value) {
        this.${relationship|domain|name} = value;
    }
      $[/if]
  $[/foreach]
```

That takes care of generating our model classes.

#### Exercise

In this exercise you will start out with all the entities defined in the `Space.edl` file but you will create the template file and define our `Model` domain. Then in order to test our newly generated classes, a simple Java program called Main is provided.

##### Step 1

The first step is to create the template starting with a partial actual Java class. Edit `ModelClassTemplate.eml`. Using the technique we learned in the above discussion, transform this Java class into a template that generates a Java class for all of our entities.

##### Step 2

Now we need to define the `Model` domain, the one we reference from our template. Edit `Domain.edl`.

First we should set our namespace:

```
    namespace com.example.model
```

Then for entities, we should prefix the class names with `ZM`:

```
    naming entity {
        prefix "ZM"
    }
```

Next we should make all our primary keys be just `id`:

```
    naming attribute {
        primarykey id
    }
```

Finally, since in this domain we want to reference "to-one" relationships by their primary key we will suffix the relationship names with `Id`:

```
    naming relationship {
        suffix "Id"
    }
```

##### Step 3

Now it's time to run the compiler followed by running the provided Main program that will test out our generated classes.

The run script now looks like this:

```
ec -c Tutorial ec/Space.edl ec/Configuration.edl ec/Units.edl ec/Domains.edl -tp ec/templates
rm -rf classes ; mkdir classes
javac  -d classes src/com/example/model/*.java src/com/example/Main.java
java -cp classes com.example.Main
```

The top line is our usual running of the Entity Compiler.

The next line simply removes any previously compiled classes and creates the directory where we will place our compiled java classes.

Then we run the Java compiler to compile the Java source files we generated.

Finally we run the Main class. The output should look like this:

```
Lance has 2 Transport magic spells
```

## Session 3: Preserving Custom Code

### Objective

In this session we will discuss a feature that allows you to preserve some custom code you want to add to a generated source file.

> Although this capability is offered, in most situations the Publishing/Authoring feature (that is described in an upcoming module) is better suited for injecting custom code.

### Discussion

There are times when you may want to add some methods to a class that has its source file generated by the compiler. If we just added that method, it would be gone after you run the compiler again. So here we will talk about a template statement that allows you to preserve code you add by hand.

First you need to decide where in the generated code you want to have this preserved area. Then add the following template statement:

`$[preserve ` *preserveBlockName* `]`

*// optional comment that helps the developer know what to put here*

`$[/preserve]`

For instance, inside the class definition **of your template**, in a place where you want custom methods, add something like this:

```
  $[preserve customClassCode]
// This is a good place to put custom methods for ${entity.name}
  $[/preserve]
```

> Since this is template code you can use `${}` to insert template variable values there.

Now when you run the compiler, the **generated source output** will look like this:

```
// =====preserve===== start-customClassCode =====
// This is a good place to put custom methods for Player
// =====preserve===== end-customClassCode =====
```

**It is very important never to touch the lines that contain `=====preserve=====` and only modify the lines between those.**

Now we are ready to add our custom code.

Let's say, for the `Player` entity, which has `firstName` and `lastName` attributes, you want to have a method to get a player's full name. This method may look like this:

```
    public String getFullName() {
        return firstName + " " + lastName;
    }
```

To add this method insert it between the `start` and `end` `=====preserve=====` lines that were generated in our source file.

That would look like:

```
// =====preserve===== start-customClassCode =====
    public String getFullName() {
        return firstName + " " + lastName;
    }
// =====preserve===== end-customClassCode =====
```

Now, no matter how many times we run the compiler, it will make sure to preserve this block of custom code.

If your custom code requires some imports, simply add another `preserve` block **in your template** for this where import statements go. For instance:

```
  $[preserve customImports]
// put custom imports here
  $[/preserve]
```

That will create a place in the generated source file where you can add your own custom imports.

You can add as many of these `preserve` blocks as you like.

> **WORD OF CAUTION**: If you rename the preserve block name in the template, the next time you generate the source file your custom code will be gone! To avoid this, you can either also rename every occurrence of this preserve block name in all generated output or you can use the `deprecates` option on the `preserve` statement: `$[preserve `*preserveBlockName*` deprecates `*oldPreserveBlockName*`]`. This will tell the compiler to convert any occurrence of the *oldPreserveBlockName* to the new *preserveBlockName*.

### Exercise

In this exercise we will start with our solution to the previous exercise, but instead of having the `Main` program print out just the player's first name, we want it to print out their full name.

#### Step 1

Since the preserve feature works by adding special markers in commented out code, we have to tell the compiler how to create comments in our target language.

Edit `Configuration.edl` and under `language java {` add:

```
    comments {
        line "//"
        blockStart "/*"
        blockEnd "*/"
    }
```

#### Step 2

Edit `ModelClassTemplate.eml`. You'll notice it starts out as the solution to our last exercise. We are simply going to add the following near the bottom of the template just **above** the line of the class' closing `}`:

```
  $[preserve customClassCode]
// Add custom methods here
  $[/preserve]
```

Now we should run our run script so that our generated source files include the preserve sections:

```
./run.sh
```

#### Step 3

The generated source files are located in the `src/com/example/model` directory. Edit `ZMPlayer.java`, look for the line:

```
// =====preserve===== start-customClassCode =====
```

Replace the line below it (which should be `// Add custom methods here`) with:

```
    public String getFullName() {
        return firstName + " " + lastName;
    }
```

Now let's run our run script again.

```
./run.sh
```

The method we added should stay there.

#### Step 4

Now we can update our `Main` application so it outputs the player's full name. Edit `src/com/example/Main.java` and look for the `printPlayerMagicSpells` near the bottom. In the `System.out.println()` function call, change `player.getFirstName()` to `player.getFullName()`. The full line should look like:

```
            System.out.println(player.getFullName() + " has " + playerMagicSpell.getRemainingCasts() + " " + magicSpell.getName() + " magic " + spellPlural);
```

#### Step 5

Now we are ready for our final run:

`./run.sh`

The output should look like:

```
Lance Skyrunner has 2 Transport magic spells
```

Notice the player's full name is now shown. Feel free to experiment by adding other custom methods and adjusting the Main app to use them.

## Session 4: Attribute Constraints

### Objective

Often times it is necessary to place limits or constraints on the values of attributes. These can be string length constraints or numeric value constraints. This session will show how to define constraints, then in the next session we will learn how to generate code from the constraints.

### Discussion

An attribute constraint is simply an expression that produces a boolean value. The expression should contain the name of the attribute in it. Constraints must be assigned a name themselves so it can be referenced if necessary. Constraints are specified inside an attribute's declaration block. For example:

```
        int32 level {
            constraint levelValue {
                level <= 12
            }
        }
```

Here we simply want to make sure the level never goes above 12. When rendering the constraint in the database code (such as SQL) we can use this to ensure that this attribute field in the entity table is never set above 12 using this expression. When rendering to client code (assuming this was something settable by someone in a UI) we may provide some immediate feedback to the user if they enter a value over 12.

So the above constraint is good for a numeric based attribute but another useful constraint would be for string types where you want to limit the length of a string. To accomplish this expressions are permitted to call functions, in this case a function called `length()` which, when rendered to a target language, will return the length of the attribute string value. More about the mapping to code later but for this session we will stick to just the declaration of the constraint which might look like this:

```
        string name {
            constraint correctLength {
                length(name) >= 3 && length(name) <= 20
            }
        }
```
In this case we want to ensure the name is at least 3 characters long but no more than 20.

### Exercise

#### Step 1

First we start with the `Player` entity from a previous session and add constraints to two of its attributes. Edit `Space.edl` and inside the attribute block for `name`, under its description, add:

```
            constraint correctLength {
                length(name) >=3 && length(name) <= 20
            }
```

Next inside the attribute block for `level` add the following constraint:

```
            constraint levelValue {
                level <= 12
            }
```

#### Step 2

Now it's time to update the template to print out the constraint information. Edit `SimpleTemplate.eml` and under the line that prints out the attribute add:

```
$[foreach constraint in attribute.constraints]
    Constraint: ${constraint.name}
      Expression: ${constraint.expression}
$[/foreach]
```

This will go though all constraints for the attribute (we only have one per attribute but you can have more if you want). It prints out the constraint name using its `name` field. Then it prints out the constraint expression by simply referencing its `expression` field. This will output the expression its its native entity language format. If we wanted to render it in another language we would use the language filter which will be discussed in a later module.

#### Step 3

Now its time to run our template:

```
./run.sh
```

The output should look like this:

```
Entity: Player
  Attribute: string name
    Constraint: correctLength
      Expression: length(name) >= 3 && length(name) <= 20
  Attribute: int64 experiencePoints
  Attribute: int32 level
    Constraint: levelValue
      Expression: level <= 12
  Attribute: int32 health
  Attribute: int64 coins
```

Feel free to try other expressions, just make sure you reference the attribute at least once and only reference attributes of the entity.

## Session 5: Attribute Constraint Code Generation

### Objective

This session will focus on how to generate code used to enforce constraints placed on attributes.

### Discussion

We learned in a previous session how to define constraints on attributes but now we are going to actually generate code that enforces them. We will expand on what was used in the previous sessions of this module.

If we remember back, to place a constraint on an attribute you would use code such as this when declaring an attribute:

```
        string name {
            D "Name of the player."
            constraint correctLength {
                length(name) >=3 && length(name) <= 20
            }
        }
```

Then we also learned how to convert the constraint expression to our target language using the `language` template filter along with configuring the `length` function for the Java language. This resulted in the expression rendering as:

```
name.length() >= 3 && name.length() <= 20
```

Now we just need to incorporate this into the generation of classes used in our microservice. There are two things we want to accomplish:

1. We don't want values that violate the constraints to make it into our database.
2. When there is a constraint violation we need to report back to the requestor which constraint(s) were violated.

The first can be accomplished with the model class since it is closest to the database. The second could be done in a data transfer (DTO) class and the endpoint code.

#### Model Class

For this class we simply need to ignore the setting of values that violate a constraint. For instance, for the `name` attribute, the set function should look like:

```
    public void setName(String name) {
        if(!(name.length() >= 3 && name.length() <= 20)) {
            return;
        }
        this.name = name;
    }
```

The template code to generate a `set` function would look like:

```
    $[let domainAttribute = attribute|domain]
    $[let variableName = domainAttribute|name]
    public void set${attribute|domain|name|capitalize}(${attribute.type|language} ${variableName}) {
    $[if attribute.hasConstraints]
        $[foreach constraint in domainAttribute.constraints]
            $[if constraint.hasDescription]
        // ${constraint.description}
            $[/if]
        if (!(${constraint.expression|language})) {
            return;
        }
        $[/foreach]
    $[/if]
        this.${variableName} = ${variableName};
    }
```

We start out setting a template variable for the attribute filtered through the domain. This gives the domain the chance to rename the attribute name both when accessed directly and also in the constraint expression. More specifically in the `$[foreach]`  statement, `domainAttribute.constraints` represents constraints that have been filtered through the domain, so therefore any attributes used it their expressions will have domain defined names. 

For each constraint we basically have an `if` statement such that if the constraint is not met it should simply return so that the member variable is not set. Of course we pass the constraint expression through the `language` filter so it will come out as executable code.

Finally we set the member variable if all constraints are met.

#### DTO Class

This class is used for transferring data in and out of the server. When data comes into the server (such as in a POST endpoint), it would be a good time to see if there is a constraint violation, and if so, which constraint was violated.

This can be accomplished by adding a method on the DTO class that returns the constraints that where violated, if any. This method might look like the following:

```
    public List<String> nameConstraintsViolated() {
        List<String> violations = new ArrayList<>();
        if (!(name.length() >= 3 && name.length() <= 20)) {
            violations.add("correctLength");
        }
        return violations;
    }
```

To accomplish this with a template we could do the following:

```
  $[foreach attribute in entity.attributes]
      $[let domainAttribute = attribute|domain]
      $[if attribute.hasConstraints]
$[send imports]
import java.util.List;
import java.util.ArrayList;
$[/send]
          $[let variableName = domainAttribute|name]
    public List<String> ${variableName}ConstraintsViolated() {
        List<String> violations = new ArrayList<>();
          $[foreach constraint in domainAttribute.constraints]
        if (!(${constraint.expression|language})) {
            violations.add("${constraint.name}");
        }
          $[/foreach]
        return violations;
    }
      $[/if]
  $[/foreach]
```

> The `$[send imports]` statement assumes you have a `$[receive distinct imports]` statement above where imports go. Alternately you can just always import those list classes if you don't mind that sometimes they won't be needed.

#### Endpoint Code

The endpoint code is where validation of inputs typically happens and any problems reported back to the requestor by way of a status code that reflects the nature of the problem. In this example environment, for a constraint violation we would simply throw a `ValidationException` along with a message and this will trigger an appropriate REST error response to be returned.

The code responsible for a POST endpoint for this entity might look like:

```
    List<String> nameViolations = dtoObject.nameConstraintsViolated();
    if (nameViolations.contains("correctLength")) {
        throw new ValidationException("The name field was not the correct length.");
    }
```

Here we are using the method on the DTO class that we created above to do the constraints check and just return the name of any constraint that was violated.

To accomplish this with a template we could do:

```
  $[foreach attribute in entity.attributes]
      $[if attribute.hasConstraints]
$[send imports]
import java.util.List;
$[/send]
          $[let violationsListName = attribute.name + "Violations"]
        List<String> ${violationsListName} = dtoObject.${attribute.name}ConstraintsViolated();
        if (${violationsListName}.size() > 0) {
          $[foreach constraint in attribute.constraints]
            if (${violationsListName}.contains("${constraint.name}")) {
                throw new ValidationException("The constraint was not met: ${constraint.expression}");
            }
          $[/foreach]
        }
      $[/if]
  $[/foreach]
```

Again we use `$[send]` to add the import of a class we are using. Since the receiver uses the `distinct` qualifier we can send multiple times without duplication.

The message we use in the exception is different than our above code but it might be more useful to include the language independent constraint expression so they can see the exact constraint condition.

### Exercise

In this exercise we will build the three class files described in the discussion above and then simulate a POST REST call by simply making method calls within a simple Java app. Most of the files are already setup with code, however the template files `ModelClassTemplate.eml`, `DTOClassTemplate.eml` and `ControllerClassTemplate.eml` are missing some code. You will use what you learned in this session to fill in the missing code.

#### Step 1

Edit `ModelClassTemplate.eml` and search for the comment:
```
$[* Replace this with code that returns right away if a constraint is violated. *]
```
As it says replace it with code as was mentioned in the discussion of this session.

#### Step 2

Edit `DTOClassTemplate.eml` and search for the comment:

```
$[* Replace this with code that checks for a constraint violation and adds the constraint name to the
    violations list for each violation. *]
```

Again, as it says replace it with code as was mentioned in the discussion of this session.

#### Step 3

Edit `ControllerClassTemplate.eml` and search for the comment:

```
$[* Replace this with code to see if the dtoObject has any constraint violations and throw
    an exception if so. *]
```

Again, as it says replace it with code as was mentioned in the discussion of this session.

#### Step 4

Now time to run. Like in the last session, the run script first runs the Entity Compiler followed by compiling the Java sources and then running the Main class.

```
./run.sh
```

The output should look as follows:

```
Expected validation error: The constraint was not met: length(firstName) >= 1 && length(firstName) <= 15
Expected validation error: The constraint was not met: length(lastName) >= 3 && length(lastName) <= 20
Expected validation error: The constraint was not met: level <= 12
Good to go, no more constraint issues!
```

Feel free to experiment with altering the constraints or adding new constraints, but just be sure to update the Main class to correspond to your constraint changes or additions.
