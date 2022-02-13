# Module 7: Advanced Topics

This module will talk about some advanced topics such as:

- Template Publishing / Authoring Constructs
- Secondary Entities
- Attribute Bit Fields

## Session 1: Template Publishing and Authoring

### Objective

In this session we will learn how to write templates using special "publishing" language constructs that provide a powerful mechanism for extending templates from other templates.

### Discussion

When adding a feature to an application, often times it involves adding little bits of code throughout the entire application code base. This can make it difficult to understand how the feature works as a whole. The idea of template publishing is designed to address this.

Traditionally when you add code for a particular feature to an existing class or method, you simply add the code right there in the class or method. With the Template Publishing method, instead of adding the code there you instead make the template that generates that class or method a `publisher` then add an `outlet` precisely where you want to be able to insert code for your feature. Then in a template that represents your feature, you `author` to the `outlet` the code you want to insert there. This keeps all the feature code in one place separate from the other application code.

#### Publisher

When designing application templates, you may immedately recognize places where features may want to insert code and can simply add outlets for them right at the start. In other cases, you may not realize until a feature is being added that you need a new outlet somewhere. In either case it is pretty easy to add an outlet. Once an `outlet` is added, it can be used by any `author` (feature).

Before getting to our exercise, let's run through a couple examples.

Assume we have simple template that generates classes from our entities:

```
$[foreach entity in module.entities]
class ${entity.name} {
}
$[/foreach]
```

A typical class has both member variables and methods. If we turn this template into a publisher and provide outlets we can allow other templates to author code into it. For this the template would look like:

```
$[publisher com.example.tutorial.publishing]
    $[foreach entity in module.entities]
class ${entity.name} {

    // member variables
$[outlet members]
$[/outlet]

    // methods
$[outlet methods]
$[/outlet]
}
    $[/foreach]
$[/publisher]
```

Notice we have declared that we are a publisher and given it its name. The name should be something that is unique across all other possible publishers - thus the reverse domain name naming being used. Also notice the outlet for member variables and an outlet for methods. The names of the outlets only have to be unique within its publisher.

#### Authors

Now if a template wants to add members and methods, they would `publish` to the `outlet`s as follows:

```
$[author to com.example.tutorial.publishing outlet members]
    private int charisma;
$[/author]

$[author to com.example.tutorial.publishing outlet methods]
    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }
$[/author]

```

The above can also be structured as:

```
$[author to com.example.tutorial.publishing]

    $[author to outlet members]
    private int charisma;
    $[/author]

    $[author to outlet methods]
    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }
    $[/author]
$[/author]

```

Functionally it is the same in this instance, but this has the advantage that the publisher is only specified once for both outlets. Another advantage to this structure is having a shared execution context as will be explained next.

#### Execution Context

The contents of the `outlet` authoring block (that is between `$[author to outlet ...]` and `$[/author]`) doesn't have to be static text, it can be any template code mixed with static text.

> **One thing to keep in mind is that this block of template code will be executed inside the publisher's `outlet` as the publisher code executes.**

Let's say we want to conditionally add this code based on some tagging on the entity. We can do this using template code *inside* our authoring to an outlet as follows:

```
$[author to com.example.tutorial.publishing]

    $[author to outlet members]
        $[if entity.hasTag("character")]
    private int charisma;
        $[/if]
    $[/author]

    $[author to outlet methods]
        $[if entity.hasTag("character")]
    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }
        $[/if]
    $[/author]
$[/author]

```
This will ensure that the code will only be added for entities tagged with "character".

> It is tempting to have just a single `if` statement surrounding the two inner author statements or around the outer author statement, but you have to keep in mind that only the code *inside* the author blocks is executed (by the publisher template).

If you want to have some code executed in common with both authors, you can do this using another author that uses a special author option. If we know which outlet is executed first, we can author there, or sometimes a publisher template can provide a special outlet that is documented to execute at or near the top of the tempate (above the outlets we are authoring to). Let's assume the template supports a `top` outlet. We could author code such as this to this outlet that will be used by our other two authors:

