package com.marzam.com.appventas.Tab_pedidos;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import com.marzam.com.appventas.R;

public class pedido extends ActivityGroup {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
