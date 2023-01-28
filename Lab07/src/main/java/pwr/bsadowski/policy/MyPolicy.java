package pwr.bsadowski.policy;

import java.net.SocketPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.util.PropertyPermission;

public class MyPolicy extends Policy {

    private static PermissionCollection perms;

    public MyPolicy() {
        super();
        if (perms == null) {
            perms = new MyPermissionCollection();
            addPermissions();
        }
    }

    @Override
    public PermissionCollection getPermissions(CodeSource codesource) {
        return perms;
    }

    private void addPermissions() {
        SocketPermission socketPermission = new SocketPermission("*:1024-65535", "connect, resolve");
        PropertyPermission propertyPermission = new PropertyPermission("*", "read, write");

        perms.add(socketPermission);
        perms.add(propertyPermission);
    }

}
