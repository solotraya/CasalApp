package ccastro.casal.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ccastro.casal.R;

/**
 * @author Carlos Alberto Castro Cañabate
 *
 * Classe adaptador que segueix el patró de disseny viewHolder i defineix classe interna que extend
 * de RecyclerView.ViewHolder
 */


public class HeaderAdapterFactura extends RecyclerView.Adapter<HeaderAdapterFactura.ViewHolder> {
    private ArrayList<HeaderFactura> mDataset;

    /**
     * Constructor de la clase Headeradapter
     * @param myDataset dataSet
     */
    public HeaderAdapterFactura(ArrayList<HeaderFactura> myDataset) {
        mDataset = myDataset;
    }

    /**
     * Mètode que crea vistes noves (invocades pel gestor de disseny)
     * @param parent view parent
     * @param viewType tipus view
     * @return viewHolder
     */
    @Override
    public HeaderAdapterFactura.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // crea una nova vista
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_header_adapter_factura, parent, false);
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

        holder.nomProducte.setText(mDataset.get(position).getNombreProducto());
        holder.tipusProducte.setText(mDataset.get(position).getTipoProducto());
        holder.preuProducte.setText(mDataset.get(position).getPrecioProducto()+"€");
        holder.quantitat.setText(mDataset.get(position).getCantidadProducto());
        holder.total.setText(mDataset.get(position).getPrecioLinea()+"€");
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
        TextView nomProducte,preuProducte,tipusProducte,quantitat,total;
        View v;
        Context context;
        /**
         * Constructor de classe statica View Holder
         * @param v view
         */
        public ViewHolder(View v) {
            super(v);
            nomProducte=(TextView)v.findViewById(R.id.nomProducte);
            preuProducte=(TextView) v.findViewById(R.id.preuProducte);
            tipusProducte = (TextView) v.findViewById(R.id.tipusProducte);
            quantitat = (TextView) v.findViewById(R.id.quantitat);
            total = (TextView) v.findViewById(R.id.total);
            context = itemView.getContext();
            v.setOnClickListener(this);
        }

        /**
         * Mètode per gestionar l'esdeveniment onClick
         * @param view que interacturarà
         */
        @Override
        public void onClick(View view) {
            //  Toast.makeText(view.getContext(), idServei.getText().toString(), Toast.LENGTH_LONG).show();
           // Intent intent = new Intent(context,FacturaActivity.class);
           //  intent.putExtra("ID_VENTA",idVenta.getText().toString());
           // context.startActivity(intent);
        }
    }

    /**
     * Mètode per actualitzar el recycler un cop canviat el filtratge.
     * @param llistaConsultes arrayList
     */
    public void actualitzaRecycler(ArrayList<HeaderFactura> llistaConsultes) {
        mDataset = llistaConsultes;
        this.notifyDataSetChanged();
    }
}