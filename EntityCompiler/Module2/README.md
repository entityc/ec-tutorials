# Module 2: Templates Intermediate

In this module we will learn about new template statements.

## Session 1: Conditional

### Objective

This session will cover conditional statements in the template language.

### Discussion

We have already used the `$[if]` statement in previous modules but here we will cover all conditional statements.

#### Condition

All conditional statements have a condition. This is typically a boolean expression or boolean variable but can actually be any expression. If the expression evaluates to a numeric value it is converted to a boolean value automatically using `!= 0`. Likewise if the expression evaluates to an object, it will automatically be converted to a boolean value using `!= null`. For readability it's best to have the condition expression be a boolean expression.

#### If/ElseIf/Else

|Statement|Description|
| -------	| ---------	|
| `$[if `*condition*`] ... ` | If the *condition* is true, the code inside the if block (shown as `...`) will be executed.|
| `$[elseif `*condition*`] ...` | If all previous conditions in this if/elseif group are false and this *condition* is true the elseif block (shown as `...`) will be executed|
| `$[else] ...`| If all previous conditions in this if/elseif/else group are false the else block (shown as `...`) will be executed.|
| `$[/if]`| The if/elseif/else group needs to be terminated with this statement.

If statements can be nested as long as they are terminated properly with a `$[/if]`.

#### Switch/Case

The switch/case is similar in concept from other languages but differs in that there is **no fall-through**. The case can be declared with a list of values that the case corresponds to and its code block does **not** *require* a break statement - although it can.

| Statement | Description |
| -------	| ---------	|
| `$[switch `*expression*`]`| The *expression* can evaluate to a numeric value or there are special objects that are supported (see below)|
| `$[case `*constant* [`, `*constant* ]`]`|The *constant* is either a numeric constant or a special value of the supported switch objects (see below). If the switch expression evaluates to any of the constants then its block is executed. |
| `$[default]`|If none of the cases are met, this block is executed.|
| `$[/switch]`	| The switch/case structure must be terminated with this statement.	|

Currently the switch supports the following types of expressions:

| Switch Expression Type | Description |
| ----------------------	| -----------	|
| numeric	| If the expression evaluates to a number, the cases are simply number constants.|
| data type	| If the expression evaluates to a data type (such as the `type` member of an attribute), then the cases can be the type names (`uuid`, `int32`, `int64`, `boolean`, `float`, `double`, `string`, `date`, `enum`, `entity`, `typedef`) |

### Exercise

In this module we will just concentrate on template code so the entity model is already setup for you in `Space.edl`.

#### Step 1

Here we want to go through all entities and for each entity go through all its attributes. For each attribute we want to print out some long name for its type. To do this we will use the switch/case statements. Edit the `SimpleTemplate.eml`  file and just **under** the line `$[foreach attribute...]` add the following:

```
            $[switch attribute.type]
                $[case string]
                    $[let typeLongName = "String of characters"]
                $[case int32]
                    $[let typeLongName = "32-bit Integer"]
                $[case int64]
                    $[let typeLongName = "64-bit Integer"]
                $[case double]
                    $[let typeLongName = "64-bit Floating Point"]
                $[case float]
                    $[let typeLongName = "32-bit Floating Point"]
                $[case uuid]
                    $[let typeLongName = "Unique Identifier - 128 bits"]
                $[case typedef]
                    $[let typeLongName = "Typedef named " + attribute.type.name]
                $[case enum]
                    $[let typeLongName = "Enum named " + attribute.type.name]
                $[case entity]
                    $[let typeLongName = "Entity named " + attribute.type.name]
                $[default]
                    $[let typeLongName = "Unknown"]
            $[/switch]
```

For each unique attribute type it will set the variable `typeLongName` to some more descriptive name of the type.

#### Step 2

Now we simply run our template:

```
./run.sh
```

You should see the following output:

```
Typedef: HighColor16

Enum: Rank

Entity: Player
  Attribute
    Name: rank
    Type: Enum named Rank
  Attribute
    Name: name
    Type: String of characters
  Attribute
    Name: color
    Type: Typedef named HighColor16
  Attribute
    Name: experiencePoints
    Type: 64-bit Integer
  Attribute
    Name: level
    Type: 32-bit Integer
  Attribute
    Name: health
    Type: 32-bit Floating Point
  Attribute
    Name: strength
    Type: 64-bit Floating Point
  Attribute
    Name: social
    Type: Entity named Social

Secondary Entity: Social
  Attribute
    Name: email
    Type: String of characters
  Attribute
    Name: forumName
    Type: String of characters
  Attribute
    Name: forumId
    Type: Unique Identifier - 128 bits
```

## Session 2: Capture

### Objective

This session will cover a statement that allows you to capture text that would normally go to the log (or a file) and instead places it in a variable.

### Discussion

As you have used in previous sessions there is a `$[let var=expression]` statement that assigns a variable to some value using an expression. There are, however, times when you want to construct a string that is more complex than is practical with let statements. That's where the **capture** statement comes in handy. This statement allows you to basically redirect the template output into a string for some defined span of the template - thus allowing you to "capture" that into a string variable. Inside that capture block can be anything. For instance,

```
$[capture fullAttributeName]
${entity.name}.${attribute.name}
$[/capture]
The full attribute name is: ${fullAttributeName}
```

Here we capture the full attribute name (the entity name dot the attribute name) into the variable `fullAttributeName` then send that variable to the output. Ideally you would capture something that you would use multiple times.

The capture block can even contain template statements such as loops, conditional, etc.

### Exercise

Here we are going to do basically the same thing we did in the previous session but this time we will use the capture statement.

#### Step 1

Edit the `SimpleTemplate.eml`  file and just **under** the line `$[foreach attribute...]` add the following:

```
            $[capture typeLongName]
                $[switch attribute.type]
                    $[case string]
String of characters
                    $[case int32]
32-bit Integer
                    $[case int64]
64-bit Integer
                    $[case double]
64-bit Floating Point
                    $[case float]
32-bit Floating Point
                    $[case uuid]
Unique Identifier - 128 bits
                    $[case typedef]
Typedef named ${attribute.type.name}
                    $[case enum]
Enum named ${attribute.type.name}
                    $[case entity]
Entity named ${attribute.type.name}
                    $[default]
Unknown
                $[/switch]
            $[/capture]
```

Notice it has the same switch structure as the previous session but here it is now surrounded by a capture statement and the capture variable is the same as the variable used previously. The real different is that instead of using let statements, it simply includes the string values in the template itself. However, to insert the attribute type name, we have to use `${attribute.type.name}` to extract the attribute type name into the template stream.

#### Step 2

Now we run our template:

```
./run.sh
```

Since we are doing the same thing as the previous session, just a different way using the capture statement, the output is the same:

```
Typedef: HighColor16

Enum: Rank

Entity: Player
  Attribute
    Name: rank
    Type: Enum named Rank
  Attribute
    Name: name
    Type: String of characters
  Attribute
    Name: color
    Type: Typedef named HighColor16
  Attribute
    Name: experiencePoints
    Type: 64-bit Integer
  Attribute
    Name: level
    Type: 32-bit Integer
  Attribute
    Name: health
    Type: 32-bit Floating Point
  Attribute
    Name: strength
    Type: 64-bit Floating Point
  Attribute
    Name: social
    Type: Entity named Social

Secondary Entity: Social
  Attribute
    Name: email
    Type: String of characters
  Attribute
    Name: forumName
    Type: String of characters
  Attribute
    Name: forumId
    Type: Unique Identifier - 128 bits
```

## Session 3: Send/Receive

### Objective

In this session we will discuss a unique statement that allows you to inject text **upward** in the output.

### Discussion