```
    $[author to outlet top phase=initial]
        $[let addCharisma = entity.hasTag("character")]
    $[/author]
```

Notice the `phase=initial` that is specified. This will insure that the code being authored there will be inserted first before other authors. In our case we don't really need this since the `top` outlet runs before our other outlets and we are not authoring any other code to the `top` outlet but is probably good to do for this type of initializing code. If the publisher did not provide a `top` outlet, we could author to the `members` outlet, where using the `phase=initial` would be more important since we are authoring there multiple times.

Our other two author blocks could then be written as:

```
    $[author to outlet members]
        $[if addCharisma]
    private int charisma;
        $[/if]
    $[/author]

    $[author to outlet methods]
        $[if addCharisma]
    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }
        $[/if]
    $[/author]
```

Notice we just have to say `$[if addCharisma]` since we set the `addCharisma` variable in the `top` outlet. This has the benefit that if we wanted to change the condition for adding the authoring code we could do it in one place.

#### Execution Scope

Execution scope refers to a sort of layer in which variables that are set by template code hold their value. When executing the template code inside an author block, there are two possible ways this can happen: Author Scope or Publisher Scope.

##### Author Scope

One thing that wasn't mentioned in the above examples is that by default, when the code in our author blocks executes, it has access to any variable values that exist before its outlet executes (set by the publisher template); however, variable values set by the author block are *not* available to the publisher template - or any other authors. The variable values are only available to the other authors inside an outer `author` block. So in our above examples, the three `author` blocks inside the outside `$[author to com.example.tutorial.publishing]` block will all share the same execution scope, but only them. This allows multiple templates to author to the same outlets but not interfere with eachother in terms of variable names.

This scope is the default execution scope which is referred to as **author** execution scope. If desired, it can still be explicitly specified in the author statement as follows:

```
    $[author to outlet members scope=author]
```

##### Publisher Scope

Alternatively, if you don't want your author to have its own scope, you can choose to set its scope to **publisher**. In that case, any variables you set to a value in your template code will be passed on to the publisher execution context which affects not only publisher template code but also potentially other authors. This can be useful in some situations but takes more careful coordination between all involved templates. For this option, the author statement must explicitly specify the scope option as follows:

```
    $[author to outlet wildWest scope=publisher]
```

#### Documentation

Its a good idea for a publisher to document themselves and their outlets so that developers of authors that author code there will know how to use its outlets.

This is best done by annotating the documentation onto the `publisher` and `outlet` statements themselves using the `D` keyword as follows:

```
$[publisher com.example.tutorial.publishing
  D "This publisher allows authors to add member variables and methods to the entity"
  D "classes created by this template."
]
    $[foreach entity in module.entities]
class ${entity.name} {

    // member variables
$[outlet members
  D "This outlet is intended for member variables. The variable `entity` contains the"
  D "entity associated with the class created by the template."
]
$[/outlet]

    // methods
$[outlet methods
  D "This outlet is intended for methods. The variable `entity` contains the"
  D "entity associated with the class created by the template."
]
$[/outlet]
}
    $[/foreach]
$[/publisher]
```

The documentation text can be extracted by a template written specifically for documenting templates. This documentation is often times written in markdown so the documentation text here can contain markdown elements.

### Exercise

In this exercise we start with a basic template that generates an empty class definition for all the entities in the model, then we expand the template to include publishing and authoring.

Inside the `ec` directory of this session you will find the usual `Space.edl` and `Configuration.edl` files along with two template files inside a `templates` directory. The templates are as follows:

|Template|Desription|
| ------	| --------	|
| `PublisherTemplate.eml`|This template simply generates empty class definitions for our entities (defined in the `Spaces.edl`). In this exercise, we are going to make it a publisher.|
| `AuthorTemplate.eml`|This starts out as an empty file. We will make it author code to the publisher template.|

#### Step 1

Open `PublisherTemplate.eml` and in a line above the `$[foreach...]` add the following line:

```
$[publisher com.example.tutorial.publishing]
```

Then at the bottom of the file, close the publisher by adding the following line **under** the `$[/foreach]`.

