# Module 3: Template Filters

In this module you will learn about one of the more powerful features in the template engine: **Filters**. Filters provide the ability to transform an object into another in an elegant and intuitive way.

## Session 1: Filter Basics

### Objective

Understand how to invoke filters.

### Discussion

A filter expression can be used anywhere an expression is used. The syntax for all filters is as follows:

*expression* `|` *filter*[`:`*parameter*] [*option*` `...]

| Where: |  |
| --------|----------|
| *expression*	| can be a simple template variable or any expression of template variables. It can even itself include a filter expression, thus allowing filter expressions to be chained together.|
|\|| is a pipe character, similar to a unix pipe used to send output of one command to the input of another.|
|*filter* | is the name of the filter. This module will cover most of the filters, then the remaining more complex ones will be covered in later modules.|
|*parameter*	| is either an identifier or an expression. If it is an expression it should be surrounded in parenthesis like `(`*expression*`)`. **This is optional.**|
|*option* | is a supported option for the filter. If the option has a value it is specified as: *option*`:`*value*. Multiple options are space delimited. **These are also optional.**|

For example:

```
entity.name|capitalize
```

This would invoke a filter called capitalize, sending the value of `entity.name` to its input. The output would be the capitalization of that string.

## Session 2: String Manipulation

### Objective

To cover filters that are used specifically for manipulating strings.

### Discussion

There are different types of string manipulation discussed.

#### Case

This is a group of filters that simply reformats a string in terms of case:

| Filter 	| Supported Inputs	|  Description 	| 
| ------	|:----------------:	| -----------	|
| `capitalize`	| string	| Forces the first character to be uppercase.	|
| `uncapitalize`	| string	| Forces the first character to be lowercase.	|
| `uppercase`	| string	| Forces all characters to be uppercase.	|
| `lowercase`	| string	| Forces all characters to be lowercase.	|

#### Words

There is another group of filters that first breaks up a string into words. This is done by the **camel case** naming convention used for naming all elements in the entity language.

For instance, say we have the name of an entity as: `PlayerAchievement`. This broken into words would simply be `Player Achievement`. Same for an attribute name such as: `primaryColor`. This would be `primary Color`. Essentially at the point when the case goes from lowercase to uppercase a space is added.

| Filter 	| Supported Inputs	|  Description 	| 
| ------	|:----------------:	| -----------	|
| `words`	| string	| Breaks the string into words as described above. **No change is case is made**.	|
|  `title`	| string	| After breaking into words it capitalizes each word. So `primaryColor` would become `Primary Color`	|
|  `dashes`	| string	| After breaking into words it forces all words to be lowercase then replaces the space to a dash `-` character. So `primaryColor` would become `primary-color`. |

#### Formatting


| Filter 	| Supported Inputs	|  Description 	| 
| ------	|:----------------:	| -----------	|
| `join`	| string	| Joins multiple lines of text into a single line. It basically replaces all `\n` characters with a space.
| `wrap`	| string	| Wraps text into multiple lines. This is used when a long description of an entity or attribute (for instance) is to be placed into a code comment block. Since this filter is more complex it is described below |

##### Wrap

The wrap filter has two options. You can control both the line prefix (default is "//") and the line width (default is 80):

`${str|wrap:("`*prefix*`") lineWidth:`*width*`}`

The *prefix* is any string of characters that should be used on new lines created as a result of wrapping to another line. The *width* is whatever line width you would like. This includes all characters from the start of the line *including* the prefix.

For example, lets say we want to insert the value of `str` right after the `Description: ` and we want it to wrap to a line width of 60:

	# Description: ${str|wrap:("#") lineWidth:60}

If the number of words in `str` cause us to wrap, the new line will start with the prefix specified `#` then spaces will be inserted to reach the same position on the line as the `$` in `$(str|wrap...)`, then more words of `str` will be output until it reaches the line width again. This will repeat in a word-wrap fashion until there are no more words.

### Exercise

In this exercise we will add the above filters to a template to see how they work.

#### Step 1

Edit `SimpleTemplate.eml`, notice that it is already largely populated. At the top we assign a variable `str` to what looks like could be an attribute name. Then we output it many times but without using any filter. We are going to add filters so it comes out correctly.

Under `Case)` we should add filters as follows:

```
Capitalize:   ${str|capitalize}
Uncapitalize: ${str|uncapitalize}
Uppercase:    ${str|uppercase}
Lowercase:    ${str|lowercase}
```

