package net.njay.dynamicdatabase.module;

import net.njay.dynamicdatabase.documents.Document;

import java.util.List;

public abstract class ExternalModule {

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract List<Class<? extends Document>> getDocuments();

}
