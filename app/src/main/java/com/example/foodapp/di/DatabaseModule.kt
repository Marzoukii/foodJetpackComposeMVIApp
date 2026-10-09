package com.example.foodapp.di

import android.content.Context
import androidx.room.Room
import com.example.foodapp.data.local.AppDatabase
import com.example.foodapp.data.local.CartDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        // Le panier est temporaire : à un changement de schéma, on le vide plutôt que de le migrer.
        return Room.databaseBuilder(context, AppDatabase::class.java, "food_app.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun provideCartDao(database: AppDatabase): CartDao {
        return database.cartDao()
    }
}
