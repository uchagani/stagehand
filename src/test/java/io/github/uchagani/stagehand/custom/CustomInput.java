package io.github.uchagani.stagehand.custom;

import com.microsoft.playwright.Locator;

public class CustomInput {
    public final Locator input;
    private final Locator containerLocator;

    public CustomInput(Locator locator) {
        this.containerLocator = locator;
        input = containerLocator.locator("input");
    }

    public void customMethod(String value) {
        this.input.fill(value);
    }
}
