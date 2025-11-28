package com.piyush.bmusurat.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.piyush.bmusurat.data.ApiService
import com.piyush.bmusurat.data.datastore.PreferencesDataStore
import com.piyush.bmusurat.data.local.HomeDao
import com.piyush.bmusurat.data.repository.AuthRepository
import com.piyush.bmusurat.data.repository.AuthRepositoryImpl
import com.piyush.bmusurat.data.repository.HomeRepository
import com.piyush.bmusurat.data.repository.HomeRepositoryImpl
import com.piyush.bmusurat.data.repository.ProgramRepository
import com.piyush.bmusurat.data.repository.ProgramRepositoryImpl
import com.piyush.bmusurat.util.ConnectivityObserver
import com.piyush.bmusurat.util.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://ibmu.onrender.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("bmu_prefs") }
        )
    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(dataStore: DataStore<Preferences>): PreferencesDataStore {
        return PreferencesDataStore(dataStore)
    }

    @Provides
    @Singleton
    fun provideRepository(
        apiService: ApiService,
        dao: HomeDao,
        preferencesDataStore: PreferencesDataStore
    ): HomeRepository {
        return HomeRepositoryImpl(apiService, dao, preferencesDataStore)
    }

    @Provides
    @Singleton
    fun provideProgramRepository(
        apiService: ApiService,
        preferencesDataStore: PreferencesDataStore
    ): ProgramRepository {
        return ProgramRepositoryImpl(apiService,preferencesDataStore)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideConnectivityObserver(@ApplicationContext context: Context): ConnectivityObserver {
        return NetworkConnectivityObserver(context)
    }
}