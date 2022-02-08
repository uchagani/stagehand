package io.github.uchagani.stagehand.pages;

import com.microsoft.playwright.Locator;
import io.github.uchagani.stagehand.annotations.Find;
import io.github.uchagani.stagehand.annotations.PageObject;

@PageObject
public class PageWithoutConstructor {
    @Find("#headerId")
    public Locator header;

    @Find(".paragraph")
    public Locator paragraph;

}
