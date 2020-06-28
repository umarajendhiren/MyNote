package com.androidapps.mynote.repository;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")


/*By annotating this Java class with @Entity,
we can let room generate all the necessary code to create an SQLite table for this object,
as well as columns for all it's fields*/

//by default table name is class name(NoteEntity)


public class NoteEntity {

    /*Room will generate unique id for each row */
    @PrimaryKey(autoGenerate = true)
    private int id;

    /*Room will automatically create coloum for these fields */
    @ColumnInfo(name = "Title")
    private String title;
    private String description;


    @ColumnInfo(name = "Created_Date")
    private String createdDate;

    @ColumnInfo(name = "Last_Modified_Date")
    private String lastEditedDate;


    @Ignore
    public NoteEntity() {
    }


    /*to get all value and update using id we need this constructor*/
    public NoteEntity(int id, String title, String description, String createdDate, String lastEditedDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdDate = createdDate;
        this.lastEditedDate = lastEditedDate;


    }

    /*room will accept only one constructor with all fields as parameter.so when you annotate constructor with @Ignore
     * room will ignore this constructor.*/

    /*we no need to create setter for every field,
            indirectly we are using setter for fields*/
    @Ignore
    public NoteEntity(String title, String description, String createdDate, String lastEditedDate) {
        this.title = title;
        this.description = description;
        this.createdDate = createdDate;
        this.lastEditedDate = lastEditedDate;


    }


    /*find row by its id*/
    @Ignore
    public NoteEntity(int id) {
        this.id = id;
    }



    /*if we have public fields we no need to create getter method (we can call it directly)
    but we need to encapsulate the filed using private*/

    public int getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


    /*Using this setter method ROOM set id for each row.*/
    public void setId(int id) {
        this.id = id;
    }


    public String getCreatedDate() {
        return createdDate;
    }

    public String getLastEditedDate() {
        return lastEditedDate;
    }

    /*auto generate toString() method for testing*/
    @Override
    public String toString() {
        return "NoteEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +

                '}';
    }


}
