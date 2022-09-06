package gg.moonflower.gradle.pollen.extension;

import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

import javax.inject.Inject;
import java.io.Serial;
import java.io.Serializable;

public class PollenExtension implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    private final Property<String> apiName;
    private final ListProperty<String> moduleDependencies;

    @Inject
    public PollenExtension(ObjectFactory factory, Project project) {
        this.apiName = factory.property(String.class);
        this.apiName.finalizeValueOnRead();
        this.moduleDependencies = factory.listProperty(String.class);
        this.moduleDependencies.finalizeValueOnRead();
    }

    public void moduleDependency(String module) {
        this.moduleDependencies.add(module);
    }

    @Input
    public ListProperty<String> getModuleDependencies() {
        return moduleDependencies;
    }

    public void setModuleDependencies(Iterable<String> list) {
        this.moduleDependencies.set(list);
    }


    @Input
    public Property<String> getApiName() {
        return apiName;
    }

    public void setApiName(String name) {
        this.apiName.set(name);
    }
}
