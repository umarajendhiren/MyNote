package com.androidapps.mynote.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.androidapps.mynote.repository.NoteEntity;
import com.androidapps.mynote.repository.NoteRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;


public class AddEditViewModel extends ViewModel {

    private static final String TAG = "AddEditViewModel";
    private NoteRepository repository;
    private Executor executor = Executors.newSingleThreadExecutor();

    NoteEntity noteToEdit;


    public MutableLiveData<NoteEntity> editTextValue =
            new MutableLiveData<>();


    @Inject
    public AddEditViewModel(NoteRepository noteRepository) {
        this.repository = noteRepository;


    }

    /*insert() method that calls the Repository's insert() method.
       In this way, the implementation of insert() is completely hidden from the UI.*/
    public void saveNote(NoteEntity noteEntity) {

        repository.insert(noteEntity);

        /*to set all edittext value and date we need to use setValue() in mainthread to notify observer about changes*/
        editTextValue.setValue(noteEntity);

    }

    public void loadNoteToEdit(final int id) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                noteToEdit = repository.getNoteById(id);
                /*we are getting note from background thread.so we should use post value to notify observer about changes*/

                //running in backround thread .so postValue
                editTextValue.postValue(noteToEdit);
            }
        });
    }

    public void getNoteById(int noteId) {

        final NoteEntity noteEntity = repository.getNoteById(noteId);


        executor.execute(new Runnable() {
            @Override
            public void run() {


                //running in backround thread .so postValue
                editTextValue.postValue(noteEntity);
            }
        });

    }

    public void deleteNote(int noteId) {


        executor.execute(new Runnable() {
            @Override
            public void run() {

                final NoteEntity noteEntity = repository.getNoteById(noteId);
                repository.delete(noteEntity);

            }
        });
    }

}
