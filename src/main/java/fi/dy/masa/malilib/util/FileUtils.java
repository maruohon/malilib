package fi.dy.masa.malilib.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import com.mumfrey.liteloader.core.LiteLoader;
import net.minecraft.client.Minecraft;

public class FileUtils
{
    public static final FileFilter DIRECTORY_FILTER = (file) -> file.isDirectory() && file.getName().equals(".") == false && file.getName().equals("..") == false;

    public static File getConfigDirectory()
    {
        return LiteLoader.getCommonConfigFolder();
    }

    public static File getMinecraftDirectory()
    {
        return Minecraft.getMinecraft().gameDir;
    }

    /**
     * Checks that the target directory exists, and the file either doesn't exist,
     * or the canOverwrite argument is true and the file is writable
     */
    public static boolean canWriteToFile(File dir, String fileName, boolean canOverwrite)
    {
        if (dir.exists() && dir.isDirectory())
        {
            File file = new File(dir, fileName);
            return file.exists() == false || (canOverwrite && file.isFile() && file.canWrite());
        }

        return false;
    }

    public static File getCanonicalFileIfPossible(File file)
    {
        try
        {
            File fileCan = file.getCanonicalFile();

            if (fileCan != null)
            {
                file = fileCan;
            }
        }
        catch (IOException ignore)
        {
        }

        return file;
    }

    public static List<File> getSubDirectories(File dir)
    {
        return Arrays.asList(dir.listFiles(DIRECTORY_FILTER));
    }

    public static List<File> getDirsForRootPath(File dir, File root)
    {
        List<File> dirs = new ArrayList<>();
        int rootPathStrLen = root.getAbsolutePath().length();

        while (dir != null && dir.getAbsolutePath().length() >= rootPathStrLen)
        {
            dirs.add(dir);

            if (root.equals(dir))
            {
                break;
            }

            dir = dir.getParentFile();
        }

        return dirs;
    }

    public static List<File> getSiblingDirs(File dir)
    {
        List<File> dirs = new ArrayList<>();
        File parent = dir.getParentFile();

        if (parent != null)
        {
            dirs.addAll(getSubDirectories(parent));
            dirs.sort(Comparator.comparing(File::getName));
        }

        return dirs;
    }

    public static boolean copyFilesToDirectory(Collection<File> files,
                                               File destinationDir,
                                               Consumer<String> messageConsumer)
    {
        boolean success = true;

        for (File file : files)
        {
            if (copyFileToDirectory(file, destinationDir, messageConsumer) == false)
            {
                success = false;
            }
        }

        return success;
    }

    public static boolean copyFileToDirectory(File sourceFile, File destinationDir, Consumer<String> messageConsumer)
    {
        File destinationFile = new File(destinationDir, sourceFile.getName());
        return copyFile(sourceFile, destinationFile, messageConsumer);
    }

    public static boolean copyFile(File sourceFile, File destinationFile, Consumer<String> messageConsumer)
    {
        if (sourceFile.exists())
        {
            if (destinationFile.exists())
            {
                String msg = StringUtils.translate("malilib.message.error.failed_to_copy_file.destination_exists",
                                                   sourceFile.getAbsolutePath(), destinationFile.getAbsolutePath());
                messageConsumer.accept(msg);
                return false;
            }

            try
            {
                org.apache.commons.io.FileUtils.copyFile(sourceFile, destinationFile);
                return true;
            }
            catch (Exception e)
            {
                String msg = StringUtils.translate("malilib.message.error.failed_to_copy_file",
                                                   sourceFile.getAbsolutePath(), destinationFile.getAbsolutePath());
                messageConsumer.accept(msg);
                messageConsumer.accept(e.getMessage());
            }
        }

        return false;
    }

    public static boolean moveFilesToDirectory(Collection<File> files,
                                               File destinationDir,
                                               Consumer<String> messageConsumer)
    {
        boolean success = true;

        for (File file : files)
        {
            if (moveFileToDirectory(file, destinationDir, messageConsumer) == false)
            {
                success = false;
            }
        }

        return success;
    }

    public static boolean moveFileToDirectory(File sourceFile, File destinationDir, Consumer<String> messageConsumer)
    {
        File destinationFile = new File(destinationDir, sourceFile.getName());
        return moveFile(sourceFile, destinationFile, messageConsumer);
    }

    public static boolean moveFile(File sourceFile, File destinationFile, Consumer<String> messageConsumer)
    {
        if (sourceFile.exists())
        {
            if (destinationFile.exists())
            {
                String msg = StringUtils.translate("malilib.message.error.failed_to_move_file.destination_exists",
                                                   sourceFile.getAbsolutePath(), destinationFile.getAbsolutePath());
                messageConsumer.accept(msg);
                return false;
            }

            try
            {
                org.apache.commons.io.FileUtils.moveFile(sourceFile, destinationFile);
                return true;
            }
            catch (Exception e)
            {
                String msg = StringUtils.translate("malilib.message.error.failed_to_move_file",
                                                   sourceFile.getAbsolutePath(), destinationFile.getAbsolutePath());
                messageConsumer.accept(msg);
                messageConsumer.accept(e.getMessage());
            }
        }

        return false;
    }

    public static boolean renameFile(File sourceFile, File destinationFile, Consumer<String> messageConsumer)
    {
        if (sourceFile.exists())
        {
            if (destinationFile.exists())
            {
                messageConsumer.accept(StringUtils.translate("malilib.message.error.failed_to_rename_file_exists",
                                                             sourceFile.getName(), destinationFile.getName()));
                return false;
            }

            try
            {
                return sourceFile.renameTo(destinationFile);
            }
            catch (Exception e)
            {
                messageConsumer.accept(StringUtils.translate("malilib.message.error.failed_to_rename_file_exception",
                                                             sourceFile.getName(), destinationFile.getName(),
                                                             e.getMessage()));
            }
        }

        return false;
    }

    public static boolean deleteFiles(Collection<File> files, Consumer<String> messageConsumer)
    {
        boolean success = true;

        for (File file : files)
        {
            if (file.isDirectory())
            {
                messageConsumer.accept(StringUtils.translate("malilib.message.error.failed_to_delete_file_is_dir",
                                                             file.getName()));
                success = false;
                continue;
            }

            try
            {
                if (Files.deleteIfExists(file.toPath()) == false)
                {
                    messageConsumer.accept(StringUtils.translate("malilib.message.error.failed_to_delete_file",
                                                                 file.getName()));
                    success = false;
                }
            }
            catch (Exception e)
            {
                messageConsumer.accept(StringUtils.translate("malilib.message.error.failed_to_delete_file",
                                                             file.getName()));
                messageConsumer.accept(e.getMessage());
                success = false;
            }
        }

        return success;
    }
}
