package com.example.listaprioridade

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Note::class], version = 1,exportSchema = false)
abstract class NoteDatabase: RoomDatabase() {

    abstract fun noteDao():NoteDao

    companion object{
        @Volatile
        private var INSTANCE:NoteDatabase?=null

        fun getDatabase(context:Context):NoteDatabase{
            val tempInstance = INSTANCE
            if (tempInstance!= null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database")
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                return instance
            }
        }

        class NoteDatabaseCallback(
            private val scope:CoroutineScope
        ): RoomDatabase.Callback(){
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        var noteDao = database.noteDao()

                        noteDao.deleteAllNotes()


                        var note = Note("title 1", "description 1", 1)
                        noteDao.insert(note)
                    }
                }
            }
        }

    }

}