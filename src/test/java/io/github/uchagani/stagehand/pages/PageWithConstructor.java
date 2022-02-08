package io.github.uchagani.stagehand.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.github.uchagani.stagehand.annotations.Find;
import io.github.uchagani.stagehand.annotations.PageObject;

@PageObject
public class PageWithConstructor {
    @Find("#headerId")
    public Locator header;

    @Find(".paragraph")
    public Locator paragraph;

    private final Page page;

    public PageWithConstructor(Page page) {
        this.page = page;
    }
}
