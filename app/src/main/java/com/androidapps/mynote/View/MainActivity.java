package com.androidapps.mynote.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.mynote.R;
import com.androidapps.mynote.adapter.NoteAdapter;
import com.androidapps.mynote.di.DaggerAppComponent;
import com.androidapps.mynote.di.RoomModule;
import com.androidapps.mynote.repository.NoteEntity;
import com.androidapps.mynote.repository.NoteRepository;
import com.androidapps.mynote.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @Inject
    NoteRepository noteRepository;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @OnClick(R.id.fab)
    public void fabClickHandler() {
        Intent intent = new Intent(this, AddEditActivity.class);
        startActivity(intent);
    }

    Toolbar toolbar;
    NoteAdapter adapter;

    /*create a member variable for the ViewModel, because all the activity's interactions are with the MainViewModel only.*/
    @Inject
    public MainViewModel mainViewModel;
    List<NoteEntity> allNoteList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DaggerAppComponent.builder()
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);


        initiateRecyclerView();
        initializeViewModel();


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                NoteEntity noteToDelete = adapter.getUserAt(viewHolder.getAdapterPosition());
                Log.d("onSwiped: ", String.valueOf(noteToDelete.getId()));
                int noteId = noteToDelete.getId();

                Log.d("ifOfNoteToDelete", String.valueOf(noteId));
                mainViewModel.delete(noteToDelete);

            }
        }).attachToRecyclerView(recyclerView);


    }

    private void initializeViewModel() {
/*add an observer for the MediatorLiveData to observe source changes.
The automatic update happens because in the MainActivity, there is an Observer that observes liveData, notified when the Source change.
 When there is a change, the observer's onChange() method is executed and updates allNoteList in the NoteAdapter.
*/
        mainViewModel.liveNote.observe(this, new Observer<List<NoteEntity>>() {
            @Override
            public void onChanged(List<NoteEntity> noteEntities) {
                Log.d("AllLiveNote ", String.valueOf(noteEntities));

                allNoteList.clear();
                allNoteList.addAll(noteEntities);

                if (adapter == null) {
                    adapter = new NoteAdapter(allNoteList, MainActivity.this);
                    recyclerView.setAdapter(adapter);


                } else {
                    adapter.notifyDataSetChanged();
                }


            }

        });


    }


    private void initiateRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_sample) {

            mainViewModel.addSampleData();
            return true;
        }

        if (id == R.id.sort) {

            showSortDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {


        SortDialogFragment dialogFragment = SortDialogFragment.newInstance();


        dialogFragment.show(getSupportFragmentManager(), "sortDialog");


    }


}
