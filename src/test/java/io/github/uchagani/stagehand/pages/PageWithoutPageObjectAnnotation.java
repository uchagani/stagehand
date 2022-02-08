package io.github.uchagani.stagehand.pages;

import com.microsoft.playwright.Locator;
import io.github.uchagani.stagehand.annotations.Find;

public class PageWithoutPageObjectAnnotation {
    @Find("#headerId")
    public Locator header;

    @Find(".paragraph")
    public Locator paragraph;
}
