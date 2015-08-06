package com.marzam.com.appventas.Mensajes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.marzam.com.appventas.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by imartinez on 21/07/2015.
 */
public class Dialog extends Activity {

    public static boolean active = false;
    public static Dialog myDialog;

    ListView lsMensaje;
    RelativeLayout layout;
    Context context;
    static ArrayList<HashMap<String,?>> data=null;
    HashMap<String,String> producto_row;
    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);
        context=this;

        myDialog = Dialog.this;

        layout=(RelativeLayout)findViewById(R.id.relativedialog);
        lsMensaje=(ListView)findViewById(R.id.listView);

        ObtenerMensajes();
        simpleAdapter=new SimpleAdapter(context,data,R.layout.stile_message,new String[]{"A"},new int[]{R.id.textView});
        lsMensaje.setAdapter(simpleAdapter);
        lsMensaje.setSelection(lsMensaje.getAdapter().getCount()-1);


        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Dialog.active){
                    if(Dialog.active){
                        Dialog.myDialog.finish();
                    }
                }
            }
        });


    }

    public void ObtenerMensajes(){

        data = new ArrayList<HashMap<String, ?>>();
        producto_row = new HashMap<String, String>();

        SharedPreferences preferences=getSharedPreferences("Mensajes",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        Map<String,?> map=preferences.getAll();
        ArrayList<Map<String,?>> list=new ArrayList<Map<String, ?>>();
        list.add(map);

        for(int i=1;i<map.size();i++){

            String mensaje=list.get(0).get("msj" +i)==null?"":list.get(0).get("msj"+i)==null?"":list.get(0).get("msj"+i).toString();

            if(!mensaje.isEmpty()) {
                producto_row.put("A", mensaje);
                data.add(producto_row);
                producto_row = new HashMap<String, String>();
            }

            String status=list.get(0).get("est" +i)==null?"":list.get(0).get("est"+i)==null?"":list.get(0).get("est"+i).toString();

            if(!status.isEmpty())
                editor.putString("est"+i,"20");


        }
        editor.commit();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        active = true;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        active = false;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        active = false;
    }

}
