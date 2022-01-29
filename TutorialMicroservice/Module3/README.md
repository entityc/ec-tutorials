# Module 3: Web

Now that our microservice is complete we can build a web application on top of it. First, having an admin console will allow us to add and edit data. In Session 1, we will use the template library to synthesize this. Then in Session 2, we will cover how to use the Document Builder templates to help us build markdown documents from data in the database. Finally in Session 3, we will write a custom web application that will be the Student website to view the Tutorials entered using the admin console and formatted using the Document Builder.

## Session 1: Web Admin Console

### Objective

With the base microservice in place, we need a way to populate data. This session will cover how to synthesize a web-based admin console that will allow a user with admin or instructor role to create and populate a tutorial.

### Discussion

First, we will discuss what the Admin Console is from a visual web view perspective, then go into more detail including how to configure it.

#### Composition

The Admin Console has several elements:

| Element | Description |
| -------	| -----------	|
| Home Page	| The top page of the admin console starts out with a title and description of the web site, followed by summary tables of objects for some top level entities that you choose.|
| Details Page | The Detail Page allows a user to view and edit a single object (assuming they have permission). The top of the page shows fields of the object (of which you can configure) followed by summary table(s) of child objects. If a field of the page object is editable by the logged in user (based on roles and role assignments you made), it will have an Edit button that allows the user to edit that field. Each child object shown in the summary table(s) below has a View button so you can go to its Detail Page.|
| Breadcrumb bar | As you cascade down an object hierarchy, the breadcrumb bar shows you all the parents above your current detail page. You can click on a parent link to take you up the hierarchy. For instance, if you are viewing a Step object of the Tutorial, you can click on its Session object to take you back to the Session's detail page. You can also configure how objects look in this breadcrumb bar. |
| Headline | Like elements in the breadcrumb bar, the title of a detail page (headline) can be configured. For instance, if the detail page is for a Module, you can extract fields of the Module to construct the title. For example, you can make it look like this for modules: "Module 2: Language Intermediate" - which is a composite of three elements, not just a single attribute.|
| Markdown | You can tag specific attributes of your entities to indicate they contain (or can contain) markdown text. For these attributes, they will be rendered to HTML before being included in the web page. Also if an attribute tagged as markdown is editable, pressing the Edit button will bring up a web-based markdown editor to edit its value. |

#### Technology

The following libraries and technology are used in the admin console construction:

