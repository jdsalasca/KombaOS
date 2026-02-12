package com.kombaos.inventory.material.repository.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kombaos.config.KombaosProperties;
import com.kombaos.inventory.material.domain.Material;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileMaterialStoreTest {

    @Test
    void persistsToDisk(@TempDir Path tempDir) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        KombaosProperties props = new KombaosProperties();
        props.setEnvironment("local");
        props.setLocalStorageDir(tempDir.toString());

        FileMaterialStore store1 = new FileMaterialStore(mapper, props);
        Material created = store1.create("Algodón", "kg", "Proveedor", "CO", true, 2500L, "COP");

        FileMaterialStore store2 = new FileMaterialStore(mapper, props);
        var all = store2.list();

        assertEquals(1, all.size());
        assertEquals(created.id(), all.getFirst().id());
        assertEquals("Algodón", all.getFirst().name());
        assertTrue(tempDir.resolve("materials.json").toFile().exists());
    }
}
