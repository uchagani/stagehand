package io.github.uchagani.stagehand.pages;

import com.microsoft.playwright.Locator;
import io.github.uchagani.stagehand.annotations.Find;
import io.github.uchagani.stagehand.annotations.PageObject;
import io.github.uchagani.stagehand.annotations.Under;
import io.github.uchagani.stagehand.custom.CustomInput;

@PageObject
public class CustomPage {
    @Find("#firstNameFormDiv")
    public Locator firstNameFormDiv;

    @Find("form")
    @Under("firstNameFormDiv")
    public CustomInput customInput;
}
