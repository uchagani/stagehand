package io.github.uchagani.stagehand;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.impl.LocatorAssertionsImpl;

import java.lang.reflect.Proxy;

public class Assertions {
    public static LocatorAssertions assertThat(Locator locator) {
        Locator proxiedLocator = ((LocatorHandler)Proxy.getInvocationHandler(locator)).getProxiedLocator();
        return new LocatorAssertionsImpl(proxiedLocator);
    }
}
