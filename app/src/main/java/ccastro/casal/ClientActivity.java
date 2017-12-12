package ccastro.casal;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import ccastro.casal.SQLite.ContracteBD;
import ccastro.casal.SQLite.DBInterface;
import ccastro.casal.Utils.Cursors;

public class ClientActivity extends AppCompatActivity {
    DBInterface db;
    ListView listView ;
    List<String> clientes = null;
    List<String> clientesSoloNombres = null;
    ArrayAdapter<String> adapterClientes;
    String id_cliente,nombreCliente;
    Integer idVentaFactura;
    boolean clienteFactura = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        db = new DBInterface(this);
        listView = (ListView) findViewById(R.id.listView);
        clientes= new ArrayList();
        clientesSoloNombres = new ArrayList();
        adapterClientes = new ArrayAdapter<String> (this,
                android.R.layout.simple_list_item_1, android.R.id.text1, clientesSoloNombres);
        // Assign adapter to ListView
        listView.setAdapter(adapterClientes);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO ESCONDEMOS EL TECLADO DEL MOVIL:
                /*
                view = ClientActivity.this.getCurrentFocus();
                view.clearFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } */
                if (clienteFactura){
                    String nombre =(String) listView.getItemAtPosition(position);
                    for (String client: clientes){
                        if (client.contains(nombre)){
                            nombre = client;
                            break;
                        }
                    }
                    String [] cogerIDCliente = nombre.split(" ");
                    id_cliente = cogerIDCliente[0];
                    String [] cogerTipoPago = nombre.split(":");
                    nombreCliente = cogerTipoPago[0].split(" ",2)[1];

                    Log.d("NOMBRE CLIENTE: ",nombreCliente);

                   // Toast.makeText(view.getContext(), cogerIDCliente[0], Toast.LENGTH_SHORT).show();

                    // TODO: BUSCAMOS QUE EL CLIENTE TENGA ALGUNA FACTURA ABIERTA SIN PAGAR
                    db.obre();
                    Cursor cursorVentaFactura = db.EncontrarId_VentaFacturaSinPagar(id_cliente);
                    idVentaFactura = Cursors.cursorIDVentaFactura(cursorVentaFactura);

                    Log.d("IDVENTA: ", Integer.toString(idVentaFactura));
                    db.tanca();

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("ID_CLIENTE",id_cliente);
                    returnIntent.putExtra("NOMBRE_CLIENTE",nombreCliente);
                    returnIntent.putExtra("ID_VENTA",Integer.toString(idVentaFactura));
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                } else {
                    // TODO AQUI HAREMOS QUE SE PUEDA ENTRAR AL CLIENTE PARA MODIFICARLO O ELIMINARLO.
                }
            }
        });
        getIntents();
        retornaClients();
    }
    public void getIntents(){
        if (getIntent().hasExtra("CLIENTE_FACTURA")){  // pasado desde HeaderAdapterVenta
            clienteFactura = getIntent().getExtras().getBoolean("CLIENTE_FACTURA");
        }
    }
    public void retornaClients(){
        db.obre();
        clientes.clear();
        clientesSoloNombres.clear();
        Cursor cursor= db.RetornaTotsElsClients();
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(ContracteBD.Client.NOM_CLIENT)).contains("Cliente Barra")){ // Con mayuscula Barra, no mostramos los nuevos clientes barra temporales
                    cursor.moveToNext();
                } else {
                    clientes.add(cursor.getString(cursor.getColumnIndex(ContracteBD.Client._ID))+" "+cursor.getString(cursor.getColumnIndex(ContracteBD.Client.NOM_CLIENT))
                            +" "+cursor.getString(cursor.getColumnIndex(ContracteBD.Client.COGNOMS_CLIENT))+" :"+cursor.getString(cursor.getColumnIndex(ContracteBD.Client.TIPO_PAGO)));
                    clientesSoloNombres.add(cursor.getString(cursor.getColumnIndex(ContracteBD.Client.NOM_CLIENT))
                            +" "+cursor.getString(cursor.getColumnIndex(ContracteBD.Client.COGNOMS_CLIENT)));
                }
            } while (cursor.moveToNext());
        }
        db.tanca();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu_mesa_widgets, menu);
        menu.findItem(R.id.spinnerMesa).setVisible(false);

        MenuItem item = menu.findItem(R.id.searchViewClientes);

        final SearchView searchView = (SearchView)item.getActionView();
        searchView.setQueryHint("Nombre Cliente...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // adapter.getFilter().filter(newText);
                // TODO: MOSTRAMOS TEXTO FILTRADO DE EL ADAPTER DE CLIENTES
                // TODO: BUSCAR COMO DAR FORMATO AL LISTVIEW DE CLIENTES PARA QUE SEA MAS CHULO
                adapterClientes.getFilter().filter(newText);
                return true;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED,returnIntent);
        finish();
    }
}