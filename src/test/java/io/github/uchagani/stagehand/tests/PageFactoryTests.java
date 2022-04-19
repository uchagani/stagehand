package io.github.uchagani.stagehand.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.github.uchagani.stagehand.PageFactory;
import io.github.uchagani.stagehand.custom.CustomElementFieldDecorator;
import io.github.uchagani.stagehand.exeptions.InvalidParentLocatorException;
import io.github.uchagani.stagehand.exeptions.MissingPageObjectAnnotation;
import io.github.uchagani.stagehand.pages.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class PageFactoryTests {
    Page page;

    @BeforeEach
    public void beforeEach() {
        Browser browser = Playwright.create().chromium().launch();
        page = browser.newPage();
        page.setContent(HTMLConstants.SIMPLE_HTML);
    }

    @Test
    public void canInitializePageWithCustomFieldDecorator() {
        CustomPage homePage = PageFactory.create(CustomPage.class, page, new CustomElementFieldDecorator(page));
        homePage.customInput.customMethod("foo");
        assertThat(homePage.customInput.input).hasValue("foo");
    }

    @Test
    public void create_canInitializeFieldsMarkedWithFindAnnotation_withPlaywrightAssertions() {
        PageWithoutConstructor homePage = PageFactory.create(PageWithoutConstructor.class, page);

        assertThat(homePage.header).containsText("Header Text");
        assertThat(homePage.paragraph).containsText("This is a paragraph.");
    }

    @Test
    public void create_canInitializeFieldsMarkedWithFindAnnotation() {
        PageWithoutConstructor homePage = PageFactory.create(PageWithoutConstructor.class, page);

        assertThat(homePage.header).containsText("Header Text");
        assertThat(homePage.paragraph).containsText("This is a paragraph.");
    }

    @Test
    public void create_throwsException_whenCalledOnPageWithoutPageObjectAnnotation() {
        assertThatThrownBy(() -> {
            PageFactory.create(PageWithoutPageObjectAnnotation.class, page);
        }).isInstanceOf(MissingPageObjectAnnotation.class);
    }

    @Test
    public void create_throwsException_whenTryingToInitializePage_withDependentLocatorThat_doesNot_exist() {
        assertThatThrownBy(() -> {
            PageFactory.create(PageWithBadDependentLocator.class, page);
        }).isInstanceOf(InvalidParentLocatorException.class);
    }

    @Test
    public void create_canInitializeFieldsInBaseClass_whenBeingCalledOnChildClass() {
        InheritedPageWithoutConstructor inheritedPage = PageFactory.create(InheritedPageWithoutConstructor.class, page);

        assertThat(inheritedPage.header).containsText("Header Text");
        assertThat(inheritedPage.paragraph).containsText("This is a paragraph.");
    }

    @Test
    public void afterCreateHookIsCalledSuccessfully() {
        PageWithConstructor homePage = PageFactory.create(PageWithConstructor.class, page);

        assertThat(homePage.cityInput).hasValue("New York");
    }

    @Test
    public void create_canInitializeFields_inClassWithAConstructor() {
        PageWithConstructor homePage = PageFactory.create(PageWithConstructor.class, page);

        assertThat(homePage.header).containsText("Header Text");
        assertThat(homePage.paragraph).containsText("This is a paragraph.");
    }

    @Test
    public void create_canInitialize_dependentFields() {
        PageWithoutConstructor homePage = PageFactory.create(PageWithoutConstructor.class, page);

        assertThat(homePage.firstNameInput).hasAttribute("placeholder", "First Name");
        assertThat(homePage.lastNameInput).hasAttribute("placeholder", "Last Name");
    }

    @Test
    public void create_canInitialize_dependentFields_inChildClass() {
        InheritedPageWithoutConstructor homePage = PageFactory.create(InheritedPageWithoutConstructor.class, page);

        assertThat(homePage.cityInput).hasAttribute("placeholder", "City");
        assertThat(homePage.firstNameInput).hasAttribute("placeholder", "First Name");
        assertThat(homePage.lastNameInput).hasAttribute("placeholder", "Last Name");
    }

    @Test
    public void initElements_throwsException_whenTryingToInitializePage_withDependentLocatorThat_doesNot_exist() {
        assertThatThrownBy(() -> {
            PageWithBadDependentLocator homePage = new PageWithBadDependentLocator();
            PageFactory.initElements(homePage, page);
        }).isInstanceOf(InvalidParentLocatorException.class);
    }

    @Test
    public void initElements_canInitialize_dependentFields_inChildClass() {
        InheritedPageWithoutConstructor homePage = new InheritedPageWithoutConstructor();
        PageFactory.initElements(homePage, page);

        assertThat(homePage.cityInput).hasAttribute("placeholder", "City");
        assertThat(homePage.firstNameInput).hasAttribute("placeholder", "First Name");
        assertThat(homePage.lastNameInput).hasAttribute("placeholder", "Last Name");
    }

    @Test
    public void initElements_canInitialize_dependentFields() {
        PageWithoutConstructor homePage = new PageWithoutConstructor();
        PageFactory.initElements(homePage, page);

        assertThat(homePage.firstNameInput).hasAttribute("placeholder", "First Name");
        assertThat(homePage.lastNameInput).hasAttribute("placeholder", "Last Name");
    }

    @Test
    public void initElements_canInitializeFieldsMarkedWithFindAnnotation() {
        PageWithoutConstructor homePage = new PageWithoutConstructor();
        PageFactory.initElements(homePage, page);

        assertThat(homePage.header).containsText("Header Text");
        assertThat(homePage.paragraph).containsText("This is a paragraph.");
    }

    @Test
    public void initElements_throwsException_whenCalledOnPageWithoutPageObjectAnnotation() {
        assertThatThrownBy(() -> {
            PageWithoutPageObjectAnnotation homePage = new PageWithoutPageObjectAnnotation();
            PageFactory.initElements(homePage, page);
        }).isInstanceOf(MissingPageObjectAnnotation.class);
    }

    @Test
    public void initElements_canInitializeFieldsInBaseClass_whenBeingCalledOnChildClass() {
        InheritedPageWithoutConstructor inheritedPage = new InheritedPageWithoutConstructor();
        PageFactory.initElements(inheritedPage, page);

        assertThat(inheritedPage.header).containsText("Header Text");
        assertThat(inheritedPage.paragraph).containsText("This is a paragraph.");
    }

    @Test
    public void initElements_canInitializeFieldsInClass_with_aConstructor() {
        PageWithConstructor homePage = new PageWithConstructor(page);
        PageFactory.initElements(homePage, page);

        assertThat(homePage.header).containsText("Header Text");
        assertThat(homePage.paragraph).containsText("This is a paragraph.");
    }

    @Test
    public void initElements_canInitializeFieldsInClass_without_aConstructor() {
        PageWithoutConstructor homePage = new PageWithoutConstructor();
        PageFactory.initElements(homePage, page);

        assertThat(homePage.header).containsText("Header Text");
        assertThat(homePage.paragraph).containsText("This is a paragraph.");
    }

    @Test
    public void create_canFindLocatorsInsideIframe() {
        page.setContent(HTMLConstants.IFRAME_HTML);
        PageWithIframe iframePage = PageFactory.create(PageWithIframe.class, page);

        assertThat(iframePage.paragraph).containsText("Hello from inside an iframe!");
    }

    @Test
    public void create_canFindLocatorsInsideNestedIframe() {
        page.setContent(HTMLConstants.IFRAME_HTML);
        PageWithNestedIframe iframePage = PageFactory.create(PageWithNestedIframe.class, page);

        assertThat(iframePage.paragraph).containsText("Child iFrame paragraph");
    }

    @Test
    public void create_canInitializePageObjects_thatHaveOtherPageObjectsAsFields() {
        page.setContent(HTMLConstants.IFRAME_HTML);
        PageWithEmbeddedPageObject homePage = PageFactory.create(PageWithEmbeddedPageObject.class, page);

        assertThat(homePage.embeddedIframe.paragraph).containsText("Hello from inside an iframe!");
    }
}
