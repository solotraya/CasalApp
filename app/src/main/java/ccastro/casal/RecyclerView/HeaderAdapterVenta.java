package ccastro.casal.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ccastro.casal.FacturaActivity;
import ccastro.casal.R;

/**
 * @author Carlos Alberto Castro Cañabate
 *
 * Classe adaptador que segueix el patró de disseny viewHolder i defineix classe interna que extend
 * de RecyclerView.ViewHolder
 */


public class HeaderAdapterVenta extends RecyclerView.Adapter<HeaderAdapterVenta.ViewHolder> {
    private ArrayList<HeaderVenta> mDataset;

    /**
     * Constructor de la clase Headeradapter
     * @param myDataset dataSet
     */
    public HeaderAdapterVenta(ArrayList<HeaderVenta> myDataset) {
        mDataset = myDataset;
    }

    /**
     * Mètode que crea vistes noves (invocades pel gestor de disseny)
     * @param parent view parent
     * @param viewType tipus view
     * @return viewHolder
     */
    @Override
    public HeaderAdapterVenta.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // crea una nova vista
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_header_adapter_venta, parent, false);
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

        String data = mDataset.get(position).getDataVenta();
        String dataCorrecta[] = data.split(" ");
        String dataFormatSpain = dataCorrecta[2]+"/"+dataCorrecta[1]+"/"+dataCorrecta[0];
        holder.nomClient.setText(mDataset.get(position).getNomClient());
        holder.nomTreballador.setText(mDataset.get(position).getNomTreballador());
        holder.dataVenta.setText(dataFormatSpain);
        holder.ventaPagada.setText(mDataset.get(position).getVentaPagada());
        holder.idVenta.setText(mDataset.get(position).getIdVenta());
        holder.horaVenta.setText(mDataset.get(position).getHoraVenta());
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
        TextView idVenta,nomClient,nomTreballador,dataVenta,ventaPagada,horaVenta;
        View v;
        Context context;
        /**
         * Constructor de classe statica View Holder
         * @param v view
         */
        public ViewHolder(View v) {
            super(v);
            nomClient=(TextView)v.findViewById(R.id.nomClient);
            nomTreballador=(TextView)v.findViewById(R.id.nomTreballador);
            dataVenta=(TextView) v.findViewById(R.id.dataVenta);
            idVenta = (TextView) v.findViewById(R.id.idVenta);
            ventaPagada = (TextView) v.findViewById(R.id.ventaPagada);
            horaVenta = (TextView) v.findViewById(R.id.horaVenta);


            idVenta.setVisibility(View.GONE);
            context = itemView.getContext();
            v.setOnClickListener(this);
        }

        /**
         * Mètode per gestionar l'esdeveniment onClick
         * @param view que interacturarà
         */
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,FacturaActivity.class);
            intent.putExtra("ID_VENTA",idVenta.getText().toString());
            intent.putExtra("DATA_VENTA",dataVenta.getText().toString());
            intent.putExtra("HORA_VENTA",horaVenta.getText().toString());
            intent.putExtra("ESTAT_VENTA",ventaPagada.getText().toString());
            intent.putExtra("NOM_CLIENT",nomClient.getText().toString());
            intent.putExtra("NOM_TREBALLADOR",nomTreballador.getText().toString());
            context.startActivity(intent);
        }
    }

    /**
     * Mètode per actualitzar el recycler un cop canviat el filtratge.
     * @param llistaConsultes arrayList
     */
    public void actualitzaRecycler(ArrayList<HeaderVenta> llistaConsultes) {
        mDataset = llistaConsultes;
        this.notifyDataSetChanged();
    }

}