package com.androidapps.mynote.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.mynote.repository.NoteEntity;
import com.androidapps.mynote.R;
import com.androidapps.mynote.View.AddEditActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.androidapps.mynote.utilities.Constants.NOTE_ID_KEY;
/* a class NoteAdapter that extends RecyclerView.Adapter.
 The adapter caches data and populates the RecyclerView with it.
The inner class ViewHolder holds and manages a view for one list item. */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> implements Filterable {
    Context context;
    /*cached copy of notes*/
    List<NoteEntity> allNoteList = new ArrayList<>();

    /*make copy of list to do search*/

    List<NoteEntity> searchList;

    public NoteAdapter(List<NoteEntity> allNoteList, Context context) {

        this.allNoteList = allNoteList;
        this.context = context;
        this.searchList = new ArrayList<>(allNoteList);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.note_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NoteEntity noteEntity = allNoteList.get(position);
        /*if title is empty and note is not empty ,set note text as title text */

        if (noteEntity.getTitle().isEmpty() && !noteEntity.getDescription().isEmpty()) {
            holder.title.setText(noteEntity.getDescription().trim());
        }

        /*if title is not empty*/
        else {


            holder.title.setText(noteEntity.getTitle());

        }


        //MMM dd,YYYY  hh:mm:ss:SSS a
        Log.d("lastEditedDate", noteEntity.getLastEditedDate().substring(0, 11));
        String lastEditedDate = noteEntity.getLastEditedDate().substring(0, 11);

        holder.date.setText(lastEditedDate);
    }

    @Override
    public int getItemCount() {
        return allNoteList.size();
    }

    public NoteEntity getUserAt(int adapterPosition) {
        return allNoteList.get(adapterPosition);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;

        @BindView(R.id.date)
        TextView date;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);


            itemView.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    int noteId = allNoteList.get(position).getId();

                    Log.d("noteIdIs: ", String.valueOf(noteId) + "\n adapterPositionIs " + position);

                    Intent startAddEditActivityIntent = new Intent(context, AddEditActivity.class);
                    startAddEditActivityIntent.putExtra(NOTE_ID_KEY, noteId);
                    context.startActivity(startAddEditActivityIntent);
                }
            });
        }

    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<NoteEntity> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(searchList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (NoteEntity noteEntity : searchList) {
                    if (noteEntity.getTitle().toLowerCase().contains(filterPattern) || noteEntity.getDescription().toLowerCase().contains(filterPattern)) {
                        filteredList.add(noteEntity);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            allNoteList.clear();
            allNoteList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

}
