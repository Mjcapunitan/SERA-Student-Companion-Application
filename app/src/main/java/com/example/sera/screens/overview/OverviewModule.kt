package com.example.sera.screens.overview

import com.example.sera.data.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class AgendaModule {

    @ViewModelScoped
    @Binds
    abstract fun provideManageAgendaDataDelegate(
        manageAgendaDataDelegateImpl: ManageAgendaDataDelegateImpl
    ): ManageAgendaDataDelegate

}