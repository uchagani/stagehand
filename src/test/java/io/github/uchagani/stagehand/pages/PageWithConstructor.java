package io.github.uchagani.stagehand.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.github.uchagani.stagehand.AfterCreate;
import io.github.uchagani.stagehand.annotations.Find;
import io.github.uchagani.stagehand.annotations.PageObject;
import io.github.uchagani.stagehand.annotations.Under;

@PageObject
public class PageWithConstructor implements AfterCreate {
    @Find("#headerId")
    public Locator header;

    @Find(".paragraph")
    public Locator paragraph;

    @Find("#cityDiv input")
    public Locator cityInput;

    private final Page page;

    public PageWithConstructor(Page page) {
        this.page = page;
    }

    @Override
    public void afterCreate() {
        cityInput.fill("New York");
    }
}
