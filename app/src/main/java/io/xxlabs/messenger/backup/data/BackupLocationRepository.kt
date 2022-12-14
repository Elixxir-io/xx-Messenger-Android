package io.xxlabs.messenger.backup.data

import io.xxlabs.messenger.backup.bindings.BackupService
import io.xxlabs.messenger.backup.cloud.drive.GoogleDrive
import io.xxlabs.messenger.backup.cloud.dropbox.Dropbox
import io.xxlabs.messenger.backup.cloud.sftp.transfer.Sftp
import io.xxlabs.messenger.backup.data.backup.BackupPreferencesRepository
import io.xxlabs.messenger.backup.model.AccountBackup

abstract class BackupLocationRepository(
    preferences: BackupPreferencesRepository,
    backupService: BackupService,
) : AccountBackupDataSource {

    protected val googleDrive = GoogleDrive.getInstance(backupService, preferences)
    protected val dropbox = Dropbox.getInstance(backupService, preferences)
    protected val sftp = Sftp.getInstance(backupService, preferences)

    override val locations: List<AccountBackup> = listOf(
        googleDrive,
        dropbox,
        sftp
    )

    override fun getBackupFrom(source: BackupSource): AccountBackup =
        when (source) {
            BackupSource.DRIVE -> googleDrive
            BackupSource.DROPBOX -> dropbox
            BackupSource.SFTP -> sftp
        }

    override fun getSourceFor(backup: AccountBackup): BackupSource? =
        when (backup) {
            is GoogleDrive -> BackupSource.DRIVE
            is Dropbox -> BackupSource.DROPBOX
            is Sftp -> BackupSource.SFTP
            else -> null
        }
}