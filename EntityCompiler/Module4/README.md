# Module 4: Enterprise Concepts

At the center of any enterprise application is a model that embodies the features of that application. Usually this model is implemented independently in the various parts (spaces) of the enterprise such as the server, web apps, mobile apps, desktop apps, etc. This is largely because there are independent development teams responsible for each space of the enterprise application - there is a server team, a mobile team, web team, etc. These teams often operate in silos and communicate through email, documents, video calls, and other means of communication with each other which is not always efficient or effective and can lead to misunderstanding and mistakes.

This is at the heart of why the Entity Compiler was created. It was created to put the enterprise application model at the center and allow each development space to source directly from it to generate code or files that are dependent on the model. This means that changes in the model can be instantly reflected in the development spaces without the chance of error caused by humans mis-transcribing or mis-interpreting the model.

This module is broken down into three areas of the entity compiler that relate to its enterprise nature:

* Spaces
* Modules
* Domains

Each will be given their own session.

## Session 1: Spaces

### Objective

In this session we will learn about enterprise spaces.

### Discussion

An enterprise space is defined as a place where entities are rendered into an implementation of just some portion of the entire enterprise application.

Example spaces include:

* Microservice
* Mobile Application
* Web Application
* Desktop Application

A space can interface with other spaces by importing them (we will cover that in a future tutorial). Spaces often share entity definitions, but they can also define ones that are specific to themselves.

### Exercise

Each session in this tutorial involves at least one space, so we will not actually have an exercise for this session. A future tutorial will cover how to create a multi-space enterprise application.

## Session 2: Modules

### Objective

In this session we will learn about entity modules.

### Discussion

In a large enterprise application the number of entities can become rather large. To help organize entities the concept of modules was created. A module is simply a grouping of entities. An entity can only be in a single module.

A module is defined in the entity language as:

`module` *module_name* `{`...`}`

For example:

```
module FeatureX {
    entity EntityA {
        ...
    }
    entity EntityB {
        ...
    }
    entity EntityC {
        ...
    }
}
```

Modules typically represent some kind of functional grouping such as a feature or it can be entities that share a common use case or facility and are usually defined in their own `.edl` file.

Modules can be queried inside templates and are often used as a way to iterate through entities. Actually for entities that are defined within a module, the preferred method of iterating through them is through their module.

### Exercise

In this exercise we will define entities within modules and use that as a way to process our model.

#### Step 1

Edit `Space.eml`. Notice there are various model elements - the first being a `typedef`. Just above this insert:

	module graphics {

and below the `typedef` close the module by adding the line:

	}

Now do the same above `enum SpellType` adding:

	module accessories {

and below the `entity MagicSpell` block, add:

	}

Finally add one more module, above `enum PlayerRank`:

	module user {

and below the `entity Player`  block, add:

	}

This partitions the model elements into modules.

#### Step 2

Now we will upgrade the template to output model elements based on the modules they are in.

Edit `SimpleTemplate.eml`. We will need to add an outer loop that loops through the modules in the space.

Just below the `$[log]` line add:

```
    $[foreach module in space.modules]

MODULE: ${module.name|uppercase}
```

Then just above the `$[/log]` add:

```
    $[/foreach]
```

to close the module foreach.

Now we need to replace instances of `space` with `module` so that we iterate through model elements of a module instead of the entire space.

Update the template so the foreach statements look like this:

```
...
        $[foreach typedef in module.typedefs]
...
        $[foreach enum in module.enums]
...
        $[foreach entity in module.entities]
...
```

#### Step 3

Now we run our upgraded template:

```
./run.sh
```

The output should look as follows:

```
MODULE: ACCESSORIES

  Enum: SpellType
    1) Transport
    2) Zap
    3) Shield

  Entity: MagicSpell
      Attribute: type
      Attribute: name
      Attribute: castEnergy
      Attribute: price
      Attribute: weight

MODULE: GRAPHICS

  Typedef: HighColor16

MODULE: USER

  Enum: PlayerRank
    1) Rookie
    5) SuperDuperRank
    2) Bronze
    3) Silver
    4) Gold

  Entity: Player
      Attribute: rank
      Attribute: name
      Attribute: primaryColor
      Attribute: experiencePoints
      Attribute: level
      Attribute: health
      Attribute: strength
      Attribute: social

  Entity: Social
      Attribute: email
      Attribute: forumName
      Attribute: forumId
```

Notice how the model elements are now organized by module.

## Session 3: Domains

### Objective

