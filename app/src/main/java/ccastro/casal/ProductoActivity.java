package ccastro.casal;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import ccastro.casal.RecyclerView.HeaderAdapterProducte;
import ccastro.casal.RecyclerView.HeaderProducte;
import ccastro.casal.SQLite.ContracteBD;
import ccastro.casal.SQLite.DBInterface;

public class ProductoActivity extends AppCompatActivity {
    DBInterface db;
    Integer tipoProducto;
    private HeaderAdapterProducte headerAdapterProducte;
    private ArrayList<HeaderProducte> myDataset;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);
        db = new DBInterface(this);
        myDataset = new ArrayList<>();
        headerAdapterProducte= new HeaderAdapterProducte(myDataset);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_consulta);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(headerAdapterProducte);


        // TODO HACER UN LIST DE PRODUCTOS, PERO SOLO MOSTRAT SEGUN EL PARAMETRO ENVIADO EN EL INTENT DE PEDIDOS
        // POR EJEMPLO SI MANDO TIPO 0, SOLO SE MOSTRARAN LOS PRODUCTOS DE CAFE/TE EN LA CONSULTA
        getIntents();
        retornarProductes();
    }
    public void getIntents(){
        if (getIntent().hasExtra("TIPO_PRODUCTO")){
            tipoProducto = (getIntent().getExtras().getInt("TIPO_PRODUCTO"));
        }
    }
    public void retornarProductes(){
        db.obre();
        Cursor cursor = db.RetornaProductes(Integer.toString(tipoProducto));
        myDataset = mouCursor(cursor);
        db.tanca();
    }
    public ArrayList mouCursor(Cursor cursor) {
        if (cursor.moveToFirst()) {
            do {
                myDataset.add(new HeaderProducte(
                        cursor.getString(cursor.getColumnIndex(ContracteBD.Producte._ID)),
                        cursor.getString(cursor.getColumnIndex(ContracteBD.Producte.NOM_PRODUCTE)),
                        cursor.getString(cursor.getColumnIndex(ContracteBD.Producte.PREU_PRODUCTE))));
            } while (cursor.moveToNext());
        }
        return myDataset;
    }
}
