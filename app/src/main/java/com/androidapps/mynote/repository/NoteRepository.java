package com.androidapps.mynote.repository;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.androidapps.mynote.utilities.Filter;
import com.androidapps.mynote.utilities.SampleData;

import java.util.List;

import javax.inject.Inject;

/*A Repository is a class that abstracts access to multiple data sources.
The Repository manages one or more data sources.
 In the MyNote app, that backend is a Room database.
The Repository is not part of the Architecture Components libraries,
but is a suggested best practice for code separation and architecture.
A Repository class handles data operations.
It provides a clean API to the rest of the app for app data. */
public class NoteRepository {
    public LiveData<List<NoteEntity>> allNotesSortByLaseEditedDateDesc;
    public LiveData<List<NoteEntity>> getNotes;
    private NoteDao noteDao;

    /*Add a constructor that gets a handle to the database and initializes the member variables.*/
    @Inject
    public NoteRepository(NoteDao noteDao) {
        this.noteDao = noteDao;
//        this.allNotesSortByLaseEditedDateDesc = noteDao.getNotesSortByLastEditedDateDesc();
    }


    public void insert(NoteEntity noteEntity) {
        /*Room doesn’t allow database queries on the main thread.if we do ,app will crash .
            so we use AsyncTasks to execute them in background thread asynchronously.*/
        //        noteDao.insert(noteEntity);

        new InsertNoteAsyncTask(noteDao).execute(noteEntity);

    }


    public void update(NoteEntity noteEntity) {

        new UpdateNoteAsyncTask(noteDao).execute(noteEntity);
    }

    public void delete(NoteEntity noteEntity) {

        new DeleteNoteAsyncTask(noteDao).execute(noteEntity);
    }


    public void deleteAllNotes() {

        new DeleteAllNotesAsyncTask(noteDao).execute();
    }


    public void addSampleData() {

        new addSampleNotesAsynTask(noteDao).execute();
    }

    public NoteEntity getNoteById(int noteId) {
        return noteDao.getNoteById(noteId);
    }

    public LiveData<List<NoteEntity>> getAllNote(Filter filter) {

        String sortBy = filter.getSortBy();
        String orderBy = filter.getOrderBy();
        String queryString = "SELECT * FROM NOTE_TABLE ORDER BY " + sortBy + " " + orderBy;
        Log.d("query ", queryString);
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        getNotes = noteDao.getNote(query);
        return getNotes;
    }

    private static class InsertNoteAsyncTask extends AsyncTask<NoteEntity, Void, Void> {
        private NoteDao noteDao;

        private InsertNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }


        @Override
        protected Void doInBackground(NoteEntity... noteEntities) {
            noteDao.insertNote(noteEntities[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<NoteEntity, Void, Void> {
        private NoteDao noteDao;

        private UpdateNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(NoteEntity... noteEntities) {
//            noteDao.update(noteEntities[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<NoteEntity, Void, Void> {
        private NoteDao noteDao;

        private DeleteNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(NoteEntity... noteEntities) {
            noteDao.deleteNote(noteEntities[0]);
            return null;
        }
    }

    private static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;

        private DeleteAllNotesAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllNotes();
            return null;
        }
    }


    private static class addSampleNotesAsynTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;

        private addSampleNotesAsynTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insertAll(SampleData.getNotes());
            return null;
        }
    }


}
