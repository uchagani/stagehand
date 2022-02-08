package io.github.uchagani.stagehand;

import com.microsoft.playwright.Page;
import io.github.uchagani.stagehand.annotations.PageObject;
import io.github.uchagani.stagehand.exeptions.MissingPageObjectAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

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
        for (Field field : fields) {
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
}
