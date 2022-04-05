package io.github.uchagani.stagehand;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class LocatorFieldDecorator implements FieldDecorator {
    Page page;
    LocatorFactory locatorFactory;

    public LocatorFieldDecorator(Page page) {
        this.page = page;
        this.locatorFactory = new LocatorFactory(page);
    }

    @Override
    public Object decorate(Field field, Object pageObjectInstance) {
        Locator locator = locatorFactory.createLocator(field, pageObjectInstance);
        ClassLoader loader = pageObjectInstance.getClass().getClassLoader();
        return proxyForLocator(loader, locator);
    }

    protected Locator proxyForLocator(ClassLoader loader, Locator locator) {
        InvocationHandler handler = new LocatorHandler(locator);

        Locator proxy;
        proxy = (Locator) Proxy.newProxyInstance(
                loader, new Class[]{Locator.class}, handler);
        return proxy;
    }
}
