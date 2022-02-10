package io.github.uchagani.stagehand;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.github.uchagani.stagehand.annotations.Find;
import io.github.uchagani.stagehand.annotations.PageObject;
import io.github.uchagani.stagehand.annotations.Under;

import java.lang.reflect.Field;

public class LocatorFactory {
    Page page;

    public LocatorFactory(Page page) {
        this.page = page;
    }

    public Locator createLocator(Field field, Object pageObjectInstance) {
        Class<?> clazz = field.getDeclaringClass();
        PageObject pageAnnotation = clazz.getAnnotation(PageObject.class);
        Find findAnnotation = field.getAnnotation(Find.class);
        Under underAnnotation = field.getAnnotation(Under.class);

        if (pageAnnotation.frame().length == 0) {
            if (underAnnotation == null) {
                return page.locator(findAnnotation.value());
            }

            Locator locator;
            try {
                Field depField = clazz.getField(underAnnotation.value());
                locator = (Locator) depField.get(pageObjectInstance);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e.getCause());
            }

            return locator.locator(findAnnotation.value());
        }

        FrameLocator frameLocator = page.frameLocator(pageAnnotation.frame()[0]);

        if (pageAnnotation.frame().length > 1) {
            for (int i = 1; i < pageAnnotation.frame().length; i++) {
                frameLocator = frameLocator.frameLocator(pageAnnotation.frame()[i]);
            }
        }

        return frameLocator.locator(findAnnotation.value());
    }

}
