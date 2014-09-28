package net.njay.dynamicdatabase.module;

import net.njay.dynamicdatabase.Document;

import java.util.List;

/**
 * Class to represent a jar that is loaded externally.
 *
 * @author skipperguy12
 */
public abstract class ExternalModule {

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract List<Class<? extends Document>> getDocuments();

}
