package io.github.uchagani.stagehand;

import java.lang.reflect.Field;

public interface FieldDecorator {
    Object decorate(Field field, Object pageObjectInstance);
}
