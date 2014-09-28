package net.njay.dynamicdatabase;

import net.njay.dynamicdatabase.config.Configuration;

import java.io.File;

/**
 * Class to hold any configuration options that need to be set before the project loads (Cannot be set externally after load).
 *
 * @author Austin Mayes
 */
public class ProjectConfiguration {
    private final boolean storeEmpties;
    private final boolean actLikeSerializer;
    private final boolean ignoreFinals;
    private final boolean storeNulls;
    private final Configuration projectConfig;
    private final File modulesContainer;

    public ProjectConfiguration(boolean storeEmpties, boolean actLikeSerializer, boolean ignoreFinals, boolean storeNulls, Configuration projectConfig, File modulesContainer) {
        this.storeEmpties = storeEmpties;
        this.actLikeSerializer = actLikeSerializer;
        this.ignoreFinals = ignoreFinals;
        this.storeNulls = storeNulls;
        this.projectConfig = projectConfig;
        this.modulesContainer = modulesContainer;
    }

    public boolean storeEmpties() {
        return storeEmpties;
    }

    public boolean actLikeSerializer() {
        return actLikeSerializer;
    }

    public boolean ignoreFinals() {
        return ignoreFinals;
    }

    public boolean storeNulls() {
        return storeNulls;
    }

    public Configuration getProjectConfig() {
        return projectConfig;
    }

    public File getModulesContainer() {
        return modulesContainer;
    }

    @Override
    public String toString() {
        return "ProjectConfiguration{" +
                "storeEmpties=" + storeEmpties +
                ", actLikeSerializer=" + actLikeSerializer +
                ", ignoreFinals=" + ignoreFinals +
                ", storeNulls=" + storeNulls +
                ", projectConfig=" + projectConfig +
                ", modulesContainer=" + modulesContainer +
                '}';
    }
}
