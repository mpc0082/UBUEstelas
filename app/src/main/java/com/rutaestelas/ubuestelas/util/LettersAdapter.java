package com.rutaestelas.ubuestelas.util;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rutaestelas.ubuestelas.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Adaptador para la malla de las opciones de las letras de cada hueco.
 *
 * @author Marcos Pena
 */
public class LettersAdapter extends BaseAdapter {

    private final Context context;

    private final String[] letters;

    /**
     * Inicialización de la clase.
     * @param context Contexto de la aplicación.
     * @param fileName Nombre del fichero de la prueba.
     * @param location Posición del hueco en la frase.
     * @param difficulty Dificultad en la que juega el usuario.
     */
    public LettersAdapter(Context context, String fileName, int location, String difficulty){
        this.context=context;
        this.letters=getLettersOptions(fileName, location, difficulty);
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return letters.length;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return letters[position];
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        textView.setText(letters[position]);
        textView.setTextSize(24);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        textView.setBackground(context.getDrawable(R.drawable.border));
        return textView;
    }

    /**
     * Obtiene, del fichero de la prueba, las letras opcionales para el hueco seleccionado.
     * @param fileName Nombre del fichero de la prueba.
     * @param location Posición del hueco en la frase.
     * @param difficulty Dificultad en la que juega el usuario.
     * @return Array con las letras posibles para ese hueco.
     */
    private String[] getLettersOptions(String fileName, int location, String difficulty){
        String[] letts = {};
        JSONObject obj;
        try {
            obj = new JSONObject(Util.loadJSONFromAsset(context, fileName));
            JSONArray gaps = obj.getJSONArray("gaps");
            JSONObject gap = gaps.getJSONObject(location);
            JSONObject opts = gap.getJSONObject("options");
            letts = opts.getString(difficulty).split(",");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return letts;
    }
}
