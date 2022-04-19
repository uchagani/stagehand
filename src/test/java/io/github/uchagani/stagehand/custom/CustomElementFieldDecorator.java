package io.github.uchagani.stagehand.custom;

import com.microsoft.playwright.Page;
import io.github.uchagani.stagehand.FieldDecorator;

import java.lang.reflect.Field;

public class CustomElementFieldDecorator implements FieldDecorator {
    private final CustomElementFactory customElementFactory;

    public CustomElementFieldDecorator(Page page) {
        this.customElementFactory = new CustomElementFactory(page);
    }

    @Override
    public Object decorate(Field field, Object pageObjectInstance) {
        return customElementFactory.createCustomElement(field, pageObjectInstance);
    }
}