Those are pretty straight forward.

Next under `Words)` these involve filters that deal with breaking a string into words. They should look as follows:

```
Words:        ${str|words}
Title:        ${str|title}
Dashes:       ${str|dashes}
```

Next under `Model)` we have some foreach loops that print out the enum, enum items, entities and their attributes, but without filters so we want to add a filter where it outputs their names.

Change the line printing the enum to:

```
Enum: ${enum.name|words|uppercase}
```

and the enum item line to:

```
    ${item.value}) ${item.name|title}
```

Now the entity name line:

```
Entity: ${entity.name|words|uppercase}
```

and finally the attribute name:

```
    Attribute: ${attribute.name|title}
```

Next we will go onto the `Wrap)` section. Here we construct a paragraph using a `$[capture]` statement so that we can insert entity names in it. Notice how we don't care about how long each line is since we want to clean it up with filters after it is all captured.

The first time we output the `paragraph` is just raw so we don't need any changes there.

In each of the remaining places we output `paragraph` we want to first run a `join` filter. This will convert all those lines of varying length into a single line. Then we will follow with a `wrap` filter using different options.

The first use of `wrap` is just using defaults. That should look as follows:

```
Line Comments (default width of 80):
// Paragraph: ${paragraph|join|wrap}
```

Next we want to override the default line width and make it be 100 characters. This should look as follows:

```
Line Comments (width of 100):
// Paragraph: ${paragraph|join|wrap lineWidth:100}
```

Finally, instead of using the default `//` line comment we want to place this into a block style comment `/* ... */`. These lines should look like:

```
Block Comment:
/*
 * Paragraph: ${paragraph|join|wrap:(" *") lineWidth:60}
 */
```

Notice our prefix is a space followed by an asterisk - we need that initial space to line up with the asterisks above and below it. We also change the line width to just 60 to show more wrapping.

#### Step 2

Now its time to run our template:

```
./run.sh
```

The output should look like this:

```
Case)

Capitalize:   PlayerAchievement
Uncapitalize: playerAchievement
Uppercase:    PLAYERACHIEVEMENT
Lowercase:    playerachievement

Words)

Words:        player Achievement
Title:        Player Achievement
Dashes:       player-Achievement

Combination)

Words Uppercase:  PLAYER ACHIEVEMENT
Words Lowercase:  player achievement
Dashes Lowercase: player-achievement

Model)

Enum: PLAYER RANK
    1) Rookie
    2) Bronze
    3) Silver
    4) Gold
    5) Super Duper Rank

Entity: PLAYER
    Attribute: Rank
    Attribute: Name
    Attribute: Primary Color
    Attribute: Experience Points
    Attribute: Level
    Attribute: Health
    Attribute: Strength
    Attribute: Social

Entity: SOCIAL
    Attribute: Email
    Attribute: Forum Name
    Attribute: Forum Id

Wrap)

Raw Paragraph:
In this exercise we will work with entities
Player and Social so that we can see how wrapping works to create a comment
block
to be used in code generation.


Line Comments (default width of 80):
// Paragraph: In this exercise we will work with entities Player and Social so that we can
//            see how wrapping works to create a comment block to be used in code
//            generation.

Line Comments (width of 100):
// Paragraph: In this exercise we will work with entities Player and Social so that we can see how wrapping works to
//            create a comment block to be used in code generation.

Block Comment:
/*
 * Paragraph: In this exercise we will work with entities Player
 *            and Social so that we can see how wrapping works to
 *            create a comment block to be used in code generation.
 */
```

Feel free to experiment with different filters or chaining filters together.

## Session 3: List Sorting

### Objective

This session discusses the ability to sort lists using the sort filter.

### Discussion

There are many lists in the entity model: `space.entities`, `entity.attributes`, etc. This filter gives you the ability to sort them by their `name` field (with the except of enum items which are sorted by their numeric value).

Sorting is usually done when using a list in a `$[foreach]` statement. For example:

```
$[foreach attribute in entity.attributes|sort]
```

This will iterate through the attributes of `entity` sorted alphabetically. 

Other lists that can be sorted:

| List Type	|
| ---------	|
| modules\*	|
| entities	| 
| attributes	|
| relationships	|
| enums	| 
| enum items	|
| array\*\*	|

\* modules will be discussed in a future tutorial module

\*\* template arrays were discussed in Module 3

### Exercise

#### Step 1

