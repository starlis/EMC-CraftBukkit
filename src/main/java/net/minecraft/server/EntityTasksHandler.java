package net.minecraft.server;

import com.empireminecraft.entityai.EntityTasksApi;
import org.bukkit.*;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.empireminecraft.entityai.EntityTasksApi.EntityTask;
import static java.util.Map.Entry;

public final class EntityTasksHandler {

    private EntityTasksHandler() {}

    public static void tickHandler(Entity entity) {
        if (entity.entityTasks.isEmpty()) {
            return;
        }
        int entityKey = entity.ticksLived - entity.getId();
        for (Entry<Integer, List<EntityTask>> entry : entity.entityTasks.entrySet()) {
            int runEvery = entry.getKey();
            if (entityKey % runEvery != 0 || entry.getValue().isEmpty()) {
                continue;
            }
            final Iterator<EntityTask> it = entry.getValue().iterator();
            while (it.hasNext()) {
                EntityTask task = it.next();
                if (task.limit > 0 && task.count++ > task.limit) {
                    it.remove();
                    break;
                }
                task.run(entity.getBukkitEntity());
            }
        }
    }

    public static void reload() {
        for (final World world : Bukkit.getWorlds()) {
            for (org.bukkit.entity.Entity entity : world.getEntities()) {
                EntityTasksApi.cancelTasks(entity);
            }
        }
    }

    public static class TaskList extends HashMap<Integer, List<EntityTask>> {

    }
}
