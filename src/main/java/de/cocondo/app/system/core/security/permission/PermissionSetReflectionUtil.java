package de.cocondo.app.system.core.security.permission;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PermissionSetReflectionUtil {

    public static String[] getPermissionsFromClass(Class<?> permissionSetClass) {
        List<String> permissions = new ArrayList<>();
        Field[] fields = permissionSetClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == String.class && !field.getName().equals("SCOPE")) {
                try {
                    String permission = (String) field.get(null);
                    permissions.add(permission);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return permissions.toArray(new String[0]);
    }

}
