# Module 1: Language Basics

The purpose of the first module is to learn the basics of the entity language. As well, you will also learn some basics of the template language.

## Session 1: Entity Definition

### Objective

In this first session you will define an entity called `Player` and for now with just a primary key.

### Discussion

An entity has three parts: primary key, attributes and relationships.

```
entity Player {
    D "A player in the game."

    primarykey uuid playerId // or can be another data type

    attributes {
        // attributes are defined here
    }

    relationships {
        // relationships are defined here
    }
} 
```

An entity name must start with an **uppercase** character. A description for the entity can be provided using the `D` keyword followed by a string. We will save the attributes and relationships for a later session.

### Exercise

#### Step 1

Change your working directory to the `Module1/Session1` directory.

#### Step 2

Edit the `ec/Space.edl` file and add the following entity definition **under** the space declaration.

```
entity Player {
    D "A player in the game."

    primarykey uuid playerId // or can be another data type
} 
```

#### Step 3

On the command line, type:
```
./run.sh
```

If all went well you should see no errors, just simply returns.

## Session 2: Simple Template

### Objective

In this session you will create a simple template to print out information about the entity defined in the last session.

### Discussion

Templates are a powerful way to generate files based on your entity model. A single template can generate any number of files but in this case, for tutorial purposes, we will simply output to standard out so you can see what it will look like.

The file containing the template should be named with the `.eml` file extension. The name of the file can be anything since you will need to declare the template in your configuration. In this tutorial we simply name it `SimpleTemplate.eml` and place it in our `ec` directory.

### Exercise

Here we are going to create a template and run it.

#### Step 1

In the `ec` directory create a file called `SimpleTemplate.eml` and add the following to it:

```
$[log]
    $[foreach entity in space.entities]
Entity: ${entity.name}
    $[/foreach]
$[/log]

```

Notice the `$[log]` statement, this will direct all output to standard output (useful for a tutorial). Then we will loop through all the declared entities using a `$[foreach `*var*` in `*collection*`]` statement. In this case our *collection* is all the entities in our local space and provided to the template as the variable `space`. This space object has a member called `entities` that is a collection of the entitles we've defined. The variable of our for loop can be anything, in this case we use `entity`. The name of the entity is accessed using its `name` member. To output this name to the log we simply use `${entity.name}` inline with whatever other text we want to output.

#### Step 2

Add this template to the `ec/Configuration.edl` file, it should look like this:

```
configuration Tutorial
{
    templates {
        template SimpleTemplate {}
    }
}
```

This does two things. First it declares that you want to run a template called `SimpleTemplate` (that is in a file by the same name and ending with `.eml`). Secondly can declare a directory to send the output of the template. In this tutorial it is not necessary (thus omitted) since we will explicitly be sending it to the log output but in later modules we will add configuration to direct output to a file.

#### Step 3

Update the run script to tell the compiler where to find template files. Basically we just need to add `-tp ec` to the command line, this will tell the compiler to look in the `ec` directory for template files (where tp is template path). The run script should look like this:

```
ec build Tutorial ec/Space.edl ec/Configuration.edl -tp ec
```

#### Step 4

Now run the run.sh:

```
./run.sh
```

The output should look like:

```
Entity: Player
```

## Session 3: Entity Attributes

### Objective

In this session we will learn about entity attributes - the different attribute data types and qualifiers and how they are used.

### Discussion

An attribute is declared as: [*qualifiers*] *type* *name* [`in` *unit*]

#### Attribute Qualifiers

There are many different *qualifiers*, some of them will be discussed here and others will be discussed in later modules. For this session, the following qualifiers are used:

| Qualifier | Description |
| ---------	| -----------	|
| `unique` | When an attribute is declared as unique it means that each object of this entity should have a unique value for this attribute. |
| `creation` | This should only be used for a date field that is used to denote when an object of this entity is created.|
| `modification` | This should only be used for a date field that is used to denote when an object of this entity is modified.|
| `optional` | Indicates that a value for this attribute is not required to be set for an object of this entity. In other words, if this is used, the attribute is considered nullable.|
| `many` | Means that more than one value can be assigned to this attribute in the form of an array or list.|

From the compiler's perspective, these qualifiers are simply declarations about an attribute, it is up to a transform or template to interpret them and synthesize them into code as appropriate.

#### Attribute Data Types

The following data types are available:

| Type	| Description	| 
| --	| ---------	| 
| `uuid`	| Represents a 128-bit UUID.	| 
| `int64`	| Represents a 64-bit integer type.	| 
| `int32`	| Represents a 32-bit integer type.	| 
| `byte[]`	| Represents an array of 8-bit integers.	| 
| `float`	| Represents a floating point type.	| 
| `double`	| Represents a double precision floating point type.	| 
| `boolean`	| Represents a boolean type.	| 
| `date`	| Represents a date.	| 
| `string`	| Represents a string of characters.	| 

#### Attribute Name

Attribute names must start with a **lowercase** character. This convention helps not only with consistency but also helps in converting these names to other naming conventions (as you will learn in a later module).