If you look at how the template input text flows to the output it is pretty much from top to bottom just like the flow of code execution. But many times there are cases when you need something to be placed near the top of the output but don't realize it until you are already farther down the template input. A common case for this is if you are generating some source code file and as you are generating some piece of code in the middle of the file you realize that you need to define some kind of import statement at the top. But you have already missed the opportunity to include that import statement and in order to detect up above that we needed that import it would take a lot of (probably) redundant code to accomplish it. This is where the send/receive statements are very effective.

Here is a good example, let's say we want to generate a class in Java that uses a special class that we need to import if we use it. First our template needs to define a place where the imports should be inserted; this is done with the receive.

```
$[receive distinct imports]
```

You can have multiple receive points so each has to be given a name, in this case `imports`. The qualifier `distinct` means that when you send lines of text to this receiver you only want lines that are unique or distinct. This way you don't attempt to import something multiple times.

Now somewhere farther down in the template, you can send something to an above receive as follows:

```
$[send imports]
import com.example.datatypes.UUID;
$[/send]
```

Notice here how the send statement references the `imports` receiver. The text inside the send block (between `$[send imports]` and `$[/send]`) will be inserted where the `[receive distinct imports]` is located above. If it is executed multiple times, only one import statement will be included since the receiver is qualified with `distinct`.

### Exercise

In this exercise we will use the same example as in our previous session but this time we will add a send/receive pair to illustrate how they work.

#### Step 1

The `Space.edl` file is already setup with an entity model so lets start by editing the `SimpleTemplate.eml` file. It starts out just as it was when we finished the last session. 

At the top of the file, just **below** the `$[log]` statement insert:

```
$[receive distinct imports]
```

This is where we want to receive imports. Now lets go into our switch block and just inside/under the `$[case uuid]` add:

```
$[send imports]
Import: com.example.SpecialUUIDClass
$[/send]
```

Likewise under the `$[case string]` add:

```
$[send imports]
Import: com.example.SpecialStringClass
$[/send]
```

For these two attribute types, it will send those lines up to the receiver we added.

#### Step 2

Now we run our template:

```
./run.sh
```

Since we basically used the same template from the last session its output will look mostly the same with the exception that at the top it should have the "Import" lines that we sent up there. So the top two lines of our output should look like:

```
Import: com.example.SpecialStringClass
Import: com.example.SpecialUUIDClass
```

Note that even though we have more than one string attribute in our model, it only added that `com.example.SpecialStringClass` line once since the receiver was qualified with `distinct`.

## Session 4: Arrays

### Objective

In this session we will discuss how to create and use array variables in templates.

### Discussion

To create an empty array variable you use a let statement as follows:

```
$[let myList = @[]@]
```

Or you can initialize it with objects:

```
$[let myList = @["someString", 5, attribute]@]
```

The objects can of different types, but you just have to make sure you pull them out as those object types.

You can iterate through the array using a foreach loop:

```
$[foreach obj in myList.values]
...
$[/foreach]
```

> Note: You have to append `.values` to pull the array values out.

You can also get the number of items in the list by using `myList.count`:

```
My List has ${myList.count} items in it.
```

As well you can see if it contains an object with:

```
$[if myList.contains("someString")]
...
$[/if]
```

You can add to the list simply by:

```
$[do myList.add("anotherString")]
```

**Note:** Since we just want to call a method on the array object, we have to use the `$[do ...]` statement.

The following is a list of methods that are currently available on this array object:

| Return | Method | Description |
| ------	| ------	| -----------	|
| `void`	| `add(obj)`	| Adds an object to the array.	|
| `void`	| `addAll(array)`	| Adds all items from the specified array.|
| *object*	| `get(index)`	| Gets an object from the array at the specified index.	|
| `boolean`	| `contains(obj)`	| Returns true if the array contains the specified object.	|
| `boolean`	| `isEmpty()`	| Returns true if the array is empty.	|
| `void`	| `clear()`	| Removes all items in d the array.	|
| `void`	| `remove(obj)`	| Removes the specified object from the array.	|
| `int32`	| `indexOf(obj)`	| Returns the index into the array where the specified object can be found. If not found -1 is returned.|

