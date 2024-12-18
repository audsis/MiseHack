package org.minecraft.wise.api.management;

import org.minecraft.wise.api.feature.Feature;

import java.util.ArrayList;
import java.util.List;

public class FeatureManager {
    public static FeatureManager INSTANCE;
    List<Feature> features = new ArrayList<>();

    public List<Feature> getFeatures() {
        return this.features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public Feature getFeature(Class<?> feature) {
        return this.features.stream()
                .filter(feat -> feat.getClass().equals(feature))
                .findFirst()
                .orElse(null);
    }

    public List<Feature> getFeaturesFromCategory(Feature.Category category) {
        return features.stream()
                .filter(module -> module.getCategory().name().equals(category.name()))
                .toList();
    }
}