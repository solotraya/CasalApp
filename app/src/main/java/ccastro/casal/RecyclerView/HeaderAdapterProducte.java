package ccastro.casal.RecyclerView;

/**
 * Created by Carlos on 06/12/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import ccastro.casal.PedidoActivity;
import ccastro.casal.ProductoActivity;
import ccastro.casal.R;

/**
 * @author Carlos Alberto Castro Cañabate
 *
 * Classe adaptador que segueix el patró de disseny viewHolder i defineix classe interna que extend
 * de RecyclerView.ViewHolder
 */


public class HeaderAdapterProducte extends RecyclerView.Adapter<HeaderAdapterProducte.ViewHolder> {
    private ArrayList<HeaderProducte> mDataset;

    /**
     * Constructor de la clase Headeradapter
     * @param myDataset dataSet
     */
    public HeaderAdapterProducte(ArrayList<HeaderProducte> myDataset) {
        mDataset = myDataset;
    }

    /**
     * Mètode que crea vistes noves (invocades pel gestor de disseny)
     * @param parent view parent
     * @param viewType tipus view
     * @return viewHolder
     */
    @Override
    public HeaderAdapterProducte.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // crea una nova vista
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_header_adapter_producte, parent, false);
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

        holder.idProducte.setText(mDataset.get(position).getIdProducte());
        holder.nomProducte.setText(mDataset.get(position).getNomProducte());
        holder.preuProducte.setText(mDataset.get(position).getPreuProducte());
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
        LinearLayout layoutProducte;
        TextView total, idProducte,nomProducte,preuProducte, quantitatProducte;
        Button seleccionarProducte;
        View v;
        Context context;

        /**
         * Constructor de classe statica View Holder
         * @param v view
         */
        public ViewHolder(View v) {
            super(v);
            idProducte=(TextView)v.findViewById(R.id.idProducte);
            nomProducte=(TextView)v.findViewById(R.id.nomProducte);
            preuProducte=(TextView) v.findViewById(R.id.preuProducte);
            layoutProducte = (LinearLayout) v.findViewById(R.id.layoutButtonsProducte);
            seleccionarProducte = (Button) v.findViewById(R.id.seleccionarProducte);
            quantitatProducte = (TextView) v.findViewById(R.id.quantitatProducte);
            total = (TextView) v.findViewById(R.id.preuTotalProductes);

            idProducte.setVisibility(View.GONE);
            layoutProducte.setVisibility(View.GONE);
            context = itemView.getContext();

            v.setOnClickListener(this);
            v.findViewById(R.id.masProducte).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Integer quantitat = Integer.parseInt(quantitatProducte.getText().toString());
                    if (quantitat<100){
                        quantitat++;
                        quantitatProducte.setText(Integer.toString(quantitat));
                        DecimalFormat df = new DecimalFormat("0.00");
                        df.setMaximumFractionDigits(2);
                        Float preuTotal = quantitat*Float.parseFloat(preuProducte.getText().toString());
                        total.setText(df.format(preuTotal));
                    }
                }
            });
            v.findViewById(R.id.menosProducte).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Integer quantitat = Integer.parseInt(quantitatProducte.getText().toString());
                    if (quantitat>1){
                        quantitat--;
                        quantitatProducte.setText(Integer.toString(quantitat));
                        quantitatProducte.setText(Integer.toString(quantitat));
                        DecimalFormat df = new DecimalFormat("0.00");
                        df.setMaximumFractionDigits(2);
                        Float preuTotal = quantitat*Float.parseFloat(preuProducte.getText().toString());
                        total.setText(df.format(preuTotal));
                    }
                }
            });
            seleccionarProducte.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(context, PedidoActivity.class);
                    intent.putExtra("ID_PRODUCTE",idProducte.getText().toString());
                    intent.putExtra("QUANTITAT",quantitatProducte.getText().toString());
                    intent.putExtra("NOM_PRODUCTE",nomProducte.getText().toString());
                    intent.putExtra("TOTAL_PRODUCTE",total.getText().toString());
                    context.startActivity(intent);
                    ((ProductoActivity)context).finish();
                }
            });
        }

        /**
         * Mètode per gestionar l'esdeveniment onClick
         * @param view que interacturarà
         */
        @Override
        public void onClick(View view) {
            layoutProducte.setVisibility(View.VISIBLE);
            //  HACER QUE AL CLICAR SE PONGA EN VISIBLE EL LAYOUT CON LOS BOTONES PARA CANTIDAD Y AÑADIR
            if (quantitatProducte.getText().toString().equals("1")){
                total.setText(preuProducte.getText().toString());
            }
        }
    }

    /**
     * Mètode per actualitzar el recycler un cop canviat el filtratge.
     * @param llistaConsultes arrayList
     */
    public void actualitzaRecycler(ArrayList<HeaderProducte> llistaConsultes) {
        mDataset = llistaConsultes;
        this.notifyDataSetChanged();
    }

}