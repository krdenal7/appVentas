package com.marzam.com.appventas.Graficas;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.marzam.com.appventas.R;
import com.marzam.com.appventas.SQLite.CSQLite;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class Grafica_Vendedor extends Activity {

    Context context;
    CSQLite lite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica__vendedor);
        context=this;

        String agente=ObtenerClavedeAgente();
        String url=url(agente);
        WebView webView=(WebView)findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/www/agente.html?"+url);
        WebSettings settings=webView.getSettings();
        settings.setJavaScriptEnabled(true);


    }

    public String url(String clave_agente){
        String url="";
        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();

       String query="select sum(impote_total) as vendido,a.nombre,p.presupuesto_ventas from encabezado_pedido as e\n" +
               "inner join agentes as a on e.clave_agente=a.clave_agente\n" +
               "inner join presupuesto_agente as p on a.clave_agente=p.clave_agente  where e.clave_agente='"+clave_agente+"'";
       Cursor rs=db.rawQuery(query,null);


        if(rs.moveToFirst()){
            List<NameValuePair> params = new LinkedList<NameValuePair>();

            params.add(new BasicNameValuePair("A", String.valueOf(rs.getDouble(0))));
            Double ritmo=(rs.getDouble(0)/dias_transcurridos())*dias();
            params.add(new BasicNameValuePair("B", String.valueOf(ritmo)));
            params.add(new BasicNameValuePair("C",rs.getString(1)));
            String presupuesto=rs.getString(2)==null?"0":rs.getString(2);
            params.add(new BasicNameValuePair("D",presupuesto));
            url = URLEncodedUtils.format(params, "utf-8");

        }

        return  url;
    }
    public String ObtenerClavedeAgente(){

        lite=new CSQLite(context);
        SQLiteDatabase db=lite.getWritableDatabase();
        String clave="";

        Cursor rs=db.rawQuery("select clave_agente from agentes where Sesion=1",null);
        if(rs.moveToFirst()){

            clave=rs.getString(0);
        }


        return clave;
    }

    private int dias(){

        String dia="";
        int contador=0;

        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();
        SimpleDateFormat df=new SimpleDateFormat("yyyy");
        SimpleDateFormat dfm=new SimpleDateFormat("MM");

       int año=Integer.parseInt(df.format(dt.getTime()));
       int mes=Integer.parseInt(dfm.format(dt.getTime()));

        GregorianCalendar FechaCalendario=new GregorianCalendar();

        for(int i=1;i<=31;i++) {

            try {
                Date dt1 = new Date(año, mes, i, 00, 00, 00);
                FechaCalendario.setTime(dt1);
                int numDia = FechaCalendario.get(Calendar.DAY_OF_WEEK);
                if (numDia != 1)
                    contador++;
            }catch (Exception e){
                return contador;
            }

        }


        return contador;
    }
    private int dias_transcurridos(){
        int dias=0;

        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();
        SimpleDateFormat df=new SimpleDateFormat("yyyy");
        SimpleDateFormat dfm=new SimpleDateFormat("MM");
        SimpleDateFormat dfd=new SimpleDateFormat("dd");

        int año=Integer.parseInt(df.format(dt.getTime()));
        int mes=Integer.parseInt(dfm.format(dt.getTime()));
        int dia=Integer.parseInt(dfd.format(dt.getTime()));

        GregorianCalendar FechaCalendario=new GregorianCalendar();

        for(int i=0;i<=dia;i++){

            Date dtt=new Date(año,mes,i,00,00,00);

            FechaCalendario.setTime(dtt);
            int numDia = FechaCalendario.get(Calendar.DAY_OF_WEEK);
            if (numDia != 1)
                     dias++;
                               }


        return dias;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_grafica__vendedor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
