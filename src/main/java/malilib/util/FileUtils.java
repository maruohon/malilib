package malilib.util;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import malilib.MaLiLib;
import malilib.overlay.message.MessageDispatcher;
import malilib.util.game.wrap.GameUtils;

public class FileUtils
{
    public static final Predicate<Path> DIRECTORY_FILTER = FileUtils::isRegularDirectory;
    public static final Predicate<Path> ALWAYS_FALSE_FILEFILTER = p -> false;
    public static final Predicate<Path> ANY_FILE_FILEFILTER = Files::isRegularFile;
    public static final Predicate<Path> JSON_FILEFILTER = (f) -> Files.isRegularFile(f) && f.getFileName().toString().endsWith(".json");

    public static Path getMinecraftDirectory()
    {
        return GameUtils.getClient().runDirectory.toPath();
    }

    public static Path getRootDirectory()
    {
        return Paths.get("/");
    }

    public static long size(Path file)
    {
        try
        {
            return Files.size(file);
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public static long getMTime(Path file)
    {
        try
        {
            return Files.getLastModifiedTime(file).toMillis();
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public static boolean createDirectoriesIfMissing(Path dir)
    {
        return createDirectoriesIfMissing(dir, MaLiLib.LOGGER::warn);
    }

    public static boolean createDirectoriesIfMissing(Path dir,
                                                     Consumer<String> messageConsumer)
    {
        return createDirectoriesIfMissing(dir, messageConsumer, "Failed to create the directory '%s'");
    }

    public static boolean createDirectoriesIfMissing(Path dir,
                                                     @Nullable Consumer<String> messageConsumer,
                                                     @Nullable String message)
    {
        try
        {
            if (Files.isDirectory(dir) == false)
            {
                Files.createDirectories(dir);
            }
        }
        catch (Exception e)
        {
            if (messageConsumer != null && message != null)
            {
                messageConsumer.accept(String.format(message, dir.toAbsolutePath()));
            }

            return false;
        }

        return Files.isDirectory(dir);
    }

    public static boolean createFile(Path file)
    {
        return createFile(file, MaLiLib.LOGGER::warn);
    }

    public static boolean createFile(Path file, Consumer<String> messageConsumer)
    {
        return createFile(file, messageConsumer, "Failed to create the file '%s'");
    }

    public static boolean createFile(Path file,
                                     @Nullable Consumer<String> messageConsumer,
                                     @Nullable String message)
    {
        try
        {
            Files.createFile(file);
            return true;
        }
        catch (Exception e)
        {
            if (messageConsumer != null && message != null)
            {
                messageConsumer.accept(String.format(message, file.toAbsolutePath()));
            }
        }

        return false;
    }

    public static boolean copy(Path srcFile, Path dstFile)
    {
        return copy(srcFile, dstFile, true);
    }

    public static boolean copy(Path srcFile, Path dstFile, boolean overwrite)
    {
        return copy(srcFile, dstFile, overwrite, MaLiLib.LOGGER::warn);
    }

    public static boolean copy(Path srcFile, Path dstFile, boolean overwrite, Consumer<String> messageConsumer)
    {
        try
        {
            if (overwrite)
            {
                Files.copy(srcFile, dstFile, StandardCopyOption.REPLACE_EXISTING);
            }
            else
            {
                Files.copy(srcFile, dstFile);
            }

            return true;
        }
        catch (Exception e)
        {
            messageConsumer.accept(String.format("Failed to copy file '%s' to '%s'\n%s",
                                                 srcFile.toAbsolutePath(), dstFile.toAbsolutePath(), e));
            return false;
        }
    }

    public static boolean move(Path srcFile, Path dstFile)
    {
        return move(srcFile, dstFile, true);
    }

    public static boolean move(Path srcFile, Path dstFile, boolean overwrite)
    {
        return move(srcFile, dstFile, overwrite, MaLiLib.LOGGER::warn);
    }

    public static boolean move(Path srcFile, Path dstFile, boolean overwrite, Consumer<String> messageConsumer)
    {
        try
        {
            if (overwrite)
            {
                Files.move(srcFile, dstFile, StandardCopyOption.REPLACE_EXISTING);
            }
            else
            {
                Files.move(srcFile, dstFile);
            }

            return true;
        }
        catch (Exception e)
        {
            messageConsumer.accept(String.format("Failed to move file '%s' to '%s'",
                                                 srcFile.toAbsolutePath(), dstFile.toAbsolutePath()));
            return false;
        }
    }

    public static boolean delete(Path file)
    {
        return delete(file, MaLiLib.LOGGER::warn);
    }

    public static boolean delete(Path file, Consumer<String> messageConsumer)
    {
        try
        {
            Files.delete(file);
            return true;
        }
        catch (Exception e)
        {
            messageConsumer.accept(String.format("Failed to delete file '%s'", file.toAbsolutePath()));
            return false;
        }
    }

    /**
     * Checks that the target directory exists, and the file either doesn't exist,
     * or the canOverwrite argument is true and the file is writable
     */
    public static boolean canWriteToFile(Path dir, String fileName, boolean canOverwrite)
    {
        if (Files.isDirectory(dir))
        {
            Path file = dir.resolve(fileName);
            return Files.exists(file) == false ||
                   (canOverwrite && Files.isRegularFile(file) && Files.isWritable(file));
        }

        return false;
    }

    public static boolean writeDataToFile(final Path file, Consumer<BufferedWriter> dataWriter)
    {
        Path dir = file.getParent();
        Path realPath = file;

        if (FileUtils.createDirectoriesIfMissing(dir) == false)
        {
            return false;
        }

        try
        {
            if (Files.exists(file))
            {
                realPath = file.toRealPath();
            }
        }
        catch (Exception e)
        {
            MessageDispatcher.error(8000).console(e).translate("malilibdev.message.error.failed_to_resolve_file",
                                                               file.toAbsolutePath());
            return false;
        }

        try
        {
            // Don't replace/override symbolic links, but just write to the pointed file directly
            if (Files.isSymbolicLink(file))
            {
                BufferedWriter writer = Files.newBufferedWriter(realPath, StandardCharsets.UTF_8);
                dataWriter.accept(writer);
                writer.close();
                return true;
            }
            // For non-symlinks, first write to a separate temporary file, and then rename it over the old file
            else
            {
                Path fileTmp = dir.resolve(realPath.getFileName() + ".tmp");

                if (Files.exists(fileTmp))
                {
                    fileTmp = realPath.getParent().resolve(UUID.randomUUID() + ".tmp");
                }

                BufferedWriter writer = Files.newBufferedWriter(fileTmp, StandardCharsets.UTF_8);
                dataWriter.accept(writer);
                writer.close();

                return FileUtils.move(fileTmp, file);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to write to file '{}'", file.toAbsolutePath(), e);
        }

        return false;
    }

    public static boolean isRegularDirectory(Path file)
    {
        String name = file.getFileName() + ""; // cheap & lazy NPE protection...
        return Files.isDirectory(file) && name.equals(".") == false && name.equals("..") == false;
    }

    public static boolean isCurrentOrParentDirectory(Path file)
    {
        String name = file.getFileName() + ""; // cheap & lazy NPE protection...
        return Files.isDirectory(file) && (name.equals(".") || name.equals(".."));
    }

    public static List<Path> getDirectoryContents(Path dir, Predicate<Path> filter, boolean sortByName)
    {
        List<Path> list = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir))
        {
            for (Path file : stream)
            {
                if (filter.test(file))
                {
                    list.add(file);
                }
            }

            if (sortByName)
            {
                list.sort(Comparator.comparing(Path::getFileName));
            }
        }
        catch (Exception ignore) {}

        return list;
    }

    public static List<Path> getSubDirectories(Path dir)
    {
        return getDirectoryContents(dir, FileUtils::isRegularDirectory, true);
    }

    public static boolean isDirectoryEmpty(Path dir)
    {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir))
        {
            for (Path file : stream)
            {
                if (isCurrentOrParentDirectory(file) == false)
                {
                    return false;
                }
            }
        }
        catch (Exception ignore) {}

        return true;
    }

    public static List<Path> getDirsForRootPath(Path dir, Path root)
    {
        List<Path> dirs = new ArrayList<>();

        while (dir != null && dir.startsWith(root))
        {
            dirs.add(dir);

            if (root.equals(dir))
            {
                break;
            }

            dir = dir.getParent();
        }

        return dirs;
    }

    public static List<Path> getSiblingDirs(Path dir)
    {
        List<Path> dirs = new ArrayList<>();
        Path parent = dir.getParent();

        if (parent != null)
        {
            dirs.addAll(getSubDirectories(parent));
            dirs.sort(Comparator.comparing(Path::getFileName));
        }

        return dirs;
    }

    public static boolean copyFilesToDirectory(Collection<Path> files,
                                               Path destinationDir,
                                               Consumer<String> messageConsumer)
    {
        boolean success = true;

        for (Path file : files)
        {
            if (copyFileToDirectory(file, destinationDir, messageConsumer) == false)
            {
                success = false;
            }
        }

        return success;
    }

    public static boolean copyFileToDirectory(Path sourceFile, Path destinationDir, Consumer<String> messageConsumer)
    {
        Path destinationFile = destinationDir.resolve(sourceFile.getFileName());
        return copyFile(sourceFile, destinationFile, messageConsumer);
    }

    public static boolean copyFile(Path sourceFile, Path destinationFile, Consumer<String> messageConsumer)
    {
        if (Files.exists(sourceFile))
        {
            if (Files.exists(destinationFile))
            {
                String msg = StringUtils.translate("malilibdev.message.error.failed_to_copy_file.destination_exists",
                                                   sourceFile.toAbsolutePath().toString(),
                                                   destinationFile.toAbsolutePath().toString());
                messageConsumer.accept(msg);
                return false;
            }

            try
            {
                Files.copy(sourceFile, destinationFile);
                return true;
            }
            catch (Exception e)
            {
                String msg = StringUtils.translate("malilibdev.message.error.failed_to_copy_file",
                                                   sourceFile.toAbsolutePath().toString(),
                                                   destinationFile.toAbsolutePath().toString());
                messageConsumer.accept(msg);
                messageConsumer.accept(e.getMessage());
            }
        }

        return false;
    }

    public static boolean moveFilesToDirectory(Collection<Path> files,
                                               Path destinationDir,
                                               Consumer<String> messageConsumer)
    {
        boolean success = true;

        for (Path file : files)
        {
            if (moveFileToDirectory(file, destinationDir, messageConsumer) == false)
            {
                success = false;
            }
        }

        return success;
    }

    public static boolean moveFileToDirectory(Path sourceFile, Path destinationDir, Consumer<String> messageConsumer)
    {
        Path destinationFile = destinationDir.resolve(sourceFile.getFileName());
        return moveFile(sourceFile, destinationFile, messageConsumer);
    }

    public static boolean moveFile(Path sourceFile, Path destinationFile, Consumer<String> messageConsumer)
    {
        if (Files.exists(sourceFile))
        {
            if (Files.exists(destinationFile))
            {
                String msg = StringUtils.translate("malilibdev.message.error.failed_to_move_file.destination_exists",
                                                   sourceFile.toAbsolutePath().toString(),
                                                   destinationFile.toAbsolutePath().toString());
                messageConsumer.accept(msg);
                return false;
            }

            try
            {
                Files.move(sourceFile, destinationFile);
                return true;
            }
            catch (Exception e)
            {
                String msg = StringUtils.translate("malilibdev.message.error.failed_to_move_file",
                                                   sourceFile.toAbsolutePath().toString(),
                                                   destinationFile.toAbsolutePath().toString());
                messageConsumer.accept(msg);
                messageConsumer.accept(e.getMessage());
            }
        }

        return false;
    }

    public static boolean renameFile(Path sourceFile, Path destinationFile, Consumer<String> messageConsumer)
    {
        if (Files.exists(sourceFile))
        {
            if (Files.exists(destinationFile))
            {
                messageConsumer.accept(StringUtils.translate("malilibdev.message.error.failed_to_rename_file.exists",
                                                             sourceFile.toAbsolutePath().toString(),
                                                             destinationFile.toAbsolutePath().toString()));
                return false;
            }

            try
            {
                return move(sourceFile, destinationFile);
            }
            catch (Exception e)
            {
                messageConsumer.accept(StringUtils.translate("malilibdev.message.error.failed_to_rename_file.exception",
                                                             sourceFile.toAbsolutePath().toString(),
                                                             destinationFile.toAbsolutePath().toString(),
                                                             e.getMessage()));
            }
        }

        return false;
    }

    public static boolean renameFileToName(Path oldFile, String newName, Consumer<String> messageConsumer)
    {
        if (FileNameUtils.doesFileNameContainIllegalCharacters(newName))
        {
            String key = "malilibdev.message.error.illegal_characters_in_file_name";
            messageConsumer.accept(StringUtils.translate(key, newName));
            return false;
        }

        String oldName = oldFile.getFileName().toString();
        int indexExt = oldName.lastIndexOf('.');
        String ext = indexExt > 0 ? oldName.substring(indexExt) : null;

        if (ext != null && newName.endsWith(ext) == false)
        {
            newName = newName + ext;
        }

        Path newFile = oldFile.getParent().resolve(newName);

        if (Files.exists(newFile) == false)
        {
            if (move(oldFile, newFile))
            {
                return true;
            }
            else
            {
                String key = "malilibdev.message.error.file_rename.rename_failed";
                messageConsumer.accept(StringUtils.translate(key, oldName, newName));
            }
        }
        else
        {
            String key = "malilibdev.message.error.failed_to_rename_file.exists";
            messageConsumer.accept(StringUtils.translate(key, oldName, newName));
        }

        return false;
    }

    public static boolean deleteFiles(Collection<Path> files, Consumer<String> messageConsumer)
    {
        boolean success = true;

        for (Path file : files)
        {
            if (Files.isDirectory(file))
            {
                String key = "malilibdev.message.error.failed_to_delete_file_is_dir";
                messageConsumer.accept(StringUtils.translate(key, file.getFileName().toString()));
                success = false;
                continue;
            }

            try
            {
                if (Files.deleteIfExists(file) == false)
                {
                    String key = "malilibdev.message.error.failed_to_delete_file";
                    messageConsumer.accept(StringUtils.translate(key, file.getFileName().toString()));
                    success = false;
                }
            }
            catch (Exception e)
            {
                String key = "malilibdev.message.error.failed_to_delete_file";
                messageConsumer.accept(StringUtils.translate(key, file.getFileName().toString()));
                messageConsumer.accept(e.getMessage());
                success = false;
            }
        }

        return success;
    }

    /**
     * Reads the given file as a String.
     * @param file the file to read
     * @param maxFileSize only read the file if it's at most this size. Use -1 for no limit.
     * @return the file contents as a String
     */
    @Nullable
    public static String readFileAsString(Path file, int maxFileSize)
    {
        if (Files.isReadable(file) &&
            (maxFileSize == -1 || FileUtils.size(file) <= maxFileSize))
        {
            try
            {
                return String.join("\n", Files.readAllLines(file));
            }
            catch (Exception ignore) {}
        }

        return null;
    }

    public static boolean writeStringToFile(String str, Path file, boolean override)
    {
        if (Files.isDirectory(file.getParent()) == false)
        {
            return false;
        }

        if (Files.exists(file) == false || (override && Files.isWritable(file)))
        {
            try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8))
            {
                writer.write(str);
                writer.close();
                return true;
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.warn("Failed to write string to file '{}'", file.toAbsolutePath(), e);
            }
        }

        return false;
    }
}