### Exercise

In this exercise we will just declare an array, then add just attributes of string type to it, then print them out.

#### Step 1

Edit the `SimpleTemplate.eml`. Lets start by declaring our array variable just **under** the `$[log]` as follows:

```
$[let stringAttributeList = @[]@]
```

Now just under the `$[foreach attribute...` statement add:

```
            $[switch attribute.type]
                $[case string]
                    $[do stringAttributeList.add(attribute)]
                $[default]
            $[/switch]
```

This will add attributes of string type to that array we declared. Now after the second `$[/foreach]` statement (outside our entity loop) lets print out the attributes that we captured in our array:

```
String Attributes:
    $[foreach attribute in stringAttributeList.values]
  ${attribute.entity.name}.${attribute.name}
    $[/foreach]
```

This loops through our array and prints out the entity name "." attribute name.

#### Step 2

Now we run our template:

```
./run.sh
```

The output should look as follows:

```
String Attributes:
  Player.name
  Social.email
  Social.forumName
```

## Session 5: Functions

### Objective

In this session we will cover how to declare and use functions.

### Discussion

As with any language functions provide a great way to reuse code and provide structure to help simplify complexity.

#### Function Declaration

A function declaration is done as follows:

`$[function `*name* `(`*input arguments*`) ->(`*output arguments*`)]`

`...`

`$[/function]`

The *name* is whatever name you want to give the function, it will be used when calling it.

The *input arguments* can be empty or can be a comma delimited list of arguments (e.g., `entity, type, auxName`). The caller of the function must reference these arguments by name when assigning them to an expression (see below) As well, these names are used in the function's code. 

Functions can also *optionally* output data using the `->` operator followed by the *output arguments* as a comma delimited list inside parentheses. These output arguments can be assigned to actual variables when the function is called (see below).

> Functions must be declared **before** being called.

Inside a function you can return at any time using a `$[return]` statement but is not required.

Sending values to a function output is as simple as treating an output argument as a variable and assigning to it. The value will actually go to the variable that is mapped to this output argument in the function call.

Be aware that **output arguments are not initialized**, so don't assume they have any value assigned to them.

#### Function Call

Calling a function is accomplished first by mapping input and output arguments of the function to variables in the scope of the call, then the function is executed.

Calling the function is done as follows:

`$[call `*name*`(`*input mapping*`) -> (`*output mapping*`)]`

As you remember, the function declaration has named arguments for both input and output of the function. Mapping these is done with a comma delimited list of the following:

*argument*`:`*local variable*

> If the function has no output, then the `-> ()`  part is not required and can be omitted.

> Input and output arguments can be mapped *in any order*.

For instance, lets say we have a function declared as follows:

```
$[function FindAllStringAttributes(space) -> (attributes)]
```

We would call this function as follows:

```
$[call FindAllStringAttributes(space:space) -> (attributes:stringAttributeList)]
```

This will map the `space` variable at the scope of this call to the input argument `space` of the function. Likewise, it maps the `attributes` output argument to the local variable `stringAttributeList`.

In this case since the input argument is the same as the local variable (both `space`) that mapping does not need to be specified as it will automatically happen. Therefore the call could be given as:

```
$[call FindAllStringAttributes() -> (attributes:stringAttributeList)]
```

This goes for any input argument, you only need to map the arguments where the variable name is different, but of course you can still map ones that are the same.

If you do not want to map automatically, then you can add the `explicit` qualifier to the call as follows:

```
$[call explicit FindAllStringAttributes() -> (attributes:stringAttributeList)] // error
```

In this case, because `space` is not mapped, this **would cause an error** as follows:

```
The call with explicit arguments is missing some arguments.
```

