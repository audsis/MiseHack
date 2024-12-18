package org.minecraft.wise.api.value.builder;

import org.minecraft.wise.api.feature.Feature;
import org.minecraft.wise.api.value.Value;

import java.util.function.Consumer;

public class ValueBuilder<Type>
        extends Value<Type> {
    Value<String> parent;
    String page;

    public ValueBuilder<Type> withDescriptor(String name, String tag) {
        setName(name);
        setTag(tag);
        return this;
    }

    public ValueBuilder<Type> withDescriptor(String name) {
        setName(name);
        String camelCase = name.replace(" ", "");
        char[] chars = camelCase.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        camelCase = new String(chars);
        setTag(camelCase);
        return this;
    }

    public ValueBuilder<Type> withValue(Type value) {
        setValue(value);
        return this;
    }

    public ValueBuilder<Type> withAction(Consumer<Value<Type>> action) {
        setAction(action);
        return this;
    }

    public ValueBuilder<Type> withRange(Type min, Type max) {
        setMin(min);
        setMax(max);
        return this;
    }

    public ValueBuilder<Type> withPageParent(Value<String> parent) {
        this.parent = parent;
        return this;
    }

    public ValueBuilder<Type> withPage(String page) {
        this.page = page;
        return this;
    }

    @Override
    public boolean isActive() {
        if (parent != null) {
            return parent.getValue().equals(page) && super.isActive();
        }
        return super.isActive();
    }

    public ValueBuilder<Type> withModes(String... modes) {
        setModes(modes);
        return this;
    }
//
//    public ValueBuilder<Type> withComponent(IComponent widget) {
//        if (!(widget instanceof ICustomComponent)) {
//            throw new IllegalArgumentException();
//        }
//        ICustomComponent comp = (ICustomComponent) widget;
//        comp.setValue(this);
//        setComponent(widget);
//        return this;
//    }
//
    public Value<Type> register(Feature feature) {
        feature.getValues().add(this);
        return this;
    }
}