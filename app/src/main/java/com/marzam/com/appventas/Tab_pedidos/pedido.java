package com.marzam.com.appventas.Tab_pedidos;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import com.marzam.com.appventas.Gesture.Dib_firma;
import com.marzam.com.appventas.KPI.KPI_General;
import com.marzam.com.appventas.R;
import com.marzam.com.appventas.Sincronizacion.envio_pedido;
import com.marzam.com.appventas.WebService.WebServices;

public class pedido extends ActivityGroup {

    Context context;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        context=this;

try {
    TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
    tabHost.setup(getLocalActivityManager());

    TabHost.TabSpec spec1 = tabHost.newTabSpec("CABECERA");
    spec1.setIndicator("GENERAL");
    Intent intent = new Intent(this, pcabecera.class);
    spec1.setContent(intent);

    TabHost.TabSpec spec2 = tabHost.newTabSpec("DETALLE");
    spec2.setIndicator("DETALLE");
    Intent inten2 = new Intent(this, pdetalle.class);
    spec2.setContent(inten2);

    TabHost.TabSpec spec3 = tabHost.newTabSpec("LIQUIDACION");
    spec3.setIndicator("COTIZACION");
    Intent inten3 = new Intent(this, pliquidacion.class);
    spec3.setContent(inten3);


    tabHost.addTab(spec1);
    tabHost.addTab(spec2);
    tabHost.addTab(spec3);
    tabHost.setCurrentTab(1);

    Intent intent1=new Intent(this,pcatalogo.class);
    startActivity(intent1);

}catch (Exception e){

    Log.d("ErrorTab:",e.toString());
}

    }

    public void ShowisEnvio(){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setTitle("Aviso");
        alert.setMessage("Desea enviar el pedido?");
        alert.setPositiveButton("Si",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new UpLoadTask().execute("");
                progress= ProgressDialog.show(context, "Transmitiendo pedidos", "Cargando..", true, false);
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog=alert.create();
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pedido, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

       switch (id){

           case R.id.Firma:
               Intent intent=new Intent(context, Dib_firma.class);
               startActivity(intent);
               break;
           case R.id.Pedido:
               ShowisEnvio();
               break;
           case R.id.Producto:
               Intent intent2=new Intent(context,pcatalogo.class);
               startActivity(intent2);

       }

        return super.onOptionsItemSelected(item);
    }

    private class UpLoadTask extends AsyncTask<String,Void,Object> {

        @Override
        protected Object doInBackground(String... strings) {
            WebServices web=new WebServices();

            envio_pedido pedido=new envio_pedido();
            String res= pedido.GuardarPedido(context);


            return res;
        }

        @Override
        protected void onPostExecute(Object result){

            AlertDialog.Builder alert=new AlertDialog.Builder(context);
            alert.setTitle("Envio de pedido");
            alert.setIcon(android.R.drawable.ic_dialog_info);

            if(progress.isShowing()) {
                String res=String.valueOf(result);
                if(res!="")
                    alert.setMessage(res);
                else
                    alert.setMessage("Pedido enviado exitosamente");

                progress.dismiss();

                alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        startActivity(new Intent(getBaseContext(), KPI_General.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        finish();

                    }
                });

                AlertDialog alertDialog=alert.create();
                alertDialog.show();
            }
        }
    }
}
