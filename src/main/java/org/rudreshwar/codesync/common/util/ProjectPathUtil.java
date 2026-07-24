package org.rudreshwar.codesync.common.util;

import org.rudreshwar.codesync.projectitem.entity.ProjectItem;

public final class ProjectPathUtil {

    private ProjectPathUtil() {}

    public static String buildPath(ProjectItem item) {
        StringBuilder path = new StringBuilder(item.getName());
        ProjectItem parent = item.getParent();
        while (parent != null) {
            path.insert(0, parent.getName() + "/");
            parent = parent.getParent();
        }
        return path.toString();
    }

}