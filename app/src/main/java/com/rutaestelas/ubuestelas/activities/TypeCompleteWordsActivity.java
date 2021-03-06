package com.rutaestelas.ubuestelas.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rutaestelas.ubuestelas.R;
import com.rutaestelas.ubuestelas.util.LettersAdapter;
import com.rutaestelas.ubuestelas.util.Util;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad donde se carga la prueba de completar los huecos que faltan en una frase.
 *
 * @author Marcos Pena
 */
public class TypeCompleteWordsActivity extends AppCompatActivity {

    private JSONObject fileToRead;
    private static String markName;
    private String fileNameMark;

    private EditText textToComplete;

    private int relativePosition = 0;
    private int absolutePosition = 0;

    private int attempts = 0;

    private final List<Double> errorsAttempt = new ArrayList<>();

    private boolean hintUsed = false;

    /**
     * Inicializa la actividad con su respectivo layout. Se llama a otros métodos para inicializar el resto de la actividad.
     * @param savedInstanceState Si la actividad se ha reiniciado se le pasa el contenido de datos más reciente.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_complete_words);

        init();

        fillGrid();

        Bundle bundle = getIntent().getExtras();
        boolean first = bundle.getBoolean("first_game");
        if(first){
            Button button = new Button(this);
            button.setText(R.string.ok);
            button.setTextSize(24);
            button.setTextColor(Color.BLACK);
            button.setBackgroundColor(Color.WHITE);
            Target target = new ViewTarget(R.id.hint_complete, this);
            new ShowcaseView.Builder(this)
                    .setTarget(target)
                    .setContentTitle(R.string.hint)
                    .setContentText(R.string.hint_explain)
                    .replaceEndButton(button)
                    .setStyle(R.style.CustomShowcaseTheme)
                    .hideOnTouchOutside()
                    .build();
        }
    }

    /**
     * Muestra la frase original con los huecos en los que hay que rellenar las letras que faltan.
     */
    private void init(){
        try{
            SharedPreferences sharedPref = getSharedPreferences("navDrawFileName", 0);
            fileNameMark = sharedPref.getString("fileName", "error");
            String[] splitName = fileNameMark.split("\\.");
            markName = splitName[0];
            fileToRead = new JSONObject(Util.loadJSONFromAsset(getApplicationContext(), fileNameMark));
            String text = fileToRead.getString("text");
            JSONArray gaps = fileToRead.getJSONArray("gaps");
            JSONObject gap = gaps.getJSONObject(relativePosition);
            absolutePosition = gap.getInt("position");
            text = text.replace("%l%", "_");
            textToComplete = findViewById(R.id.text_to_complete);
            textToComplete.setText(text);
            textToComplete.setTextSize(24);
            textToComplete.setBackground(getDrawable(R.drawable.border));
            textToComplete.setKeyListener(null);
            selectPosition();
            textToComplete.requestFocus();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Completa la malla con las opciones de letras que se obtienen del adaptador.
     */
    private void fillGrid(){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String difficulty = sharedPreferences.getString("difficulty", "easy");

        GridView gridView = findViewById(R.id.letters_selection);

        gridView.setAdapter(new LettersAdapter(this, fileNameMark, relativePosition, difficulty));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            /**
             * Callback method to be invoked when an item in this AdapterView has
             * been clicked.
             * <p>
             * Implementers can call getItemAtPosition(relativePosition) if they need
             * to access the data associated with the selected item.
             *
             * @param parent   The AdapterView where the click happened.
             * @param view     The view within the AdapterView that was clicked (this
             *                 will be a view provided by the adapter)
             * @param position The relativePosition of the view in the adapter.
             * @param id       The row id of the item that was clicked.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView letter = (TextView) view;
                CharSequence letts = letter.getText();
                textToComplete.getText().replace(absolutePosition-1,absolutePosition,letts);
            }
        });
    }

    /**
     * Cuando se pulsa la flecha hacia la izquierda se llama a este método.
     * Mueve el cursor en la frase hacia el siguiente hueco disponible
     * a la izquierda.
     * @param view La vista que se ha clickado.
     */
    public void leftArrowPressed(View view){
        try {
            if(relativePosition != 0){
                relativePosition--;
                fillGrid();
                JSONArray gaps = fileToRead.getJSONArray("gaps");
                JSONObject gap = gaps.getJSONObject(relativePosition);
                absolutePosition = gap.getInt("position");
                selectPosition();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Cuando se pulsa la flecha hacia la derecha se llama a este método.
     * Mueve el cursor en la frase hacia el siguiente hueco disponible
     * a la derecha.
     * @param view La vista que se ha clickado.
     */
    public void rightArrowPressed(View view){
        try {
            JSONArray gaps = fileToRead.getJSONArray("gaps");
            if(relativePosition != gaps.length()-1){
                relativePosition++;
                fillGrid();
                JSONObject gap = gaps.getJSONObject(relativePosition);
                absolutePosition = gap.getInt("position");
                selectPosition();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Remarca el hueco que está seleccionado.
     */
    private void selectPosition(){
        textToComplete.setSelection(absolutePosition-1,absolutePosition);
    }

    /**
     * Comprueba si todos los huecos tienen alguna letra colocada.
     * @return true si no queda ningún hueco, false si algún hueco todavía está libre.
     */
    private boolean isAllPlaced(){
        return !textToComplete.getText().toString().contains("_");
    }

    /**
     * Comprueba si la frase tiene todas las letras correctas.
     * @return true si la frase es correcta, false si no lo es.
     */
    private boolean checkCorrect(){
        boolean solved = true;
        List<Integer> wrongPositions = new ArrayList<>();
        double errorsNumber = 0;
        try {
            JSONArray gaps = fileToRead.getJSONArray("gaps");
            for(int i = 0; i< gaps.length(); i++){
                JSONObject gap = gaps.getJSONObject(i);
                int position = gap.getInt("position");
                String correct = gap.getString("correct");
                if(textToComplete.getText().toString().charAt(position - 1) != correct.charAt(0)){
                    solved=false;
                    wrongPositions.add(position);
                    errorsNumber++;
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        if(!solved){
            indicateWrong(wrongPositions);
            errorsAttempt.add(errorsNumber);
        }
        return solved;
    }

    /**
     * Se llama cuando se le da al botón de enviar. Tras la comprobación de si es correcta la frase,
     * en caso de serlo, se hace el cálculo de la puntuación, se guarda la información en el fichero de guardado
     * y se finaliza la actividad. Si no es correcta, se muestra un mensaje y se continúa jugando.
     * @param view La vista que se ha clickado.
     */
    public void sendPhrase(View view){
        if(isAllPlaced()){
            attempts++;
            try {
                if(checkCorrect()){
                    double score;
                    if (attempts==1){
                        score=100.00;
                        if(hintUsed){
                            SharedPreferences sharedPreferences =
                                    PreferenceManager.getDefaultSharedPreferences(this);
                            String difficulty = sharedPreferences.getString("difficulty", "easy");
                            switch (difficulty){
                                case "easy":
                                    score = score-(score * 0.05);
                                    break;
                                case "normal":
                                    score = score-(score * 0.1);
                                    break;
                                case "hard":
                                    score = score-(score * 0.2);
                                    break;
                            }
                        }
                    }else {
                        JSONArray gaps = fileToRead.getJSONArray("gaps");
                        JSONObject gap = gaps.getJSONObject(0);
                        String [] letts = gap.getString("options").split(",");
                        score = Util.completeWordsScoreIfFail(attempts, gaps.length(), errorsAttempt, letts.length, hintUsed, this);
                    }
                    Toast.makeText(this,getString(R.string.correct) + ". " + getString(R.string.points_obtained, score), Toast.LENGTH_SHORT).show();
                    JSONObject obj;
                    obj = new JSONObject(Util.loadJSONFromFilesDir(this, "userInfo"));
                    JSONArray marks = obj.getJSONArray("marks");
                    for (int i = 0; i < marks.length(); i++) {
                        JSONObject mark = marks.getJSONObject(i);
                        if(markName.equals(mark.getString("mark"))){
                            mark.put("solved", true);
                            if (markName.equals(mark.getString("mark"))) {
                                mark.put("solved", true);
                                if (score == 100) {
                                    mark.put("color", "green");
                                } else if (score == 0) {
                                    mark.put("color", "red");
                                } else if (score > 0 && score < 100) {
                                    mark.put("color", "yellow");
                                } else {
                                    mark.put("color", "azure");
                                }
                            }
                        }
                    }
                    obj.put("marks", marks);
                    double scoreFile = obj.getDouble("score");
                    scoreFile += score;
                    double scoreToFile = Math.round(scoreFile*100)/100.0;
                    obj.put("score", scoreToFile);
                    Util.writeJSONToFilesDir(this,"userInfo", obj.toString());
                    SharedPreferences scoreSP = getSharedPreferences("scoreEvent", 0);
                    SharedPreferences.Editor scoreEditor = scoreSP.edit();
                    scoreEditor.putString("score", String.valueOf(score));
                    scoreEditor.apply();
                    SharedPreferences nameFileSP = getSharedPreferences("nameFileSP", 0);
                    SharedPreferences.Editor nameFileEditor = nameFileSP.edit();
                    nameFileEditor.putString("fileName", markName);
                    nameFileEditor.apply();
                    Intent intent = new Intent(this, DidYouKnowActivity.class);
                    intent.putExtra("score", score);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(this,getString(R.string.incorrect) + ". " + getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this,getString(R.string.incorrect) + ". " + getString(R.string.complete_gaps), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Si la frase no es correcta marca en color rojo las letras que están equivocadas.
     * @param wrongPositions Lista con las posiciones de las letras incorrectas.
     */
    private void indicateWrong(List<Integer> wrongPositions){
        Spannable modifiedText = new SpannableString(textToComplete.getText());
        modifiedText.setSpan(new ForegroundColorSpan(getColor(R.color.colorBlack)), 0, modifiedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textToComplete.setText(modifiedText);
        for(Integer position : wrongPositions) {
            modifiedText = textToComplete.getText();
            modifiedText.setSpan(new ForegroundColorSpan(getColor(R.color.colorWrong)), position-1, position, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textToComplete.setText(modifiedText);
        }
    }

    /**
     * Al pulsar en el icono de la pista se llama a este método.
     * Muestra un diálogo con la pista correspondiente a esa prueba.
     * @param view Vista que se ha clickado.
     */
    public void hintClick(View view){
        try {
            hintUsed=true;
            String hint = fileToRead.getString("hint");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.hint).setMessage(hint).setPositiveButton(R.string.ok,null);
            AlertDialog dialog = builder.create();
            dialog.show();
            ImageButton imageButton = findViewById(R.id.hint_complete);
            imageButton.setImageResource(R.drawable.game_hint_used);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
