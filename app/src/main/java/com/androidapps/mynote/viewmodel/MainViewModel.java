package com.androidapps.mynote.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.androidapps.mynote.repository.NoteEntity;
import com.androidapps.mynote.repository.NoteRepository;
import com.androidapps.mynote.utilities.Filter;

import java.util.List;

import javax.inject.Inject;


/*The ViewModel is a class whose role is to provide data to the UI and survive configuration changes
 *  A ViewModel acts as a communication center between the Repository and the UI.
 * the MainViewModel hides everything about the backend from the user interface.
 *  it returns LiveData so that MainActivity can set up the observer relationship
 * Views, activities, and fragments only interact with the data through the ViewModel.
 *  As such, it doesn't matter where the data comes from.
 * In this case, the data comes from a Repository. The ViewModel does not need to know what that Repository interacts with.
 * It just needs to know how to interact with the Repository, which is through the methods exposed by the Repository.  */
public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";

    //    Add a private member variable to hold a reference to the Repository.
    private NoteRepository repository;

    /*Add a private LiveData member variable to cache the list of Notes*/
    private LiveData<List<NoteEntity>> allNotesSortByLaseEditedDateDesc;
    public static LiveData<List<NoteEntity>> liveNote;
    public static MutableLiveData<Filter> filter = new MutableLiveData<>();

    public static Filter filterObj;
    public static MutableLiveData<Integer> selectedSortRadioId = new MutableLiveData<>();
    public static MutableLiveData<Integer> selectedOrderRadioId = new MutableLiveData<>();


    /*add constructor, that gets a reference to the NoteRepository and list of livedata from NoteRepository */
    @Inject
    public MainViewModel(NoteRepository noteRepository) {
        this.repository = noteRepository;
        this.allNotesSortByLaseEditedDateDesc = repository.allNotesSortByLaseEditedDateDesc;
        filterObj = Filter.getFilterForCarInstance();
        if (filterObj.getOrderBy() == null && filterObj.getSortBy() == null) {
            filterObj.setSortBy("Last_Modified_Date");
            filterObj.setOrderBy("DESC");
            filter.setValue(filterObj);
        }
        if (selectedSortRadioId.getValue() == null && selectedOrderRadioId.getValue() == null) {
            setdefaultSortRadioId(2131230863);
            setdefaultOrderRadioId(2131230817);
        }

        getNote();

    }

    public void setdefaultSortRadioId(int radioId) {


        if (radioId != 0)
            selectedSortRadioId.setValue(radioId);
    }

    public int getSelectedSortRadioId() {
        return selectedSortRadioId.getValue();
    }

    public void setdefaultOrderRadioId(int radioId) {


        if (radioId != 0)
            selectedOrderRadioId.setValue(radioId);
    }

    public int getSelectedOrderRadioId() {
        return selectedOrderRadioId.getValue();
    }


    /*insert() method that calls the Repository's insert() method.
    In this way, the implementation of insert() is completely hidden from the UI.*/

    public void delete(NoteEntity noteToDelete) {
        repository.delete(noteToDelete);
    }

    public void addSampleData() {
        repository.addSampleData();
    }

    public LiveData<List<NoteEntity>> getNote() {

        liveNote = Transformations.switchMap(filter, repository::getAllNote);
        return liveNote;
    }
}


/*we are going to update sortby value using setValue().so we need to make MutableLiveData sortby instance as singleton
 * thats how we can access same instance of sortby  throughout this viewmodel*/

   /* public  MutableLiveData<String> getSortBy() {
        if (sortBy == null) {
            sortBy = new MutableLiveData<String>();
        }
        return sortBy;
    }
*/

   /*  public MediatorLiveData<List<NoteEntity>> getLiveDataMerger() {

        Log.d("SortByValue ", String.valueOf(sortBy.getValue()));
        Log.d("OrderByValue ", String.valueOf(orderBy.getValue()));


        if (sortBy.getValue().equals("Created Date") && orderBy.getValue().equals("ASC")) {

            *//*To make Sure ,same object of liveDataMerger observed by observer*//*
            Log.d("LiveDataMergerInstance ", liveDataMerger.toString());


            liveDataMerger.addSource(allNotesSortByCreatedDateAsc, new Observer<List<NoteEntity>>() {
                @Override
                public void onChanged(List<NoteEntity> noteEntities) {

                    liveDataMerger.setValue(noteEntities);
                    *//*we should not remove source ,because if somewhere note deleted or edited we need to observe that.*//*
                    //liveDataMerger.removeSource(allNotesSortByCreatedDateAsc);
                }
            });
        }

}


*/
