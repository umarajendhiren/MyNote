package com.androidapps.mynote.View;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;

import com.androidapps.mynote.R;
import com.androidapps.mynote.adapter.PdfDocumentAdapter;
import com.androidapps.mynote.adapter.PrintJobMonitorService;
import com.androidapps.mynote.di.DaggerAppComponent;
import com.androidapps.mynote.di.RoomModule;
import com.androidapps.mynote.repository.NoteEntity;
import com.androidapps.mynote.viewmodel.AddEditViewModel;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static androidx.core.content.FileProvider.getUriForFile;
import static com.androidapps.mynote.utilities.Constants.NOTE_ID_KEY;


public class AddEditActivity extends AppCompatActivity implements View.OnFocusChangeListener {
    private static final String TAG = "AddEditActivity";
    private static final int STORAGE_CODE = 1000;
    String createdDate, enteredTitle, enteredNote, mFileName, currentTime, mFilePath, lastEditedDate, formattedDate;

    int noteId;
    File file;
    Uri uriForFile;

    private PrintManager mgr = null;

    /*we can not use butterknife for menuItem.it is only for activity view*/

    MenuItem saveMenu, shareMenu;

    @BindView(R.id.edit_title)
    EditText title;

    @BindView(R.id.edit_description)
    EditText description;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.last_modified_date)
    TextView lastModifiedDate;

    @BindView(R.id.created_date)
    TextView noteCreatedDate;

    @Inject
    AddEditViewModel addEditViewModel;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mgr = (PrintManager) getSystemService(PRINT_SERVICE);

        /*currentTime to create unique file name*/
        currentTime = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());


        /* addEditViewModel only available after this build*/
        DaggerAppComponent.builder()
                .roomModule(new RoomModule(getApplication()))
                .build()
                .injectForEditor(this);


        Intent intent = getIntent();
        if (intent.hasExtra(NOTE_ID_KEY)) {
            Log.d(TAG, "onCreate: intentHasExtras");
            setTitle("Edit Note");
            /*if noteId is 1 or 2 it will update existing note.if noteId 0 ,it will add new note*/
            noteId = getIntent().getIntExtra(NOTE_ID_KEY, 0);

            addEditViewModel.loadNoteToEdit(noteId);

        } else {

            setTitle("New Note");

            title.requestFocus();


        }


        addEditViewModel.editTextValue.observe(this, new Observer<NoteEntity>() {


            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(NoteEntity noteEntity) {
                title.setText(noteEntity.getTitle());
                description.setText(noteEntity.getDescription());

                /*to save already existing note with created date*/

                createdDate = noteEntity.getCreatedDate().substring(0, 11);
                noteCreatedDate.setText("Created " + createdDate);


                lastEditedDate = noteEntity.getLastEditedDate();
                formatDate(lastEditedDate);
                lastModifiedDate.setText("Last Modified " + formattedDate);


                Log.d("createdDate: ", createdDate);
                Log.d("editedDate: ", lastEditedDate);
                Log.d("formattedDate: ", formattedDate);


            }
        });


        title.setOnFocusChangeListener(this);
        description.setOnFocusChangeListener(this);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String formatDate(String date) {

        // LocalDateTime inputDate = LocalDateTime.parse("Mar 17,2020 09:34:10:526 AM", stringToDateFormat);

        // H  0-24  13 MEANS 1 pm
        // h 8 Am,5Pm
        TemporalAccessor stringToDateFormat = DateTimeFormatter.ofPattern("MMM dd,yyyy hh:mm:ss:SSS a")
                .withLocale(Locale.US)
                .parse(date);

//        Log.d("inputeDate", String.valueOf(stringToDateFormat));//2020-03-17T09:34:10.526
        formattedDate = DateTimeFormatter.ofPattern("MMM dd,yyyy hh:mm a").format(stringToDateFormat);


//        Log.d("outDate", String.valueOf(formattedDate));
        return formattedDate;

    }


    @Override
    public void onFocusChange(View view, boolean hasFocus) {

        if (hasFocus) {
            /*call invalidateOptionsMenu() to trigger onCreateOptionsMenu()
            to set visibility of share,save button  based on focus*/
            noteCreatedDate.setVisibility(View.GONE);
            lastModifiedDate.setVisibility(View.GONE);
            invalidateOptionsMenu();
        } else {
            noteCreatedDate.setVisibility(View.VISIBLE);
            lastModifiedDate.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.editor_menu, menu);
        shareMenu = menu.findItem(R.id.share);
        saveMenu = menu.findItem(R.id.save);

        /*if edit text has focus,save button should be visible and share button should be invisible*/


        if (description.hasFocus() || title.hasFocus()) {

            shareMenu.setVisible(false);


        } else

            saveMenu.setVisible(false);


        return super.onCreateOptionsMenu(menu);


    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        enteredTitle = title.getText().toString().trim();
        enteredNote = description.getText().toString().trim();


        if (id == R.id.save) {

            saveAndReturn();


        }

        if (id == R.id.pdfNote) {

            if (isStoragePermissionGranted()) {
                createPdf(enteredTitle, enteredNote);

                if (!file.exists() || !file.canRead()) {
                    Toast.makeText(AddEditActivity.this, "Attachment Error", Toast.LENGTH_SHORT).show();
                    return false;
                }

                /*get content based uri using FileProvider to send as pdf*/
                getUriForTextFile(file);
                if (uriForFile != null) {

                    Log.d("fileUri", String.valueOf(uriForFile));

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("application/pdf");
                    intent.putExtra(Intent.EXTRA_STREAM, uriForFile);
                    Intent chooser = Intent.createChooser(intent, "Share Pdf");


                    List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);


                    /*read and write permission for all note inside folder*/
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        this.grantUriPermission(packageName, uriForFile, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }

                    startActivity(chooser);

                }
            }
        }


        if (id == R.id.textNote) {


            if (isStoragePermissionGranted()) {
                createTextFile(enteredTitle, enteredNote);

                getUriForTextFile(file);

                Log.d("uriForFile", String.valueOf(uriForFile));

                if (uriForFile != null) {

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType(" image/*  ");
                    intent.putExtra(Intent.EXTRA_STREAM, uriForFile);
                    Intent chooser = Intent.createChooser(intent, "Share Note");


                    List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        this.grantUriPermission(packageName, uriForFile, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }

                    startActivity(chooser);

                }
            }


        }


        if (id == R.id.imageNote) {

            if (isStoragePermissionGranted()) {

                Log.d("storagePermission", String.valueOf(isStoragePermissionGranted()));

                createImageFile();
                getUriForTextFile(file);

                Log.d("imageNoteUri", String.valueOf(uriForFile));

                if (uriForFile != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/jpeg*");
                    intent.putExtra(Intent.EXTRA_STREAM, uriForFile);

                    Intent chooser = Intent.createChooser(intent, "Share Note");


                    List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        this.grantUriPermission(packageName, uriForFile, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }

                    startActivity(chooser);


                }

            }
        }


        if (id == R.id.delete) {

            Log.d(" NoteIdToDelete", String.valueOf(noteId));

            addEditViewModel.deleteNote(noteId);
            Toast.makeText(this, "Note Deleted", Toast.LENGTH_SHORT).show();
            finish();

        }

        if (id == R.id.print) {


            createPdf(enteredTitle, enteredNote);
            getUriForFile(this, "com.androidapps.note.AddEditActivity", file);
            String filePath = file.getAbsolutePath();
            Log.d("filePathInPrint: ", filePath);
            print("Test PDF",
                    new PdfDocumentAdapter(getApplicationContext(), filePath),
                    new PrintAttributes.Builder().build());
        }


