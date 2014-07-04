package us.nsakt.dynamicdatabase.util.config;

/**
 * Annotation to replace config code comments
 */
public @interface ConfigAnnotation {
    /**
     * The type of comment
     *
     * @return ConfigStructure type of comment
     */
    ConfigStructure type();

    /**
     * Description of this part of the Configuration's structure
     *
     * @return a description of the structure area
     */
    String desc();

    /**
     * The default value of the variable
     *
     * @return the default value
     */
    String def() default "";
}
