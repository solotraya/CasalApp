package ccastro.casal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import ccastro.casal.RecyclerView.HeaderAdapterFactura;
import ccastro.casal.RecyclerView.HeaderFactura;
import ccastro.casal.SQLite.ContracteBD;
import ccastro.casal.SQLite.DBInterface;
import ccastro.casal.Utils.Cursors;
import ccastro.casal.Utils.Utilitats;

import static ccastro.casal.LoginActivity.NOM_USUARI;
import static ccastro.casal.R.id.ventaPagada;

public class FacturaActivity extends AppCompatActivity {
    DBInterface db;
    TextView dataVenta,horaVenta,nomClient,nomTreballador,estatVenta,preuTotalFactura;
    Button buttonPagar,buttonAñadirProducto;
    static String idVenta;
    String fechaReserva;
    public static String id_cliente; // id_cliente lo cogemos de la reserva.
    public static String nombreCliente; // id_cliente lo cogemos de la reserva.
    private android.support.v7.widget.Toolbar mToolbar;
    View v;
    static Boolean actualizarReserva = false; Boolean idVentaFalta = true;
    String data;
    static Integer idVentaFactura;
    String pagar = "pagar", pagada= "pagada";
    boolean reembolsar = false;
    private HeaderAdapterFactura headerAdapterFactura;
    private ArrayList<HeaderFactura> myDataset;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura);
        db = new DBInterface(this);
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar_factura);
        dataVenta = (TextView) findViewById(R.id.dataVenta);
        horaVenta = (TextView) findViewById(R.id.horaVenta);
        nomClient = (TextView) findViewById(R.id.nomClient);
        nomTreballador = (TextView) findViewById(R.id.nomTreballadorF);
        estatVenta = (TextView) findViewById(ventaPagada);
        preuTotalFactura = (TextView) findViewById(R.id.precioTotalFactura);
        buttonPagar = (Button) mToolbar.findViewById(R.id.buttonPagar);
        buttonAñadirProducto = (Button) mToolbar.findViewById(R.id.buttonAñadirProducto);

        buttonPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v=view;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Verifica que quieras "+pagar+" la factura antes de aceptar")
                        .setTitle("Atencion!!")
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Aceptar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        Toast.makeText(FacturaActivity.this, "Factura "+pagada, Toast.LENGTH_LONG).show();

                                        buttonPagar.setVisibility(View.INVISIBLE);
                                        buttonAñadirProducto.setVisibility(View.INVISIBLE);
                                        DBInterface db=new DBInterface(v.getContext());
                                        db.obre();
                                        if (reembolsar){
                                            db.ActalitzaEstatVenta(idVenta,"4");
                                            estatVenta.setText("Reembolsado");
                                        } else {
                                            db.ActalitzaEstatVenta(idVenta,"1");
                                            estatVenta.setText("Pagado");
                                            //TODO: Si todos los clientes de barra han pagado, los borramos y empezaremos de cero
                                            Cursor cursor = db.obtenirCuantitatClienteBarraSinPagar();
                                            int quantitat = Cursors.cursorQuantitat(cursor);
                                            Log.d("QUANTITAT C.BARRA PAGAT",Integer.toString(quantitat));
                                            if (quantitat==0){
                                                db.EliminarTotsElsClientsDeBarra();
                                            }
                                        }
                                        db.tanca();

                                        // TODO ACTUALIZAR PAGO DE RESERVA, QUIZAS SE PUEDE QUITAR SI SE QUITA RESERVADA_PAGADA Y SE RELACIONA CON ID_VENTA
                                        db.obre();
                                        Log.d("prueba: "," actualizad");
                                      //  db.ActalitzarPagoReservaFecha(id_cliente,obtenerFechaReserva());
                                        db.ActalitzarPagoReservaFecha(id_cliente);
                                        db.tanca();
                                    }
                                }
                        );
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        myDataset = new ArrayList<>();
        headerAdapterFactura= new HeaderAdapterFactura(myDataset);
        db = new DBInterface(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_consulta);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(headerAdapterFactura);

        buttonAñadirProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FacturaActivity.this,PedidoActivity.class);
                startActivity(intent);
                finish();
            //    startActivityForResult(intent,1);


            }
        });
        cogerIntents();
        // TODO  Retorna tots els clients, l'utilitzarem per a la llista que usa el SEARCH VIEW, cuando buscamos cliente!!!

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK){
                id_cliente = data.getStringExtra("ID_CLIENTE");
                nombreCliente = (data.getStringExtra("NOMBRE_CLIENTE"));
                nomClient.setText(nombreCliente);
                if (id_cliente.equalsIgnoreCase("1")){
                    //TODO: SI el cliente elegido es cliente barra creamos un nuevo cliente
                    db.obre();
                    Cursor cursor = db.obtenirNumeroDeClients("Cliente barra");
                    Integer quantitat = Cursors.cursorQuantitat(cursor);
                    Log.d("QUANTITAT CLIENTBARRA",Integer.toString(quantitat));
                    long idCliente = db.InserirClient("Cliente Barra",Integer.toString(quantitat),"",0,"","","Cliente de barra sin identificar");
                    String nombreCliente = "Cliente Barra "+Integer.toString(quantitat);
                    id_cliente = Long.toString(idCliente);
                    Log.d("ID_CLIENTE_BARRA",Long.toString(idCliente));
                    nomClient.setText(nombreCliente);
                    idVentaFactura=null;
                    db.tanca();
                } if (!id_cliente.equalsIgnoreCase("1")){
                    idVenta = data.getStringExtra("ID_VENTA");
                    idVentaFactura = Integer.parseInt(idVenta);
                    idVentaFalta = false;
                    actualizarReserva = true;
                    actualizarRecyclerView();
                    headerAdapterFactura.actualitzaRecycler(myDataset);
                }

            }  else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                Toast.makeText(this, "Selecciona cliente", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    public  ArrayList CursorBD(Cursor cursor){
        float preuProducteQuantitat=0;
        float preuTotal=0;
        ArrayList <String> factura = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);
        if(cursor.moveToFirst()){
            factura.clear();
            do {
                myDataset.add(new HeaderFactura(
                        cursor.getString(cursor.getColumnIndex(ContracteBD.Producte.NOM_PRODUCTE)),
                                cursor.getString(cursor.getColumnIndex(ContracteBD.Producte.PREU_PRODUCTE)),
                                cursor.getString(cursor.getColumnIndex(ContracteBD.Producte.TIPUS_PRODUCTE)),
                                cursor.getString(cursor.getColumnIndex(ContracteBD.Factura.QUANTITAT_PRODUCTE)),
                                df.format(Float.parseFloat(cursor.getString(cursor.getColumnIndex(ContracteBD.Producte.PREU_PRODUCTE)))
                                * Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContracteBD.Factura.QUANTITAT_PRODUCTE))))
                        ));
                if (!actualizarReserva) id_cliente = cursor.getString(cursor.getColumnIndex(ContracteBD.Venta.ID_CLIENT));

                preuProducteQuantitat = Float.parseFloat(cursor.getString(cursor.getColumnIndex(ContracteBD.Producte.PREU_PRODUCTE)))
                        * Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContracteBD.Factura.QUANTITAT_PRODUCTE)));
                preuTotal = preuTotal + preuProducteQuantitat;

                preuTotalFactura.setText(df.format(preuTotal)+"€");
                data = cursor.getString(cursor.getColumnIndex(ContracteBD.Venta.DATA_VENTA));
                String dataCorrecta[] = data.split(" ");
                String dataFormatSpain = dataCorrecta[2]+"/"+dataCorrecta[1]+"/"+dataCorrecta[0];
                nomClient.setText(nombreCliente);
                dataVenta.setText(dataFormatSpain);
                horaVenta.setText(cursor.getString(cursor.getColumnIndex(ContracteBD.Venta.HORA_VENTA)));
                estatVenta.setText(verificarEstadoFactura(cursor.getString(cursor.getColumnIndex(ContracteBD.Venta.VENTA_COBRADA))));
                if (preuTotal>0 && estatVenta.getText().toString().equalsIgnoreCase("Reembolsar")){
                    estatVenta.setText("Falta Pagar");
                    db.obre();
                    db.ActalitzaEstatVenta(idVenta,"0");
                    db.tanca();
                } else if (preuTotal==0 && estatVenta.getText().toString().equalsIgnoreCase("Reembolsar")){
                    estatVenta.setText("Reembolsado");
                    db.obre();
                    db.ActalitzaEstatVenta(idVenta,"4");
                    db.tanca();
                }
                nomTreballador.setText(NOM_USUARI);

            } while(cursor.moveToNext());
        }
        return myDataset;
    }
    public String verificarEstadoFactura (String estado){
        if (estado.equalsIgnoreCase("0")){
            return "Falta pagar";
        }
        else if (estado.equalsIgnoreCase("1")){
            return "Pagado";
        }
        else if (estado.equalsIgnoreCase("2")){
            return "Anulado";
        }
        else if (estado.equalsIgnoreCase("3")){
            buttonPagar.setText("Reembolsar Factura");
            pagar =  "reembolsar"; pagada = "reembolsada";
            reembolsar = true;
            return "Reembolsar";
        }
        else if (estado.equalsIgnoreCase("4")){
            return "Reembolsado";
        }
        return "";
    }
    public  Integer cursorIDVentaFactura(Cursor cursor){
        Integer idVenta=-1;
        if(cursor.moveToFirst()){
            do {
                idVenta=Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContracteBD.Venta._ID)));
            } while(cursor.moveToNext());
        }
        return idVenta;
    }
    public void actualizarRecyclerView(){
        myDataset.clear();
        db.obre();
        Cursor cursor = db.RetornaFacturaIdCliente(id_cliente);
        myDataset = CursorBD(cursor);
        db.tanca();
    }
    public void cogerIntents(){

        if (getIntent().hasExtra("NUEVO_PEDIDO")){   // VIENE DE VENTAS, ES UN PEDIDO NUEVO:
            seleccionarCliente();
            crearNuevoPedido();  // TODO: Desde Ventas entramos a crear nuevo pedido
        }  else if (getIntent().hasExtra("ID_CLIENT")){   // VIENE DE COMEDOR
            Log.d("COMEDOR","true");
            id_cliente = getIntent().getExtras().getString("ID_CLIENT");
            actualizarReserva=true;
            fechaReserva = Utilitats.obtenerFechaActual();
            Log.d("Fecha", fechaReserva);
            if (getIntent().hasExtra("NOM_CLIENT_RESERVA")){
                nombreCliente = (getIntent().getExtras().getString("NOM_CLIENT_RESERVA"));
                nomClient.setText(nombreCliente);
            }

            db.obre();
            Cursor cursorVentaFactura = db.EncontrarId_VentaFacturaSinPagar(id_cliente);
            idVentaFactura = cursorIDVentaFactura(cursorVentaFactura);
            idVenta = Integer.toString(idVentaFactura);
            db.tanca();

            actualizarRecyclerView();

        } else {  // VIENE DE LISTADO DE VENTAS
            Log.d("VENTAS","true");
            if (getIntent().hasExtra("ID_VENTA")){  // pasado desde HeaderAdapterVenta
                idVenta = getIntent().getExtras().getString("ID_VENTA");
            }
            if (getIntent().hasExtra("DATA_VENTA")){
                dataVenta.setText(getIntent().getExtras().getString("DATA_VENTA"));
            }
            if (getIntent().hasExtra("NOM_CLIENT")){
                nombreCliente = (getIntent().getExtras().getString("NOM_CLIENT"));
                nomClient.setText(nombreCliente);
            }
            if (getIntent().hasExtra("NOM_TREBALLADOR")){
                nomTreballador.setText(getIntent().getExtras().getString("NOM_TREBALLADOR"));
            }
            if (getIntent().hasExtra("ESTAT_VENTA")){
                estatVenta.setText(getIntent().getExtras().getString("ESTAT_VENTA"));
                Log.d("ESTADO: ",estatVenta.getText().toString());
                if (estatVenta.getText().toString().equalsIgnoreCase("Pagado") || estatVenta.getText().toString().equalsIgnoreCase("Anulado")
                        || estatVenta.getText().toString().equalsIgnoreCase("Reembolsado")){
                    mToolbar.setVisibility(View.GONE);
                }
            }
            if (getIntent().hasExtra("HORA_VENTA")){
                horaVenta.setText(getIntent().getExtras().getString("HORA_VENTA"));
            }
            if (getIntent().hasExtra("ID_CLIENT_VENTA")){
                id_cliente = getIntent().getExtras().getString("ID_CLIENT_VENTA");
            }
            db.obre();
            Cursor cursor = db.RetornaFacturaId_Venta(idVenta);
            myDataset = CursorBD(cursor);
            db.tanca();
        }
    }
    public void seleccionarCliente(){
        Intent intent = new Intent(FacturaActivity.this, ClientActivity.class);
        intent.putExtra("CLIENTE_FACTURA",true);
        startActivityForResult(intent,2);

    }
    public void crearNuevoPedido(){
        // dataVenta,horaVenta,nomClient,nomTreballador,estatVenta,preuTotalFactura;
        Log.d("NUEVO PEDIDO","true");
        dataVenta.setText(Utilitats.getFechaFormatSpain(Utilitats.obtenerFechaActual()));
        horaVenta.setText(Utilitats.obtenerHoraActual());
        estatVenta.setText("Falta Pagar");
        buttonPagar.setVisibility(View.GONE);
        if (idVentaFactura==null){
            nomTreballador.setText(LoginActivity.NOM_USUARI);

        }
    }




}
