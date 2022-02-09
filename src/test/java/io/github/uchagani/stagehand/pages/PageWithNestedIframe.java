package io.github.uchagani.stagehand.pages;

import com.microsoft.playwright.Locator;
import io.github.uchagani.stagehand.annotations.Find;
import io.github.uchagani.stagehand.annotations.PageObject;

@PageObject(frame = {"#parentIframe", "#childIframe"})
public class PageWithNestedIframe {
    @Find("#child-iframe-paragraph")
    public Locator paragraph;
}