In this session we will discuss a powerful feature known as domains.

### Discussion

A domain represents an environment in which elements of a model (entities, attributes, enums, etc.) can be represented in a unique way.

Example domains can include:

* Database
* Model Classes (Server and Client)
* Data Transfer Objects
* Protocols
* Various implementation layers of an application
* API

Domains provide the following facilities:

* Naming
* Namespace assignment
* Bit field mapping

Declaring a domain is simply done as:

`domain` *name* `{`...`}`

Where *name* is the name of the domain. Inside this block we can configure the various aspects of a domain.

#### Naming

Each domain can have its own naming convention. The naming facility of the domain is by far the most powerful facility as it allows you to transform the names of model elements differently for each domain yet they are all still connected to the central model. As we will show later, when writing templates you can take advantage of the `domain` **filter** which automatically performs naming based on the domain's configuration.

The naming facility has the following options for achieving the naming you desire in a domain:

* Naming Method
* Prefix and Suffix
* Unit in Name
* Primary Key Naming
* Explicit Naming

Inside the domain block you first setup a naming block for a particular model element as follows:

`naming` *element* `{`...`}`

The supported elements are: `space`, `module`, `entity`, `attribute`, `enum`, `enumItem`. In a template, passing an object of one of these elements through a domain (via the `domain` filter) will use the naming methods of its associated `naming` block.

##### Naming Method

A naming method is a way to transform a name using predefined methods. Specifying the method done as follows:

`method` *method_name*

where *method_name* is one of:

| Method Name | Description | Example |
|---|---|---|
| `standard` | A name is not converted and simply adopts the standard camel case naming. |`TrainingPlan` would stay as `TrainingPlan`|
| `lowercase` | A name is converted simply by naming it all lowercase. | `TrainingPlan` would become `trainingplan` |
| `uppercase` | A name is converted simply by naming it all uppercase. | `TrainingPlan` would become `TRAININGPLAN` |
| `underscoreLowercase` | A name is broken into words separated by underscores and made to be all lowercase. | `TrainingPlan` would become `training_plan` |
| `underscoreUppercase` | A name is broken into words separated by underscores and made to be all uppercase. | `TrainingPlan` would become `TRAINING_PLAN` |
| `dashesLowercase` | A name is broken into words separated by dashes and made to be all lowercase. | `TrainingPlan` would become `training-plan` |
| `dashesUppercase` | A name is broken into words separated by dashes and made to be all uppercase. | `TrainingPlan` would become `TRAINING-PLAN` |

##### Prefix and Suffix

Some domains may have a naming convention that is the entity name either prefixed by something or suffixed by something (or both). For prefix simply use:

`prefix` *prefix*

and for suffix:

`suffix` *suffix*

For example:

```
prefix Z
suffix Dto
```
In this case an entity named `Player` would become `ZPlayerDto`.

> This also may be desired in situations where classes would be named the same and would cause an inconvenience even if their namespaces were different.

##### Attribute Naming

The following options apply only to attribute naming.

###### Unit Usage in Name

By default, if an attribute is defined with a unit, that unit will be used in its name across all domains. However if you do not want a particular domain to use units you can do the following:

	without units

This will make sure units are not included in the naming of attributes in a particular domain. Likewise you could say:

	with units

to ensure units are used (even though that is the default).

###### Primary Key

To rename the primary key for all entities in the domain, we can simply add:

`primarykey` *name*

For instance:

	primarykey id

All primary keys in this domain will simply be named `id`.

##### Explicit Entity Naming

Choosing a very explicit name for an entity in just one domain might be done for legacy reasons (such as if you are interfacing to an existing code base that uses a different name).

To rename an entity within a domain you would add the following **inside a domain block**:

`entity` *entity_name* `rename` *new_entity_name* `{`..`}`

For instance:
```
domain Database {
    entity PlayerProfile rename user_info {}
}
```

When the `PlayerProfile` entity name is filtered through the `Database` domain its name will become `user_info`.

##### Explicit Attribute Naming

A domain can also explicitly rename any attribute for just that domain. This can be done either with respect to a single entity or for all entities.

###### Single Entity

This can be very useful in cases where a database already exists but the column name may not be a good or appropriate name anymore.

This type of renaming must be done in the context of a domain and further in the context of an entity. It would be specified as follows:

`domain` *domain_name* `{ entity `*entity_name*`{ attributes { rename `*attribute_name* `to` *new_attribute_name*`}}}`

