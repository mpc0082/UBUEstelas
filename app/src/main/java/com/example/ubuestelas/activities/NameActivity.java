package com.example.ubuestelas.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.ubuestelas.R;
import com.example.ubuestelas.util.Util;
import com.google.android.gms.common.util.ArrayUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NameActivity extends AppCompatActivity{

    JSONArray chars;
    Integer[] characs = {};

    int prevIdCharacter = R.id.character;
    int prevSelecCharacter = R.drawable.character01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nombre);
        fillCharacters();
    }

    public void continueToScene(View view){

        SharedPreferences nameSP= getSharedPreferences("nameActivity", 0);
        SharedPreferences.Editor nameEditor = nameSP.edit();
        EditText editText = findViewById(R.id.input_name);
        String name = editText.getText().toString();
        nameEditor.putString("name", name);
        nameEditor.commit();

        Intent intent = new Intent(this, SceneActivity.class);
        startActivity(intent);
    }

    public void fillCharacters(){
        JSONObject obj;
        try {
            obj = new JSONObject(Util.loadJSONFromAsset(this, "characters.json"));
            chars = obj.getJSONArray("characters");
            for (int i = 0; i<chars.length(); i++){
                JSONObject charac = chars.getJSONObject(i);
                int resourceId = getResources().getIdentifier(charac.getString("characterImage"), "drawable", getPackageName());
                characs = ArrayUtils.appendToArray(characs, resourceId);
            }}catch (JSONException e){
            e.printStackTrace();
        }
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.images_scroll);
        for (int i =0; i<characs.length; i++) {
            ImageView iv = new ImageView (this);
            iv.setImageResource(characs[i]);
            iv.setId(R.id.character+i);
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            iv.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          for (int i = 0; i<chars.length(); i++) {
                                              if (v.getId() == R.id.character + i) {
                                                  ImageView ivSelected = (ImageView) findViewById(R.id.character + i);
                                                  ImageView ivPreviousSelected = (ImageView) findViewById(prevIdCharacter);
                                                  ivPreviousSelected.setImageResource(prevSelecCharacter);
                                                  ivPreviousSelected.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                                  ivSelected.setImageResource(R.drawable.checknewdimen);
                                                  ivSelected.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                                  prevSelecCharacter=characs[i];
                                                  prevIdCharacter =R.id.character+i;
                                                  SharedPreferences characSP= getSharedPreferences("characterSelected", 0);
                                                  SharedPreferences.Editor characEditor = characSP.edit();
                                                  characEditor.putInt("drawableCharac", characs[i]);
                                                  characEditor.commit();
                                              }
                                          }
                                      }});
            linearLayout.addView(iv);
        }

    }
}
