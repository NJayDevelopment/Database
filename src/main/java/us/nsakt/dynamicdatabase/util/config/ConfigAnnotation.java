package us.nsakt.dynamicdatabase.util.config;

/**
 * Annotation to easily comment the configuration java file.
 *
 * @author skipperguy12
 */
public @interface ConfigAnnotation {
    ConfigStructure type();

    String desc();

    String def() default "";
}
