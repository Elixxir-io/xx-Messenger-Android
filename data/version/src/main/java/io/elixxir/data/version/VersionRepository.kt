package io.elixxir.data.version

import io.elixxir.data.version.model.VersionState

interface VersionRepository {
    suspend fun checkVersion(): VersionState
}