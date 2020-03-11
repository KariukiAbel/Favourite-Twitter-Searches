package com.nabesh.favouritetwittersearches;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;

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
            makeTagGui(newTag, Arrays.binarySearch(tags, newTag));
        }else{
            for(int index = 0, index < tags.length, ++index){
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
}