```
$[/publisher]
```

Note the name of the publisher `com.example.tutorial.publishing`, this name must be used by any authors wishing to author code here.

#### Step 2

Keep the `PublisherTemplate.eml` file open, we will now add outlets where authors can add code.

Under the line starting with `package` and above the line starting with `public class` add the following outlet:

```
$[outlet top]
$[/outlet]
```

This will simply be used by the author as a place to execute template code before the other outlets execute.

Now, under the line with the comment `// member variables` add the following outlet:

```
$[outlet members]
$[/outlet]
```

This will be a place authors can put member variable declarations.

Finally under the line with the comment `// methods` add the following outlet:

```
$[outlet methods]
$[/outlet]
```

This will be used by authors to place methods.

That's it for publishing, now on to authoring.

#### Step 3

Open the file `AuthorTemplate.eml` and first add an author statement that doesn't specify outlets as follows:

```
$[author to com.example.tutorial.publishing]

$[/author]
```

This will set us up so we can author to multiple outlets without having to always specify the publisher, we can just specify it once here.

Now lets add to the first outlet `top`. Here we simply want to run some template code to set a variable that we will use in our template code for the remaining two outlets. Add the following inside our previous `author` block.

```
    $[author to outlet top phase=initial]
        $[let addCharisma = entity.hasTag("character")]
    $[/author]
```

Now we can add the other two authors. The next one authors a member variable. Add the following below the above author.

```
    $[author to outlet members]
        $[if addCharisma]
    private int charisma;
        $[/if]
    $[/author]
```

Notice it is using the `addCharisma` variable we set in the previous outlet authoring.

Next we will add a couple of methods in the `method` outlet. Add the following code below the above code:

```
    $[author to outlet methods]
        $[if addCharisma]
    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }
        $[/if]
    $[/author]
```

Again this uses the `addCharisma` variable to only add the code if the entity is tagged with `character`.

#### Step 4

Now lets open our `Space.edl` file and tag the `Player` entity with the tag `character`. In `Space.edl` locate the entity definition for `Player` and under its description statement add the following:

```
    T "character"
```

This will trigger the authors to insert the "charisma" code into its class definition.

#### Step 5

Now we are ready to run the compiler:

```
./run.sh
```

This will create a `src/com/example/tutorial` directory and inside will be three Java source files corresponding to the three entities in our model.

Open `Player.java` and notice that it is the only one that includes the "charisma" code. It should look as follows:

```
package com.example.tutorial;


public class Player
{
    // member variables
    private int charisma;

    // methods
    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }
}
```

Feel free to experiment by tagging another entity with `character` or even change the template code to insert different code.

## Session 2: Secondary Entities

### Objective

In this session we will learn about secondary entities and how to use them.

### Discussion

When defining attributes of an entity, sometimes you have a group of attributes that are related in some way but they don't warrant having their own entity since that data is not shared with other objects. That's where the secondary entity comes into play, its attributes are essentially instantiated into an instantiating entity. When rendering the entity to code, you can decide to either flatten the secondary entity attributes along with the entity's own attributes or you can choose to implement a separate class or data structure of the secondary entity and embed an instance of that into the primary entity.

And you can choose different renderings for different parts of your enterprise application. So the database may prefer it to be flat but the data transfer objects may prefer to have the hierarchy.

Another benefit to using secondary entities is that you can instantiate them in multiple entities or multiple times in a single entity and therefore improve code reuse.

Secondary entities are declared much like regular entities except they start with the keyword `secondary` and do not have a primary key or relationships.

### Exercise

In this session we will explore two uses of secondary entities: instantiating a secondary entity twice and instantiating an array of a secondary entity.

#### Step 1

Edit `Space.edl` and start by adding a secondary entity called `HashedCloudAsset`:

