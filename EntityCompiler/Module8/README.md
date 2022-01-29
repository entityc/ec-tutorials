# Module 8: Tags

One thing we haven't discussed yet in this tutorial is how to build templates to be flexible in how they see your entity model. Obviously we don't want to hard code entity or attribute names in our templates; instead, we want to offer a method by which someone can connect the elements of their entity model to a template. This is where tags come into the picture.

Tags are a very powerful way to annotate your model elements so that templates can find what they are looking for. Tags then become the primary mechanism by which templates adapt to your model when generating code. Let's take a simple example, say we have a template that wants to support generation of code related to a "user" of your system. Instead of requiring that you have an entity named **User**, the template would just require that you tag our "user" entity with the tag "user". So if your "user" entity is called **Person** then you just tag it with "user" and the template will know that entity is the "user" entity.

In this module we will start out with some basics of tagging, then move to domain based tagging that gets more sophisticated and finally show how you can document your templates so they fully explain the tags they support.

## Session 2: Tag Basics

### Objective

This session is about how to add tags to the elements in your model. This will give you a basic understanding of how tags work, then in the next session we will expand on this to create domain-based tags.

### Discussion

Without tags, the entity language does provide a limited method for annotating key characteristics about entities and attributes using qualifiers, such as `secondary` for entities or `creation` for date attributes, etc.; but there will be many times when you will want to annotate your own characteristics on one of these model elements. That is where tags come into play.

Here we will first talk about how to apply tags, followed by a discussion on how templates can process the tags.

#### Applying Tags

When you apply a tag to something, you will be able to see these tags from your templates and based on those tags alter the behavior of your template. In fact, when you develop templates you should document which tags they support and how their synthesis of code is affected by those tags.

One application of a tag is to allow a developer to tag a particular attribute as serving a specific function available in the tempate. This way the template does not have to look for an attribute by name, it allows the developer to name an attribute as they like but still tell the template to use that attribute in some specific way.

Another application of tags is to allow the developer to use a specific set of tags in conjunction with a specific relationship of entities to help guide the template in synthesizing the code in some way. You may even have many templates that simply look for some specific tag/entity relationship pattern and only generate code when it sees that pattern.

A tag is set simply using the `T` keyword:

`T "`*tag*`"`[`, "`*tag*`"`]

As shown multiple tags can be defined at once separated by a comma (`,`) or you can simply just have another `T` statement. For example:

```
    T "one", "two"
    T "three"
```

This will attach all three tags to the same model element.

The *tag* can be composed of uppercase or lowercase characters, numbers, a dash (`-`) or a colon (`:`).

The colon in the *tag* is intended as a separator to allow the creation of tag namespaces. This gives templates the ability to have distinct tags. For instance, a tag of `release:version` would relate to a release pattern supported by a template and specifically tagging the entity that represents the version of what is being released.

> As the community creates more and more templates, some standardization of tags may be necessary.

#### Processing Tags

Here we will discuss how templates can locate tags and adjust their output.

All model elements have a basic set of methods available for accessing tags:

| Method | Description |
| ------	| -----------	|
| `hasTags()`|Returns true if the model element has one or more tags.|
| `hasTag("`*tag*`")`|Returns true if the model element has the specified *tag*|
| `tagsSeparatedBy(`*delimiter*`)`|Returns a string where the tags are separated by the specified delimiter. This can be useful when generating documentation. | 

You can also iterate on the tags using the `tags` member variable of a model element. For example:

```
$[foreach tag in attribute.tags]
...
$[/foreach]
```

The `entity` model element has the following tag related methods:

