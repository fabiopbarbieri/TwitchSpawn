package net.programmer.igoodie.twitchspawn.udl;

import net.programmer.igoodie.twitchspawn.TwitchSpawn;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class FileUtil {
    private FileUtil() {
    }

    public static void renameFile(File from, File to, String errorMessage, String streamerName) {
        try {
            Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(from.toPath());
        } catch (Exception e) {
            TwitchSpawn.LOGGER.error(errorMessage, streamerName);
            e.printStackTrace();
        }
    }
}
