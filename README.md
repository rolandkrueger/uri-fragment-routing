URI Fragment Routing
====================

[![Build Status](https://travis-ci.org/rolandkrueger/uri-fragment-routing.svg?branch=develop)](https://travis-ci.org/rolandkrueger/uri-fragment-routing) [![Code Coverage](https://img.shields.io/codecov/c/github/rolandkrueger/uri-fragment-routing/develop.svg)](https://codecov.io/github/rolandkrueger/uri-fragment-routing?branch=develop) [![License Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)](https://github.com/rolandkrueger/uri-fragment-routing/blob/master/LICENSE) 
[ ![Download](https://api.bintray.com/packages/rolandkrueger/maven/uri-fragment-routing/images/download.svg?version=1.2.0) ](https://bintray.com/rolandkrueger/maven/uri-fragment-routing/1.2.0/link)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.roklib/uri-fragment-routing/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.roklib/uri-fragment-routing)
[![Documentation](https://img.shields.io/badge/documentation-1.2.0-blue.svg)](http://roklib.org/)

This library provides a framework for mapping URI fragments as found in URIs, such as `http://www.example.com/shop#!signin`, on custom action command objects. For example, the URI fragment `signin` could be mapped on an action class `GoToSignInPageAction` which will be executed when the user visits this address.

This is useful for single-page applications where the application state is typically encoded in the URI fragment. For example, the following URI might be used by an application to show a product detail page in some web shop. 

`http://www.example.com/shop#!products/group/books/id/4711/view`

Here the part `!products/group/books/id/4711/view` forms the URI fragment that carries the application's current state. As you can see, such a URI fragment consists of a directory-like path structure. This structure may additionally contain parameter values such as identifiers, categories, or geographic coordinates. Since visited URI fragments are pushed to the browser history, they allow the development of navigable web applications that support the browser back button and deep linking.

Single-page applications typically use a large number of such fragments in order to allow users to navigate to the different parts of the application. In addition to navigating to some sub-view, a URI fragment could also be responsible for changing the state of some UI component visible on a view. For instance, some particular URI fragment could bring a specific panel of a panel stack (or tab sheet) to the foreground.

When using URI fragments for encoding application state, you're facing two problems that have to be solved. Firstly, a URI fragment visited by the user has to be parsed and interpreted. Parameter values must be extracted from the fragment, they need to be converted and validated, and finally they must be passed to the code that is responsible for handling the fragment.

Secondly, valid URI fragments have to be generated from the application's current state so that they can be used for creating HTML hyperlinks. These two tasks have to be kept consistent throughout the code base of an application so that URI fragments generated in one part of the application can be consistently interpreted by other parts of the application. If the structure of a URI fragment is changed during application development, this has to be reflected everywhere a URI fragment is generated for a link.

This library helps you with these tasks. Find below a list of features provided by this library:

# Features

* Mapper objects map individual path segments of a URI fragment on action command objects. There will be individual mapper objects responsible for the path segments `products`, `group`, `books`, and `view` from the example URI above.
* The set of mappers defined for an application is kept in a tree-like data structure (*mapper tree*) reflecting the directory structure of the set of URI fragments used by an application. There is exactly one instance of this mapper tree available per application. This instance is shared by the user sessions.
* A convenient builder infrastructure can be used to construct a full mapper tree. You will be guided by the code completion feature of your IDE during this process.
* A default action command can be executed if a URI fragment could not be successfully resolved to an action command.  
* For every URI fragment path segment, which is handled by a mapper object, a set of parameters can be defined which will be interpreted when a particular URI fragment visited by the user is handled. In the example above, this is the `id` parameter.
* Parameters can be single-valued (e. g. an ID) or multi-valued (e. g. a pair of coordinates).
* Parameter values are automatically converted from their String representation as used in a URI fragment to their respective domain types by converter classes and vice versa.
* Different parameter modes are available: directory style with names, directory style without names, and query style.
* Fully parameterized URI fragments can be assembled for individual mappers of the mapper tree to be used in HTML links.
* The URI fragment interpretation process is thread-safe so that only one mapper tree object needs to be managed per application.
* The library requires Java 8 and fully supports lambda expressions. 

Refer to the library's documentation to obtain more information about how the library works in detail and how to use its API.

# Usage

This library is available from Maven Central. You can use it with the following Maven coordinates:

```xml
<dependency>
    <groupId>org.roklib</groupId>
    <artifactId>uri-fragment-routing</artifactId>
    <version>1.2.0</version>
</dependency>
```
Adapt the version number accordingly.

# Examples

Following are some usage examples that show how to use this library for a simple scenario. More complex examples can be found in the [user manual](http://www.roklib.org).

## Single path element with parameter

Consider the following URI: 

`http://www.example.com#!profile/user/john.doe`

This URI's fragment `profile/john.doe` (the exclamation mark in the beginning can be ignored) consists of a single path segment `profile` and a String-valued parameter with name `user`. The parameter's value is `john.doe` in this example.

Let's see how we can build the mapper tree for this fragment:

```
AbstractUriPathSegmentActionMapper[] mappers = new AbstractUriPathSegmentActionMapper[1];

UriActionMapperTree mapperTree =
   UriActionMapperTree.create()
      .useParameterMode(DIRECTORY_WITH_NAMES)
      .buildMapperTree()
          .map("profile").onActionFactory(ShowUserProfileCommand::new)
          .withSingleValuedParameter("user").forType(String.class).noDefault()
          .finishMapper(mapper -> mappers[0] = mapper)
      .build();
```

We will see what to do with the `mappers` array shortly.

This mapper tree can now be fed with the URI fragments visited by the users:

```
mapperTree.interpretFragment("profile/user/jane.doe")
```

As a result of the interpretation process, the mapper tree resolves the given URI fragment to the action command factory provided with the `onActionFactory()` method. The interface `UriActionCommandFactory` is a functional interface and is given as a lambda expression (a method reference) in this example. The interpretation process uses this factory to create an action command object of type `ShowUserProfileCommand` and executes it. Letˈs check out this class:

```
public static class ShowUserProfileCommand implements UriActionCommand {
    ParameterValue<String> user;
    
    @Override
    public void run() {
        // display the profile page to the user
    }

    @CapturedParameter(mapperName = "profile", parameterName = "user")
    public void setUserName(ParameterValue<String> user) {
        this.userName = userName;
    }
}
```

This class implements interface `UriActionCommand` which is derived from the `java.lang.Runnable` interface. When interpreting a URI fragment, the `run()` method of this class is executed at the end of the interpretation process. By adding annotated setter methods to this class, the current parameter values can be passed into the action objects.

## Generate a parameterized URI fragment  

Given the mapper tree defined above, the next step in developing a web application is to add links to the resources thus defined. You will want to avoid code such as the following:

`view.addLinkTo("profile/user/john.doe")`

This is hardly maintainable, since you'd have to adapt all such code when you alter the structure of the URI fragments. Just imagine the amount of work you would have to do if you changed the parameter name `user` to `userName`.

The mapper tree you built in the beginning will take care of this problem. It lets you generate a valid URI fragment from its internal model and the parameter values you pass it:

```
CapturedParameterValues parameterValues = new CapturedParameterValues();
parameterValues.setValueFor("profile", "user", ParameterValue.forValue("john.doe"));

String uriFragment = mapperTree.assembleUriFragment(parameterValues, mappers[0]);

view.addLinkTo(uriFragment);
```

Here, we instruct the mapper tree to assemble a parameterized URI fragment for the mapper instance we captured in the `mappers` array while constructing the mapper tree. This URI fragment contains a value for the `user` parameter which we provide with an object of type `CapturedParameterValues`.

## Creating mapping trees with sub-trees

Let's look at another example. Now we create a mapper tree that contains a sub-tree structure. For example, we want to map the following URI fragment on an action. This time, we don't want to define any parameters.
 
 `http://www.example.com#!users/profile`

This can be done with the following code:

```
mapperTree = UriActionMapperTree.create().buildMapperTree()
   .mapSubtree("users").onSubtree()
       .map("profile").onActionFactory(ShowUserProfileCommand::new).finishMapper()
   .finishMapper()
   .build();
```

Using this technique, you can define arbitrarily complex mapper trees.