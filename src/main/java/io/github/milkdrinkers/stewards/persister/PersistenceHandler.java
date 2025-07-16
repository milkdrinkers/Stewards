package io.github.milkdrinkers.stewards.persister;

import net.citizensnpcs.api.persistence.PersistenceLoader;

import java.time.Instant;

/**
 * Registers type persisters with the citizens api.
 */
public class PersistenceHandler {
    static {
        PersistenceLoader.registerPersistDelegate(Instant.class, InstantTypePersister.class);
    }
}
