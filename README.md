# Stagehand

*Page Objects for [Playwright Java](https://playwright.dev/java/) made easy!*

Stagehand is a Java library that helps you create clean and readable Page Objects. Inspired
by [Selenium's PageFactory](https://github.com/SeleniumHQ/selenium/wiki/PageFactory), Stagehand allows you to create
locators easily by annotating fields.

## Installation

```xml
<dependency>
    <groupId>io.github.uchagani</groupId>
    <artifactId>stagehand</artifactId>
    <version>1.0</version>
</dependency>
```

## Getting Started

Creating a Page Object is as easy as annotating your class with `@PageObject`:

```java

@PageObject
public class HomePage {

}

```

To define [Locators](https://playwright.dev/java/docs/locators), create a Field and annotate it with `@Find`:

```java

@PageObject
public class HomePage {

    @Find("#some-id")
    private Locator myButton;
}
```

`@Find` accepts any Playwright [Selector](https://playwright.dev/java/docs/selectors).

To use your Page Object in your tests use the `PageFactory` to create an instance of your page and pass it your page
class and an instance of Playwright's [Page](https://playwright.dev/java/docs/pages):

```java
HomePage homePage=PageFactory.create(HomePage.class,page)
```

The PageFactory can create an instance of any page that has a default constructor or a constructor with just a
Playwright Page parameter.

## Iframes

Stagehand gives you the ability to easily locate elements inside an Iframe by passing in a selector to find the Iframe:

```java

@PageObject(frame = {".iframe-foo"})
public class PageWithIframe {

    @Find("#iframe-button")
    public Locator someButton;
}
```

Any Locators defined inside a class that is decorated with `@PageObject(frame = {"X"}` will be scoped to that Iframe.
You can find nested Iframes by passing in multiple parameters to `frame`.

The above example is equivalent to Playwright's `page.frameLocator(".iframe-foo").locator("#iframe-button")`.

## Dependent Locators

At times, you may want to find a locator that is under another locator. The way to do this in Playwright would
be: `page.locator("#parent").locator(".child")`. To define this in Stagehand you can use the `@Under` annotation:

```java

@PageObject
public class HomePage {

    @Find("#parent")
    public Locator parentLocator;

    @Under("parentLocator")
    @Find(".child")
    public Locator childLocator;

    @Under("parentLocator")
    @Find(".another-child")
    public Locator anotherChild;
}
```

The value you pass to the `@Under` annotation should be the name of the parent Locator.

## Assertions

Because of the way `Locator` fields are initialized by the `PageFactory`, you have to use Stagehand's `assertThat`
method in order to use Playwright's Assertions. But don't worry, it's very simple and the only difference is the import
statement:

```java
// Import Stagehand's assertThat method instead of Playwright's assertThat.

import static io.github.uchagani.stagehand.Assertions.assertThat;

public void HomePageTests{
    
@Test
public void myTest() {
    HomePage homePage = PageFactory.create(HomePage.class, page);

    assertThat(homePage.header).containsText("Header Text");
    assertThat(homePage.paragraph).containsText("This is a paragraph.");
}
```

## Hooks

Stagehand allows you to define a method that will automatically be called after creation of the page object by the PageFactory.  
To define a hook implement the `AfterCreate` interface and put whatever code you want in that method.

```java
import io.github.uchagani.stagehand.AfterCreate;

@PageObject
public class HomePage implements AfterCreate {

    @Find("#some-id")
    private Locator myButton;
    
    @Override
    public void afterCreate() {
        myButton.click();
    }
}
```

## Requirements

Stagehand requires Java 8+ and Playwright 1.18.0+.