#### Units

Attributes of a numeric type can also have units. These units must be defined in an `.edl` file. To assign a unit to an attribute simply follow the attribute name with `in` then the unit name.

Later, when learning how to synthesize code from attributes, you will learn how to incorporate an attribute's unit in its name when creating member variables but for now you'll just learn how to simply access the attribute's unit in a template.

### Exercise

In this session we will add some attributes to our entity and update our template to print out the attributes.

#### Step 1

Edit the `ec/Space.edl` file and add the following under the `primarykey` declaration:

```
    attributes {
        unique string name { D "Name of the player." }
        creation date createdOn { D "The date this player was created." }
        modification date modifiedOn { D "The date this player was last modified." }
        int64 experiencePoints { D "Experience Points" }
        int32 level { D "Level achieved." }
        int32 health { D "Amount of health remaining." }
        int64 coins { D "Number of coins the player has." }
        float height in millimeters
        optional string bio { D "Optional information about the player." }
        many string skills
    }
```

#### Step 2

Now we need to enhance our template to print out these new attributes. As well, we will also print out the primary key which is actually just a special type of attribute. Under the line that prints out the entity name, add the following:

```
  Primary Key: ${entity.primaryKeyAttribute.type} ${entity.primaryKeyAttribute.name}
        $[foreach attribute in entity.attributes]
  Attribute:
      name: ${attribute.name}
      type: ${attribute.type}
            $[if attribute.hasUnit]
      unit: ${attribute.unit.name} (${attribute.unit.abbreviation})
            $[/if]
$[* Qualifiers *]
            $[if attribute.isUnique]
      UNIQUE
            $[/if]
            $[if attribute.isOptional]
      OPTIONAL
            $[/if]
            $[if attribute.isCreation]
      CREATION
            $[/if]
            $[if attribute.isModification]
      MODIFICATION
            $[/if]
            $[if attribute.isArray]
      ARRAY
            $[/if]

        $[/foreach]
```

The primary key of an entity can be accessed using its `primaryKeyAttribute` member which is the same as any attribute object and thus has `type` and `name` members. The `entity` variable from the outer loop has an `attributes` member that is a collection of its attribute so we simply use a `$[foreach]` statement to loop through all attributes.

For the attribute unit there is a `hasUnit` boolean to see there is a unit, then you can access the unit with the `unit` member of the attribute object.

The qualifiers are just simple booleans as shown in the code.

#### Step 3

Since we are using units with one of our attributes,

```
        float height in millimeters
```

we will need to declare `millimeters` as a unit. Later in a more advanced module we will learn how to import units from a repository but for now we will define them in a `Units.edl` file and include that file when we run the compiler.

Create a file called `Units.edl` and add the following:

```
units {
    coins {
        D "Currency in the game."
        abbr "coins"
    }
    meters {
        abbr "m"
    }
    millimeters extends meters {
        abbr "mm"
        multiplier 0.001
    }
}
```

#### Step 4

Now we need to update our `run.sh` script to include the new `Units.edl` file. The `run.sh` script should look like this:

```
ec build Tutorial ec/Space.edl ec/Configuration.edl ec/Units.edl -tp ec
```

#### Step 5

Now we are finally ready to run the compiler:

```
./run.sh
```

The output should look like the following:

```
Entity: Player
  Primary Key: uuid playerId
  Attribute:
      name: name
      type: string
      UNIQUE

  Attribute:
      name: createdOn
      type: date
      CREATION

  Attribute:
      name: modifiedOn
      type: date
      MODIFICATION

  Attribute:
      name: experiencePoints
      type: int64

  Attribute:
      name: level
      type: int32

  Attribute:
      name: health
      type: int32

  Attribute:
      name: coins
      type: int64

  Attribute:
      name: height
      type: float
      unit: millimeters (mm)

  Attribute:
      name: bio
      type: string
      OPTIONAL

  Attribute:
      name: skills
      type: string
      ARRAY
```

Feel free to change the attributes or add new ones to see how the output changes.

## Session 4: Entity Relationships

### Objective

In this session another entity is created and the relationship between them is defined. The template is expanded to print out that information.

### Discussion

The relationships supported between entities can be the traditional (one-to-one, one-to-many or many-to-many) or more complex relationships that will be discussed in a later module.

Relationships are defined in a `relationships` block, similar to how attributes are defined, as follows:

```
relationships {
    // defined here
}
```

A relationship is defined in the form: [*qualifiers*] *plurality* *entityName* *relationshipName*

Where: *plurality* is: `one` or `many`, the *entityName* is the name of another entity and *relationshipName* is whatever you want to call this relationship.

An optional *qualifier* is `parent`. This can be included when the plurality is `one` and indicates that the referenced entity is considered a parent to this entity. For instance, if this entity is Planet, a parent entity might be SolarSystem. The compiler simply annotates this on the relationship and templates can take advantage of this information when synthesizing code.

### Exercise

#### Step 1

First lets edit our `Space.edl` file to include a new entity called `MagicSpell`:

