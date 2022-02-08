package io.github.uchagani.stagehand.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.github.uchagani.stagehand.PageFactory;
import io.github.uchagani.stagehand.exeptions.MissingPageObjectAnnotation;
import io.github.uchagani.stagehand.pages.PageWithConstructor;
import io.github.uchagani.stagehand.pages.PageWithoutConstructor;
import io.github.uchagani.stagehand.pages.PageWithoutPageObjectAnnotation;
import io.github.uchagani.stagehand.pages.InheritedPageWithoutConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    public void create_canInitializeFieldsMarkedWithFindAnnotation() {
        PageWithoutConstructor homePage = PageFactory.create(PageWithoutConstructor.class, page);

        assertThat(homePage.header.textContent()).isEqualTo("Header Text");
        assertThat(homePage.paragraph.textContent()).isEqualTo("This is a paragraph.");
    }

    @Test
    public void create_throwsException_whenCalledOnPageWithoutPageObjectAnnotation() {
        assertThatThrownBy(() -> {
            PageFactory.create(PageWithoutPageObjectAnnotation.class, page);
        }).isInstanceOf(MissingPageObjectAnnotation.class);
    }

    @Test
    public void create_canInitializeFieldsInBaseClass_whenBeingCalledOnChildClass() {
        InheritedPageWithoutConstructor inheritedPage = PageFactory.create(InheritedPageWithoutConstructor.class, page);

        assertThat(inheritedPage.header.textContent()).isEqualTo("Header Text");
        assertThat(inheritedPage.paragraph.textContent()).isEqualTo("This is a paragraph.");
    }

    @Test
    public void create_canInitializeFields_inClassWithAConstructor() {
        PageWithConstructor homePage = PageFactory.create(PageWithConstructor.class, page);

        assertThat(homePage.header.textContent()).isEqualTo("Header Text");
        assertThat(homePage.paragraph.textContent()).isEqualTo("This is a paragraph.");
    }

    @Test
    public void initElements_canInitializeFieldsMarkedWithFindAnnotation() {
        PageWithoutConstructor homePage = new PageWithoutConstructor();
        PageFactory.initElements(homePage, page);

        assertThat(homePage.header.textContent()).isEqualTo("Header Text");
        assertThat(homePage.paragraph.textContent()).isEqualTo("This is a paragraph.");
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

        assertThat(inheritedPage.header.textContent()).isEqualTo("Header Text");
        assertThat(inheritedPage.paragraph.textContent()).isEqualTo("This is a paragraph.");
    }

    @Test
    public void initElements_canInitializeFields_inClassWithAConstructor() {
        PageWithConstructor homePage = new PageWithConstructor(page);
        PageFactory.initElements(homePage, page);

        assertThat(homePage.header.textContent()).isEqualTo("Header Text");
        assertThat(homePage.paragraph.textContent()).isEqualTo("This is a paragraph.");
    }

}
