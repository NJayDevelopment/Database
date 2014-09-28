package net.njay.dynamicdatabase.module.dao;

import com.google.common.collect.Maps;
import net.njay.dynamicdatabase.Document;
import net.njay.dynamicdatabase.DynamicDatabase;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * Service to get DAOs from external modules.
 *
 * @author Nick
 */
public class ExternalDAOService {

    private static Map<Class<? extends BasicDAO>, BasicDAO> loadedDaos = Maps.newHashMap();

    public static <T> T getDao(Class<? extends BasicDAO> daoClass, Class<? extends Document> docClass) {
        if (loadedDaos.containsKey(daoClass))
            return (T) loadedDaos.get(daoClass);
        try {
            Constructor constructor = daoClass.getDeclaredConstructor(Datastore.class);
            constructor.setAccessible(true);
            BasicDAO basicDAO = (BasicDAO) constructor.newInstance(DynamicDatabase.getInstance().getDatastores().get(docClass));
            loadedDaos.put(daoClass, basicDAO);
            return getDao(daoClass, docClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve DAO!");
        }
    }

}