```
entity MagicSpell {
    D "Gives you the ability to do magical things."
    primarykey uuid magicSpellId

    attributes {
        string name { D "Name of the magic spell." }
        int32 castEnergy { D "The amount of energy it requires to cast it." }
        int64 price { D "How much it costs to buy." }
        int32 weight { D "How much it weighs. This affects how many spells a player can carry." }
    }

    relationships {
    }
}
```

Players can have magic spells so we will need to model this with another entity we'll call `PlayerMagicSpell`:

```
entity PlayerMagicSpell {
    D "Represents a magic spell in a players inventory of spells."

    primarykey uuid playerMagicSpellId

    attributes {
        int32 remainingCasts { D "Number of casts a player has left for this magic spell." }
    }

    relationships {
        parent one Player player { D "A player that has one of these magic spells." }
        one MagicSpell magicSpell { D "The magic spell the player has." }
    }
}
```

Notice the relationships. The first one is to the `Player` and to just one. The second is to a `MagicSpell` and also just one. This entity also has an attribute who's value is specific to this player having this magic spell, the remaining number of casts.

We can also explicitly add the `many` relationship between the `Player` entity and the `PlayerMagicSpell` entity by adding this inside the `relationship { }` block inside the `Player` entity definition:

```
        many PlayerMagicSpell magicSpells
```

This makes it more clear that a player can have multiple magic spells.

#### Step 2

Now we need to update the template to print out our new relationships.

Edit the `SimpleTemplate.eml` file and add the following **under** the attribute foreach loop:

```
        $[foreach relationship in entity.relationships]
  Relationship: ${relationship.name} ${relationship.to.plurality} ${relationship.to.entity.name}$[if relationship.isParent] (parent)$[/if]
        $[/foreach]
```

This will loop through all the relationships and print them out. Relationships have a `from` and a `to` - most of the time the `to` part of the relationship is used since the relationship is pulled from the `from` entity.

#### Step 3

Now we can run the compiler:

```
./run.sh
```

The output will now include two more entities along with the relationships:

```
Entity: MagicSpell
  Primary Key: uuid magicSpellId
  Attribute:
      name: name
      type: string

  Attribute:
      name: castEnergy
      type: int32

  Attribute:
      name: price
      type: int64

  Attribute:
      name: weight
      type: int32
...
Entity: PlayerMagicSpell
  Primary Key: uuid playerMagicSpellId
  Attribute:
      name: remainingCasts
      type: int32

  Relationship: player ONE Player (parent)
  Relationship: magicSpell ONE MagicSpell
```

Notice the added relationship to the `Player` entity and the `PlayerMagicSpell` entity with its two relationships.

## Session 5: Enumerations

### Objective

This session will cover how to define and use enums with entities.

### Discussion

Enums are supported in many languages and are a useful way to group and name a set of constants. The entity language requires that you assign a numeric value for each enum item. The syntax for an enum declaration is the following:

```
enum AnEnum {
    D "An example enum"

    anItem = 1 {
        D "The first item"
    }
    anotherItem = 2 {
        D "The second item"
    }
}
```
The descriptions are not required but are annotated in that way. The numbering can be anything as long as each item has its own unique number.

An enum is used as the type of an entity attribute the same way as a native data type:

```
    AnEnum anEnumAttribute
```

### Exercise

Here we will simplify our entity model to just the `MagicSpell` entity then add an enum to it to illustrate how to use them.

#### Step 1

Edit the `Space.edl` file. Above the `MagicSpell` entity declaration add this `SpellType` entity declaration:

```
enum SpellType {
    D "Indicates the type of spell."
    Transport = 1 { D "Allows a player to move anywhere." }
    Zap = 2 { D "Zaps opponent, reducing health." }
    Shield = 3 { D "Places a shield around the player." }
}
```

Now inside the `MagicSpell` entity declaration add this attribute:

```
        SpellType type { D "The type of spell." }
```

#### Step 2

Since this is the first time to work with enums, we need to update the template code to include them.

Edit `SimpleTemplate.eml`. Above the entity foreach insert the following lines:

```
    $[foreach enum in space.enums]

Enum: ${enum.name}
        $[foreach item in enum.items]
  Item: ${item.name} = ${item.value}
        $[/foreach]
    $[/foreach]
```

This will print out all the enums and their items.

Next, inside the attribute foreach just **under** the line starting with `$[let attributeType = ...]` add these lines:

```
$[if attributeType.isEnumType]
  $[let attributeType = attributeType.name]
$[/if]
```

This will make sure we use the name of the enum as the attribute type to print out.

#### Step 3

Finally we run the compiler:

```
./run.sh
```

The output will show the new enum and its use as an attribute:

```
Enum: SpellType
  Item: Transport = 1
  Item: Zap = 2
  Item: Shield = 3

Entity: MagicSpell
  Attribute: SpellType type
  Attribute: string name
  Attribute: int32 castEnergy
  Attribute: int64 price
  Attribute: int32 weight
```
