package ccastro.casal;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ccastro.casal.SQLite.DBInterface;
import ccastro.casal.Utils.Cursors;

public class InsertarClienteActivity extends AppCompatActivity {
    Button insertarClientes;
    EditText numClientes;
    DBInterface db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DBInterface(this);
        setContentView(R.layout.activity_insertar_cliente);
        numClientes = (EditText) findViewById(R.id.editTextNumClientes);
        insertarClientes = (Button) findViewById(R.id.buttonAfegirClientes);
        insertarClientes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Integer quantitat;
                db.obre();
                Cursor cursor = db.obtenirNumeroDeClients("#Cliente Comedor");
                quantitat = Cursors.cursorQuantitat(cursor);
                for (int i=quantitat+1; i<=quantitat+Integer.parseInt(numClientes.getText().toString()); i++){
                    long idCliente = db.InserirClient("#Cliente Comedor",""+Integer.toString(i),"0",12,"0","0","Cliente sin identificar");
                }
                db.tanca();
            }
        });
    }
}
