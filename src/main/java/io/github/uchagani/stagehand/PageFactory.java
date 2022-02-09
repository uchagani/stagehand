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
import java.util.Comparator;
import java.util.List;

public class PageFactory {
    public static <T> T create(Class<T> pageToCreate, Page page) {
        return instantiatePage(pageToCreate, page);
    }

    public static void initElements(Object pageObject, Page page) {
        if(pageObject.getClass().isAnnotationPresent(PageObject.class)) {
            initElements(new FieldDecorator(page), pageObject);
        } else {
            throw new MissingPageObjectAnnotation("Only pages marked with @PageObject can can be initialized by the PageFactory.");
        }
    }

    private static <T> T instantiatePage(Class<T> pageClassToProxy, Page page) {
        try {
            if(pageClassToProxy.isAnnotationPresent(PageObject.class)) {
                T pageObjectInstance;
                try {
                    Constructor<T> constructor = pageClassToProxy.getConstructor(Page.class);
                    pageObjectInstance = constructor.newInstance(page);
                } catch (NoSuchMethodException e) {
                    pageObjectInstance =  pageClassToProxy.getDeclaredConstructor().newInstance();
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
        Class<?> proxyIn = pageObjectInstance.getClass();
        while (proxyIn != Object.class) {
            proxyFields(decorator, pageObjectInstance, proxyIn);
            proxyIn = proxyIn.getSuperclass();
        }
    }

    private static void proxyFields(FieldDecorator decorator, Object page, Class<?> proxyIn) {
        Field[] fields = proxyIn.getDeclaredFields();
        List<String> proxiedFields = new ArrayList<>();
        List<Field> scopedFields = new ArrayList<>();

        for (Field field : fields) {
            if(isProxyable(field)) {
                if(hasDependencies(field)) {
                    scopedFields.add(field);
                    continue;
                }
                proxyField(decorator, field, page);
                proxiedFields.add(field.getName());
            }
        }

        int size = scopedFields.size();
        while(!scopedFields.isEmpty()) {
            List<Field> proxiedScopedFields = new ArrayList<>();
            for(Field field : scopedFields) {
                List<String> dependencies = Arrays.asList(field.getAnnotation(Under.class).value());
                if(proxiedFields.containsAll(dependencies)) {
                    proxyField(decorator, field, page);
                    proxiedFields.add(field.getName());
                    proxiedScopedFields.add(field);
                }
            }

            for(Field proxied : proxiedScopedFields) {
                scopedFields.remove(proxied);
            }

            if(size == scopedFields.size()) {
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

    private static void proxyField(FieldDecorator decorator, Field field, Object page) {
        Object value = decorator.decorate(page.getClass().getClassLoader(), field);
        if (value != null) {
            try {
                field.setAccessible(true);
                field.set(page, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
