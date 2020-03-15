package com.nabesh.favouritetwittersearches;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences savedSearches;
    private TableLayout queryTableLayout;
    private EditText queryEditText,tagEditText;
    Button saveButton, clearTagsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        savedSearches = getSharedPreferences("searches", MODE_PRIVATE);
        queryTableLayout = findViewById(R.id.queryTableLayout);
        queryEditText = findViewById(R.id.queryEditText);
        tagEditText = findViewById(R.id.tagEditText);
        saveButton = findViewById(R.id.saveButton);
        clearTagsButton = findViewById(R.id.clearTaggButton);
        clearTagsButton.setOnClickListener(clearTagsButtonListener);
        refreshButtons(null);

    }

    private void refreshButtons(String newTags) {
        //store saved tags in the tags array
        String [] tags = savedSearches.getAll().keySet().toArray(new String [0]);
        Arrays.sort(tags, String.CASE_INSENSITIVE_ORDER);

        if (newTags != null){
            makeTagGui(newTags, Arrays.binarySearch(tags, newTags));
        }else{
            for(int index = 0; index < tags.length; ++index){
                makeTagGui(tags[index], index);
            }
        }
    }

    public void makeTag(String query, String tag){
        String originalQuery = savedSearches.getString(tag, null);
        SharedPreferences.Editor preferenceEditor = savedSearches.edit();
        preferenceEditor.putString(tag, query);
        preferenceEditor.apply();

        if(originalQuery == null){
            refreshButtons(tag);
        }
    }

    public void makeTagGui(String tag, int index){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newTagView = inflater.inflate(R.layout.new_tag_view, null);

        Button newTagButton = newTagView.findViewById(R.id.newTagButton);
        newTagButton.setText(tag);
        newTagButton.setOnClickListener(queryButtonListener);

        Button newEditButton = newTagView.findViewById(R.id.newEditButton);
        newEditButton.setOnClickListener(editButtonListener);

        queryTableLayout.addView(newTagView, index);
    }

    private void clearButtons(){
        queryTableLayout.removeAllViews();
    }

    public View.OnClickListener saveButtonListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            if(queryEditText.getText().length() > 0 && tagEditText.getText().length() > 0){
                makeTag(queryEditText.getText().toString(), tagEditText.getText().toString());
                queryEditText.setText("");
                tagEditText.setText("");

                //hide soft keyboard
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(tagEditText.getWindowToken(), 0);
            }
            else{
                //Create a new alert Dialog bulider
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.missingTitle);
                //provides the ok button that dismisses it
                builder.setPositiveButton(R.string.ok, null);
                //set the message on the alert
                builder.setMessage(R.string.missingMessage);

                //create the alert dialog from the AlertDialog.Builder
                AlertDialog errDialog = builder.create();
                errDialog.show();

            }
        }
    };

    public View.OnClickListener clearTagsButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.confirmTitle);
            builder.setPositiveButton(R.string.erase, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    clearButtons();
                    SharedPreferences.Editor preferenceEditor = savedSearches.edit();
                    preferenceEditor.clear();
                    preferenceEditor.apply();
                }
            });
            builder.setCancelable(true);
            builder.setNegativeButton(R.string.cancel, null);
            builder.setMessage(R.string.confirmMessage);
            AlertDialog confirmDialog = builder.create();
            confirmDialog.show();
        }
    };

    public View.OnClickListener queryButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String buttonText = ((Button)view).getText().toString();
            String query = savedSearches.getString(buttonText, null);

            //create the URL corresponding toto the touched Button query
            String URLstring = getString(R.string.searchURL) + query;

            //Creating an intent to launch the web browser
            Intent getURL = new Intent(Intent.ACTION_VIEW, Uri.parse(URLstring));
            startActivity(getURL);
        }
    };

    public View.OnClickListener editButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //get all necessary GUI components
            TableRow buttonTableRow = (TableRow) view.getParent();
            Button searchButton = buttonTableRow.findViewById(R.id.newTagButton);
            String tag = searchButton.getText().toString();

            //set the EditText to match the chosen tag and query
            tagEditText.setText(tag);
            queryEditText.setText(savedSearches.getString(tag, null));
        }
    };



}
