package io.github.uchagani.stagehand;

import com.microsoft.playwright.Page;
import io.github.uchagani.stagehand.annotations.Find;
import io.github.uchagani.stagehand.annotations.PageObject;
import io.github.uchagani.stagehand.annotations.Under;
import io.github.uchagani.stagehand.exeptions.InvalidParentLocatorException;
import io.github.uchagani.stagehand.exeptions.MissingPageObjectAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PageFactory {
    public static <T> T create(Class<T> pageToCreate, Page page) {
        return instantiatePage(pageToCreate, page, new LocatorFieldDecorator(page));
    }

    public static <T> T create(Class<T> pageToCreate, Page page, FieldDecorator decorator) {
        return instantiatePage(pageToCreate, page, decorator);
    }

    public static void initElements(Object pageObject, Page page) {
        initElements(pageObject, new LocatorFieldDecorator(page));
    }

    public static void initElements(Object pageObject, FieldDecorator decorator) {
        if (pageObject.getClass().isAnnotationPresent(PageObject.class)) {
            initElements(decorator, pageObject);
        } else {
            throw new MissingPageObjectAnnotation("Only pages marked with @PageObject can can be initialized by the PageFactory.");
        }
    }

    private static void callAfterCreateHook(Object pageObjectInstance) {
        if (AfterCreate.class.isAssignableFrom(pageObjectInstance.getClass())) {
            ((AfterCreate) pageObjectInstance).afterCreate();
        }
    }

    private static <T> T instantiatePage(Class<T> pageObjectClass, Page page, FieldDecorator decorator) {
        try {
            if (pageObjectClass.isAnnotationPresent(PageObject.class)) {
                T pageObjectInstance;
                try {
                    Constructor<T> constructor = pageObjectClass.getConstructor(Page.class);
                    pageObjectInstance = constructor.newInstance(page);
                } catch (NoSuchMethodException e) {
                    pageObjectInstance = pageObjectClass.getDeclaredConstructor().newInstance();
                }
                initElements(decorator, pageObjectInstance);
                callAfterCreateHook(pageObjectInstance);
                return pageObjectInstance;
            }
            throw new MissingPageObjectAnnotation("Only pages marked with @PageObject can can be created by the PageFactory.");

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initElements(FieldDecorator decorator, Object pageObjectInstance) {
        List<Class<?>> classes = new ArrayList<>();
        Class<?> pageObjectClass = pageObjectInstance.getClass();

        while (pageObjectClass != Object.class) {
            classes.add(pageObjectClass);
            pageObjectClass = pageObjectClass.getSuperclass();
        }

        // Initialize in reverse order so base classes get initialized first
        // so its fields are available to child classes
        Collections.reverse(classes);
        proxyFields(decorator, pageObjectInstance, classes);
    }

    private static void proxyFields(FieldDecorator decorator, Object pageObjectInstance, List<Class<?>> classes) {
        List<String> fieldNamesAlreadyProxied = new ArrayList<>();
        List<Field> fieldsWithDependencies = new ArrayList<>();

        for (Class<?> pageObjectClass : classes) {
            Field[] fields = pageObjectClass.getDeclaredFields();

            for (Field field : fields) {
                if (isALocator(field)) {
                    if (hasDependencies(field)) {
                        fieldsWithDependencies.add(field);
                        continue;
                    }
                    setField(decorator, field, pageObjectInstance);
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
                        setField(decorator, field, pageObjectInstance);
                        fieldNamesAlreadyProxied.add(field.getName());
                        proxiedScopedFields.add(field);
                    }
                }

                for (Field proxied : proxiedScopedFields) {
                    fieldsWithDependencies.remove(proxied);
                }

                if (sizeBefore == fieldsWithDependencies.size()) {
                    String fieldNames = fieldsWithDependencies.stream().map(Field::getName).collect(Collectors.joining(", "));
                    String message = String.format(
                            "\nUnable to find dependencies for the following Fields:\nPage Object: %s\nFields: %s",
                            pageObjectClass.getName(), fieldNames);

                    throw new InvalidParentLocatorException(message);
                }
            }
        }
    }

    private static boolean hasDependencies(Field field) {
        return field.isAnnotationPresent(Under.class);
    }

    private static boolean isALocator(Field field) {
        return field.isAnnotationPresent(Find.class);
    }

    private static void setField(FieldDecorator decorator, Field field, Object pageObjectInstance) {
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
