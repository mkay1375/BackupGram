package ir.mkay.backupgram.repository;

import ir.mkay.backupgram.domain.persisted.Persistable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public abstract class BaseRepository<ENTITY extends Persistable, ID> {
    private static final Map<String, Map> GLOBAL_MAP = new ConcurrentHashMap<>(); // Don't use it directly; use getCurrentClassMap()

    public void add(List<ENTITY> entities) {
        entities.forEach(data -> getCurrentClassMap().put((ID) data.getId(), data));
    }

    public void add(ENTITY entity) {
        getCurrentClassMap().put((ID) entity.getId(), entity);
    }

    public Optional<ENTITY> find(ID id) {
        return Optional.ofNullable(getCurrentClassMap().get(id));
    }

    public Collection<ENTITY> findAll() {
        return getCurrentClassMap().values();
    }

    public boolean exists(ID id) {
        return getCurrentClassMap().containsKey(id);
    }

    public void forEach(BiConsumer<ID, ENTITY> action) {
        getCurrentClassMap().forEach(action);
    }

    public ENTITY removeById(ID id) {
        return getCurrentClassMap().remove(id);
    }

    public void clear() {
        getCurrentClassMap().clear();
    }

    public static void clearRepositoriesData() {
        GLOBAL_MAP.values().forEach(map -> map.clear());
    }

    private Map<ID, ENTITY> getCurrentClassMap() {
        String className = this.getClass().getName();

        if (GLOBAL_MAP.containsKey(className)) {
            return GLOBAL_MAP.get(className);
        } else {
            Map currentClassMap = new ConcurrentHashMap<ID, ENTITY>();
            GLOBAL_MAP.put(className, currentClassMap);
            return currentClassMap;
        }

    }


}