|Name|Description| Link |
| --	| ---------	| --- |
| Thymeleaf |The admin console uses Thymeleaf to dynamically create the final HTML for most of the web pages. Thymeleaf has basically two parts, a Java component that provides the data for the construction of the HTML and an HTML file that contains special constructs that help you build the final HTML from data provided by the Java component.|[http://thymeleaf.org](http://thymeleaf.org) |
| Bootstrap | This is a javascript based framework for building web sites. It supports many types of form elements, as well as elements for layout, content, etc.|[https://getbootstrap.com/docs/5.0](https://getbootstrap.com/docs/5.0)|
| InscrybMDE | Markdown editor - "A drop-in JavaScript textarea replacement for writing beautiful and understandable Markdown".|[https://github.com/Inscryb/inscryb-markdown-editor](https://github.com/Inscryb/inscryb-markdown-editor)|
|Marked| Renders markdown text into HTML. This is used to display markdown content on the web page. |[https://marked.js.org](https://marked.js.org)|

#### AdminUI Domain

This domain allows you to customize how an admin console is constructed based on your entity model. The customization is done by applying the tags described here on the entities, attributes and relationships in your model.

The admin console is comprised of basically three typs of web pages, each described below.

##### Admin Home Page

This is the top most page of the admin console. It can be configured to include summary views of the top level entities. From each object of an entity you can go to the detail page. To have an entity show up on this page, simply tag it with `home`. To also make it so the admin can create new objects of a home page entity, tag it with `edit`.

##### Admin Detail Page

Whenever you click on a link of an object it takes to this detail page. This will let an admin see and edit the object for the attributes that you choose, just tag them with `detail`.

Markdown for string attributes is also supported. Simply tag an attribute with `markdown` and when editing, the admin will be given a web-based markdown editor. As well, it will be rendered to HTML for viewing.

This page can also build a table for `many` relationships, just tag them with `detail`. This table is considered a "summary" view of the entity on the other side of this relationship. The columns of the table are attributes of the relationship entity and you can control which are visible in this summary table by tagging those attributes with the `summary` tag.

At the top of the admin detail page there are two configurable elements: a **breadcrumb bar** and a **headline**.

###### Breadcrumb bar

A breadcrumb bar generally tells the user where they are in some kind of hierarchy or view history. In our case it represents the hierarchy of your model, starting with the `home` entities on the admin home page then cascades as the admin clicks on the relationships shown in a detail page. Each item in the breadcrumb list is represented by an entity and you can customize how an entity's item will look using tags. A breadcrumb item can have three components:

*prefix* *number*: *title*

The *prefix* can be the entity's name, just tag the entity with `breadcrumb:prefix`. The *number* can be from an integer attribute of the entity, just tag it with `breadcrumb:number`. Finally the *title* can be from a string attribute of the entity, just tag it with `breadcrumb:title`. Examples are: "Module 1: Introduction" (has all three components), "Step 4" (only has the *prefix* and *number*).

###### Headline

The headline lets the admin know what they are looking at without having to look at the individual attributes. You construct the headline from attributes of the entity or the entity name itself. Like a breadcrumb item, a headline can have three components:

*prefix* *number*: *title*

The *prefix* can be the entity's name, just tag the entity with `headline:prefix`. The *number* can be from an integer attribute of the entity, just tag it with `headline:number`. Finally the *title* can be from a string attribute of the entity, just tag it with `headline:title`. Examples are: "Module 1: Introduction" (has all three components), "Step 4" (only has the *prefix* and *number*).

##### Admin New Object Page

When the admin clicks on a "New ..." button, they will be taken to this page. There are no available customization tags for this page.

##### Entity Tags

These tags can be placed on an entity itself.

| Tag | Description |
|:--|:--|
|`breadcrumb:prefix`|The breadcrumb bar is part of the detail page for an entity and it helps navigate the hierarchy of your admin console. Placing this tag on an entity means that the entity name will be used in the breadcrumb item for this entity.|
|`edit`|When an entity is tagged with this tag the admin console home page will provide a button to allow you create new objects of this entity.|
|`headline:prefix`|The headline is part of the detail page for an entity. Placing this tag on an entity means that the entity name will be part of the header part of this headline.|
|`home`|Placing this tag on an entity designates it to be included on the home page of the admin console.|

##### Attribute Tags

These tags can be placed on the attribute of an entity.

| Tag | Description |
|:--|:--|
|`breadcrumb:number`|Tagging an integer attribute with this tag will include it in the breadcrumb bar item as the number.|
|`breadcrumb:title`|Tagging a string attribute with this tag will include it in the breadcrumb bar item as the title.|
|`detail`|Tagging an attribute with this tag will ensure that it will be included on the admin console detail page for the corresponding entity.|
|`headline:number`|Tagging an integer attribute with this tag will include it in the headline as the number.|
|`headline:title`|Tagging a string attribute with this tag will include it in the headline as the title.|
|`markdown`|If the text of a string attribute is markdown text, then tagging that attribute with this tag will both allow it to be rendered to HTML for display and also when the admin edits the attribute, a web-based markdown editor will be used.|
|`summary`|Tagging an attribute with this tag will allow it to be included in a summary view of its entity.|

##### Relationship Tags

These tags can be placed on the relationships of an entity.

| Tag | Description |
|:--|:--|
|`detail`|Tagging a relationship with this tag will ensure that it will be included on the admin console detail page for the corresponding entity.|

### Exercise

As was done in the previous session's exercise, we will provide most of the solution leaving you with enough to understand the important concepts for the session.

#### Step 1

As we saw in the above discussion, the `AdminUI` domain is used to customize our admin console. We will want to include that as a domain when importing domains. The templates that use this domain are actually dependent on another domain called `WebUserUI` so we will need to import that as well.

Edit the `Space.edl` file and where it is importing from `DomainRepo` also import these two domains. There should be a line already importing domains from previous tutorial sessions so we can add them here:

```
    import Security, Localization, AdminUI, WebUserUI from DomainRepo
```

#### Step 2

Now that we are importing the `AdminUI` domain, we can specialize it and customize the admin console. Most of the customization has already been done, but we will customize it for the `Session` entity.

Edit the `ec/domains/AdminUIDomain.edl` file. You should notice that inside the specialization of the `AdminUI` domain there is a declaration of the `Session` entity with attributes and relationships but no tags. Lets add the tags.

In our tagging we have the following objectives:

- Define what a Session object looks like in the **breadcrumb bar**
- Define what the **headline** should look like on a Session detail page
- Define which **attributes and relationships** show up **on the detail page**
- Define the **attributes containing Markdown**

##### Breadcrumb Bar

We want our session to look like this when used in the breadcrumb bar: **Session 1**

The prefix of **Session** comes from the entity name, so we need to tag the `Session` entity like this:

```
    entity Session {
        T "breadcrumb:prefix"
        ...
    }
```

Now for the session number, this comes from its `number` attribute so we should tag it like this:

```
        attributes {
            number       { T "breadcrumb:number" }
            ...
        }
```

That should take care of a session object in the breadcrumb bar.

##### Headline

For the headline we want our session object to look like: **Session 1: Introduction**

This is similar to the breadcrumb bar but here we also want to include the title of the session. It would be tagged as followed:

```
    entity Session {
        T "headline:prefix"
        attributes {
            number       { T "headline:number" }
            title        { T "headline:title" }
            ...
        }
        ...
    }
```

> Of course you should be adding to the tags that were previously added for the breadcrumb bar.

This will take care of the headline for a Session detail page.

##### Attributes and Relationships on the Detail Page

Here we will configure which attributes and relationships should be present on a detail page for the `Session` entity. If we tag a `many` relationship to be on the detail page, child objects of the displayed object will be shown in a small "summary" table. You can also control which attributes of an entity show up in its summary table. In the case of the `Session` entity, we would be configuring how its attributes appear on the `Module` detail page, assuming it was configured to show a summary table of `Session` objects.

Lets start with attributes on the detail page and lets assume we want to show the `number`, `title`, `objective` and `discussion` attributes. You should add tags like this:

```
        attributes {
            number       { T "detail" }
            title        { T "detail" }
            objective    { T "detail" }
            discussion   { T "detail" }
        }
```

We also want to show the Exercises that are for this Session so we should tag that relationship as well:

```
        relationships {
            exercises         { T "detail" }
        }
```

Now lets take care of when a `Session` object is shown in a summary table. We want just the `number`, `title`, and `objective` attributes for this, so add this tag like this:

        attributes {
            number       { T "summary" }
            title        { T "summary" }
            objective    { T "summary" }
        }

That should do it for the detail and summary field.

##### Attributes Containing Markdown

This is important so we display and edit these attributes with special rendering and editing functions. For the `Session` entity, the attributes containing markdown are `objective` and `discussion`. Lets tag them as follows:

```
        attributes {
            objective    { T "markdown" }
            discussion   { T "markdown" }
        }
```

##### Putting It All Together

After you have added all the above tags, the merged result should look something like:

```
    entity Session {
        T "headline:prefix", "breadcrumb:prefix"
        attributes {
            number       { T "summary", "detail", "headline:number", "breadcrumb:number" }
            title        { T "summary", "detail", "headline:title" }
            objective    { T "summary", "detail", "markdown" }
            discussion   { T            "detail", "markdown" }
        }
        relationships {
            exercises         { T "detail" }
        }
    }
```

You can look at the other entities in this file to see how they have been configured.

#### Step 3

In this step we will cover how to add the template used for generating the Admin Console. The template supports three configuration parameters:

| Parameter | Description |
| ---------	| -----------	|
| `appTitle` | This is the name of the web site (application) as it should be displayed. |
| `appHomeMessage` | This is a message to place on the admin console home page. |
| `adminUrlPrefix` | The prefix to use in all admin console URL paths. |

For our website, add the following declaration for the `AdminConsole` template:

```
        template AdminConsole in "web/thymeleaf-bootstrap/admin" {
            output primary SourceMain
            config {
                "appTitle" : "Tutorial Central Admin",
                "appHomeMessage": "This website was built as part of an Entity Compiler tutorial to show the power of the Entitle Compiler and templates written for it. The templates used generate a website implemented with Springboot, Thymeleaf and Bootstrap. It includes user authentication and this full web admin console. ",
                "adminUrlPrefix" : "admin/"
            }
        }
```

Since the Admin Console is dependent on other parts of the website (that we haven't discussed yet), we need to add these templates as well:

```
        template AuthPageTemplate in "web/thymeleaf-bootstrap/auth" {
            output primary SourceMain
        }

        template LocalizedMessagesTemplate in "web/thymeleaf-bootstrap" {
            output primary SourceMain
        }
```

The `AuthPageTemplate` supports a login and signup web page and `LocalizedMessagesTemplate` supports generating a file that will contain localization for text entity and attribute names.

#### Step 4

Now we are ready to run the compiler:

```
./run.sh
```

If you follow the generated `src/main/java` directory down to the `web` directory, you'll notice it contains a directory called `admin` and another called `user`. The `admin` directory contains generated Controller class files for the admin console home page, detail page and "new" page (used when creating a new object). The `user` directory contains a Controller class file for the login and signup web pages.

Also, if you look in the `src/main/resources` directory you will see a directory named `templates`. These contain the Thymeleaf "template" HTML files containing its template constructs used for web page creation - these files start with `admin_`. As well, we have a few HTML files for authentication.

Finally in `src/main/resources` you will see `Messages_en.properties` which contains localization for the entity and attribute names that are used in our generated web pages.

#### Step 5

We should now compile what we have generated to verify it at least compiles.

```
./build.sh
```

In the log output you should see this line:

```
[INFO] BUILD SUCCESS
```

You can now deploy this Spring Boot application to a web server environment (such as a local docker instance) to play with the Admin Console and populate some data to see how it works.

## Session 2: Document Builder

### Objective

This session will go over how a markdown document is generated from tutorial elements using the Document Builder templates.

### Discussion

Tutorials can really benefit from having rich content that is easily editable. This makes Markdown a good choice for the content.

The Document Builder support is essentially the building of a markdown document for the whole tutorial or just part of the tutorial (such as just one tutorial module or one tutorial session).

As with most features in this tutorial, it is implemented with a domain that is specific to the feature and with templates to generate code from tags in the domain. We will cover each in a section below:

#### Domain

The `DocumentBuilder` domain allows one to customize how code is generated that will take entity attribute values and build a document with them. 

If we look at a typical structured document we have sections that have a header portion and a body. You can have sections inside other sections to create hierarchy. This creation of hierarchy is also supported where a specific entity would represent a specific level in the document and its "child" entities (those it has a relationship with) can be used to create sub-sections. 

A section heading for an entity can be constructed from elements of the entity. The section heading has three parts: 

*prefix* *number*: *title* 

The *prefix* can come from only the entity name and will be a title-ized version of the entity name. Just tag the entity with `section:prefix` to have it included in the heading. The *number* can come from an integer attribute of entity, just tag the attribute with `section:number`. If either a prefix or number is specified, it will be followed by a colon `:` character. The *title* can come from a string attribute, just tag it with `section:title`. All three elements are **optional**. Example headings are: "Module 1: Introduction" (all three elements), "Summary" (only title), "Step 5" (prefix and number only). 

##### Entity Tags

These tags can be placed on an entity itself.

| Tag | Description |
|:--|:--|
|`section:prefix`|Tagging an entity with this tag indicates that the name of the entity should be used in any section header of the document that is constructed for this entity.|

##### Attribute Tags

These tags can be placed on the attribute of an entity.

| Tag | Description |
|:--|:--|
|`if:multiple`|This is used in conjunction with the `section:number` tag to indicate that it should only include the number if the total number of such sections will be greater than 1.|
|`section:body`|The string attribute that is to supply the body of the section should be tagged with this tag.|
|`section:number`|This should be used on an integer attribute that provides a number in a sequence that is relavent for objects of this entity in a particular scope. For instance if the entity was Step, this could be used to construct section headings Step 1, Step 2, etc.|
|`section:title`|This is applied to any string attribute that is to be used as a title text for a section header.|
|`subsection`|Tagging a string attribute with this tag causes its text to be included as a sub-section (after the section body) where its sub-section header is taken from the attribute name. If you want the sub-section header to be different than the attribute name, simply rename the attribute in this domain. If you don't want a sub-section header, simply additionally add the tag `untitled`.|
|`untitled`|This is used in conjunction with the `subsection` tag to indicate that you don't want the sub-section to have a heading.|

##### Relationship Tags

These tags can be placed on the relationships of an entity.

| Tag | Description |
|:--|:--|
|`subsection`|Tagging a relationship with this tag indicates you want to create a sub-section for each object that exists in that relationship. The formatting for its section header is defined by that entity. This is how you can create hierarchy in the document.|

#### Templates

The `DocumentBuilderAuthor` template uses the tags above (from the `DocumentBuilder` domain) to create a single method for each entity in its Spring Boot service class called `buildMarkdownDocSection()`. It creates hierarchy in the document by calling the `buildMarkdownDocSection()` methods of child entities as it cascades down your model.

You can create a document starting at any level in your model by calling this method and the markdown heading level will start at the level you specify. For example, if you start at the `Tutorial` entity, so calling the `TutorialService.buildMarkdownDocSection()` method passing a 1 for the level, you can create a document for an entire tutorial. Or you can choose to create a separate document for each session by starting calling `SessionService.buildMarkdownDocSection()` and passing 1 for the level.

### Exercise

Again, as was done in previous session exercises, we will provide most of the solution leaving you with enough to understand the important concepts for the session.

#### Step 1

As we saw in the above discussion, the `DocumentBuilder` domain is used to customize how we build the document. We will want to include that as a domain when importing domains.

Edit the `Space.edl` file and where it is importing from `DomainRepo` also import this domain. There should be a line already importing domains from previous tutorial sessions so we can add them here:

```
    import Security, Localization, AdminUI, WebUserUI, DocumentBuilder from DomainRepo
```

Our specialization of the `DocumentBuilder` domain is located in our `ec/domains` local directory so we need to make sure that is imported as well. If you go down further in the `Space.edl` file where it imports from `LocalDomains`, add `DocumentBuilderDomain` there, it should look as follows:

```
    import MicroserviceDomains, SecurityDomain, LocalizationDomain, AdminUIDomain, DocumentBuilderDomain from LocalDomains
```

#### Step 2

Now that we are importing the `DocumentBuilder` domain, we can specialize it and customize the admin console. Most of the customization has already been done, but we will customize it for the `Session` entity.

Edit the `ec/domains/DocumentBuilderDomain.edl` file. You should notice that inside the specialization of the `DocumentBuilder` domain there is a declaration of the `Session` entity with attributes and relationships but no tags. Lets add the tags.

First we will setup how a section heading will look for this entity. We want it to look like: **Session 1: Introduction**. To do this we should first add a tag to the `Session` entity of `section:prefix`:

```
domain DocumentBuilder (Tutorial) {
    ...
    entity Session {
        T "section:prefix"
        ...
    }
    ...
}
```

Now lets tag the number part, this is attribute `number`, we simply tag it `section:number`. And finally the title part is comes from the `title` attribute so we tag it with `section:title`. This gives us this so far:

```
    entity Session {
        T "section:prefix"
        attributes {
            number     { T "section:number" }
            title      { T "section:title" }
            ...
        }
        ...
    }
```

Next we need to take care of subsections. We want to have a section called **Objective** with a body that comes from the value of the `objective` attribute. To do this we simply tag that attribute with `subsection`. Likewise we want to do the same with a section called **Discussion**. To do this we tag the `discussion` attribute with `subsection`. Of course, the order of these sections is important to us. Just make sure the order in which you declare these domain attributes is the order you would like the sub-sections in the document. So here **Objective** will come first then **Discussion**.

Lastly we need to include the Exercise or Exercises of our Session. The exercises exist as a `many relationship` in the `Session` entity, so we basically just need to tag that relationship, `exercises`, with `subsection`. The relationship subsections always follow after the attribute subsections.

Putting it all together, you should have this:

```
    entity Session {
        T "section:prefix"
        attributes {
            number     { T "section:number" }
            title      { T "section:title" }
            objective  { T "subsection" }
            discussion { T "subsection" }
        }
        relationships {
            exercises  { T "subsection" }
        }
    }
```

This would produce a markdown section like this:

	# Session 1: Introduction
	
	## Objective
	
	<<the value of the "objective" attribute>>
	
	## Discussion
	
	<<the value of the "discussion" attribute>>
	
	## Exercise
	
	<<the markdown for the Exercise object with its Steps as they are configured via tags>>

The section levels (#, ##, ...) depend on where you start rendering the document, here we started at the Session level.

#### Step 3

Now lets add the Document Builder template to our project configuration. Edit `Configuration.edl` and add the following template declaration:

```
        template DocumentBuilderAuthor  in "documentBuilder" {
            output primary ServerCode
        }
```

The template name ends with `Author` since it authors code into the Service classes.

#### Step 4

Now we are ready to run the compiler:

```
./run.sh
```

Follow the path down from `src/main/java` to the `service` directory and view the `SessionService.java` file. You should see a method called `buildMarkdownDocSection()` that places values as instructed into building the section associated with a `Session` object. In fact all the Service classes that are involved in the document creation will have a similar `buildMarkdownDocSection()` method. Calling one will call the same method on child entities assuming they have been tagged as sub-sections.

#### Step 5

Finally just to make sure our generated code compiles we can run:

```
./build.sh
```

In the log output you should see this line:

```
[INFO] BUILD SUCCESS
```

In the next tutorial session we will actually use this to generate markdown for the student part of the web site where it will be rendered in HTML.

## Session 3: Tutorial Student Web Site

### Objective

In this module we have shown how to build a micro-service as well as add many important features such as localization, security, admin console and markdown document creation; all of which leads us to the construction of a "student" web site. In this session we will go over how to write this student portal code **not** using templates to show how custom code can still be developed along side generated code.

### Discussion

We finally come to the last part of the Tutorial web site, the Student Portal. For this part of the web site we want to basically show each session at a time on a full page. To do this we will use the Document Builder feature to construct a markdown document for a section then render that markdown to HTML when including in a web page.

The student portal will show all available tutorials on the top page. Then if you open a tutorial it will go to a page that shows all modules for that tutorial. Then clicking on a module shows all sessions. Finally clicking on a session will open a single page for the entire session rendered from a constructed markdown document.

For the student portal we need to develop the following types source files:

|Source File Type | Description |
| -- | -- |
| Thymeleaf Template| This is responsible for generating the final HTML that is sent to the user's web browser. We will make four of these files as described below.|
| Controller Class | The Controller class will establish the endpoints associated with the student portal and feed the Thymeleaf web pages with the data they need for rendering their content.|

#### Thymeleaf Template

We will develop four templates, one for each web page of the Student portal:

| Template | Description |
| -- | -- |
| `UserHome.html` | This will serve as the top page of the Student portal. It has a simple table listing out all of the templates hosted on the site. The user can click on a **Open** button to open that tutorial. This will take them to the tutorial page.|
|`UserTutorial.html`| This displays some information about the tutorial itself then shows a list of its modules. The user can click on a **Open** button to open a module.|
|`UserModule.html`| This displays some information about the module then shows a list of its sessions. The user can click on a **Open** button to open a session.|
|`UserSession.html`| This is where the session and its containing exercise and steps are rendered as a single HTML from a generated markdown document.|

##### User Home Page

This tutorial is not intended to cover Thymeleaf in detail but to cover enough to help illustrate how it is used here. Lets look at some of the HTML code inside the `UserHome.html` template file:

```
	<h2 th:text="|${Tutorial.title}|">Headline</h2>
	<br/>
	<p th:text="${Tutorial.summary}"></p>
	<br/>
	<div class="container-sm">
		<hr/>
		<th:block th:each="item : ${ModulesList}">
			<table class="table table-borderless align-middle" th:unless="${#lists.isEmpty(ModulesList)}">
				<tbody>
			<tr>
				<th class="h3"  th:text="|Module ${item.number}: ${item.title}|" scope="row"></th>
				<td align="right"><a class="btn btn-secondary active" role="button" aria-pressed="true" th:href="${'/module/' + item.id}">Open</a></td>
			</tr>
			<tr>
			    <td colspan="2" th:text="${item.summary}"></td>
			</tr>
				</tbody>
			</table>
			<hr/>
		</th:block>
	</div>
```

Notice the entity compiler like notation for variables in the HTML, it turns out that Thymeleaf uses the same notation. Looking at the first one in the first line `${Tutorial.title}` we see it is making a reference to a variable called `Tutorial` and a field in that object called `title`. This is where the Controller Class comes into play. When the Controller Class instructs for the web page to be this `UserHome.html` page, it needs to set the `Tutorial` variable to an object that has a `getTitle()` method that returns the tutorial title.

Thymeleaf also supports constructs for iterating. This works well when we need to show a table listing the modules of tutorial. We can see that with the line containing `<th:block th:each="item : ${ModulesList}">`. Again the Controller Class need to make sure to set the `ModulesList` variable with an array/list type object that contains module objects. As you can see for each module object it is creating a new row in the table and the columns are used to include fields of the module object.

The `UserTutorial.html` and `UserModule.html` pages are very similar so they won't be covered in detail here.

##### User Session Page

This is where the markdown is rendered into HTML using a javascript library called **marked**. It is pretty easy to use, the following HTML is used to render the session's markdown document:

```
	<input type="hidden" id="markdown-content-hidden" th:value="${SessionMarkdown}"/>
	<div id="markdown-content" class="markdown-body"/>
	<script>
		document.getElementById('markdown-content').innerHTML =
		  marked(document.getElementById('markdown-content-hidden').value, {pedantic: false, gfm: true});
	</script>
```

The input variable `SessionMarkdown` contains the markdown document that our `SessionService` class generates using the document builder function (we will see that below). This is placed into a hidden input element so that it can be fed into the marked function. The `div` element is defined as the place where the HTML output of the marked function will place its HTML. Basically we run a script there that replaces the `innerHTML` of the `div` we defined with the output of the marked function: `marked(document.getElementById('markdown-content-hidden').value`. Notice the input to the marked function is our hidden input element. Next we will show how we set that variable from inside our controller class.

#### Controller Class

The controller class is called `UserWebPageController.java` and supports endpoints needed by the Thymeleaf templates. These endpoints are as follows:

| Endpoint | Page | Variables Set | Description |
| -- | -- | -- | -- |
| `/` | `UserHome` | `TutorialList`| The home page of the student portal that displays a list of the tutorials.|
| `/tutorial/{id}` | `UserTutorial` | `Tutorial`, `ModulesList`| When a user clicks **Open** on a tutorial from the home screen, this endpoint is invoked.|
| `/module/{id}` | `UserModule` |`Tutorial`, `Module`, `SessionList` | When a user clicks **Open** on a module from the Tutorial page, this endpoint is invoked.|
| `/session/{id}`| `UserSession` | `Tutorial`, `Module`, `Session`, `SessionMarkdown` | When a user clicks **Open** on a session from the Module page, this endpoint is invoked. |

For each endpoint there is a Java method in this controller class. This method is passed a `Model` object by Thymeleaf and it is up to the endpoint method to set variables in that model object. For instance, on the first endpoint above it has to pass a list of tutorials as `TutorialList`. This can be done with the following line:

```
        model.addAttribute("TutorialList", tutorialService.getTutorialDtoList(0, 100, true));
```

We are invoking our `TutorialService` to get just up to 100 tutorials. We don't support paging but if we did this should be replaced by code that manages paging. Anway, the array of `TutorialDto` objects are being set to the `TutorialList` variable that will be passed to the `UserHome.html` page.

The next thing of interest is how we pass the session markdown document. This is done in the `/session/{id}` endpoint with the following code:

```
        model.addAttribute("SessionMarkdown", sessionService.buildMarkdownDocSection(1, sessionDto, 1));
```

Here we can see it calling our document builder method `buildMarkdownDocSection()` using the `sessionDto` object for the current session page.

### Exercise

This exercise will not require you to write the entire student portal but instead will just have you fill in a couple of places that are talked about in the discussion.

You will notice that we are including the `src` directory tree for this exercise however it only contains the custom source files that were written for the Student (User) Portal. As well, two of the source files each have lines removed for you to add in the exercise.

#### Step 1

First, we will actually run the compiler. It will **not** overwrite our custom source files.

```
./run.sh
```

You should still see our custom source files, now mixed in with all the generated source files.

#### Step 2

Next we will add the required HTML/Javascript code to hold and render the markdown document for a session to HTML elements inside the Session web page.

Edit the file `UserSession.html` in the `src/main/resources/templates` directory. Look for a comment that says `Add code here to render the session markdown and place in a div element here`. Replace the comment with:

```
	<input type="hidden" id="markdown-content-hidden" th:value="${SessionMarkdown}"/>
	<div id="markdown-content" class="markdown-body"/>
	<script>
		document.getElementById('markdown-content').innerHTML =
		  marked(document.getElementById('markdown-content-hidden').value, {pedantic: false, gfm: true});
	</script>
```

An explanation of the code was made in the discussion section above.

#### Step 3

Now we need to add some code to the Controller class to set the `SessionMarkdown` variable to the session markdown document.

Edit the file `UserWebPageController.java` in the `src/main/java/org/entitycompiler/tutorial/web/user` folder. Look for a line with the comment `set SessionMarkdown here with the markdown document for the session`. Replace this comment line with:

```
        model.addAttribute("SessionMarkdown", sessionService.buildMarkdownDocSection(1, sessionDto, 1));
```

This will use our document builder function to generate the markdown for our session then set the `SessionMarkdown` variable with that.

#### Step 4

This is all we need to do code wise. We should at least build it to make sure there are no compile errors:

```
./build.sh
```

In the log output you should see this line:

```
[INFO] BUILD SUCCESS
```

You can now deploy the application to a server machine and if you go to the base path of the website with a web browser you should see the Student Portal home page. Of course, you should first use the admin console to populate a tutorial so that you have something to look at.
