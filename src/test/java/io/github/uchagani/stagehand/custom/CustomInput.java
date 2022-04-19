package io.github.uchagani.stagehand.custom;

import com.microsoft.playwright.Locator;

public class CustomInput implements CustomElement {
    private final Locator input;
    public final Locator containerLocator;

    public CustomInput(Locator locator) {
        this.containerLocator = locator;
        input = containerLocator.locator("input");
    }

    public void fill(String value) {
        this.containerLocator.fill(value);
    }
}
