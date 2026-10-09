package com.example.foodapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CartItemEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}
