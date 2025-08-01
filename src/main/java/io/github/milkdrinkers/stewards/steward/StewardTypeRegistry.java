package io.github.milkdrinkers.stewards.steward;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A registry storing all types of stewards.
 */
public class StewardTypeRegistry implements Iterator<StewardType>, Iterable<StewardType> {
    private final Set<StewardType> registry = new HashSet<>();

    public StewardTypeRegistry() {
    }

    public void register(StewardType stewardType) {
        registry.add(stewardType);
    }

    public Map<String, StewardType> getAll() {
        return registry
            .stream()
            .collect(Collectors.toUnmodifiableMap(StewardType::id, type -> type));
    }

    public Set<String> getKeys() {
        return registry
            .stream()
            .map(StewardType::id)
            .collect(Collectors.toUnmodifiableSet());
    }

    public Set<StewardType> getValues() {
        return Set.copyOf(registry);
    }

    public @NotNull StewardType getType(String id) {
        return Objects.requireNonNull(
            registry
                .stream()
                .filter(type -> type.id().equals(id))
                .findAny()
                .orElse(null),
            "Steward type is null!"
        );
    }

    public boolean isRegistered(String key) {
        return registry.stream().anyMatch(type -> type.id().equals(key));
    }

    public boolean isRegistered(StewardType stewardType) {
        return registry.contains(stewardType);
    }

    protected void clear() {
        registry.clear();
    }

    @Override
    public @NotNull Iterator<StewardType> iterator() {
        return registry.iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator().hasNext();
    }

    @Override
    public StewardType next() {
        return iterator().next();
    }
}
