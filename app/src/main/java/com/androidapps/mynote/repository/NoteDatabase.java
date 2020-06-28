package com.androidapps.mynote.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;


/*we can give more than one entity as parameter in @Database.each entities are converted into a table in database.
 * if we make changes in database we need to increase the version number for migration*/

/*it is important to make the class and method as abstract, Room will return the right DAO implementation*/
@Database(entities = {NoteEntity.class}, version = NoteDatabase.VERSION)


/*
    Room uses the DAO to issue queries to its database.
    By default, to avoid poor UI performance, Room doesn't allow you to issue database queries on the main thread. LiveData applies this rule by automatically running the query asynchronously on a background thread, when needed.
    Room provides compile-time checks of SQLite statements.
    Your Room class must be abstract and extend RoomDatabase.
    Usually, you only need one instance of the Room database for the whole app.
*/
public abstract class NoteDatabase extends RoomDatabase {


    static final int VERSION = 5;
    //to initiate database at least we need to insert one row of data

    public abstract NoteDao noteDao();


}


