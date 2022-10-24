package malilib.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.commons.codec.digest.DigestUtils;

import malilib.MaLiLib;
import malilib.MaLiLibConfigs;
import malilib.config.util.ConfigUtils;

public class BackupUtils
{
    /**
     * Create a "normal/regular" backup of the given file, in the default backup directory
     * at "<active_config_dir>/backups/<file_name>.bak_<number>".
     * The maximum number of backups to keep, and whether they should be "anti-duplicated"
     * come from the malilib configs.
     * @return true on success, false on failure
     */
    public static boolean createRegularBackup(Path fileIn)
    {
        Path configDir = ConfigUtils.getActiveConfigDirectory();
        Path backupDir = configDir.resolve("backups");

        return createRegularBackup(fileIn, backupDir);
    }

    /**
     * Create a "normal/regular" backup of the given file, in the given backup directory.
     * The maximum number of backups to keep, and whether they should be "anti-duplicated"
     * come from the malilib configs.
     * @return true on success, false on failure
     */
    public static boolean createRegularBackup(Path fileIn, Path backupDir)
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
    public static boolean createRollingBackup(Path fileIn, Path backupDirectory, String suffix,
                                              int maxBackups, boolean antiDuplicate)
    {
        if (maxBackups <= 0)
        {
            return true;
        }

        if (FileUtils.createDirectoriesIfMissing(backupDirectory) == false)
        {
            MaLiLib.LOGGER.error("Failed to create the config backup directory '{}'", backupDirectory.toAbsolutePath());
            return false;
        }

        // Don't fail if the file doesn't even exist (yet)
        if (Files.exists(fileIn) == false)
        {
            return true;
        }

        final int numberLength = (int) Math.ceil(Math.log10(maxBackups));
        final String formatString = "%s%0" + numberLength + "d";
        final String name = fileIn.getFileName().toString();
        final String nameAndSuffix = name + suffix;
        final Path backupFile = backupDirectory.resolve(String.format(formatString, nameAndSuffix, 1));
        @Nullable Path existingIdenticalBackup = null;
        boolean foundExistingBackup = false;

        if (antiDuplicate)
        {
            List<Path> identicalBackups = findIdenticalBackupFiles(backupDirectory, fileIn, maxBackups, suffix);

            if (identicalBackups.isEmpty() == false)
            {
                Path tmpFile = identicalBackups.get(0);

                // If the existing identical backup was already the first/latest backup file,
                // then there is nothing more to do
                if (tmpFile.equals(backupFile))
                {
                    return true;
                }

                String tmpName = name + UUID.randomUUID();
                existingIdenticalBackup = backupDirectory.resolve(tmpName);

                // Move the existing identical backup to a temporary name.
                // Then rotate the files from 1 up to that existing name,
                // and finally move that existing backup as the latest backup file.
                if (Files.exists(existingIdenticalBackup) == false &&
                    FileUtils.move(tmpFile, existingIdenticalBackup))
                {
                    foundExistingBackup = true;
                }
            }
        }

        // Only rotate the backups if the first name is in use
        if (Files.exists(backupFile) &&
            rotateNumberedFiles(backupDirectory, nameAndSuffix, formatString, maxBackups) == false)
        {
            return false;
        }

        if (foundExistingBackup)
        {
            if (FileUtils.move(existingIdenticalBackup, backupFile))
            {
                return true;
            }

            MaLiLib.LOGGER.error("Failed to rename backup file '{}' to '{}'",
                                 existingIdenticalBackup.toAbsolutePath(), backupFile.toAbsolutePath());
        }

        return FileUtils.copy(fileIn, backupFile, false, MaLiLib.LOGGER::error);
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
    public static boolean rotateNumberedFiles(Path dir, String baseFileName, String nameFormatString, int maxCount)
    {
        // If there are unused backup file names, only rotate the backups up to the first empty slot
        int firstEmptySlot = maxCount;

        for (int i = 1; i <= maxCount; ++i)
        {
            Path tmp = dir.resolve(String.format(nameFormatString, baseFileName, i));

            if (Files.exists(tmp) == false)
            {
                firstEmptySlot = i;
                break;
            }
        }

        Path tmp1 = dir.resolve(String.format(nameFormatString, baseFileName, firstEmptySlot));

        for (int i = firstEmptySlot; i > 1; --i)
        {
            Path tmp2 = tmp1;
            tmp1 = dir.resolve(String.format(nameFormatString, baseFileName, i - 1));

            if (Files.isRegularFile(tmp1) && FileUtils.move(tmp1, tmp2) == false)
            {
                return false;
            }
        }

        return true;
    }

    public static boolean createBackupFileForVersion(Path file, Path backupDirectory, int configVersion)
    {
        if (FileUtils.createDirectoriesIfMissing(backupDirectory) == false)
        {
            MaLiLib.LOGGER.error("Failed to create the config backup directory '{}'", backupDirectory.toAbsolutePath());
            return false;
        }

        // Don't fail if the file doesn't even exist (yet)
        if (Files.exists(file) == false)
        {
            return true;
        }

        String fullName = file.getFileName().toString();
        String name = FileNameUtils.getFileNameWithoutExtension(fullName);
        String dateStr = FileNameUtils.getDateTimeString();
        String extension = FileNameUtils.getFileNameExtension(fullName);
        String backupFileName = name + "_v" + configVersion + "_" + dateStr;

        if (extension.length() > 0)
        {
            backupFileName += "." + extension;
        }

        return FileUtils.copy(file, backupDirectory.resolve(backupFileName), false, MaLiLib.LOGGER::error);
    }

    public static List<Path> findIdenticalBackupFiles(Path backupDirectory, Path file, int maxBackups, String suffix)
    {
        final List<Path> files = new ArrayList<>();
        final long fileSize = FileUtils.size(file);
        final int numberLength = (int) Math.ceil(Math.log10(maxBackups));
        final String formatString = "%s%0" + numberLength + "d";
        final String nameAndSuffix = file.getFileName().toString() + suffix;
        final MessageDigest digest = DigestUtils.getSha1Digest();
        @Nullable String currentHash = null;

        for (int i = 1; i <= maxBackups; ++i)
        {
            Path tmp = backupDirectory.resolve(String.format(formatString, nameAndSuffix, i));

            if (Files.isRegularFile(tmp) && FileUtils.size(tmp) == fileSize)
            {
                // lazy initialization, only calculate the current file's hash if it's needed
                if (currentHash == null)
                {
                    currentHash = HashUtils.getHashAsHexString(file, digest);
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
