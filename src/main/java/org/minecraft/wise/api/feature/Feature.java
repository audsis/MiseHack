package org.minecraft.wise.api.feature;

import org.minecraft.wise.api.event.bus.Bus;
import org.minecraft.wise.api.config.ISavable;
import org.minecraft.wise.api.management.FeatureManager;
import org.minecraft.wise.api.management.SavableManager;
import org.minecraft.wise.api.utils.render.animations.Animation;
import org.minecraft.wise.api.utils.render.animations.Easings;
import org.minecraft.wise.api.value.Value;
import org.minecraft.wise.api.value.builder.ValueBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Feature
        implements ISavable {
    public final Value<Boolean> visible;
    public final Value<String> displayName;
    public float offset;
    public boolean sliding;
    String name;
    boolean enabled;
    FeatureType type;
    List<Value<?>> values;
    Category category;
    private String description;
    public final Animation animation = new Animation(Easings.EASE_OUT_QUAD, 250);
    public final Animation width = new Animation(Easings.EASE_OUT_QUAD, 250);

    public Feature(String name, Category category, FeatureType type) {
        this.name = name;
        enabled = false;
        this.type = type;
        values = new ArrayList<>();
        this.category = category;
        SavableManager.INSTANCE.getSavables().add(this);
        visible = new ValueBuilder<Boolean>().withDescriptor("Visible").withValue(true).register(this);
        displayName = new ValueBuilder<String>().withDescriptor("Name").withValue(getName()).register(this);
        description = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName.getValue();
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void onEnable() {
        Bus.EVENT_BUS.register(this);
    }

    public void onDisable() {
        Bus.EVENT_BUS.unregister(this);
    }

    public void toggle() {
        setEnabled(!isEnabled());
    }

    public FeatureType getType() {
        return type;
    }

    public void setType(FeatureType type) {
        this.type = type;
    }

    public List<Value<?>> getValues() {
        return values;
    }

    public void setValues(List<Value<?>> values) {
        this.values = values;
    }

    public String getHudInfo() {
        return "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        description = desc;
    }

    @Override
    public void load(Map<String, Object> objects) {
        Object e = objects.get("enabled");
        if (e != null) {
            setEnabled((Boolean) e);
        }
        setEnabled(objects.get("enabled") != null ? (Boolean) objects.get("enabled") : isEnabled());

        for (Value<?> item : getValues()) {
            Value value = item;
            Object o = objects.get(value.getTag());
            if (o != null) {
                try {
                    if (value.getValue() instanceof Color) {
                        Map<String, Object> map = (Map) o;
                        Color colorVal = new Color((Integer) map.get("red"), (Integer) map.get("green"), (Integer) map.get("blue"), (Integer) map.get("alpha"));
                        value.setValue(colorVal);
                    } else {
                        value.setValue(o);
                    }
                } catch (Exception ec) {
                    throw new RuntimeException(ec);
                }
            }
        }
    }

    @Override
    public Map<String, Object> save() {
        HashMap<String, Object> toSave = new HashMap<>();
        toSave.put("enabled", enabled);
        for (Value<?> value : getValues()) {
            if (value.getValue() instanceof Color) {
                HashMap<String, Integer> color = new HashMap<>();
                color.put("red", ((Color) value.getValue()).getRed());
                color.put("green", ((Color) value.getValue()).getGreen());
                color.put("blue", ((Color) value.getValue()).getBlue());
                color.put("alpha", ((Color) value.getValue()).getAlpha());
                toSave.put(value.getTag(), color);
                continue;
            }
            toSave.put(value.getTag(), value.getValue());
        }
        return toSave;
    }

    @Override
    public String getFileName() {
        return getName() + ".yml";
    }

    @Override
    public String getDirName() {
        return "features";
    }

    public enum Category {
        Client,
        Combat,
        Movement,
        Player,
        Misc,
        Render,
        Hud;

        public List<Feature> getModules() {
            List<Feature> modules = new ArrayList<>();
            for (Feature module : FeatureManager.INSTANCE.getFeatures()) {
                if (module.getCategory() == this) {
                    modules.add(module);
                }
            }
            return modules;
        }


    }

    public enum FeatureType {
        Module,
        Hud
    }
}