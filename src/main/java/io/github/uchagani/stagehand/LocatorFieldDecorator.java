package io.github.uchagani.stagehand;

import com.microsoft.playwright.Page;

import java.lang.reflect.Field;

public class LocatorFieldDecorator implements FieldDecorator {
    Page page;
    LocatorFactory locatorFactory;

    public LocatorFieldDecorator(Page page) {
        this.page = page;
        this.locatorFactory = new LocatorFactory(page);
    }

    @Override
    public Object decorate(Field field, Object pageObjectInstance) {
        return locatorFactory.createLocator(field, pageObjectInstance);
    }

}
