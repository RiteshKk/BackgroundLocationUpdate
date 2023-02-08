package com.riteshkumar.backgroundlocationupdate.di

import android.content.Context
import androidx.room.Room
import com.riteshkumar.backgroundlocationupdate.common.BASE_URL
import com.riteshkumar.backgroundlocationupdate.common.DATABASE_NAME
import com.riteshkumar.backgroundlocationupdate.data.LocationUploadRepositoryImpl
import com.riteshkumar.backgroundlocationupdate.data.remote.LocationUploadApi
import com.riteshkumar.backgroundlocationupdate.database.AppDatabase
import com.riteshkumar.backgroundlocationupdate.database.LocationDao
import com.riteshkumar.backgroundlocationupdate.database.LocationRepository
import com.riteshkumar.backgroundlocationupdate.domain.repository.LocationUploadRepository
import com.riteshkumar.backgroundlocationupdate.network.MockClientInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/* class to provide dependency */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideListingApi(client: OkHttpClient): LocationUploadApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(LocationUploadApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .callTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(MockClientInterceptor())
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            ).retryOnConnectionFailure(retryOnConnectionFailure = true)
            .build()
    }

    @Provides
    @Singleton
    fun provideLocationRepository(api: LocationUploadApi): LocationUploadRepository {
        return LocationUploadRepositoryImpl(api)
    }


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .build()

    @Provides
    @Singleton
    fun provideDao(appDB: AppDatabase): LocationDao = appDB.locationDao()


    @Provides
    @Singleton
    fun provideDatabaseRepository(dao: LocationDao): LocationRepository = LocationRepository(dao)
}