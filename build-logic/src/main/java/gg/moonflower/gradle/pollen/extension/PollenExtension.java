package gg.moonflower.gradle.pollen.extension;

import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

import javax.inject.Inject;

public class PollenExtension {

    private final Property<String> apiName;

    @Inject
    public PollenExtension(ObjectFactory factory, Project project) {
        this.apiName = factory.property(String.class);
        this.apiName.finalizeValueOnRead();
    }

    @Input
    public Property<String> getApiName() {
        return this.apiName;
    }

    public void setApiName(String name) {
        this.apiName.set(name);
    }
}