//        return true;
        return super.onOptionsItemSelected(item);
    }

    private Uri getUriForTextFile(File file) {
        /*get content based uri using FileProvider to send as pdf*/
        uriForFile = getUriForFile(this, "com.androidapps.note.AddEditActivity", file);
        Log.d("Uri", String.valueOf(uriForFile));
        return uriForFile;
    }

    private File createTextFile(String enteredTitle, String enteredNote) {

        Log.d("createNote:titleAndNote", enteredTitle + "/n" + enteredNote);

        Log.d("CurrentTime: ", currentTime);
        mFileName = "Notes " + currentTime;
        Log.d("FileName ", mFileName);
        mFilePath = AddEditActivity.this.getExternalFilesDir("TextFileFolder") + "/" + mFileName + ".txt";
        file = new File(mFilePath);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);

            fileOutputStream.write(enteredTitle.getBytes());
            fileOutputStream.write(enteredNote.getBytes());
            fileOutputStream.close();

            Log.d("createTextFile: ", "noteCreated");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("can not create note: ", e.toString());
        }
        return file;
    }

    private void saveAndReturn() {


        if (enteredTitle.isEmpty() && enteredNote.isEmpty()) {

            Toast.makeText(this, "Nothing To Save.Note Discarded ", Toast.LENGTH_LONG).show();
            finish();
            return;


        }


        if (noteId > 0) {

            /*   if noteId value greaterthan 0 means,that note is editNote.
            if noteId is 0 means,its new note*/


            //  String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
            String lastEditedDate = new SimpleDateFormat("MMM dd,YYYY hh:mm:ss:SSS a").format(new Date());


            Log.d("editNote: ", String.valueOf(noteId) + "\n" + createdDate + "\n" + lastEditedDate);
            NoteEntity noteEntity = new NoteEntity(noteId, enteredTitle, enteredNote, createdDate, lastEditedDate);

            addEditViewModel.saveNote(noteEntity);

            Log.d("saveAndReturn: ", String.valueOf(noteEntity.getId()));


            noteSavedtoast();


        } else {

            /*New note*/

            String currentDate = new SimpleDateFormat("MMM dd,YYYY hh:mm:ss:SSS a").format(new Date());
            NoteEntity noteEntity = new NoteEntity(noteId, enteredTitle, enteredNote, currentDate, currentDate);
            Log.d("NewNote ", String.valueOf(noteId) + "\n" + currentDate);
            addEditViewModel.saveNote(noteEntity);
            noteSavedtoast();

        }

    }

    public void noteSavedtoast() {


        title.clearFocus();
        description.clearFocus();
           /*This will trigger a call to the onCreateOptionsMenu() event again,
        and rearrange the menu item according to visibility*/

        invalidateOptionsMenu();

        /*hide keyboard*/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(title.getWindowToken(), 0);


        Toast.makeText(this, "Note saved", Toast.LENGTH_LONG).show();


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean checkStoragePermission() {
                    /*Before writing files you must also check whether your SDCard is mounted &
                                 the external storage state is writable.*/

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            /*if media mounted ,need to check storage permission*/
            Log.d("mediaMountedOrNot ", Environment.getExternalStorageState());


            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                //system OS >= Marshmallow(6.0), check if permission is enabled or not to store pdf in local storage
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED) {

                    Log.d("onClickShare: ", "permission denied");
                    //permission was not granted, request it
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permissions, STORAGE_CODE);
                } else {
                    //permission already granted, call save pdf method
                    Log.d("onClickShare: ", "permission already granted");

                    return true;

                }
            } else {
                //system OS < Marshmallow, call save pdf method
                return true;
            }
        }
        return true;
    }

    //handle permission result
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted from popup

                    Log.d("onRequestPermissions:", "permissionGrantedFromPopUp");

                    Toast.makeText(this, "Now You Can Share...!", Toast.LENGTH_LONG).show();
                } else {
                    //permission was denied from popup, show error message
                    Toast.makeText(this, "Can Not Share Note Permission denied...!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    private File createPdf(String title, String note) {


        //create object of Document class
        Document mDoc = new Document();
        //pdf file name


        Log.d("CurrentTime: ", currentTime);
        mFileName = "Notes " + currentTime;
        Log.d("FileName ", mFileName);
//        String mFileName="testFile";

        /*Environment.getExternalStorageDirectory()
         * we should not store our data in top level storage system instead we need to use getExternalFilesDir()
         *it  Returns the absolute path to the directory on the primary shared/external storage device
         *  where the application can place persistent files it owns.
         *  These files are internal to the applications, and not typically visible to the user as media.
         *  This is like getFilesDir() in that these files will be deleted when the application is uninstalled.
         *  */

        //pdf file path
        mFilePath = AddEditActivity.this.getExternalFilesDir("MyPdfFolder") + "/" + mFileName + ".pdf";
        Log.d("filepath: ", mFilePath);
        try {
            //create instance of PdfWriter class
            PdfWriter.getInstance(mDoc, new FileOutputStream(mFilePath));
            //open the document for writing
            mDoc.open();
            //add paragraph to the document

            mDoc.add(new Paragraph(title));
            mDoc.add(new Paragraph(note));

            //close the document
            mDoc.close();

            Log.d("filePath", mFilePath);


        } catch (Exception e) {
            //if any thing goes wrong causing exception, get and show exception message
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        file = new File(mFilePath);
        return file;
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }


    public File createImageFile() {


        String note = enteredTitle + "\n" + enteredNote;

        final Rect bounds = new Rect();

        TextPaint textPaint = new TextPaint() {
            {
                setColor(Color.BLACK);
                setTextAlign(Align.CENTER);

                setTextSize(12f);
                setAntiAlias(true);
            }
        };
        textPaint.getTextBounds(note, 0, note.length(), bounds);
        StaticLayout mTextLayout = new StaticLayout(note, textPaint,
                bounds.width(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);


        int maxWidth = -1;
        for (int i = 0; i < mTextLayout.getLineCount(); i++) {
            if (maxWidth < mTextLayout.getLineWidth(i)) {
                maxWidth = (int) mTextLayout.getLineWidth(i);
            }
        }

        final Bitmap bmp = Bitmap.createBitmap(500, mTextLayout.getHeight(),
                Bitmap.Config.ARGB_8888);

        bmp.eraseColor(Color.WHITE);
        final Canvas canvas = new Canvas(bmp);
        mTextLayout.draw(canvas);


        Log.d("CurrentTime: ", currentTime);
        mFileName = "Notes " + currentTime;
        Log.d("FileName ", mFileName);

        mFilePath = AddEditActivity.this.getExternalFilesDir("imageFolder") + "/" + mFileName + ".jpg";

        file = new File(mFilePath);


        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            bmp.recycle();

            fileOutputStream.write(enteredTitle.getBytes());
            fileOutputStream.write(enteredNote.getBytes());
            fileOutputStream.close();

            Log.d("createImageFile: ", "noteCreated");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("can not create note: ", e.toString());
        }
        return file;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private PrintJob print(String name, PrintDocumentAdapter adapter,
                           PrintAttributes attrs) {
        Intent printIntent = new Intent(this, PrintJobMonitorService.class);
        printIntent.putExtra("FILE_NAME", file);
        Log.d("print: ", String.valueOf(file));

        startService(printIntent);

        return (mgr.print(name, adapter, attrs));
    }


}




