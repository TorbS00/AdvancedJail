package com.github.beastyboo.advancedjail.config;

import space.arim.dazzleconf.ConfigurationFactory;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.error.ConfigFormatSyntaxException;
import space.arim.dazzleconf.error.InvalidConfigException;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions;
import space.arim.dazzleconf.helper.ConfigurationHelper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

/**
 * Created by Torbie on 08.12.2020.
 */

public class YamlPortConfiguration<C> extends ConfigurationHelper<C> {

    private volatile C configData;

    /**
     * Creates from an enclosing directory, filename within that directory, and {@code ConfigurationFactory}. <br>
     * <br>
     * The configuration path will be located at <code>configFolder.resolve(fileName)</code>
     *
     * @param configFolder the enclosing directory
     * @param fileName     the filename within the directory
     * @param factory      the configuration factory
     */
    private YamlPortConfiguration(Path configFolder, String fileName, ConfigurationFactory<C> factory) {
        super(configFolder, fileName, factory);
    }

    public static <C> YamlPortConfiguration<C> create(Path configFolder, String fileName, Class<C> configClass) {
        SnakeYamlOptions yamlOptions = new SnakeYamlOptions.Builder()
                .useCommentingWriter(true)
                .build();
        return new YamlPortConfiguration<>(configFolder, fileName,
                new SnakeYamlConfigurationFactory<>(configClass, ConfigurationOptions.defaults(), yamlOptions));
    }

    public void reloadConfig() {
        try {
            configData = reloadConfigData();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);

        } catch (ConfigFormatSyntaxException ex) {
            configData = getFactory().loadDefaults();
            System.err.println("Uh-oh! The syntax of your configuration are invalid. "
                    + "Check your YAML syntax with a tool such as https://yaml-online-parser.appspot.com/");
            ex.printStackTrace();

        } catch (InvalidConfigException ex) {
            configData = getFactory().loadDefaults();
            System.err.println("Uh-oh! The values in your configuration are invalid. "
                    + "Check to make sure you have specified the right data types.");
            ex.printStackTrace();
        }
    }

    public C getConfigData() {
        C configData = this.configData;
        if (configData == null) {
            throw new IllegalStateException("Configuration has not been loaded yet");
        }
        return configData;
    }

}
