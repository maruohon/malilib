package fi.dy.masa.malilib.util;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.commons.codec.digest.DigestUtils;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.config.util.ConfigUtils;

public class BackupUtils
{
    /**
     * Create a "normal/regular" backup of the given file, in the default backup directory
     * at "<active_config_dir>/backups/<file_name>.bak_<number>".
     * The maximum number of backups to keep, and whether they should be "anti-duplicated"
     * come from the malilib configs.
     * @return true on success, false on failure
     */
    public static boolean createRegularBackup(File fileIn)
    {
        File configDir = ConfigUtils.getActiveConfigDirectory();
        File backupDir = configDir.toPath().resolve("backups").toFile();

        return createRegularBackup(fileIn, backupDir);
    }

    /**
     * Create a "normal/regular" backup of the given file, in the given backup directory.
     * The maximum number of backups to keep, and whether they should be "anti-duplicated"
     * come from the malilib configs.
     * @return true on success, false on failure
     */
    public static boolean createRegularBackup(File fileIn, File backupDir)
    {
        int backupCount = MaLiLibConfigs.Generic.CONFIG_BACKUP_COUNT.getIntegerValue();

        if (backupCount <= 0)
        {
            return true;
        }

        boolean antiDuplicate = MaLiLibConfigs.Generic.CONFIG_BACKUP_ANTI_DUPLICATE.getBooleanValue();

        return createRollingBackup(fileIn, backupDir, ".bak_", backupCount, antiDuplicate);
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
        if (maxBackups <= 0)
        {
            return true;
        }

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

        return FileUtils.copyFile(fileIn, backupFile, MaLiLib.LOGGER::error);
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
        String name = FileNameUtils.getFileNameWithoutExtension(fullName);
        String dateStr = FileNameUtils.getDateTimeString();
        String extension = FileNameUtils.getFileNameExtension(fullName);
        String backupFileName = name + "_v" + configVersion + "_" + dateStr;

        if (extension.length() > 0)
        {
            backupFileName += "." + extension;
        }

        return FileUtils.copyFile(fileIn, new File(backupDirectory, backupFileName), MaLiLib.LOGGER::error);
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
                    currentHash = HashUtils.getHashAsHexString(fileIn, digest);
                }

                if (HashUtils.getHashAsHexString(tmp, digest).equals(currentHash))
                {
                    files.add(tmp);
                }
            }
        }

        return files;
    }
}