Edit the template `SimpleTemplate.eml`. It starts out with most of the code already, you just need to add the `sort` filter in different places.

Basically just go to each `$[foreach ]` and add `|sort` to the end of each list variable. For example:

```
...
$[foreach enum in space.enums|sort]
...
$[foreach item in enum.items|sort]
...
$[foreach entity in space.entities|sort]
...
$[foreach attribute in entity.attributes|sort]
...
$[foreach attribute in stringAttributes.values|sort]
...
```

#### Step 2

Now its time to run our template:

```
./run.sh
```

The output should look like this:

```
Enum: PlayerRank
    1) Rookie
    2) Bronze
    3) Silver
    4) Gold
    5) SuperDuperRank

Enum: SpellType
    1) Transport
    2) Zap
    3) Shield

Entity: MagicSpell
    Attribute: castEnergy
    Attribute: name
    Attribute: price
    Attribute: type
    Attribute: weight

Entity: Player
    Attribute: experiencePoints
    Attribute: health
    Attribute: level
    Attribute: name
    Attribute: primaryColor
    Attribute: rank
    Attribute: social
    Attribute: strength

Entity: Social
    Attribute: email
    Attribute: forumId
    Attribute: forumName

String Attribute: email
String Attribute: forumName
String Attribute: name
String Attribute: name
```

## Session 4: Language

### Objective

Here we will go over the language filter which is a powerful way to abstract the template from the code language it generates.

### Discussion

The biggest use of templates are to generate code - code of a programming language. At the top of a template you can declare the language of the code that it will generate. But before you can declare that language, it must be defined - there are *no* built-in languages. The reason for declaring a language is so that some aspects of the code generation do not have to be coded in the template itself. That is, you can have a piece of template code that can work for multiple languages because that template code can be run in the context of a different languages.

The `language` filter allows us to have that language independence in our code in the following areas:

- Data types
- Expressions

For each use we will discuss not just where the language filter is used but also how languages are configured so that you can gain this language independence in the template.

#### Data Types

In our generated code, let's say we want to declare a variable associated with an entity attribute. We can get the attribute type from the `type` field of the attribute and could do something like this:

```
    ${attribute.type} ${attribute.name};
```

If the attribute was a `string` with name of `title`, this would generate code like:

```
    string title;
```

Notice how `${attribute.type}` outputted the type with its **entity** language type name. This would only work for languages where the type of a string is `string`, but what about Java which would need it to be `String`? If we instead did...

```
$[language java]
...
   ${attribute.type|language} ${attribute.name};
```

... this would allow us to look up the language specific type name defined outside the template and use it instead.

> Of course the rest of the template is specific to the language so it seems pointless, however we will show more powerful uses of the language filter later that cannot be done another way. As well, if we are using template functions we may not want the function to be language specific.

So where do we define how the types are mapped? It is written in the entity language in any `.edl` file you include when running the compiler.

For Java it would look like this:

```
language java {
    types {
        boolean boolean
        int32 int
        int64 long
        float float
        double double
        string String nullable
        date Date nullable
        uuid UUID nullable
        array List nullable
        map Map nullable
        enum enum nullable
        byte "byte[]" nullable
    }
```

We simply start with with `language` *language_name* `{}`.

There are many different configurations of a language but for now we will talk about data type mapping.

Inside the `language` block we would declare a `types {}` block. Inside that we define our type mappings. Each mapping is of the form:

*entity_type_name* *language_type_name* [`nullable`]

Where *entity_type_name* is the how the type is named in the entity language.
*language_type_name* is the native type or class name in the above language. If the *language_type_name* has special characters or a space, it should be placed in quotes. If the language type is not a native data type and is an object then it should be declared with the optional `nullable`.

Now you can see in our example:

```
$[language java]
...
   ${attribute.type|language} ${attribute.name};
```

would result in:

```
    String title;
``` 

#### Expressions

Expressions that represent attribute constraints are written in the entity language, not in a programming language so to render them to code, there needs to be some way to map them. The language filter can be used for such mapping, we just need to add additional configuration of a programming language defined in the entity language (in a `.edl` file).

Above we showed how to configure data type mappings, here we will show how to configure **operators**, **keywords** and **functions** of a language.

##### Operators

Most expressions have operators to perform various mathematical or logical operations. These need to be placed in a `operators {}` block where each is written as:

*operator_name* "*symbol*" ["*symbol*"]

