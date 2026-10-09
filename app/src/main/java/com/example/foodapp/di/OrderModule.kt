package com.example.foodapp.di

import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OrderModule {

    /** Base hors us-central1 : son URL doit être donnée explicitement. */
    private const val DATABASE_URL = "https://foodapp-74c79-default-rtdb.europe-west1.firebasedatabase.app"

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance(DATABASE_URL)
    }
}
