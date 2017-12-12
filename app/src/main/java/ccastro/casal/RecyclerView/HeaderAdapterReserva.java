package ccastro.casal.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ccastro.casal.FacturaActivity;
import ccastro.casal.LoginActivity;
import ccastro.casal.R;
import ccastro.casal.ReservaActivity;
import ccastro.casal.SQLite.DBInterface;
import ccastro.casal.Utils.Cursors;
import ccastro.casal.Utils.Utilitats;

/**
 * Created by Carlos on 17/11/2017.
 */

public class HeaderAdapterReserva extends RecyclerView.Adapter<HeaderAdapterReserva.ViewHolder> {
    private ArrayList<HeaderReserva> mDataset;

    /**
     * Constructor de la clase Headeradapter
     * @param myDataset dataSet
     */
    public HeaderAdapterReserva(ArrayList<HeaderReserva> myDataset) {
        mDataset = myDataset;
    }

    /**
     * Mètode que crea vistes noves (invocades pel gestor de disseny)
     * @param parent view parent
     * @param viewType tipus view
     * @return viewHolder
     */
    @Override
    public HeaderAdapterReserva.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // crea una nova vista
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_header_adapter_reserva, parent, false);
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

        holder.idClient.setText(mDataset.get(position).getIdClient());
        holder.nomClient.setText(mDataset.get(position).getNomClient());
        holder.tipusClient.setText(mDataset.get(position).getTipusClient());
        holder.tipoPago.setText(mDataset.get(position).getTipoPago());
        holder.tipusComida.setText(mDataset.get(position).getTipoComida());
        holder.observaciones.setText(mDataset.get(position).getObservacions());
        String pagoRealizado = mDataset.get(position).getPagado();
        if (pagoRealizado.equalsIgnoreCase("1")) holder.pagado.setChecked(true);
        String assistenciaChequeada = mDataset.get(position).getAssistencia();
        if (assistenciaChequeada.equalsIgnoreCase("1")) holder.assistenciaReserva.setChecked(true);
        holder.pagado.setEnabled(false);
        holder.assistenciaReserva.setEnabled(false);
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

        TextView idClient,nomClient,tipusClient,textViewPagado, tipusComida, tipoPago, observaciones;
        CheckBox pagado,assistenciaReserva;
        View v;
        Context context;
        /**
         * Constructor de classe statica View Holder
         * @param v view
         */
        public ViewHolder(View v) {
            super(v);
            idClient=(TextView) v.findViewById(R.id.idClient);
            nomClient = (TextView) v.findViewById(R.id.nomClientReserva);
            tipusClient = (TextView) v.findViewById(R.id.tipusClientReserva);
            assistenciaReserva = (CheckBox) v.findViewById(R.id.assistenciaReserva);
            pagado = (CheckBox) v.findViewById(R.id.pagadoReserva);
            context = itemView.getContext();
            textViewPagado = (TextView) v.findViewById(R.id.textViewPagado);
            tipoPago = (TextView) v.findViewById(R.id.tipoPago);
            tipusComida = (TextView) v.findViewById(R.id.tipoComida);
            observaciones = (TextView) v.findViewById(R.id.observaciones);
            v.setOnClickListener(this);
        }


        /**
         * Mètode per gestionar l'esdeveniment onClick
         * @param view que interacturarà
         */
        @Override
        public void onClick(View view) {

            v = view;
            if  (!pagado.isChecked() && !assistenciaReserva.isChecked()) {   // Si no tiene ausencia ni pago.
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Selecciona la opción a realizar!")
                        .setTitle("Atención!!")
                        .setNegativeButton("AUSENTAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.cancel();
                               // v.setBackgroundColor(Color.rgb(255, 51, 30));
                                Toast.makeText(v.getContext(), "Ausencia Marcada!", Toast.LENGTH_LONG).show();
                                assistenciaReserva.setChecked(true);
                                Log.d("FECHA RESERVA_ ",ReservaActivity.dataReserva);
                                DBInterface db=new DBInterface(v.getContext());
                                db.obre();
                                db.ActualitzarAsistenciaReserva(idClient.getText().toString(), ReservaActivity.dataReserva);
                                // TODO: Hay que descontar un menu en caso de tener varios, o eliminar factura si no tiene ninguno.
                                Cursor cursorVentaFactura = db.EncontrarId_VentaFacturaSinPagar(idClient.getText().toString());
                                Integer idVentaFactura = Cursors.cursorIDVentaFactura(cursorVentaFactura);
                                Log.d("VENTA SIN PAGAR: ",Integer.toString(idVentaFactura));
                                Log.d("TIPO PAGO: ",tipoPago.getText().toString());
                                Boolean restarProducte = true;
                                Cursor cursorQuantitatReservesSenseIDVenta = db.ObtenirQuantitatReservesSenseIDVenta(idClient.getText().toString());
                                Integer quantitatReservesAbiertasCliente = Cursors.cursorQuantitat(cursorQuantitatReservesSenseIDVenta);
                                Cursor cursorQuantitatProducteFactura = db.ObtenirQuantitatProductesFactura(Integer.toString(idVentaFactura));
                                Integer quantitatProductesFactura = Cursors.cursorQuantitat(cursorQuantitatProducteFactura);
                                Log.d("NUM_PROD",Integer.toString(quantitatProductesFactura));
                                if (quantitatReservesAbiertasCliente >= 0 && quantitatProductesFactura>0){
                                    if (restarProducte){
                                        if (tipoPago.getText().toString().equalsIgnoreCase("0")) db.InserirFactura(1,idVentaFactura,-1);
                                        else if (tipoPago.getText().toString().equalsIgnoreCase("1")) db.InserirFactura(2,idVentaFactura,-1);
                                        else if (tipoPago.getText().toString().equalsIgnoreCase("2")) db.InserirFactura(3,idVentaFactura,-1);
                                        quantitatProductesFactura--;
                                    }

                                    if (quantitatProductesFactura <= 0){
                                        db.ActalitzaEstatVenta(Integer.toString(idVentaFactura),"2");

                                        cursorQuantitatReservesSenseIDVenta = db.ObtenirQuantitatReservesSenseIDVenta(idClient.getText().toString());
                                        quantitatReservesAbiertasCliente = Cursors.cursorQuantitat(cursorQuantitatReservesSenseIDVenta);
                                        Log.d("CANTIDAD RESERVAS 2_",Integer.toString(quantitatReservesAbiertasCliente));
                                        if (quantitatReservesAbiertasCliente > 0){
                                            Date ahora = new Date();
                                            SimpleDateFormat formateador = new SimpleDateFormat("hh:mm");
                                            String hora = formateador.format(ahora);
                                            //       *** CAMBIAR POR FEHCA Y HORA ACTUAL ***
                                            db.InserirVenta(Integer.parseInt(idClient.getText().toString()),Integer.parseInt(LoginActivity.ID_TREBALLADOR), Utilitats.obtenerFechaActual(),"0",hora);
                                            cursorVentaFactura = db.EncontrarId_VentaFacturaSinPagar(idClient.getText().toString());
                                            idVentaFactura = Cursors.cursorIDVentaFactura(cursorVentaFactura);
                                            // idVenta = Integer.toString(idVentaFactura);
                                            // AÑADIMOS EL PRODUCTO QUE DEBEREMOS
                                            if (tipoPago.getText().toString().equalsIgnoreCase("0")) db.InserirFactura(1,idVentaFactura,-1);
                                            else if (tipoPago.getText().toString().equalsIgnoreCase("1")) db.InserirFactura(2,idVentaFactura,-1);
                                            else if (tipoPago.getText().toString().equalsIgnoreCase("2")) db.InserirFactura(3,idVentaFactura,-1);
                                            db.ActalitzaEstatVenta(Integer.toString(idVentaFactura),"3"); // TODO PONEMOS ESTADO DE VENTA EN REEMBOLSAR (estat 3)
                                        }
                                    }
                                }
                                db.tanca();
                            }
                        })
                        .setPositiveButton("PAGAR",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        Intent intent = new Intent(context,FacturaActivity.class);
                                        intent.putExtra("ID_CLIENT",idClient.getText().toString());
                                        intent.putExtra("NOM_CLIENT_RESERVA",nomClient.getText());
                                        context.startActivity(intent);
                                        Log.d("Proba:", "acces");
                                    }
                                }
                        );
                AlertDialog alert = builder.create();
                alert.show();
            } else if (pagado.isChecked() && !assistenciaReserva.isChecked()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("El cliente cobrado no ha venido?")
                        .setTitle("Atención!!")
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.cancel();

                            }
                        })
                        .setPositiveButton("AUSENTAR",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        assistenciaReserva.setChecked(true);

                                        DBInterface db = new DBInterface(v.getContext());
                                        db.obre();
                                        db.ActualitzarAsistenciaReserva(idClient.getText().toString(),ReservaActivity.dataReserva);
                                        Cursor cursorVentaFactura = db.EncontrarId_VentaFacturaSinPagar(idClient.getText().toString());
                                        Integer idVentaFactura = Cursors.cursorIDVentaFactura(cursorVentaFactura);
                                        Log.d("VENTA SIN PAGAR: ",Integer.toString(idVentaFactura));
                                        // TODO ESTA VENTA SE LE DEBERA AL CLIENTE
                                        if (idVentaFactura==-1){ // Si no tienen una factura pendiente por pagar
                                            Date ahora = new Date();
                                            SimpleDateFormat formateador = new SimpleDateFormat("hh:mm");
                                            String hora = formateador.format(ahora);
                                            //       *** CAMBIAR POR FEHCA Y HORA ACTUAL ***
                                            db.InserirVenta(Integer.parseInt(idClient.getText().toString()),Integer.parseInt(LoginActivity.ID_TREBALLADOR), Utilitats.obtenerFechaActual(),"0",hora);
                                            cursorVentaFactura = db.EncontrarId_VentaFacturaSinPagar(idClient.getText().toString());
                                            idVentaFactura = Cursors.cursorIDVentaFactura(cursorVentaFactura);
                                            // idVenta = Integer.toString(idVentaFactura);
                                        }
                                        if (tipoPago.getText().toString().equalsIgnoreCase("0")) db.InserirFactura(1,idVentaFactura,-1);
                                        else if (tipoPago.getText().toString().equalsIgnoreCase("1")) db.InserirFactura(2,idVentaFactura,-1);
                                        else if (tipoPago.getText().toString().equalsIgnoreCase("2")) db.InserirFactura(3,idVentaFactura,-1);
                                        db.ActalitzaEstatVenta(Integer.toString(idVentaFactura),"3"); // TODO PONEMOS ESTADO DE VENTA EN REEMBOLSAR (estat 3)
                                        db.tanca();
                                    }
                                }
                        );
                AlertDialog alert = builder.create();
                alert.show();
            } else if  (!pagado.isChecked() && assistenciaReserva.isChecked()) {
                Toast.makeText(v.getContext(), "Un cliente ausente no te puede pagar!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), "No se pueden realizar mas gestiones con esta reserva", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Mètode per actualitzar el recycler un cop canviat el filtratge.
     * @param llistaConsultes arrayList
     */
    public void actualitzaRecycler(ArrayList<HeaderReserva> llistaConsultes) {
        mDataset = llistaConsultes;
        this.notifyDataSetChanged();
    }

}