The *operator_name* is a name for the operator (see below example for a complete list). The **symbol** (in quotes) is how that operator appears in the programming language code. For unary and binary operators there is only one symbol but for ternary you need to specify two.

Below is how operators would be defined for Java (and other languages as well):

```
language java {
...
    operators {
        plus "+"
        minus "-"
        times "*"
        divide "/"
        modulo "%"
        logical_not "!"
        logical_and "&&"
        logical_or "||"
        bitwise_not "~"
        bitwise_and "&"
        bitwise_or "|"
        bitwise_xor "^"
        is_equal "=="
        not_equal "!="
        greater_than ">"
        greater_than_or_equal ">="
        less_than "<"
        less_than_or_equal "<="
        select "?" ":"
        dot "."
    }
}
```

##### Keywords

Another possible issue when mapping to programming language is variable names in the expressions conflicting with reserved keywords of the language. For the compiler to warn you about these conflicts, it is useful to define the keywords. For Java this would be done as:

```
language java {
...
    keywords {
        abstract assert boolean break byte case catch char class continue const default do double else
        enum exports extends final finally float for goto if implements import instanceof int interface
        long module native new package private protected public requires return short static strictfp
        super switch synchronized this throw throws transient try var void volatile while
    }
}
```

##### Functions

Functions in expressions are a way to map object methods or otherwise code patterns that can vary a lot between languages. By standardizing on a set of these functions, you can achieve efficient mapping of expressions to a target language.

One good example is how to deal with string variables in expressions. Operators don't work on strings in all languages so its best to use functions in your expressions for string types. For instance if we have a string length attribute constraint, it can be written as:

```
        string name {
            D "Name of the player."
            constraint correctLength {
                length(name) >=3 && length(name) <= 20
            }
        }
```

Here in the constraint expression we have used a function called `length()` that accepts a string attribute. In order to map this to a target programming language we should use the `functions {}` block as follows:

```
language java {
...
    functions {
        length(string str) "${str}.length()"
    }
```

Functions are declared as:

*function_name*`(`*type* *name* [`,`*type* *name*]`)` `"`*language_code*`"`

So in the example above we define the function `length` with a single argument of type `string`. In order to insert our argument into the mapping code, we simply add `${`*arg_name*`}` or in the example's case `${str}`.

When mapping takes place, the `${str}` will be substituted for the name of the attribute being passed into that function in the attribute constraint expression, in this case `name`. So for Java, the attribute constraint expression above would be:

```
name.length() >= 3 && name.length() <= 20
```

### Exercise

We are going to start this exercise with the solution of a previous session relating to attribute constraints. In that session the constraints were outputted by the template with their expression in native format. Here we will instead map that expression to the Java language.

#### Step 1

Edit `Configuration.edl`, we will add a definition for the Java language that will support the `length` function mapping. The file starts out with most of the language defined but does not include any functions. Inside the `functions {}` block lets add:

```
        length(string str) "${str}.length()"
```

#### Step 2

The `Space.edl` does not need any changes, but you can view it and see two of the attributes have constraints that will be mapped to Java.

#### Step 3

Now it's time to edit the template `SimpleTemplate.eml` to map the expressions to Java.

First, at the very top of the file declare we want to output Java code by adding:

```
$[language java]
```

The name `java` matches the name of the language we defined in the `Configuration.edl` file.

Now there are two places where we want to take advantage of mapping to Java, the first is in the attribute type. Instead of outputting the entity language type name, lets change it to the Java type name by filtering the attribute type with the language as follows:

```
  Attribute: ${attribute.type|language} ${attribute.name}
```

So the `|language` does not specify the language because it uses the language we declared at the top.

Next lets map the constraint expression by adding `|language` to it as follows:

```
      Expression: ${constraint.expression|language}
```

This will do two things, for each native operator it will map to the language specific operator. Second it will convert the function `length()` to the language specific mapping of that function.

#### Step 4

Now its time to run our template:

```
./run.sh
```

The output should look like this:

```
Entity: Player
  Attribute: String name
    Constraint: correctLength
      Expression: name.length() >= 3 && name.length() <= 20
  Attribute: long experiencePoints
  Attribute: int level
    Constraint: levelValue
      Expression: level <= 12
  Attribute: int health
  Attribute: long coins
```

Notice first how the type names are that of Java now and the expression uses the `length()` method of the `String` class on the `name` variable that represents the attribute.

Feel free to experiment with making changes to see how the output changes.