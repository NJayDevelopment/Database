package us.nsakt.dynamicdatabase.util;


import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.ServerOperator;


public class StarPermissibleBase extends PermissibleBase {

    public StarPermissibleBase(Object old, ServerOperator opable) {
        super(opable);
        ReflectionExecutor.ReflectionObject thiz = new ReflectionExecutor.ReflectionObject(this);
        ReflectionExecutor.ReflectionObject that = new ReflectionExecutor.ReflectionObject(old);
        thiz.set("attachments", that.get("attachments"));
        thiz.set("permissions", that.get("permissions"));
    }

    @Override
    public boolean hasPermission(String permission) {
        while (true) {
            if (super.hasPermission(permission))
                return true;

            if (permission.length() < 2)
                return false;

            if (permission.endsWith("*"))
                permission = permission.substring(0, permission.length() - 2);

            int lastIndex = permission.lastIndexOf(".");
            if (lastIndex < 0)
                return false;

            permission = permission.substring(0, lastIndex).concat(".*");
        }
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return hasPermission(permission.getName());
    }
}