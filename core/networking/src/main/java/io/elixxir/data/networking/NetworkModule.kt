package io.elixxir.data.networking

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.elixxir.data.networking.data.NetworkDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NetworkModule {

    @Singleton
    @Binds
    fun bindNetworkRepository(
        dataSource: NetworkDataSource
    ): NetworkRepository
}