//package gg.moonflower.pollen.api.config;
//
//import com.electronwill.nightconfig.core.CommentedConfig;
//import com.electronwill.nightconfig.core.ConfigSpec;
//import com.electronwill.nightconfig.core.UnmodifiableConfig;
//
//import java.util.List;
//
//public abstract class PollinatedConfig extends ConfigSpec {
//
//    public abstract void setConfig(CommentedConfig config);
//
//    public abstract boolean isCorrecting();
//
//    public abstract boolean isLoaded();
//
//    public abstract UnmodifiableConfig getSpec();
//
//    public abstract UnmodifiableConfig getValues();
//
//    public abstract void afterReload();
//
//    public abstract void save();
//
//    public abstract boolean isCorrect(CommentedConfig config);
//
//    public int correct(CommentedConfig config) {
//        return this.correct(config, (action, path, incorrectValue, correctedValue) -> {
//        }, null);
//    }
//
//    public abstract int correct(CommentedConfig config, ConfigSpec.CorrectionListener listener);
//
//    public abstract int correct(CommentedConfig config, ConfigSpec.CorrectionListener listener, ConfigSpec.CorrectionListener commentListener);
//
//    public interface ConfigValue<T> {
//        List<String> getPath();
//
//        T get();
//
//        PollinatedConfigBuilder next();
//
//        void save();
//
//        void set(T value);
//
//        void clearCache();
//    }
//}
