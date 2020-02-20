package fi.dy.masa.malilib.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import com.mumfrey.liteloader.core.LiteLoader;
import fi.dy.masa.malilib.LiteModMaLiLib;
import fi.dy.masa.malilib.gui.util.Message.MessageType;
import fi.dy.masa.malilib.interfaces.IConfirmationListener;
import fi.dy.masa.malilib.interfaces.IStringConsumerFeedback;

public class FileUtils
{
    public static final FileFilter DIRECTORY_FILTER = (file) -> { return file.isDirectory() && file.getName().equals(".") == false && file.getName().equals("..") == false; };
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
        catch (IOException e)
        {
        }

        return file;
    }

    public static String getJoinedTrailingPathElements(File file, File rootPath, int maxStringLength, String separator)
    {
        String path = "";

        if (maxStringLength <= 0)
        {
            return "...";
        }

        while (file != null)
        {
            String name = file.getName();

            if (path.isEmpty() == false)
            {
                path = name + separator + path;
            }
            else
            {
                path = name;
            }

            int len = path.length();

            if (len > maxStringLength)
            {
                path = "... " + path.substring(len - maxStringLength, len);
                break;
            }

            if (file.equals(rootPath))
            {
                break;
            }

            file = file.getParentFile();
        }

        return path;
    }

    public static List<File> getSubDirectories(File dir)
    {
        List<File> dirs = new ArrayList<>();

        for (File file : dir.listFiles(DIRECTORY_FILTER))
        {
            dirs.add(file);
        }

        return dirs;
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
            Collections.sort(dirs, (d1, d2) -> d1.getName().compareTo(d2.getName()));
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

        return filename.indexOf("COM") != -1 || filename.indexOf("PRN") != -1;
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
                LiteModMaLiLib.logger.warn("Failed to read NBT data from file '{}'", file.getAbsolutePath());
            }
        }

        return null;
    }

    public static class FileRenamer implements IStringConsumerFeedback
    {
        protected final File dir;
        protected final String oldName;

        public FileRenamer(File dir, String oldName)
        {
            this.dir = dir;
            this.oldName = oldName;
        }

        @Override
        public boolean setString(String string)
        {
            if (FileUtils.doesFilenameContainIllegalCharacters(string))
            {
                InfoUtils.showGuiOrInGameMessage(MessageType.ERROR, "malilib.error.illegal_characters_in_file_name", string);
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
                    InfoUtils.showGuiOrInGameMessage(MessageType.ERROR, "malilib.error.file_rename.rename_failed", this.oldName, newName);
                }
            }
            else
            {
                InfoUtils.showGuiOrInGameMessage(MessageType.ERROR, "malilib.error.file_rename.file_already_exists", newName);
            }

            return false;
        }
    }

    public static class FileDeleter implements IConfirmationListener
    {
        protected final File file;

        public FileDeleter(File file)
        {
           this.file = file;
        }

        @Override
        public boolean onActionCancelled()
        {
            return false;
        }

        @Override
        public boolean onActionConfirmed()
        {
            try
            {
                this.file.delete();
                return true;
            }
            catch (Exception e)
            {
                InfoUtils.showGuiOrInGameMessage(MessageType.ERROR, "malilib.error.file_delete_failed", this.file.getAbsolutePath());
            }

            return false;
        }
    }
}
