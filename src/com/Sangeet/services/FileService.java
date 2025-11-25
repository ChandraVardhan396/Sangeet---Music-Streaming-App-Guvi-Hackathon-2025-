package com.Sangeet.services;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * Helper to copy file into musics folder with a unique filename.
 */
public class FileService {
    /**
     * Copy sourceFile (Path) into destFolder and produce a safe filename (timestamp + uuid + original ext).
     * Returns new filename (not full path).
     */
    public static String copyToFolder(Path sourceFile, Path destFolder) throws IOException {
        if (!Files.exists(sourceFile)) throw new IOException("Source file not found: " + sourceFile);
        Files.createDirectories(destFolder);

        String original = sourceFile.getFileName().toString();
        String ext = "";
        int idx = original.lastIndexOf('.');
        if (idx >= 0) ext = original.substring(idx);

        String safeName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0,8) + ext;
        Path dest = destFolder.resolve(safeName);
        Files.copy(sourceFile, dest, StandardCopyOption.REPLACE_EXISTING);
        return safeName;
    }
}
