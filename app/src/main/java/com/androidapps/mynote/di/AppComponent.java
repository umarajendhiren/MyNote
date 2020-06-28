package com.androidapps.mynote.di;


import com.androidapps.mynote.viewmodel.MainViewModel;
import com.androidapps.mynote.View.SortDialogFragment;
import com.androidapps.mynote.View.AddEditActivity;
import com.androidapps.mynote.View.MainActivity;
import com.androidapps.mynote.repository.NoteDao;
import com.androidapps.mynote.repository.NoteDatabase;
import com.androidapps.mynote.repository.NoteRepository;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(dependencies = {}, modules = {RoomModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);

    void injectForEditor(AddEditActivity editorActivity);

    NoteDao noteDao();

    NoteDatabase noteDatabase();

    NoteRepository noteRepository();

    void injectDialogfragment(SortDialogFragment sortDialogFragment);

    MainViewModel mainViewModel();


}