### Exercise

Here is a simple exercise were we will declare a function, then call it. We will base it on our last session's exercise by expanding it to use a function.

#### Step 1

Edit the `SimpleTemplate.eml` file. We are going to transform the top part into a function. Rename the first two occurrences of `stringAttributeList` to just `attributes`.

They should now look like:

```
...
        $[let attributes = @[]@]
...
                        $[do attributes.add(attribute)]
...
```

**Above** the line `$[let attributes  = @[]@]` add a function declaration as follows:

```
    $[function FindAllStringAttributes(space) -> (attributes)]
```

Now just **under** the second `$[/foreach]` add a statement to end the function:

```
    $[/function]
```

Now, under this we can call our new function so add:

```
    $[call FindAllStringAttributes() -> (attributes:stringAttributeList)]
```

Notice here we did not map the `space` argument which means we want it to automatically map to the `space` local variable at the point of this function call. Then we map `attributes` to a new local variable called `stringAttributeList` that we will use in our subsequent loop.

The remainder of the template can stay as-is since it already uses the `stringAttributeList`.

#### Step 2

Now we run our template:

```
./run.sh
```

The output should look just like the last session's exercise since our function accomplishes the same thing:

```
String Attributes:
  Player.name
  Social.email
  Social.forumName
```

## Session 6: Input Configuration

### Objective

In this session we will learn how to configure a template by passing it configuration data.

### Discussion

The ability to pass configuration parameters to a template before it is executed allows you to write much more flexible templates. This is done with a `config` block as follows:

```
template ControllerTemplate {
    config {
        ... json style dictionary ...
    }
}
```

So for instance, lets say we want to pass in the security roles for read and write operations, we could do something like:

```
template ControllerTemplate {
    config {
        "readRole": "AUTHENTICATED_USER",
        "writeRole" "ADMINISTRATOR"
    }
}
```

Before the template is executed, the variables `readRole` and `writeRole` are set to the values shown. The template can then use those variables as it wishes.

> **Note:** So far only simple values like integers, floats and strings are supported.

### Exercise

In this exercise we will start with the solution of the previous session but make the type an input to the template (instead of it being hard-coded to a `string` type).

#### Step 1

Edit `SimpleTemplate.eml`. We will start by renaming the function on the top to `FindAllAttributesOfType` and then add an argument to the function called `type`. That line should look like this:

```
    $[function FindAllAttributesOfType(space, type) -> (attributes)]
```

Now inside this function, let's replace the whole `switch` structure to an `if` structure where we compare the `type` input to the attribute type. The `if` structure should look like this:

```
                $[if attribute.type.asString == type]
                    $[do attributes.add(attribute)]
                $[/if]
```

Notice how we use `asString` to get the string value of the attribute type, then see if it is equal to the `type` input of the function. The rest of the function is the same.

Now we should change the function call to match the new function name and pass in the `type` variable which is also the template input. This function call list should look like this:

```
    $[call FindAllAttributesOfType(type:type) -> (attributes:stringAttributeList)]
```

Actually we really didn't need to specify `type:type` in the argument list since they are the same (like we are doing for `space`).

The last change to the template is on the next line, to replace `String` to the type variable, and we'll go ahead and capitalize it as well. This should look like:

```
${type|capitalize} Attributes:
```

#### Step 2

Now we need to pass a value for `type` to the template. Since the template is defined in `Configuration.edl` let's edit that file.

Inside the `{}` of the template declaration, we should put a `config` block as follows:

```
    template SimpleTemplate {
        config {
            "type": "int32"
        }
    }
```

So here we are going to request that attributes of type `int32` are printed out.

#### Step 3

Now its time to run our modified template:

```
./run.sh
```

Our output should look like:

```
Int32 Attributes:
  Player.level
```

Feel free to change the `type` value in the `config` block to something else, even an enum or typedef (like `HighColor16`) and see what happens.
