package com.androidapps.mynote.utilities;

import com.androidapps.mynote.repository.NoteEntity;

import java.util.ArrayList;
import java.util.List;

public class SampleData {

    private static final String SAMPLE_TITLE_1 = "A simple note";
    private static final String SAMPLE_DESCRIPTION_1 = "A simple note description";

    private static final String SAMPLE_TITLE_2 = "About Android";
    private static final String SAMPLE_DESCRIPTION_2 = "Android is a mobile operating system based on a modified version of the Linux kernel and other open source software, " +
            "designed primarily for touchscreen mobile devices such as smartphones and tablets";

    private static final String SAMPLE_TITLE_3 = "Help";

    private static final String SAMPLE_DESCRIPTION_3 = "1.press + button to create new note.\n\n 2.click on saved note to edit the note.\n\n 3.swipe left or right" +
            " to delete note. \n\n 4.by default,last modified note will be in top ,if you want to change the order  ,press overflow menu to sort.\n\n" +
            "5.press share button to share note. \n\n 6.select print button to print note.";

    public static List<NoteEntity> getNotes() {
        List<NoteEntity> notes = new ArrayList<>();
        notes.add(new NoteEntity(SAMPLE_TITLE_1, SAMPLE_DESCRIPTION_1, "Apr 05,2020 06:01:23:711 PM", "Apr 13,2020 11:32:57:141 PM"));
        notes.add(new NoteEntity(SAMPLE_TITLE_2, SAMPLE_DESCRIPTION_2, "Apr 06,2020 06:01:23:711 PM", "Apr 14,2020 11:32:57:141 PM"));
        notes.add(new NoteEntity(SAMPLE_TITLE_3, SAMPLE_DESCRIPTION_3, "Apr 07,2020 06:01:23:711 PM", "Apr 15,2020 11:32:57:141 PM"));

        return notes;
    }


}

