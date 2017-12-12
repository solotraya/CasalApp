package ccastro.casal;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ccastro.casal.SQLite.DBInterface;
import ccastro.casal.Utils.Cursors;
import ccastro.casal.Utils.Utilitats;

public class PedidoActivity extends AppCompatActivity implements View.OnClickListener {
    private android.support.v7.widget.Toolbar mToolbar;
    private String id_producte, quantitat;
    private Integer tipoProducto=-1;
    DBInterface db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        db = new DBInterface(this);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar_pedido);

        findViewById(R.id.buttonCafes).setOnClickListener(this);
        findViewById(R.id.buttonRefrescos).setOnClickListener(this);
        findViewById(R.id.buttonAlimentacion).setOnClickListener(this);
        findViewById(R.id.buttonOtrosProductos).setOnClickListener(this);

        mToolbar.findViewById(R.id.buttonVerFactura).setOnClickListener(this);

        getIntents();
    }
    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.buttonCafes:   tipoProducto = 0;  SeleccionaProducte(); break;
            case R.id.buttonRefrescos:  tipoProducto = 1; SeleccionaProducte(); break;
            case R.id.buttonAlimentacion:   tipoProducto = 2; SeleccionaProducte(); break;
            case R.id.buttonOtrosProductos:    tipoProducto = 3; SeleccionaProducte(); break;

            case R.id.buttonVerFactura:
                Intent intent = new Intent(this, FacturaActivity.class);
                startActivity(intent);
                finish();
                break;
            default: break;
        }

    }
    public void crearNuevaVenta(){
        String hora = Utilitats.obtenerHoraActual();
        //       *** CAMBIAR POR FEHCA Y HORA ACTUAL ***
        db.InserirVenta(Integer.parseInt(FacturaActivity.id_cliente),Integer.parseInt(LoginActivity.ID_TREBALLADOR),Utilitats.obtenerFechaActual(),"0",hora);
        Cursor cursorVentaFactura = db.EncontrarId_VentaFacturaSinPagar(FacturaActivity.id_cliente);
        FacturaActivity.idVentaFactura = Cursors.cursorIDVentaFactura(cursorVentaFactura);
        FacturaActivity.idVenta = Integer.toString(FacturaActivity.idVentaFactura);
        Log.d("IDCLIENTEDEBARRAA",FacturaActivity.id_cliente);
        Log.d("IDVENTACLIENTEBARRA",FacturaActivity.idVenta);
        // idVenta = Integer.toString(idVentaFactura);
    }
    public void SeleccionaProducte(){
        if (tipoProducto==0 || tipoProducto==1 || tipoProducto==2 || tipoProducto==3){
            Intent intent = new Intent (PedidoActivity.this,ProductoActivity.class);
            intent.putExtra("TIPO_PRODUCTO",tipoProducto);
            //startActivityForResult(intent,1);
            startActivity(intent);
            finish();

        }
    }

    public void getIntents(){
        if(getIntent().hasExtra("ID_PRODUCTE")) {
            id_producte = (getIntent().getExtras().getString("ID_PRODUCTE"));
            quantitat = (getIntent().getExtras().getString("QUANTITAT"));
            TextView textViewQuantitat = (TextView) mToolbar.findViewById(R.id.textViewNumProductes);
            textViewQuantitat.setText(quantitat);
            TextView textViewNomProducte = (TextView) mToolbar.findViewById(R.id.textViewNombreProducto);
            textViewNomProducte.setText((getIntent().getExtras().getString("NOM_PRODUCTE")));
            TextView textViewTotalProducte = (TextView) mToolbar.findViewById(R.id.textViewTotal);
            textViewTotalProducte.setText((getIntent().getExtras().getString("TOTAL_PRODUCTE"))+"€");

            FacturaActivity.actualizarReserva = true;
            if (FacturaActivity.idVentaFactura==null){
                // TODO: BUSCAMOS QUE EL CLIENTE TENGA ALGUNA FACTURA ABIERTA SIN PAGAR

                Log.d("IDCLIENTE",FacturaActivity.id_cliente);
                db.obre();
                Cursor cursorVentaFactura = db.EncontrarId_VentaFacturaSinPagar(FacturaActivity.id_cliente);
                FacturaActivity.idVentaFactura = Cursors.cursorIDVentaFactura(cursorVentaFactura);
                FacturaActivity.idVenta = Integer.toString(FacturaActivity.idVentaFactura);
                Log.d("IDVENTA: ", Integer.toString(FacturaActivity.idVentaFactura));
                db.tanca();
            }
            db.obre();
            Log.d("IDVENTAFACTURA",Integer.toString(FacturaActivity.idVentaFactura));
            if (FacturaActivity.idVentaFactura==-1){ // Si no tienen una factura pendiente por pagar
                crearNuevaVenta();
                db.InserirFactura(Integer.parseInt(id_producte),FacturaActivity.idVentaFactura,Integer.parseInt(quantitat));
            } else {  // SI EL CLIENTE YA TIENE FACTURA SIN CERRAR
                db.InserirFactura(Integer.parseInt(id_producte),FacturaActivity.idVentaFactura,Integer.parseInt(quantitat));
                db.ActualitzarFechaHoraFactura(FacturaActivity.idVentaFactura, Utilitats.obtenerFechaActual(),Utilitats.obtenerHoraActual());
            }
            db.tanca();
        }
    }
}
