package io.github.jwgibanez.cartrack.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jwgibanez.cartrack.data.LoginDataSource
import io.github.jwgibanez.cartrack.data.LoginRepository
import io.github.jwgibanez.cartrack.data.db.AppDatabase
import io.github.jwgibanez.cartrack.data.db.AppDatabase.Companion.getInstance

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return getInstance(appContext)
    }

    @Provides
    fun provideLoginDataSource(appDatabase: AppDatabase): LoginDataSource {
        return LoginDataSource(appDatabase)
    }

    @Provides
    fun provideLoginRepository(loginDataSource: LoginDataSource): LoginRepository {
        return LoginRepository(loginDataSource)
    }
}