| Method	| Description	| 
| ------	| -----------	| 
| `hasAttributeTagged(`*tag*`)`	| Returns true if one of its attributes is tagged with the specified *tag*.	| 
| `attributeTagged(`*tag*`)`	| Returns the attribute object that is tagged with *tag*.	| 
| `hasRelationshipTagged(`*tag*`)`	| Returns true if one of its relationships is tagged with the specified *tag*.	| 
| `relationshipTagged(`*tag*`)`	| Returns the relationship object that is tagged with *tag*.	| 
| `hasRelationshipToEntityTagged(`*tag*`)`	| Returns true if one of its relationships is to an entity that is tagged with the specified *tag*.	| 
| `relationshipToEntityTagged(`*tag*`)`	| Returns the relationship object that is to entity that is tagged with *tag*.	| 

The `attribute` model element has the following tag related methods:

| Method	| Description	| 
| ------	| -----------	| 
| `secondaryEntityIsTagged(`*tag*`)`	| Returns true this attribute is of a secondary entity type and that secondary entity is tagged with *tag*.	| 

These methods allow you to search for patterns faster and with less template code.

### Exercise

In this exercise we will add tags to model elements and use the some of the above methods to create our template code.

#### Step 1

Edit `Space.edl`. Start by adding two tags to the `HashedCloudAsset` secondary entity:

```
    T "s3:file", "asset"
```

Now to its `url` attribute add a tag of `"url"`, to the `bucketName` attribute add a tag of `"s3:bucket"`, to the `path` attribute add tag `"s3:path"`, to the `md5` attribute add tag `hash:md5`, and finally to the `size` attribute a tag of `"size"`.

To the `CloudAsset` entity add the tag `"asset"`, to its `url` attribute a tag of `"url"` and finally to the `size` attribute a tag of `"size"`.

For the release pattern, to the `Achievement` entity add a tag of `"release:object"`, to the `AchievementVersion` entity add a tag of `"release:version"` and finally to the `AchievementRelease` entity add a tag of `"release:top"`.

#### Step 2

Edit `SimpleTemplate.eml`. The first part of the template will try to locate the release pattern. Below the top `$[log]` statement add:

```
$[foreach entity in space.entities]
  $[if !entity.hasTag("release:top") || !entity.hasRelationshipToEntityTagged("release:version")]
    $[continue]
  $[/if]
  $[let versionEntity = entity.relationshipToEntityTagged("release:version").to.entity]
  $[if !versionEntity.hasRelationshipToEntityTagged("release:object")]
    $[continue]
  $[/if]
  $[let objectEntity = versionEntity.relationshipToEntityTagged("release:object").to.entity]
Release entity ${entity.name} releases versions ${versionEntity.name} of object ${objectEntity.name}.
$[/foreach]
```

Next we want to find attributes which represent file assets. Below the above inserted code add the following:

```
$[foreach entity in space.entities]
  $[foreach attribute in entity.attributes]
    $[if attribute.secondaryEntityIsTagged("s3:file") || attribute.secondaryEntityIsTagged("asset")]
      $[let assetEntity = attribute.typeEntity]
      $[if attribute.secondaryEntityIsTagged("s3:file") && !assetEntity.hasAttributeTagged("url")]
        $[log error]In entity ${entity.name}: The S3 file asset entity ${assetEntity.name} of attribute ${attribute.name} does not have an attribute tagged "url"$[/log]
      $[/if]
      $[let features = @[]@]
      $[if assetEntity.hasAttributeTagged("s3:bucket") && assetEntity.hasAttributeTagged("s3:path")]
        $[do features.add("S3 bucket and path")]
      $[/if]
      $[if assetEntity.hasAttributeTagged("hash:md5")]
        $[do features.add("MD5 Hash")]
      $[/if]
      $[if assetEntity.hasAttributeTagged("size")]
        $[do features.add("File size")]
      $[/if]
      $[if features.count > 0]
  The file asset associated with attribute ${attribute.name} of entity ${entity.name} will keep track of its:
        $[foreach feature in features.values]
    ${feature}
        $[/foreach]
      $[/if]
    $[/if]
  $[/foreach]
$[/foreach]
```

#### Step 3

Now its time to run our template:

```
./run.sh
```

The output should look as follows:

