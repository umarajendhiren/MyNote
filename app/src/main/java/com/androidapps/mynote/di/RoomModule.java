package com.androidapps.mynote.di;

import android.app.Application;

import androidx.room.Room;

import com.androidapps.mynote.repository.NoteDao;
import com.androidapps.mynote.repository.NoteDatabase;
import com.androidapps.mynote.repository.NoteRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


/*this module provides NoteDatabase and NoteDao instance*/
@Module
public class RoomModule {
    private static NoteDatabase noteDatabase;

    @Singleton

    public RoomModule(Application application) {

        {
            if (noteDatabase == null) {
                noteDatabase = Room.databaseBuilder(application,
                        NoteDatabase.class, "note_database")
                        .fallbackToDestructiveMigration()

                        .build();
            }
        }
    }


    @Singleton
    @Provides
    NoteDatabase provideNoteDataBase() {
        return noteDatabase;
    }


    @Singleton
    @Provides
    NoteDao providesNoteDao(NoteDatabase noteDatabase) {
        return noteDatabase.noteDao();
    }


    @Singleton
    @Provides
    NoteRepository providesNoteRepository(NoteDao noteDao) {
        return new NoteRepository(noteDao);
    }


}
