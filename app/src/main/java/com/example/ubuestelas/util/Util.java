package com.example.ubuestelas.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Util {

    static final double RADIO_TIERRA = 6371;

    public static String loadJSONFromAsset(Context context, String name) {
        String json;
        try {
            InputStream is = context.getAssets().open(name);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public static String loadJSONFromFilesDir(Context context, String name) {
        String json;
        try {
            File file = new File(context.getFilesDir(), name);
            StringBuffer output = new StringBuffer();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                output.append(line + "\n");
            }
            json = output.toString();
            bufferedReader.close();

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }


    public static double getDistanceFromLatLong(LatLng latLng1, LatLng latLng2){
        double p = Math.PI/180;
        double distance;
        double km;
        distance=0.5-Math.cos((latLng2.latitude - latLng1.latitude) * p)/2 +
                Math.cos(latLng1.latitude * p) * Math.cos(latLng2.latitude * p) *
                        (1 - Math.cos((latLng2.longitude - latLng1.longitude) * p))/2;
        km = 2*RADIO_TIERRA * Math.asin(Math.sqrt(distance));
        return km * 1000; //distancia en metros

    }

    //Siempre que sea de solución única
    public static double testScoreIfFail(Context context, double optionsNumber, double attemptNumber){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String difficulty = sharedPreferences.getString("difficulty", "easy");
        double score;
        if (attemptNumber >= optionsNumber){
            return 0;
        }
        score=1-(attemptNumber/optionsNumber);
        score *= 100;
        double scoreF = Math.round(score*100)/100.0;
//        String strScore = String.format("%.2f", score);
//        score = Double.valueOf(strScore);
        return scoreF;
    }

    public static void writeJSONToFilesDir(Context context, String name, String data){
        try {
            File file = new File(context.getFilesDir(), name);
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(data);
            bufferedWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static double puzzleSolvedScore(Context context, CharSequence text) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String difficulty = sharedPreferences.getString("difficulty", "easy");
        double score = 0;
        String minutes = "" + text.charAt(0) + text.charAt(1);
        String seconds = "" + text.charAt(3) + text.charAt(4);
        double secondsTot = (Integer.parseInt(minutes)*60)+ Integer.parseInt(seconds);
        switch (difficulty){
            case "easy":
                if(secondsTot<=90){
                    score=100;
                }else if (secondsTot > 90 && secondsTot <=180){
                    double diff = secondsTot - 90;
                    score=100-diff;
                }else{
                    score=0;
                }
                break;
            case "mid":
                if(secondsTot<=60){
                    score=100;
                }else if (secondsTot > 60 && secondsTot <=120){
                    double diff = secondsTot - 60;
                    score=100-diff;
                }else{
                    score=0;
                }
                break;
            case "hard":
                if(secondsTot<=45){
                    score=100;
                }else if (secondsTot > 45 && secondsTot <=90){
                    double diff = secondsTot - 45;
                    score=100-diff;
                }else{
                    score=0;
                }
                break;
        }

        return score;
    }

    public static double completeWordsScoreIfFail(Context context, double attemptNumber, double gapNumber, List<Double> errorsAttempt, double lettersNumber){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String difficulty = sharedPreferences.getString("difficulty", "easy");
        double score = 0;
        double counter=0;
        double reduction=1;
        if (attemptNumber >= gapNumber){
            return 0;
        }
        for (Double wrongs : errorsAttempt){
            counter++;
            if(counter==1) {
                score = ((gapNumber - wrongs) + (wrongs * -(1 / (lettersNumber - counter))));
            }else if(counter <= (gapNumber/2)){
                reduction = reduction / 2;
                score = (score + ((wrongs-(reduction * -(1 / (lettersNumber - counter))))));
            }else{
                score = (score + ( -(1 / (lettersNumber - counter))));
            }
        }
        score = score * 100 / gapNumber;
        score = Math.round(score*100)/100.0;
        return score;
    }
}
