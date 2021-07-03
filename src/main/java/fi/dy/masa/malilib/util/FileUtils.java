package fi.dy.masa.malilib.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.apache.commons.codec.digest.DigestUtils;
import com.google.common.collect.ImmutableSet;
import com.mumfrey.liteloader.core.LiteLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.listener.ConfirmationListener;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
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
        return i != -1 && name.length() > 1 ? name.substring(i + 1) : "";
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

    public static String getPrettyFileSizeText(long fileSize, int decimalPlaces)
    {
        String[] units = {"B", "KiB", "MiB", "GiB", "TiB"};
        String unitStr = "";
        double size = fileSize;

        for (String unit : units)
        {
            unitStr = unit;

            if (size < 1024.0)
            {
                break;
            }

            size /= 1024.0;
        }

        String fmt = "%." + decimalPlaces + "f %s";
        return String.format(fmt, size, unitStr);
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

    /**
     * Creates a rolling backup copy of the given file <b>fileIn</b>.
     * The backup copies are only rotated up to the first possible empty slot/backup name.
     * If <b>antiDuplicate</b> is true, then the current file is first compared to all the
     * existing backups (up to the <b>maxBackups</b> count/name), and if an identical backup file
     * is found (by file size and SHA-1 hash), then that older identical copy is renamed as backup 1,
     * and the current file is not copied again.
     * @param fileIn the file that is being backed up
     * @param backupDirectory the directory in which the backup copies will be created
     * @param suffix the backup suffix to use. The backups will be named in the format '<filename><suffix><number>',
     *               for example if the suffix is '.bak_', then the name will be 'foo.json.bak_01'
     * @param maxBackups the maximum number of backup copies to keep. The files are rotated up to that count/name.
     * @param antiDuplicate if true, the old backups are checked for an existing identical copy first
     * @return true if the file was successfully copied in a backup file
     */
    public static boolean createRollingBackup(File fileIn, File backupDirectory, String suffix,
                                              int maxBackups, boolean antiDuplicate)
    {
        if (backupDirectory.isDirectory() == false && backupDirectory.mkdirs() == false)
        {
            MaLiLib.LOGGER.error("Failed to create the config backup directory '{}'", backupDirectory.getAbsolutePath());
            return false;
        }

        final int numberLength = (int) Math.ceil(Math.log10(maxBackups));
        final String formatString = "%s%0" + numberLength + "d";
        final String name = fileIn.getName();
        final String nameAndSuffix = name + suffix;
        final File backupFile = new File(backupDirectory, String.format(formatString, nameAndSuffix, 1));
        @Nullable File existingIdenticalBackup = null;
        boolean foundExistingBackup = false;

        if (antiDuplicate)
        {
            List<File> identicalBackups = findIdenticalBackupFiles(backupDirectory, fileIn, maxBackups, suffix);

            if (identicalBackups.isEmpty() == false)
            {
                File tmpFile = identicalBackups.get(0);

                // If the existing identical backup was already the first/latest backup file,
                // then there is nothing more to do
                if (tmpFile.equals(backupFile))
                {
                    return true;
                }

                String tmpName = name + UUID.randomUUID();
                existingIdenticalBackup = new File(backupDirectory, tmpName);

                // Move the existing identical backup to a temporary name.
                // Then rotate the files from 1 up to that existing name,
                // and finally move that existing backup as the latest backup file.
                if (existingIdenticalBackup.exists() == false && tmpFile.renameTo(existingIdenticalBackup))
                {
                    foundExistingBackup = true;
                }
            }
        }

        // Only rotate the backups if the first name is in use
        if (backupFile.exists() && rotateNumberedFiles(backupDirectory, nameAndSuffix, formatString, maxBackups) == false)
        {
            return false;
        }

        if (foundExistingBackup)
        {
            if (existingIdenticalBackup.renameTo(backupFile))
            {
                return true;
            }

            MaLiLib.LOGGER.error("Failed to rename backup file '{}' to '{}'",
                                 existingIdenticalBackup.getAbsolutePath(), backupFile.getAbsolutePath());
        }

        return copyFile(fileIn, backupFile);
    }

    /**
     * Rotates numbered files, by moving/renaming all the files in the range 1 .. maxCount.
     * The renaming starts from the last file in the range, proceeding backward until file 1.
     * Thus if there was file with the number maxCount, that file will be deleted.
     * Note however that non-existing names are checked for first, and if a free name is found,
     * then only files from 1 to that first free name are rotated.
     * @param dir the directory inside which the files are
     * @param baseFileName the base part of the file name, ie. everything except the number
     * @param nameFormatString the format string for {@link String#format(String, Object...)} to turn
     *                         the base file name and the number into the full file name.
     *                         Usually this would be something like '%s%02d'.
     * @param maxCount the number of files to rotate. The file with the number maxCount will be deleted,
     *                 if it exists, while all files before that will be renamed to one larger, but only from
     *                 file 1 up to the first possible empty name.
     * @return true if the operation was fully successful
     */
    public static boolean rotateNumberedFiles(File dir, String baseFileName, String nameFormatString, int maxCount)
    {
        // If there are unused backup file names, only rotate the backups up to the first empty slot
        int firstEmptySlot = maxCount;

        for (int i = 1; i <= maxCount; ++i)
        {
            File tmp = new File(dir, String.format(nameFormatString, baseFileName, i));

            if (tmp.exists() == false)
            {
                firstEmptySlot = i;
                break;
            }
        }

        File tmp1 = new File(dir, String.format(nameFormatString, baseFileName, firstEmptySlot));

        for (int i = firstEmptySlot; i > 1; --i)
        {
            File tmp2 = tmp1;
            tmp1 = new File(dir, String.format(nameFormatString, baseFileName, i - 1));

            if (tmp2.exists() && tmp2.isFile())
            {
                if (tmp2.delete() == false)
                {
                    MaLiLib.LOGGER.warn("Failed to delete file '{}'", tmp2.getAbsolutePath());
                }
            }

            if (tmp1.exists() && tmp1.renameTo(tmp2) == false)
            {
                MaLiLib.LOGGER.error("Failed to rename file '{}' to '{}'",
                                     tmp1.getAbsolutePath(), tmp2.getAbsolutePath());
                return false;
            }
        }

        return true;
    }

    public static boolean createBackupFileForVersion(File fileIn, File backupDirectory, int configVersion)
    {
        if (backupDirectory.isDirectory() == false && backupDirectory.mkdirs() == false)
        {
            MaLiLib.LOGGER.error("Failed to create the config backup directory '{}'", backupDirectory.getAbsolutePath());
            return false;
        }

        String fullName = fileIn.getName();
        String name = getNameWithoutExtension(fullName);
        String dateStr = getDateTimeString();
        String extension = getFileNameExtension(fullName);
        String backupFileName = name + "_v" + configVersion + "_" + dateStr;

        if (extension.length() > 0)
        {
            backupFileName += "." + extension;
        }

        return copyFile(fileIn, new File(backupDirectory, backupFileName));
    }

    public static List<File> findIdenticalBackupFiles(File backupDirectory, File fileIn, int maxBackups, String suffix)
    {
        final List<File> files = new ArrayList<>();
        final long fileSize = fileIn.length();
        final int numberLength = (int) Math.ceil(Math.log10(maxBackups));
        final String formatString = "%s%0" + numberLength + "d";
        final String nameAndSuffix = fileIn.getName() + suffix;
        final MessageDigest digest = DigestUtils.getSha1Digest();
        @Nullable String currentHash = null;

        for (int i = 1; i <= maxBackups; ++i)
        {
            File tmp = new File(backupDirectory, String.format(formatString, nameAndSuffix, i));

            if (tmp.exists() && tmp.isFile() && tmp.length() == fileSize)
            {
                // lazy initialization, only calculate the current file's hash if it's needed
                if (currentHash == null)
                {
                    currentHash = getHashAsHexString(fileIn, digest);
                }

                if (getHashAsHexString(tmp, digest).equals(currentHash))
                {
                    files.add(tmp);
                }
            }
        }

        return files;
    }

    public static String getHashAsHexString(File file, MessageDigest digest)
    {
        StringBuilder sb = new StringBuilder(64);
        digest.reset();

         try (DigestInputStream din = new DigestInputStream(new FileInputStream(file), digest))
         {
             byte[] buf = new byte[4096];

             while (din.read(buf) != -1)
             {
             }

             byte[] raw = digest.digest();

             for (byte b : raw)
             {
                 sb.append(String.format("%02X", b));
             }
         }
         catch (Exception e)
         {
             MaLiLib.LOGGER.warn("Exception while hashing file '{}': {}", file.getAbsolutePath(), e.getMessage());
         }

         return sb.toString();
    }

    public static boolean copyFilesToDirectory(Collection<File> files, File destinationDir,
                                               Consumer<String> messageConsumer)
    {
        boolean success = true;

        for (File file : files)
        {
            if (copyFileToDirectory(file, destinationDir) == false)
            {
                messageConsumer.accept(StringUtils.translate("malilib.message.error.failed_to_copy_file",
                                                             file.getName(), destinationDir.getName()));
                success = false;
            }
        }

        return success;
    }

    public static boolean copyFileToDirectory(File sourceFile, File destinationDir)
    {
        File destinationFile = new File(destinationDir, sourceFile.getName());
        return copyFile(sourceFile, destinationFile);
    }

    public static boolean copyFile(File sourceFile, File destinationFile)
    {
        if (sourceFile.exists())
        {
            if (destinationFile.exists())
            {
                MaLiLib.LOGGER.error("Won't copy file '{}' to '{}', the destination file already exists",
                                     sourceFile.getAbsolutePath(), destinationFile.getAbsolutePath());
                return false;
            }

            try
            {
                org.apache.commons.io.FileUtils.copyFile(sourceFile, destinationFile);
                return true;
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.error("Failed to copy file '{}' to '{}'",
                                     sourceFile.getAbsolutePath(), destinationFile.getAbsolutePath(), e);
            }
        }

        return false;
    }

    public static boolean moveFilesToDirectory(Collection<File> files, File destinationDir,
                                               Consumer<String> messageConsumer)
    {
        boolean success = true;

        for (File file : files)
        {
            if (moveFileToDirectory(file, destinationDir) == false)
            {
                messageConsumer.accept(StringUtils.translate("malilib.message.error.failed_to_move_file",
                                                             file.getName(), destinationDir.getName()));
                success = false;
            }
        }

        return success;
    }

    public static boolean moveFileToDirectory(File sourceFile, File destinationDir)
    {
        File destinationFile = new File(destinationDir, sourceFile.getName());
        return moveFile(sourceFile, destinationFile);
    }

    public static boolean moveFile(File sourceFile, File destinationFile)
    {
        if (sourceFile.exists())
        {
            if (destinationFile.exists())
            {
                MaLiLib.LOGGER.error("Won't move file '{}' to '{}', the destination file already exists",
                                     sourceFile.getAbsolutePath(), destinationFile.getAbsolutePath());
                return false;
            }

            try
            {
                org.apache.commons.io.FileUtils.moveFile(sourceFile, destinationFile);
                return true;
            }
            catch (Exception e)
            {
                MaLiLib.LOGGER.error("Failed to move file '{}' to '{}'",
                                     sourceFile.getAbsolutePath(), destinationFile.getAbsolutePath(), e);
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

    public static String getDateTimeString()
    {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date(System.currentTimeMillis()));
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
                MessageDispatcher.error("malilib.message.error.illegal_characters_in_file_name", string);
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
                    MessageDispatcher.error("malilib.message.error.file_rename.rename_failed", this.oldName, newName);
                }
            }
            else
            {
                MessageDispatcher.error("malilib.message.error.file_rename.file_already_exists", newName);
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
                MessageDispatcher.error("malilib.message.error.file_delete_failed", this.file.getAbsolutePath());
            }

            return false;
        }
    }
}
