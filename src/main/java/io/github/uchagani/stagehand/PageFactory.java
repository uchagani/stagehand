package io.github.uchagani.stagehand;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.github.uchagani.stagehand.annotations.Find;
import io.github.uchagani.stagehand.annotations.PageObject;
import io.github.uchagani.stagehand.annotations.Under;
import io.github.uchagani.stagehand.exeptions.MissingPageObjectAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PageFactory {
    public static <T> T create(Class<T> pageToCreate, Page page) {
        return instantiatePage(pageToCreate, page);
    }

    public static void initElements(Object pageObject, Page page) {
        if (pageObject.getClass().isAnnotationPresent(PageObject.class)) {
            initElements(new FieldDecorator(page), pageObject);
        } else {
            throw new MissingPageObjectAnnotation("Only pages marked with @PageObject can can be initialized by the PageFactory.");
        }
    }

    private static <T> T instantiatePage(Class<T> pageClassToProxy, Page page) {
        try {
            if (pageClassToProxy.isAnnotationPresent(PageObject.class)) {
                T pageObjectInstance;
                try {
                    Constructor<T> constructor = pageClassToProxy.getConstructor(Page.class);
                    pageObjectInstance = constructor.newInstance(page);
                } catch (NoSuchMethodException e) {
                    pageObjectInstance = pageClassToProxy.getDeclaredConstructor().newInstance();
                }
                initElements(new FieldDecorator(page), pageObjectInstance);
                return pageObjectInstance;
            }
            throw new MissingPageObjectAnnotation("Only pages marked with @PageObject can can be created by the PageFactory.");

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initElements(FieldDecorator decorator, Object pageObjectInstance) {
        Class<?> pageObjectClass = pageObjectInstance.getClass();
        while (pageObjectClass != Object.class) {
            proxyFields(decorator, pageObjectInstance, pageObjectClass);
            pageObjectClass = pageObjectClass.getSuperclass();
        }
    }

    private static void proxyFields(FieldDecorator decorator, Object pageObjectInstance, Class<?> pageObjectClass) {
        Field[] fields = pageObjectClass.getDeclaredFields();
        List<String> fieldNamesAlreadyProxied = new ArrayList<>();
        List<Field> fieldsWithDependencies = new ArrayList<>();

        for (Field field : fields) {
            if (isProxyable(field)) {
                if (hasDependencies(field)) {
                    fieldsWithDependencies.add(field);
                    continue;
                }
                proxyField(decorator, field, pageObjectInstance);
                fieldNamesAlreadyProxied.add(field.getName());
            }
        }

        int sizeBefore;
        while (!fieldsWithDependencies.isEmpty()) {
            sizeBefore = fieldsWithDependencies.size();
            List<Field> proxiedScopedFields = new ArrayList<>();

            for (Field field : fieldsWithDependencies) {
                List<String> dependencyNames = Collections.singletonList(field.getAnnotation(Under.class).value());

                if (fieldNamesAlreadyProxied.containsAll(dependencyNames)) {
                    proxyField(decorator, field, pageObjectInstance);
                    fieldNamesAlreadyProxied.add(field.getName());
                    proxiedScopedFields.add(field);
                }
            }

            for (Field proxied : proxiedScopedFields) {
                fieldsWithDependencies.remove(proxied);
            }

            if (sizeBefore == fieldsWithDependencies.size()) {
                throw new RuntimeException("Unable to find dependencies for the following Fields:");
            }
        }
    }

    private static boolean hasDependencies(Field field) {
        return field.isAnnotationPresent(Under.class);
    }

    private static boolean isProxyable(Field field) {
        if (!Locator.class.isAssignableFrom(field.getType())) {
            return false;
        }

        return field.isAnnotationPresent(Find.class);
    }

    private static void proxyField(FieldDecorator decorator, Field field, Object pageObjectInstance) {
        Object value = decorator.decorate(field, pageObjectInstance);
        if (value != null) {
            try {
                field.setAccessible(true);
                field.set(pageObjectInstance, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
