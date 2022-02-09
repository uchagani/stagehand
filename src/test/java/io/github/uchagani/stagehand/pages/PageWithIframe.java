package io.github.uchagani.stagehand.pages;

import com.microsoft.playwright.Locator;
import io.github.uchagani.stagehand.annotations.Find;
import io.github.uchagani.stagehand.annotations.PageObject;

@PageObject(frame = {"#parentIframe"})
public class PageWithIframe {
    @Find("#iframe-paragraph")
    public Locator paragraph;
}
