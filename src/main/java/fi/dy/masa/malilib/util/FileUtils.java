package fi.dy.masa.malilib.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableSet;
import com.mumfrey.liteloader.core.LiteLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.listener.ConfirmationListener;
import fi.dy.masa.malilib.overlay.message.MessageUtils;
import fi.dy.masa.malilib.util.consumer.StringConsumer;

public class FileUtils
{
    public static final FileFilter DIRECTORY_FILTER = (file) -> file.isDirectory() && file.getName().equals(".") == false && file.getName().equals("..") == false;
    public static final ImmutableSet<Character> ILLEGAL_CHARACTERS = ImmutableSet.of( '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' );

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
     * @param dir
     * @param fileName
     * @param canOverwrite
     * @return
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

    public static String getJoinedTrailingPathElements(File file, File rootPath, int maxStringLength, String separator)
    {
        StringBuilder path = new StringBuilder();

        if (maxStringLength <= 0)
        {
            return "...";
        }

        while (file != null)
        {
            String name = file.getName();

            if ((path.length() == 0) == false)
            {
                path.insert(0, name + separator);
            }
            else
            {
                path = new StringBuilder(name);
            }

            int len = path.length();

            if (len > maxStringLength)
            {
                path = new StringBuilder("... " + path.substring(len - maxStringLength, len));
                break;
            }

            if (file.equals(rootPath))
            {
                break;
            }

            file = file.getParentFile();
        }

        return path.toString();
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

    public static String getFileNameExtension(String name)
    {
        int i = name.lastIndexOf(".");
        return i != -1 && name.length() > 1 ? name.substring(i + 1) : name;
    }

    public static String getNameWithoutExtension(String name)
    {
        int i = name.lastIndexOf(".");
        return i != -1 && i != 0 ? name.substring(0, i) : name;
    }

    public static String generateSimpleSafeFileName(String name)
    {
        boolean endsInUnderscore = name.length() >= 1 && name.charAt(name.length() - 1) == '_';
        name = name.toLowerCase(Locale.US).replaceAll("\\W", "_").replaceAll("__", "_");

        // Remove the generated trailing underscore, if any
        if (endsInUnderscore == false && name.length() >= 2 && name.charAt(name.length() - 1) == '_')
        {
            name = name.substring(0, name.length() - 1);
        }

        return name;
    }

    public static String generateSafeFileName(String name)
    {
        StringBuilder sb = new StringBuilder(name.length());

        for (int i = 0; i < name.length(); ++i)
        {
            char c = name.charAt(i);

            if (ILLEGAL_CHARACTERS.contains(c) == false)
            {
                sb.append(c);
            }
        }

        // Some weird reserved windows keywords apparently... FFS >_>
        return sb.toString().replaceAll("COM", "").replaceAll("PRN", "");
    }

    /**
     * Checks if the given filename contains characters or strings that would be invalid in file names.
     * Most of these are just invalid on Windows...
     * @param filename
     * @return
     */
    public static boolean doesFilenameContainIllegalCharacters(String filename)
    {
        for (int i = 0; i < filename.length(); ++i)
        {
            char c = filename.charAt(i);

            if (ILLEGAL_CHARACTERS.contains(c))
            {
                return true;
            }
        }

        return filename.contains("COM") || filename.contains("PRN");
    }

    @Nullable
    public static NBTTagCompound readNBTFile(File file)
    {
        if (file.exists() && file.isFile() && file.canRead())
        {
            try
            {
                FileInputStream is = new FileInputStream(file);
                NBTTagCompound nbt = CompressedStreamTools.readCompressed(is);
                is.close();
                return nbt;
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.warn("Failed to read NBT data from file '{}'", file.getAbsolutePath());
            }
        }

        return null;
    }

    public static boolean createRollingBackup(File fileIn, File backupDirectory, int maxBackups, String suffix)
    {
        if (backupDirectory.isDirectory() == false && backupDirectory.mkdirs() == false)
        {
            MaLiLib.LOGGER.error("Failed to create the config backup directory '{}'", backupDirectory.getAbsolutePath());
            return false;
        }

        String name = fileIn.getName();
        String nameAndSuffix = name + suffix;
        File backupFile = new File(backupDirectory, nameAndSuffix + "1");

        // Only rotate the backups if the first name is in use
        if (backupFile.exists())
        {
            // If there are unused backup file names, only rotate the backups up to the first empty slot
            int firstEmptySlot = maxBackups;

            for (int i = 1; i <= maxBackups; ++i)
            {
                File tmp = new File(backupDirectory, nameAndSuffix + i);

                if (tmp.exists() == false)
                {
                    firstEmptySlot = i;
                    break;
                }
            }

            for (int i = firstEmptySlot; i > 1; --i)
            {
                File tmp1 = new File(backupDirectory, nameAndSuffix + (i - 1));
                File tmp2 = new File(backupDirectory, nameAndSuffix + i);

                if (tmp2.exists() && tmp2.isFile())
                {
                    if (tmp2.delete() == false)
                    {
                        MaLiLib.LOGGER.warn("Failed to delete backup file '{}'", tmp2.getAbsolutePath());
                    }
                }

                if (tmp1.exists() && tmp1.renameTo(tmp2) == false)
                {
                    MaLiLib.LOGGER.error("Failed to rename backup file '{}' to '{}'",
                                         tmp1.getAbsolutePath(), tmp2.getAbsolutePath());
                    return false;
                }
            }
        }

        if (fileIn.exists())
        {
            try
            {
                org.apache.commons.io.FileUtils.copyFile(fileIn, backupFile);
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.error("Failed to copy file '{}' to '{}'",
                                     fileIn.getAbsolutePath(), backupFile.getAbsolutePath(), e);
                return false;
            }
        }

        return true;
    }

    public static class FileRenamer implements StringConsumer
    {
        protected final File dir;
        protected final String oldName;

        public FileRenamer(File dir, String oldName)
        {
            this.dir = dir;
            this.oldName = oldName;
        }

        @Override
        public boolean consumeString(String string)
        {
            if (FileUtils.doesFilenameContainIllegalCharacters(string))
            {
                MessageUtils.error("malilib.message.error.illegal_characters_in_file_name", string);
                return false;
            }

            String newName = string;
            int indexExt = this.oldName.lastIndexOf('.');
            String ext = indexExt > 0 ? this.oldName.substring(indexExt) : null;

            if (ext != null && newName.endsWith(ext) == false)
            {
                newName = newName + ext;
            }

            File newFile = new File(this.dir, newName);

            if (newFile.exists() == false)
            {
                File oldFile = new File(this.dir, this.oldName);

                if (oldFile.exists() && oldFile.canRead() && oldFile.renameTo(newFile))
                {
                    return true;
                }
                else
                {
                    MessageUtils.error("malilib.message.error.file_rename.rename_failed", this.oldName, newName);
                }
            }
            else
            {
                MessageUtils.error("malilib.message.error.file_rename.file_already_exists", newName);
            }

            return false;
        }
    }

    public static class FileDeleter implements ConfirmationListener
    {
        protected final File file;

        public FileDeleter(File file)
        {
           this.file = file;
        }

        @Override
        public boolean onActionConfirmed()
        {
            try
            {
                return this.file.delete();
            }
            catch (Exception e)
            {
                MessageUtils.error("malilib.message.error.file_delete_failed", this.file.getAbsolutePath());
            }

            return false;
        }
    }
}
