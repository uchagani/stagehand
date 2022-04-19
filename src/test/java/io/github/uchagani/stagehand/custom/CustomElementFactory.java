package io.github.uchagani.stagehand.custom;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.github.uchagani.stagehand.LocatorFactory;

import java.lang.reflect.Field;

public class CustomElementFactory {
    private final LocatorFactory locatorFactory;

    public CustomElementFactory(Page page) {
        this.locatorFactory = new LocatorFactory(page);
    }

    public Object createCustomElement(Field field, Object pageObjectInstance) {
        Locator locator = locatorFactory.createLocator(field, pageObjectInstance);

        if (Locator.class.equals(field.getType())) {
            return locator;
        } else if (CustomInput.class.equals(field.getType())) {
            return new CustomInput(locator);
        }
        throw new RuntimeException("Unknown custom element");

    }
}
