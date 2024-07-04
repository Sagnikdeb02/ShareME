package com.example.shareme.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shareme.data.model.Note

@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = false
)
abstract class NoteDataBase: RoomDatabase() {
    abstract val noteDao: NoteDao

    companion object{
        const val DATABASE_NAME = "notes_db"

        @Volatile
        var INSTANCE: NoteDataBase? = null
        fun getDatabase(context: Context): NoteDataBase {
            return synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context,
                    NoteDataBase::class.java,
                    DATABASE_NAME
                ).build()
            }
        }
    }
}