package io.github.uchagani.stagehand.pages;

import com.microsoft.playwright.Page;
import io.github.uchagani.stagehand.PageFactory;
import io.github.uchagani.stagehand.annotations.PageObject;

@PageObject
public class PageWithEmbeddedPageObject {

    public PageWithIframe embeddedIframe;

    public PageWithEmbeddedPageObject(Page page) {
        embeddedIframe = PageFactory.create(PageWithIframe.class, page);
    }
}


