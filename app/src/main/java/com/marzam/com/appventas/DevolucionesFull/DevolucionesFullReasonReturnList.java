package com.marzam.com.appventas.DevolucionesFull;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.marzam.com.appventas.DevolucionesFull.DataBaseInterface.DataBaseInterface;
import com.marzam.com.appventas.DevolucionesFull.Interfaz.ColorSelect;
import com.marzam.com.appventas.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DevolucionesFullReasonReturnList extends Activity{

    //Data of List
    private ListView litsReason;
    private ArrayAdapter adaptadorListReason;

    //Search elements
    private Button btnReturnToMainMenu;
    private Button btnMakeCall;
    private EditText inputSearh;
    private ImageButton btnCancelSearh;

    //Status Returned
    private boolean IS_GOOD_STATUS_RETURNED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devoluciones_full_lista_motivos);
        overridePendingTransition(R.anim.devoluciones_full_right_slide_in, R.anim.devoluciones_full_right_slide_out);

        Bundle extras = getIntent().getExtras();
        this.IS_GOOD_STATUS_RETURNED = extras.getBoolean("isGoodStatusReturned");

        String NAME_PHARMACY = DataBaseInterface.getNameClient( DevolucionesFullReasonReturnList.this, DataBaseInterface.getIDCLiente(DevolucionesFullReasonReturnList.this) );
        setTitle("Devoluciones - " + NAME_PHARMACY);

        initInterfaz();
    }

    //@Override
    public void initInterfaz() {
        initListReason();
        initInputSearh();
        initButtons();
    }

    //@Override
    public void initButtons() {
        initButtonCancelSearch();
        initButtonGoToReasonReturnList();
        initButtonMakeCall();
    }

    //@Override
    public void removeListenerButtons() {
        this.btnReturnToMainMenu.setOnClickListener(null);
        this.litsReason.setOnItemClickListener(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void initListReason() {
        this.litsReason = (ListView) findViewById(R.id.idListDevFullReason);

        List list = this.getReasonReturns();

        this.adaptadorListReason = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = super.getView(position, convertView, parent);
                if (position % 2 == 0) {
                    convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }else {
                    convertView.setBackgroundColor(Color.parseColor("#F2F2F2"));
                }

                return convertView;
            }
        };
        this.litsReason.setAdapter(this.adaptadorListReason);
        this.litsReason.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DevolucionesFullReasonReturnList.this.litsReason.setOnItemClickListener(null);

                ((TextView) view).setTextColor(Color.parseColor(ColorSelect.COLOR_SELECTED));
                String idReasonReturn = ((TextView) view).getText().toString();
                idReasonReturn = (idReasonReturn.split(" "))[0];
                String statusInvoiceReasonReturn = getStatusInvoiceReasonReturn(idReasonReturn);

                DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setReasonReturnSelected( ((TextView) view).getText().toString() );
                DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setStatusInvoiceReasonReturn( statusInvoiceReasonReturn );
                DevolucionesFullReasonReturnList.this.onBackPressed();
            }
        });
    }

    private void initInputSearh(){
        this.inputSearh = (EditText)findViewById(R.id.idEditTxtDevFullInputSearh);

        this.inputSearh.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                DevolucionesFullReasonReturnList.this.adaptadorListReason.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    private void initButtonGoToReasonReturnList() {
        this.btnReturnToMainMenu = (Button)findViewById(R.id.idBtnDevFullReturnsConsult);
        this.btnReturnToMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initButtonMakeCall() {
        this.btnMakeCall = (Button)findViewById(R.id.idBtnDevFullMakeCall);
        this.btnMakeCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DevolucionesFullReasonReturnList.this);
                builder.setTitle("Número telefónico:");
                final EditText input = new EditText(DevolucionesFullReasonReturnList.this);
                input.setInputType(InputType.TYPE_CLASS_PHONE);
                builder.setView(input);
                builder.setPositiveButton("Llamar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DevolucionesFullReasonReturnList.this.hidenkeyboard(input);

                        String number = "tel:" + input.getText().toString();
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                        DevolucionesFullReasonReturnList.this.startActivity(callIntent);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                input.requestFocus();
                DevolucionesFullReasonReturnList.this.showkeyboard(input);
            }
        });
    }

    private void initButtonCancelSearch(){
        this.btnCancelSearh = (ImageButton)findViewById(R.id.idIconTxtDevFullCancelSearh);
        this.btnCancelSearh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevolucionesFullReasonReturnList.this.inputSearh.setText("");
                DevolucionesFullReasonReturnList.this.hidenkeyboard( DevolucionesFullReasonReturnList.this.inputSearh );
            }
        });
    }

    private List getReasonReturns(){
        List listReason = new ArrayList();
        if( this.IS_GOOD_STATUS_RETURNED ){
            listReason = DataBaseInterface.getReasonForGoodStatusReturned(this);
            /*listReason.add("ERROR EN SURTIDO");
            listReason.add("ERROR EN CAPTURA");
            listReason.add("POR VENCER / VENCIDOS");
            listReason.add("DEVOLUCION BULTO COMPLETO");
            listReason.add("RECUPERACION DE CREDITO");
            listReason.add("SURTIDO CON CORTA CADUCIDAD");
            listReason.add("OMISION DE OFERTA");
            listReason.add("DEVOLUCION DE PEDIDO CENTRALIZADO");
            listReason.add("NOTA CARGO BUEN ESTADO AUTOSERVICIO Y CADENAS");
            listReason.add("Folios con Autorización de Presupuesto");
            listReason.add("Excedentes Negociados Buen Estado");
            listReason.add("Pedido Duplicado");*/
        }else{
            listReason = DataBaseInterface.getReasonForBadStatusReturned( this );
            /*listReason.add("ROTOS Y MANCHADOS POR DISTRIBUCION");
            listReason.add("Folio con Autorización de Mermas");
            listReason.add("Excedentes Negociados Mal Estado");*/
        }

        return listReason;
    }

    /**
     * Encargado de ocultar el teclado lógico
     */
    private void hidenkeyboard( TextView textView ) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
    }

    /**
     * Encargado de mostrar el teclado lógico
    */
    private void showkeyboard( TextView textView ) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * Metodo para emular la consulta a la base de datos
     */
    private String getStatusInvoiceReasonReturn( String idReason ) {

        String needInvoice = DataBaseInterface.thisReasonNeedInvoice(this, idReason);
        String needNote = DataBaseInterface.thisReasonNeedNote(this, idReason);

        String statusInvoice = "1";
        //¿Requiere factura?
        if ( (needInvoice.compareTo("Y") == 0) ) {
            statusInvoice = "1"; //Pedir num de factura
        }else if ( (needInvoice.compareTo("N") == 0) ) {
            statusInvoice = "3"; //Pedir solo productos
        }

        //¿Requiere no ta de cargo?
        if (needNote.compareTo("Y") == 0) {
            statusInvoice = "2"; //Pedir nota de cargo
        }
        return statusInvoice;
    }
}


