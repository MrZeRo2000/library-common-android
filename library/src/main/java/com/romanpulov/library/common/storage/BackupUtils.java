package com.romanpulov.library.common.storage;

import com.romanpulov.library.common.io.FileUtils;
import com.romanpulov.library.common.io.ZipFileUtils;

import java.io.File;
import java.io.FileFilter;

/**
 * Backup and restore for file
 * Created by romanpulov on 19.12.2016.
 */

public class BackupUtils {
    private final String mDataFileName;
    private final String mBackupFolderName;
    private final String mBackupFileName;

    /**
     * Returns backup folder name from given folder name with ending slash
     * @param folderName input folder name
     * @return backup folder name
     */
    public static String getBackupFolderName(String folderName) {
        if (folderName.endsWith(File.separator))
            return folderName;
        else
            return folderName + File.separator;
    }
    /**
     * Returns backup folder name with ending slash
     * @return backup folder name
     */
    private String getBackupFolderName() {
        return getBackupFolderName(mBackupFolderName);
    }

    /**
     * Returns backup file name
     * @return file name
     */
    private String getBackupFileName() {
        return getBackupFolderName() + mBackupFileName;
    }

    private String getBackupZIPFileName() {
        return ZipFileUtils.getZipFileName(getBackupFileName());
    }

    public BackupUtils(String dataFileName, String backupFolderName, String backupFileName) {
        mDataFileName = dataFileName;
        mBackupFolderName = backupFolderName;
        mBackupFileName = backupFileName;
    }

    /**
     * Created local backup
     * @return archived file name if successful
     */
    private String createLocalBackup() {
        String backupFileName = getBackupFileName();

        //init backup folder
        File backupFolder = new File(getBackupFolderName());
        if (!backupFolder.exists()) {
            if (!backupFolder.mkdir()) {
                return null;
            }
        }

        //write file
        if (!backupFileName.equals(mDataFileName)) {
            if (!FileUtils.copy(mDataFileName, backupFileName))
                return null;
        }

        //archive file
        return ZipFileUtils.zipFile(getBackupFolderName(), mBackupFileName);
    }

    /**
     * Restores backup from archive
     * @return restored file name if successful
     */
    public String restoreBackup() {
        String backupFileName = getBackupFileName();

        //check backup availability
        File zipFile = new File(getBackupZIPFileName());
        if (!zipFile.exists())
            return null;

        //extract backup
        if (!ZipFileUtils.unZipFile(getBackupFolderName(), ZipFileUtils.getZipFileName(mBackupFileName)))
            return null;

        //check restored file availability
        File file = new File(backupFileName);
        if (!file.exists())
            return null;

        if (!backupFileName.equals(mDataFileName)) {
            //replace source file
            if (!FileUtils.copy(backupFileName, mDataFileName))
                return null;

            //delete and ignore any errors
            file.delete();
        }

        return backupFileName;
    }

    /**
     * Created rolling backup
     * @return archived file if successful
     */
    public String createRollingLocalBackup() {
        //get file names
        String fileName = getBackupFileName();
        String zipFileName = getBackupZIPFileName();

        //roll copies of data: first try rename, then copy
        if (!FileUtils.renameCopies(zipFileName))
            if ((!FileUtils.saveCopies(zipFileName)))
                return null;

        //create backup
        String result = createLocalBackup();

        //delete non zipped file, ignore any errors
        if (result != null)
            FileUtils.delete(fileName);

        return result;
    }

    /**
     * Returns backup files from backup folder
     * @return File list
     */
    public File[] getBackupFiles() {
        File folder = new File(getBackupFolderName());
        return folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String lowAbsolutePath = pathname.getAbsolutePath().toLowerCase();
                return lowAbsolutePath.endsWith(ZipFileUtils.ZIP_EXT) || lowAbsolutePath.matches("\\S*" +ZipFileUtils.ZIP_EXT + "." + FileUtils.BAK_EXT + "" + "[0-9]{2}");
            }
        });
    }
}
