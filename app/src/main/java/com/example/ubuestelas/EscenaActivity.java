package com.example.ubuestelas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class EscenaActivity extends AppCompatActivity {

    public static final int NUMERO_ESCENAS=4;

    private int contador=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escena);

    }

    public void cambiarEscena (View view){
        ImageView imagen = (ImageView) findViewById(R.id.imagen_escena);
        TextView texto = (TextView) findViewById(R.id.texto_escena);
        contador++;
        switch (contador){
            case 2:
                imagen.setImageResource(R.drawable.cararomano);
                texto.setText(R.string.textoEscena2);
                break;
            case 3:
                imagen.setImageResource(R.drawable.romano);
                texto.setText(R.string.textoEscena3);
                break;
            case NUMERO_ESCENAS:
                imagen.setImageResource(R.drawable.cararomano);
                texto.setText(R.string.textoEscena4);
                break;
            default:
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;

        }
    }
}
