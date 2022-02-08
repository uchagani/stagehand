package io.github.uchagani.stagehand;

import com.microsoft.playwright.Locator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LocatorHandler implements InvocationHandler {
    Locator locator;

    public LocatorHandler(Locator locator) {
        this.locator = locator;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("toString".equals(method.getName())) {
            return "Proxy locator for: " + locator.toString();
        }

        try {
            return method.invoke(locator, args);
        } catch (InvocationTargetException e) {
            // Unwrap the underlying exception
            throw e.getCause();
        }
    }
}
