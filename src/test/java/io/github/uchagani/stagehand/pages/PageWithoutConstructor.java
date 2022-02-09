package io.github.uchagani.stagehand.pages;

import com.microsoft.playwright.Locator;
import io.github.uchagani.stagehand.annotations.Find;
import io.github.uchagani.stagehand.annotations.PageObject;
import io.github.uchagani.stagehand.annotations.Under;

@PageObject
public class PageWithoutConstructor {
    @Find("#headerId")
    public Locator header;

    @Find(".paragraph")
    public Locator paragraph;

    @Find("#firstNameFormDiv")
    public Locator firstNameFormDiv;

    @Find("form")
    @Under({"firstNameFormDiv"})
    public Locator firstNameForm;

    @Find("input")
    @Under({"firstNameForm"})
    public Locator firstNameInput;

    @Find("#lastNameFormDiv")
    public Locator lastNameFormDiv;

    @Find("form")
    @Under({"lastNameFormDiv"})
    public Locator lastNameForm;

    @Find("input")
    @Under({"lastNameFormDiv", "lastNameForm"})
    public Locator lastNameInput;
}
