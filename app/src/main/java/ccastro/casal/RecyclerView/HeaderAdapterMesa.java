package ccastro.casal.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ccastro.casal.R;
import ccastro.casal.ReservaActivity;

/**
 * @author Carlos Alberto Castro Cañabate
 *
 * Classe adaptador que segueix el patró de disseny viewHolder i defineix classe interna que extend
 * de RecyclerView.ViewHolder
 */


public class HeaderAdapterMesa extends RecyclerView.Adapter<HeaderAdapterMesa.ViewHolder> {
    private ArrayList<HeaderMesa> mDataset;

    /**
     * Constructor de la clase Headeradapter
     * @param myDataset dataSet
     */
    public HeaderAdapterMesa(ArrayList<HeaderMesa> myDataset) {
        mDataset = myDataset;
    }

    /**
     * Mètode que crea vistes noves (invocades pel gestor de disseny)
     * @param parent view parent
     * @param viewType tipus view
     * @return viewHolder
     */
    @Override
    public HeaderAdapterMesa.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // crea una nova vista
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_header_adapter_mesa, parent, false);
        // Estableix la mida de la vista, els marges, els farcits i els paràmetres de disseny
        return  new ViewHolder(v);
    }

    /**
     * Mètode que reemplaça els continguts d'una vista (invocada pel gestor de disseny)
     * @param holder holder
     * @param position posició
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Obteniu un element del vostre conjunt de dades en aquesta posició
        // Reemplaça els continguts de la vista amb aquest element
         holder.idMesa.setText(mDataset.get(position).getIdMesa());
         holder.nomMesa.setText(mDataset.get(position).getNombreMesa());
         holder.diaReservado.setText(mDataset.get(position).getDiaReservado());
    }

    /**
     * Mètode per retornar el tamany del dataset invocat per el layout manager
     * @return tamany de mDataset.
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // nom,dni,data,qr,localitzacio,email,check;
        TextView nomMesa, idMesa, diaReservado;
        View v;
        Context context;
        /**
         * Constructor de classe statica View Holder
         * @param v view
         */
        public ViewHolder(View v) {
            super(v);
            nomMesa=(TextView)v.findViewById(R.id.nomMesa);
            idMesa=(TextView) v.findViewById(R.id.idMesa);
            diaReservado=(TextView)v.findViewById(R.id.diaReservado);

            context = itemView.getContext();
            v.setOnClickListener(this);
        }

        /**
         * Mètode per gestionar l'esdeveniment onClick
         * @param view que interacturarà
         */
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(context,ReservaActivity.class);
            intent.putExtra("ID_MESA",idMesa.getText().toString());
            intent.putExtra("NOM_MESA",nomMesa.getText().toString());
            intent.putExtra("DIA_RESERVADO",diaReservado.getText().toString());
            context.startActivity(intent);
        }
    }

    /**
     * Mètode per actualitzar el recycler un cop canviat el filtratge.
     * @param llistaConsultes arrayList
     */
    public void actualitzaRecycler(ArrayList<HeaderMesa> llistaConsultes) {
        mDataset = llistaConsultes;
        this.notifyDataSetChanged();
    }
}