package net.njay.dynamicdatabase.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to hold data about which sub-section a configuration class is used for.
 *
 * @author Austin Mayes
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationDefinition {
    String value();
}