```
secondary entity HashedCloudAsset {
    D "Represents a file asset located in the cloud, such as in S3. This also keeps track of the file's MD5 hash"
    D "signature so clients can detect if it has changed without downloading it."
    attributes {
        string url {
            D "The URL to the asset."
        }
        string bucketName {
            D "The name of the S3 bucket holding the asset."
        }
        string path {
            D "The path inside the bucket to the asset file."
        }
        byte[16] md5 {
            D "The MD5 digest/hash of the asset file contents. This can be used by the client side to"
            D "detect if there was a change without having to download the asset file."
        }
        int64 size {
            D "The number of bytes contained in the file."
        }
    }
}
```

This secondary entity is very useful for keeping track of assets uploaded to the server (or Cloud), specifically in this case if using AWS S3 for storage.

Next we will define a new primary entity called `Achievement` and use the above secondary entity to keep track of a couple assets related to achievements. Now, to the same `Space.edl` file add the following code:

```
entity Achievement {
    D "Something you earn when you achieve something in the game."

    primarykey uuid achievementId

    attributes {
        string title
        string description
        int32 bonusPoints

        HashedCloudAsset model {
            D "The 3D model for this achievement."
            contentType "application/x-blender"
        }
        HashedCloudAsset texture {
            D "The texture to apply to the 3D model."
            contentType "image/tga"
        }
    }
}
```

Notice how the secondary entity `HashedCloudAsset` neatly organizes fields associated with a cloud asset. When rendering to code you can either choose to give it the same hierarchy or you can flatten the attributes into its instantiating entity. We will cover these different ways of rendering in a later module.

The next use of a secondary entity is simply to declare it as an attribute array. We will use a new secondary entity called `Upgrade` to be used to keep track of upgrades to magic spells tracked by our `MagicSpell` entity.  To our `Space.edl` file add:

```
secondary entity Upgrade
{
    D "A magic spell upgrade."

    attributes {
        string name
        string description
        int32 weightReduction
        int32 castEnergyReduction
    }
}
```

Now we need to simply instantiate this into our `MagicSpell` entity by adding the following to its `attributes {}` block:

```
        many Upgrade upgrades
```

Since the secondary entity is instantiated as an array rendering it flat is not really a practical option but since it has no primary key its identity will still be defined by its instantiating entity.

#### Step 2

Now that we have our entity model set we can work on a template to output information about the secondary entities. Instead of reusing the previous sessions template we will make a new template that just addresses our secondary entities. Edit the empty file `SimpleTemplate.eml` and add:

```
$[log]
    $[foreach entity in space.entities]

        $[let qualifier = entity.isSecondary ? "Secondary " : ""]
${qualifier}Entity: ${entity.name}
        $[foreach attribute in entity.attributes]
            $[let attributeType = attribute.type]
            $[if attribute.isSecondaryEntityType]
                $[let attributeType = attribute.typeEntity.name]
            $[/if]
            $[if attribute.isArray]$[let attributeType = attributeType + "[]"]$[/if]
  Attribute: ${attributeType} ${attribute.name}
        $[/foreach]
    $[/foreach]
$[/log]
```

We start with a foreach loop to loop through all our entities as we did before. This time we want to detect if an entity is a secondary entity by accessing the `entity.isSecondary` boolean. If the entity is a secondary entity it will print out "Secondary" before "Entity:".

Then we have a foreach for the attributes. This time we look to see if the attribute is of a secondary entity type by accessing the `attribute.isSecondaryEntityType` boolean. If so then we use the name of the secondary entity as the type name. The secondary entity is accessed using `attribute.typeEntity`, then we just follow with `.name` to get its name.

Finally to detect if it is an array we simply use the `attribute.isArray` boolean. If it is we append `[]` to the attribute type.

#### Step 3

No need to update any configuration, we can just run our template:

```
./run.sh
```

Our output should look like this:

```
Entity: Achievement
  Attribute: string title
  Attribute: string description
  Attribute: int32 bonusPoints
  Attribute: HashedCloudAsset model
  Attribute: HashedCloudAsset texture

Secondary Entity: HashedCloudAsset
  Attribute: string url
  Attribute: string bucketName
  Attribute: string path
  Attribute: byte md5
  Attribute: int64 size

Entity: MagicSpell
  Attribute: string name
  Attribute: int32 castEnergy
  Attribute: int64 price
  Attribute: int32 weight
  Attribute: Upgrade[] upgrades

Secondary Entity: Upgrade
  Attribute: string name
  Attribute: string description
  Attribute: int32 weightReduction
  Attribute: int32 castEnergyReduction
```

