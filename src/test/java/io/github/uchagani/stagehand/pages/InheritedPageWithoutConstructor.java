package io.github.uchagani.stagehand.pages;

import com.microsoft.playwright.Locator;
import io.github.uchagani.stagehand.annotations.Find;
import io.github.uchagani.stagehand.annotations.PageObject;
import io.github.uchagani.stagehand.annotations.Under;

@PageObject
public class InheritedPageWithoutConstructor extends PageWithoutConstructor {

    @Under("lastNameFormDiv")
    @Find("#cityDiv input")
    public Locator cityInput;
}