```
Release entity AchievementRelease releases versions AchievementVersion of object Achievement.
The file asset associated with attribute model of entity AchievementVersion will keep track of its:
    File size
The file asset associated with attribute texture of entity AchievementVersion will keep track of its:
    S3 bucket and path
    MD5 Hash
    File size

```

Feel free to experiment by changing the tagging and the template to see how the output changes.

## Session 2: Domain Based Tagging

### Objective

Here we will expand on the previous session and add tags on our model elements but with respect to domains.

### Discussion

Although you can add tags directly to entites, attributes and relationships, the problem is that you may be importing these from a source that doesn't allow you to add tags. Since an entity model is shared throughout your enterprise, it probably doesn't make sense to have everyone edit the entity model just to add tags they want.

A better solution is to specialize domains that you use and within a specialized domain tag the entities, attributes and relationships. Templates should then be written to look for tags with respect to their primary domain or other domains they use. This way there is no need to modify the entity model itself and it can stay purely as an entity model.

#### Applying Tags

As we discussed in a previous module, inside a domain declaration you can specify domain-specific characteristics to entity model elements. Inside a domain specific entity declaration we can use the same tag mechanism (`T "` *tag* `"`)  that we used in the previous session.

For instance, say we have a domain called `Security` that is used to build security related code and a template is written to use this `Security` domain that needs to look for an entity that is designated as the "user" entity (the entity that represents a user of the system for authentication and authorization purposes). The template requires us to tag the entity we want to represent the "user" with the tag `"user"` **with respect to** the `Security` domain. This is achieved as follows:

```
domain Security (myapp) {
    entity Person {
        T "user"
    }
}
```

This template would likely also need to find the attribute to be used as the username and another to store an encoded password, this can be achieved as follows:

```
domain Security (myapp) {
    entity Person {
        T "user"
        attributes {
            email           { T "username" }
            encodedPassword { T "password" }
        }
    }
}
```

Now the template has the information it needs to build code that supports say a login API or login web page.

#### Processing Tags

Looking for domain-based tags on your entity model is pretty simple, we simply send the entity element through a domain filter for the domain we are concerned about them apply one of the same methods we used in the non-domain-based case.

To use the above as an example, to check if an entity is tagged in the `Security` domain with `"user"` we would use the following template code:

```
$[if (entity|domain:Security).hasTag("user")] ... $[/if]
```

The same method would be used to look for the `Security` tags on the attributes.

If you want to easily find the entity that is tagged with something in a certain domain, we can use a method on the `space` object. For instance, finding the "user" entity for the above we could use the following template code:

```
$[let userEntity = (space|domain:Security).entityTagged("user")]
```

What is being returned is a domain entity, in our case the `Person` entity within the context of the `Security` domain.

### Exercise

In this exercise we will explore a potential feature of such a Security template that can generate code to automatically set a field that represents the last user that modified a particular object.  Let's first define some entity:

```
entity Product {
    primarykey uuid productId
    attributes {
        string name
        optional string description
        float price
    }
    relationships {
        one Person lastModifiedBy { D "The person who last modified this object." }
    }
}
```

Notice it has a relationship to the `Person` entity. In order to make this field record the user that last modified an object of this entity (such as changing the description or price), we would need to have code inside a method somewhere that processes object modification and sets this field to the ID of the user doing the modification.

In this exercise we will show how this could work with tagging.

#### Step 1

Let's assume that the template we are using to generate security based code supports the generation of this last-modified-by code by simply requiring that you tag the relationship associated with the modifying user - with respect to the `Security` domain. 

In the `ec/domains` folder create a file called `SecurityDomain.edl` and add the following:

```
domain Security (myapp) {
    entity Product {
        relationships {
            lastModifiedBy { T "user:modified" }
        }
    }
}
```

Notice we are using the tag `"user:modified"` on the relationship that represents the user that last modified the object of this entity.

#### Step 2

Now lets write a template that would process this tag.

> we need to get the "user" entity, write code to get the user entity object, then get its id and finally set this field on the Project entity object....

## Session 3: Documenting Tags