For the `Achievement` entity we see the attribute type for the `model` and `texture` attributes is `HashedCloudAsset` and in the `MagicSpell` entity the `upgrades` attribute is of type `Upgrade[]` meaning an array of `Upgrade` secondary entities.

## Session 3: Attribute Bit Fields

### Objective

There are occasions you have a special data type that contains individual bit fields that you would like to be able to individually identify and extract or merge. This session shows how to define an attribute to have named bit fields.

### Discussion

One common case might be a color type. Colors have components that are typically stored inside a single integer type like a `int32`. In our example we will use a 16-bit RGB color format that has 5 bits for red, 6 bits for green and 5 bits for blue. An attribute with these bit fields would look like this:

```
        int32 color {
            (5) red
            (6) green
            (5) blue
        }
```

> **Note:** Since `red` is declared first, it will start at bit 0, then `green` will start just after red so bit 5, then finally `blue` will start at bit 11.

If you needed a second attribute to also be the same color type you could just copy and paste and make it the same with a different name but if it was something you later wanted to change in all places it would not be an optimal way to go.

So another way to accomplish this is through a `typedef`. If you instead declared a `typedef` for these bit fields as follows:

```
typedef int32 HighColor16 {
    (5) red
    (6) green
    (5) blue
}
```

Then you could simply instantiate it as a type for multiple attributes:

```
        HighColor16 primaryColor
        HighColor16 secondaryColor
```

### Exercise

#### Step 1

First let's take our `Player` entity and add attributes with bit fields but we'll use out above `typedef` to do it.

Edit `Space.edl` and **above** the `Player` entity declaration add the above `typedef`.

Now inside the `Player` entity add the same two attributes as above.

#### Step 2

In this step we will update our template to extract this bit field and typedef information.

At the top we will print out the typedefs defined (just one in our case). Add the following lines just under the top `$[log]` statement:

```
$[foreach typedef in space.typedefs]

Typedef: ${typedef.name}
    $[foreach bitField in typedef.bitFields]
    BitField: ${bitField.name} [${bitField.width + bitField.low - 1}:${bitField.low}] (${bitField.width})
    $[/foreach]
$[/foreach]
```

This loops through all typedefs then for each typedef loops through and prints out each bit field in the typedef.

Next we will add code to detect which attributes have a typedef type and if so print out any bit fields they have. Inside the attribute `$[foreach ...]` and below the `$[let attributeType ...]` line add:

```
$[let bitFields = attribute.bitFields]
$[if attribute.type.isTypedef]
    $[let attributeType = attribute.type.name]
    $[let bitFields = attribute.type.bitFields]
$[/if]
```

This code will actually cover both cases: bit fields declared inside an attribute declaration and bit fields in a typedef used in the attribute declaration.

Now **below** the line with `  Attribute: ...` add:

```
$[foreach bitField in bitFields]
    BitField: ${bitField.name} [${bitField.width + bitField.low - 1}:${bitField.low}] (${bitField.width})
$[/foreach]
```

This will print out the bit fields.

#### Step 3

Now its time to run our template:

```
./run.sh
```

The output should look like this:

```
Typedef: HighColor16
    BitField: red [4:0] (5)
    BitField: green [10:5] (6)
    BitField: blue [15:11] (5)

Entity: Player
  Attribute: string name
  Attribute: HighColor16 primaryColor
    BitField: red [4:0] (5)
    BitField: green [10:5] (6)
    BitField: blue [15:11] (5)
  Attribute: HighColor16 secondaryColor
    BitField: red [4:0] (5)
    BitField: green [10:5] (6)
    BitField: blue [15:11] (5)
  Attribute: int64 experiencePoints
  Attribute: int32 level
  Attribute: int32 health
  Attribute: int64 coins
```

Feel free to experiment with changing the bit widths or even adding new typedefs.

