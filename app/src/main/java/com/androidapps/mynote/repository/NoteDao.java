package com.androidapps.mynote.repository;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.List;

/*The data access object, or Dao, is an annotated class where you specify SQL queries and associate them with method calls.
The compiler checks the SQL for errors, then generates queries from the annotations*/

/*Dao must be interface or abstract because we are not going to create method body.
ROOM will automatically generate method body using annotation like @insert..

Room is the  persistence library .it is wrapper around Sql Database.
it wil create compile time error if you forgot something like comma or space .using this we can avoid app crash.
*/


/*NoteDao used by NoteRepository to grant access too the table(tableName = "note_table")*/
@Dao
public interface NoteDao {

    /*ROOM will automatically generate all the code to insert the value to database using @Insert annotation
     we can send list or object class(entity) or list and object class or more than one object class as argument*/
//    @Insert
//    void insert(NoteEntity noteEntity);

/*The DAO maps method calls to database queries,
so that when the Repository calls a method such as getAllWords(), Room can execute SELECT * from word_table ORDER BY word ASC.*/

    /*this annotation is for insert and update*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(NoteEntity noteEntity);


    /*this annotation is for insert and update all sample list data*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<NoteEntity> note);


    /*ROOM will automatically generate all the code to delete the value in database using @Delete annotation */
    @Delete
    void deleteNote(NoteEntity noteEntity);


    /*we dont have ready made annotation to delete all  in table.so this is the query to delete all note at once*/
    @Query("DELETE FROM NOTE_TABLE")
    void deleteAllNotes();


    /*get single note by it's id*/

    @Query("SELECT * FROM note_table WHERE id = :id")
    NoteEntity getNoteById(int id);

    /*this is the query to get all notes from table
     * LiveData, using live data  our activity or fragment gets notified as soon as a row in the queried database table changes.
     * The result returned from the query is observed LiveData. Therefore, every time the data in Room changes,
     *  the Observer interface's onChanged() method is executed and the UI is updated. */


       /* @Query("SELECT * FROM NOTE_TABLE ORDER BY lastEditedDate DESC ")
    LiveData<List<NoteEntity>> getNotesSortByLastEditedDateDesc();


    /*number of items in note_table*/

    @Query("SELECT COUNT(*) FROM note_table")
    int getCount();


    @RawQuery(observedEntities = NoteEntity.class)
    LiveData<List<NoteEntity>> getNote(SimpleSQLiteQuery runtimeQuery);


}
