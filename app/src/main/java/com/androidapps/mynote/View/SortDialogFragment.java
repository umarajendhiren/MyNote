package com.androidapps.mynote.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.androidapps.mynote.R;
import com.androidapps.mynote.di.DaggerAppComponent;
import com.androidapps.mynote.di.RoomModule;
import com.androidapps.mynote.utilities.Filter;
import com.androidapps.mynote.viewmodel.MainViewModel;

import javax.inject.Inject;


public class SortDialogFragment extends DialogFragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    @Inject
    MainViewModel viewModel;

    Button done, cancel;
    RadioGroup sortBy, orderBy;
    RadioButton setDefaultSortByChecked, setDefaultOrdertByChecked;


    static String selectedSort, selectedOrder;
    int selectedSortRadioId, selectedOrderRadioId;
    int defaultSort, defaultOrder;

    public SortDialogFragment() {
        // Required empty public constructor
    }


    public static SortDialogFragment newInstance() {
        return new SortDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        DaggerAppComponent.builder()
                .roomModule(new RoomModule(getActivity().getApplication()))
                .build()
                .injectDialogfragment(this);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View dialogView = inflater.inflate(R.layout.fragment_sort_dialog, container, false);


        sortBy = dialogView.findViewById(R.id.sort_by);

        defaultSort = viewModel.getSelectedSortRadioId();
        setDefaultSortByChecked = dialogView.findViewById(defaultSort);
        setDefaultSortByChecked.setChecked(true);

        defaultOrder = viewModel.getSelectedOrderRadioId();
        setDefaultOrdertByChecked = dialogView.findViewById(defaultOrder);
        setDefaultOrdertByChecked.setChecked(true);

        sortBy.setOnCheckedChangeListener(this);

        orderBy = dialogView.findViewById(R.id.order_by);

        orderBy.setOnCheckedChangeListener(this);

        done = dialogView.findViewById(R.id.btn_done);

        done.setOnClickListener(this);

        cancel = dialogView.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(this);

        return dialogView;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        Log.d("onCheckedChanged: ", String.valueOf(radioGroup.getCheckedRadioButtonId()));
        switch (radioGroup.getCheckedRadioButtonId()) {

            case R.id.title:
                selectedSort = "Title";
                selectedSortRadioId = R.id.title;
                break;

            case R.id.created_date:
                selectedSort = "Created_Date";
                selectedSortRadioId = R.id.created_date;
                break;
            case R.id.last_modified_date:
                selectedSort = "last_modified_date";
                selectedSortRadioId = R.id.last_modified_date;
                break;
            case R.id.ascending:
                selectedOrder = "ASC";
                selectedOrderRadioId = R.id.ascending;

                break;
            case R.id.descending:
                selectedOrder = "DESC";
                selectedOrderRadioId = R.id.descending;
                break;
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_done:
                Filter filterObj = Filter.getFilterForCarInstance();

                filterObj.setSortBy(selectedSort);
                filterObj.setOrderBy(selectedOrder);

                viewModel.filter.setValue(filterObj);

                if (String.valueOf(selectedSortRadioId) != null) {
                    viewModel.setdefaultSortRadioId(selectedSortRadioId);
                }
                if (String.valueOf(selectedOrderRadioId) != null) {
                    viewModel.setdefaultOrderRadioId(selectedOrderRadioId);
                }
                dismiss();
                break;
            case R.id.btn_cancel:

                dismiss();
                break;
        }

    }


}

