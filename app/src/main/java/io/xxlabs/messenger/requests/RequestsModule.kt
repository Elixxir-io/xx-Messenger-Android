package io.xxlabs.messenger.requests

import dagger.Binds
import dagger.Module
import io.xxlabs.messenger.requests.bindings.*
import io.xxlabs.messenger.requests.data.LocalRequestsDataSource
import io.xxlabs.messenger.requests.data.RequestsDatabase
import javax.inject.Singleton

@Module
interface RequestsModule {
    @Singleton
    @Binds
    fun localDataSource(
        database: RequestsDatabase
    ): LocalRequestsDataSource

    @Singleton
    @Binds
    fun remoteRequestsDataSource(
        remote: BindingsRequestMediator
    ): ContactRequestsService

    @Singleton
    @Binds
    fun remoteInvitationsDataSource(
        remote: BindingsInvitationsMediator
    ): GroupInvitationsService

    @Singleton
    @Binds
    fun requestVerifier(
        requestVerifier: BindingsRequestVerifier
    ): RequestVerifier
}