Where: *domain_name* is the domain in which the rename applies and *entity_name* is the entity containing the renamed attribute. Finally *attribute_name* is the original name of the attribute and *new_attribute_name* is the name of the attribute in this domain-entity context. Note that what you specify for *new_attribute_name* is **exactly** how it will be named - that is, **no other** domain naming transformations will be applied.

For example, let's say we have a legacy database with a column name that we don't want to use in our entity model. We would use the following to ensure our new name for this attribute is mapped to the legacy column name:

	domain Database {
	    entity Player {
	        attributes {
	            rename experiencePoints to xp
	        }
	    }
	}

Here in our entity model for `Player` we have an attribute named `experiencePoints` but the database column name is already `xp` so this will make sure `xp` is used for the column whenever we generate SQL (for example).

###### All Entities

A more powerful rename method is to rename all attributes of a specific name across all entities. This can be useful in situations where a particular domain has keywords and would cause issues if an attribute was not renamed in that domain. For instance, in Objective-C the member variable named `description` is special (its like Java's `toString()` method) and should be avoided.

To rename all attributes of a particular name can be done in a couple of ways. One way is simply explicitly renaming it:

	domain ObjC {
	    attributes {
	        rename description to desc
	    }
	}

Another method would be using the append or prepend method with the entity or domain name:

	domain ObjC {
	    attributes {
	        rename description prepend entity
	    }
	}

So in the above example, the `description` attribute for an entity named `Activity` would be renamed to `activityDescription`.

> **NOTE:** Unlike the entity specific attribute renaming, this renaming happens **before** any domain naming method for the attribute. So if the domain naming method is `underscoreLowercase`, the above example would result in a name: `activity_description`. The reason for this difference is that this application of renaming is not typically used for legacy name matching.

#### Namespace

The ability to assign a namespace to a domain allows any code generated for a domain to use its namespace as appropriate for the code language. For instance, for Java the domain namespace would be used to declare the code's package. The transforms can decide to use this as a base namespace and add explicit sub-namespaces to this namespace.

For instance, say we define the following namespace:

	namespace com.entityc.app

This could be rendered into an import statement as `import com.entityc.app.BigClass;` in a Java program or a class reference as `com::entityc::app::BigClass` in a C++ application.

#### Bit Field Mapping

There are times when it is preferred to compress multiple attributes (especially booleans) into a single integer field. This done on a domain basis.

When this is done, essentially multiple attributes are **replaced** by another newly defined domain specific attribute. Templates that support this bit field mapping are written so as **not** to render replaced attributes to code. Templates also must be written to create code to handle the transformation between domains (using bit-wise operations for example).

For example, let's say we have three attributes that we want to compress into a single integer attribute. The entity attribute is defined as:

	entity SomeEntity {
	    attributes {
	        boolean enableFeatureA
	        boolean enableFeatureB
	        int32   mode // value is from 0 to 15
	    }
	}

To compress them into a single field for some domain, you would do the following:

	domain SomeDomain {
	    entity SomeEntity {
	        attributes {
	            int32 options replaces {
	                (1) enableFeatureA
	                (1) enableFeatureB
	                (4) mode
	            }
	        }
	    }
	}

The number in the parentheses is the number of bits for that field. The order in which the fields are defined is important. The bits of the `int32` are allocated starting with bit 0. So `enableFeatureA` is bit 0, `enableFeatureB` is bit 1 and `mode` is bits 2 through 5.

### Exercise

This is a big exercise. To explore all aspects of the domain feature we will be creating multiple domains and multiple templates.

> The purpose of this exercise is purely to teach features of the `domain`; it **is not** trying to convey any best practices or naming conventions. Use domains as a tool to implement your own standards.

#### Step 1

First we will begin by defining some domains:

| Domain | Description |
| ------	| -----------	|
| `Model` | Represents classes that implement an entity as simple objects used **inside** a server or client application.|
| `Database` | Represents how entities are rendered into a database domain.|
| `DTO` | This stands for Data Transfer Object and it defines classes used for transfer data between client and server.|
| `APICode` | Server implementations typically have classes specific for implementing an API. This domain represents the code that implements the API.|
| `APIPath` | When constructing the paths for API endpoints, this domain defines the naming convention used when incorporating entity or attribute names in the path.|

Edit the `Domains.edl` file, the following sub steps will guide you to adding the above domains.

##### Model

Insert the following code to simply define the domain:

```
domain Model {
}
```

Next let's define a namespace for this domain by adding the following inside this domain block:

```
    namespace com.example.model
```

This can be used for a package or namespace declaration of classes created in this domain.

Now lets define some naming by inserting the following under our namespace declaration:

```
    naming module {
        method capitalize
    }
    naming entity {
        prefix "ZM"
    }
    naming attribute {
        primarykey id
    }
    naming enum {
        prefix "ZE"
    }
    naming enumItem {
        method underscoreUppercase
    }
    naming typedef {
        method standard
        suffix "Type"
    }
```

Here we are saying that each time we pass a `module` object through the domain filter we will capitalize its name. Then for the `entity` we will prefix the name with `ZM`. For an entity attribute, specifically the primary key attribute, we will always name it `id`. For enums being filtered through this domain, they should be prefixed with `ZE`. `enumItem` (enum items) should be named where an underscore is used between words and all characters are made uppercase. Finally `typedef` objects will have a suffix of `Type`.

##### Database

First, insert the following code to define the `Database` domain:

```
domain Database {
}
```

We don't require a namespace for this domain so we won't specify one.

The naming in this domain is a bit more complicated. For illustration purposes we will assume that a database already exists and we need to conform to its table and column naming - but we don't want to use that naming in the rest of the enterprise.

We also need to honor its table and column naming conventions so we will add the following inside our `Database` domain block:

```
    naming entity, attribute {
        method underscoreLowercase
        without units
    }
```

Notice that if model elements have the same settings you can declare them at the same time by using a comma delimited list (e.g., `entity, attribute`). Here we are saying that table (entity) and column (attribute) should be named with an underscore between words and be all lowercase. Then also (for attributes) not to include units in the name.

Next we need to make some special cases with naming. The database names the entity `HumanPlayer` as just `person` (and thus the primary key needs to be renamed from `playerId` to `person_id`) and it names the attribute `experiencePoints` as just `xp`. Add the following inside the `Database` domain block to accomplish this:

```
    entity HumanPlayer rename person {
        attributes {
            rename playerId to person_id
            rename experiencePoints to xp
        }
    }
```

Another difference in our database is that it packs two attributes (`castEnergy` and `weight`) into a single column (`options`). For this we will need to use the bit field feature to perform this mapping. Now insert the following in the `Database` domain block.

```
    entity MagicSpell {
        attributes {
            int32 options replaces {
                (16) castEnergy
                (16) weight
            }
        }
    }
```

Since this alters the attributes of the entity `MagicSpell`, in our template code, we will need to pass the entire entity through the `domain` filter so that we see the correct set of attributes in our `Database` domain. You will see how this is done in the next step.

##### DTO

Again we start by defining the domain by adding:

```
domain DTO {
}
```

Here we will add a namespace declaration as:

```
    namespace com.example.dto
```

For the classes name that represent the entities in this domain we will use both a prefix and suffix and as well we will make all primary key attributes be just `id`:

```
    naming entity {
        prefix "ZD"
        suffix "Dto"
    }
    naming attribute {
        primarykey id
    }
```

##### APICode

For this domain we also define a namespace and a prefix and suffix for the classes generated in this domain:

```
domain APICode {
    namespace com.example.api
    naming entity {
        prefix "ZA"
        suffix "Controller"
    }
}
```

##### APIPath

Finally this domain simply defines a naming convention for entities and attributes used in the construction of an API path:

```
domain APIPath {
    naming entity, attribute {
        method dashesLowercase
    }
}
```

#### Step 2

In this step we will modify the provided templates so that they use the `domain` filter. 

> You will notice that inside the `ec` directory there is now a `templates` directory - this is not required - just doing it here to help organize the growing number of files.

Typically a single template will be aligned around a particular domain but any template can access any domain as needed. That is, a template usually declares a default domain so that when it uses the `domain` filter it does not need to specify that default domain. Only places where you want to use the `domain` filter with a different domain do you need to specify it with the filter (we will see that in the API template later).

##### Model Template

Let's start with the Model domain and its template, `ModelTemplate.eml`. To assign a default domain of `Model` to this template add the following to the very top of the file:

```
$[domain Model]
```

Now let's add the `domain` filter to some places that we output names, starting with the line that starts with "MODULE: ". Change it to look as follows:

```
MODULE: ${module|domain|name}
```

Notice we simply added a `|domain` after the module and it is still followed by the `|name` filter. This means are we passing the module through our default domain `Model` which is configured to `capitalize` module names.

In a similar manner we change other places to use the `domain` filter. Change the following lines as well:

```
...
  Typedef: ${typedef|domain|name}
...
  Enum: ${enum|domain|name}
...
    ${item.value}) ${item|domain|name}
...
  Class: ${entity|domain|fullname:("::")}
...
      PK:     ${entity.primaryKeyAttribute|domain|name}
...
      Member: ${attribute|domain|name}
...

```

Notice the line with `Class:` was changed to output the domain entity's full name with a C++ type namespace delimiter, as simply to illustrate how to use alternate delimiters.

##### Database Template

Edit `DatabaseTemplate.eml`, as before we start by adding the default domain, this time `Database`, to the top of file:

```
$[domain Database]
```

Now, where we transform the entity name to a table name we should make that line look like:

```
Table: ${entity|domain|name}
```

For the entity primary key which is a table column, edit this line to include the `domain` filter as follows:

```
    PK:     ${entity.primaryKeyAttribute|domain|name}
```

For the other columns (which come from attributes), we not only want to transform attribute names to be column names, since the list of attributes can actually be influenced by the domain we want to pass the entity through the domain **before** accessing the list of attributes. So, for the attribute `$[foreach ...]` that line should look like this:

```
        $[foreach attribute in (entity|domain).attributes]
```

So `(entity|domain)` transforms the entity into what is actually a domain entity object. A domain based object has many of the same methods as its non-domain counterpart but the output of these methods are different based on how the domain is configured. In this case since the `Database` domain is configured to pack two attributes (`castEnergy` and `weight`) into a new attribute (`options`) of the `MagicSpell` entity, then when we get the `attributes` from that `MagicSpell` entity, it will have the `options` attribute and not the `castEnergy` and `weight` attributes.  As well, the `attributes` will contain domain attributes - meaning the entity's attributes have already passed through the `Database` domain so we don't need to do it again for each individual attribute.  For this reason, the following line **does not need to change**:

```
    Column: ${attribute|name}
```

##### DTO Template

Edit `DTOTemplate.eml`, as before we start by adding its default domain, this time `DTO`, to the top of file:

```
$[domain DTO]
```

Now, this time for the class's full name we output its namespace with the native `.` delimiter. Edit this line to be:

```
Class: ${entity|domain|fullname}
```

For the entity primary key, edit this line to include the `domain` filter as follows:

```
    PK:     ${entity.primaryKeyAttribute|domain|name}
```

Like the previous template we will also pass the entity through the domain so that each occurrence of the attribute does not need to be passed through the domain. In this exercise, we only have a single use of the attribute variable but for more complex code it could be more beneficial.

> However, when you do this, be careful of the attributes name value, as it is now the name the domain chooses for the attribute, not the original attribute name. So depending on how you plan to use this variable, it can make a difference.

Change the `$[foreach ...]` line like in the previous template:

```
        $[foreach attribute in (entity|domain).attributes]
```

And now the line using the attribute to get its name doesn't need to change.

##### API Template

This template only iterates through the entities since it is simply trying to illustrate the idea that you can filter objects through domains other than the default domain.

Edit `APITemplate.eml`, as before we start by adding the default domain, this time `APICode`, to the top of file:

```
$[domain APICode]
```

So this domain is used for the code generated for the API. But we also need another domain to represent that naming conventions used in constructing the paths for the API endpoints, for this we will need to explicitly reference the `APIPath` domain when using the domain filter.

The first line we want to change is to output the name of a class (code) used to implement the API:

```
API Class: ${entity|domain|name}
```

Since this is the default domain, we just have to say `|domain`.

Now for outputting the path, change the next line to be as:

```
Get ${entity.name|title} List: GET /api/${entity|domain:APIPath|name}
```

Here we explicitly specify the `APIPath` domain in the filter by doing `|domain:APIPath` before we get its name.

Similarly if we need to use the DTO class when implementing this API code, we can use the same method of specifying the domain. Change the next line to be:

```
Returns objects of class ${entity|domain:DTO|name}
```

#### Step 3

In this exercise we have four templates so we need to make sure we configure them in our `configuration` block. Open the `Configuration.edl` file and add the following inside the `configuration` block:

```
    templates {
	    template DatabaseTemplate {}
	    template DTOTemplate {}
	    template ModelTemplate {}
	    template APITemplate {}
    }
```

Here we simply declare that we want to run each of those templates.

#### Step 4

Now we need to make two changes to our run script: first, add `ec/Domains.edl` to our list of files to read in and second, change the path of where we read template files to be `ec/templates` since we placed them in that directory for this exercise.

Edit `run.sh` and make it look like:

```
ec -c Tutorial ec/Space.edl ec/Configuration.edl ec/Units.edl ec/Domains.edl -tp ec/templates
```

#### Step 5

This was a long exercise but we are finally ready to run our templates.

```
./run.sh
```

Instead of showing all the output here, we will cover the parts that are of particular interest. The Database template runs first:

```
=========== DATABASE ===========

Table: person
    PK:     person_id
...
    Column: xp
...
Table: magic_spell
    PK:     magic_spell_id
    Column: price
    Column: options
...
```

Notice how the `HumanPlayer` entity was renamed to `person`, the primary key to `person_id` and `experiencePoints` to `xp` as specified in the domain:

```
    entity HumanPlayer rename person {
        attributes {
            rename playerId to person_id
            rename experiencePoints to xp
        }
    }
```

Next in the `magic_spell` table notice how there are no columns for `castEnergy` and `weight` and instead is a column called `options`, again that matches our domain configuration:

```
    entity MagicSpell {
        attributes {
            int32 options replaces {
                (16) castEnergy
                (16) weight
            }
        }
    }
```

Finally notice the column called `price` in the `magic_spell` table comes from the attribute declared as `price in coins` but since we defined this `Database` domain as follows it does not include the units:

```
    naming entity, attribute {
        method underscoreLowercase
        without units
    }
```

As well, notice how column names are named with the `underscoreLowercase` naming convention (e.g., `primary_color`).

Next in the output is the DTO template output:

```
=========== DTO ===========

Class: com.example.dto.ZDHumanPlayerDto
    PK:     id
...
Class: com.example.dto.ZDMagicSpellDto
    PK:     id
    Member: price
    Member: name
    Member: weight
    Member: type
    Member: castEnergy
...
```

You can see how the name of the entity was changed to create the class name by adding `ZD` as a prefix and `Dto` as a suffix as well as the template deciding to include the `fullname` which includes the namespace of the domain (`com.example.dto`). Here is how we configured the domain:

```
    namespace com.example.dto
    naming entity {
        prefix "ZD"
        suffix "Dto"
    }
    naming attribute {
        primarykey id
    }
```

Also notice how both primary keys are just `id` as is configured by `primarykey id`. Finally notice how both `castEnergy` and `weight` show up in this domain since we did not replace them as we did in the `Database` domain.

Next in the output is the Model template output:

```
=========== MODEL ===========

MODULE: Accessories

  Enum: ZESpellType
    1) TRANSPORT
    2) ZAP
    3) SHIELD

  Class: com::example::model::ZMMagicSpell
      PK:     id
      Member: type
      Member: name
      Member: castEnergy
      Member: price
      Member: weight

MODULE: Graphics

  Typedef: HighColor16Type
...
```

Here we chose to include the module names, notice how they are capitalized as we configured in the `Model` domain:

```
    naming module {
        method capitalize
    }
```

Next you can see the enum types prefixed with `ZE` and its items are all capitals with underscores between words, as configured:

```
    naming enum {
        prefix "ZE"
    }
    naming enumItem {
        method underscoreUppercase
    }
```

Next because of this domain configuration

```
    naming entity {
        prefix "ZM"
    }
```

the entity `MagicSpell` has been prefixed with `ZM`. Also it includes the namespace in a C++ looking way as done by template code `${entity|domain|fullname:("::")}`.

Finally for the `typedef`, because it is configured as

```
    naming typedef {
        method standard
        suffix "Type"
    }
```

the typedef name becomes `HighColor16Type`.

The last part of the output is from the API Template:

```
=========== API ===========

API Class: ZAHumanPlayerController
Get Human Player List: GET /api/human-player
Returns objects of class ZDHumanPlayerDto

API Class: ZAMagicSpellController
Get Magic Spell List: GET /api/magic-spell
Returns objects of class ZDMagicSpellDto
```

Notice the class names are prefixed with `ZA` and suffixed with `Controller` just as configured:

```
domain APICode {
    namespace com.example.api
    naming entity {
        prefix "ZA"
        suffix "Controller"
    }
}
```

Also  the endpoint path has dashes since it is configured as (with `dashesLowercase` as the naming method):

```
domain APIPath {
    naming entity, attribute {
        method dashesLowercase
    }
}
```

Finally notice how the DTO class is named just as is defined in the DTO domain:

```
domain DTO {
    namespace com.example.dto
    naming entity {
        prefix "ZD"
        suffix "Dto"
    }
    ...
```

Feel free to experiment by changing the input files of the exercise to see how the output changes.
