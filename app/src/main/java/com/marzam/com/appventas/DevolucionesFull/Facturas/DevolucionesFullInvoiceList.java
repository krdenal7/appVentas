package com.marzam.com.appventas.DevolucionesFull.Facturas;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.marzam.com.appventas.DevolucionesFull.DataBaseInterface.DataBaseInterface;
import com.marzam.com.appventas.DevolucionesFull.Interfaz.ColorSelect;
import com.marzam.com.appventas.DevolucionesFull.Interfaz.ScreenChilds;
import com.marzam.com.appventas.DevolucionesFull.Productos.DevolucionesFullProductList;
import com.marzam.com.appventas.R;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DevolucionesFullInvoiceList extends Activity implements ScreenChilds{

    //Data of List.
    private ListView litsInvoice;
    private InvoiceRowAdapter invoiceRowAdapter;
    //private ArrayList<Invoice> invoiceList;

    //Calendar elements.
    private CheckBox checkBoxFilterDate;
    private ImageView imgFromDate, imgToDate;
    private TextView txtFromDate, txtToDate;

    //Search Elements.
    private EditText txtInputSearch;
    private ImageButton imgCancelSearch;
    private ImageButton imgStartSearch;

    //Button Element.
    private Button btnReturnMainMenu;

    //Tiitle.
    private String NAME_PHARMACY;
    private String REASON_TO_RETURN;

    //Elemento de la lista seleccionado.
    private View invoiceToConsult;

    //Bandera para indicar si es necesario regresar inmediatamente al menú principal
    //Si es TRUE: de la pantalla de productos pasaría a esta pantalla (de facturas) e inmediatamente
    //al menú principal.
    public static boolean returnToMainMenu = false;

    /**
     * Constructor.
     */
    public DevolucionesFullInvoiceList() {
    }

    /**
     * Creador de la Actividad.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devoluciones_full_lista_facturas);
        overridePendingTransition(R.anim.devoluciones_full_right_slide_in, R.anim.devoluciones_full_right_slide_out);

        Bundle extras = getIntent().getExtras();
        this.REASON_TO_RETURN = extras.getString("reasonToReturn");
        this.NAME_PHARMACY = DataBaseInterface.getNameClient(DevolucionesFullInvoiceList.this, DataBaseInterface.getIDCLiente(DevolucionesFullInvoiceList.this));
        setTitle("Devoluciones - " + NAME_PHARMACY);

        DevolucionesFullInvoiceList.returnToMainMenu = false;

        initInterfaz();
    }

    /**
     * Iniciador de la construcción de la interfaz grafica.
     */
    @Override
    public void initInterfaz() {
        initInvoiceList();
        initButtons();
    }

    /**
     * Inicializador de los botones.
     */
    @Override
    public void initButtons() {
        initCalendars();
        initSearchBar();

        this.btnReturnMainMenu = (Button) findViewById(R.id.idBtnDevFullInvoiceReturnMainMenu);
        this.btnReturnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevolucionesFullInvoiceList.this.onBackPressed();
            }
        });
    }

    /**
     * Remueve los eventos de los botones.
     */
    @Override
    public void removeListenerButtons() {
        //removeListenerCalendars();
        //removeListenerSearchBar();
    }

    /**
     * Sobre escritura del método "onBackPressed".
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initCalendars() {
        initEditTextCalendars();
        initCalendarsImages();
        initCheckBoxFilterDate();
    }

    /**
     * Método para inicializar los calendarios para filtrar las facturas por fechas.
     */
    private void initEditTextCalendars() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);

        this.txtFromDate = (TextView) findViewById(R.id.idTxtDevFullFromDateSearchInvoice);
        this.txtToDate = (TextView) findViewById(R.id.idTxtDevFullToDateSearchInvoice);

        this.txtFromDate.setKeyListener(null);
        this.txtToDate.setKeyListener(null);

        this.txtToDate.setText(new SimpleDateFormat("dd / MM / yyyy").format(calendar.getTime()));


        this.txtToDate.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    DevolucionesFullInvoiceList.this.txtToDate.setTextColor(Color.parseColor(ColorSelect.COLOR_SELECTED));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    DevolucionesFullInvoiceList.this.txtToDate.setTextColor(Color.parseColor("#000000"));
                    DevolucionesFullInvoiceList.this.dateClick(DevolucionesFullInvoiceList.this.txtToDate);
                }
                return true;
            }
        });

        this.txtFromDate.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    DevolucionesFullInvoiceList.this.txtFromDate.setTextColor(Color.parseColor(ColorSelect.COLOR_SELECTED));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    DevolucionesFullInvoiceList.this.txtFromDate.setTextColor(Color.parseColor("#000000"));
                    DevolucionesFullInvoiceList.this.dateClick(DevolucionesFullInvoiceList.this.txtFromDate);
                }
                return true;
            }
        });
    }

    /**
     * Método para inicializar y detectar el clic en las imágenes que ilustran los calendarios.
     */
    private void initCalendarsImages() {

        this.imgFromDate = (ImageView) findViewById(R.id.idImgDevFullFromDate);
        this.imgToDate = (ImageView) findViewById(R.id.idImgDevFullToDate);

        this.imgFromDate.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    DevolucionesFullInvoiceList.this.txtFromDate.setTextColor(Color.parseColor(ColorSelect.COLOR_SELECTED));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    DevolucionesFullInvoiceList.this.txtFromDate.setTextColor(Color.parseColor("#000000"));
                    DevolucionesFullInvoiceList.this.dateClick(DevolucionesFullInvoiceList.this.txtFromDate);
                }
                return true;
            }
        });
        this.imgToDate.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    DevolucionesFullInvoiceList.this.txtToDate.setTextColor(Color.parseColor(ColorSelect.COLOR_SELECTED));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    DevolucionesFullInvoiceList.this.txtToDate.setTextColor(Color.parseColor("#000000"));
                    DevolucionesFullInvoiceList.this.dateClick(DevolucionesFullInvoiceList.this.txtToDate);
                }
                return true;
            }
        });
    }

    /**
     * Método para inicializar el "CheckBox" para habilitar el filtrar de las facturas por fechas.
     */
    private  void initCheckBoxFilterDate(){
        this.checkBoxFilterDate = (CheckBox)findViewById(R.id.idCheckBoxDevFullFilterDate);
        this.checkBoxFilterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DevolucionesFullInvoiceList.this.invoiceRowAdapter.setFlagFilterDate(!DevolucionesFullInvoiceList.this.invoiceRowAdapter.getFlagFilterDate());

                //if( DevolucionesFullInvoiceList.this.checkBoxFilterDate.isChecked() ) {
                String fromDate = DevolucionesFullInvoiceList.this.txtFromDate.getText().toString();
                if (fromDate.compareTo("") == 0) {
                    if (DevolucionesFullInvoiceList.this.checkBoxFilterDate.isChecked()) {
                        DevolucionesFullInvoiceList.this.dateClick(DevolucionesFullInvoiceList.this.txtFromDate);
                    }
                } else {
                    filterByDate();
                }
                /*}else {
                    filterByDate();
                }*/
            }
        });
    }

    /**
     * Método para mostrar al usuario el widget de calendario
     * @param textView TextView para colocar la fecha seleccionada por el usuario.
     */
    private void dateClick( final View textView ) {

        Calendar c = Calendar.getInstance();
        Date maxDate = c.getTime();

        c.add(Calendar.DATE, -800);
        Date minDate = c.getTime();

        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                calendar.set(year, monthOfYear, dayOfMonth);
                //updateDate
                ((TextView)textView).setText(new SimpleDateFormat("dd / MM / yyyy").format(calendar.getTime()));
                if( ((TextView)textView)== DevolucionesFullInvoiceList.this.txtToDate){
                    DevolucionesFullInvoiceList.this.checkDateFrom( calendar );
                }
                if( DevolucionesFullInvoiceList.this.txtFromDate.getText().toString().compareTo("")!=0 ){
                    filterByDate();
                }
            }
        };

        //DateFrom the maxDate is the date of DateTo
        if( textView == DevolucionesFullInvoiceList.this.txtFromDate){
            String dateTo = DevolucionesFullInvoiceList.this.txtToDate.getText().toString();

            String[] dateToArr = dateTo.split("/");
            Calendar calendarAux = Calendar.getInstance();
            calendarAux.set(Calendar.YEAR, ( new Integer(dateToArr[2].trim()) ).intValue() );
            calendarAux.set(Calendar.MONTH, (new Integer(dateToArr[1].trim())).intValue()-1 );
            calendarAux.set(Calendar.DAY_OF_MONTH, (new Integer(dateToArr[0].trim())).intValue() );

            maxDate = calendarAux.getTime();
        }

        //DatePickerDialog dateDialog;// = new DatePickerDialog(DevolucionesFullInvoiceList.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) );

        String strDataFrom = DevolucionesFullInvoiceList.this.txtFromDate.getText().toString();
        if( strDataFrom.length()!=0 ){
            String[] arrayDataFrom = strDataFrom.split("/");
            calendar.set(Integer.parseInt(arrayDataFrom[2].trim()), Integer.parseInt(arrayDataFrom[1].trim())-1, Integer.parseInt(arrayDataFrom[0].trim()));
        }

        DatePickerDialog dateDialog = new DatePickerDialog(DevolucionesFullInvoiceList.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) );
        dateDialog.getDatePicker().setMinDate(minDate.getTime());
        dateDialog.getDatePicker().setMaxDate(maxDate.getTime());
        dateDialog.show();
    }

    /**
     * Método para verificar y re-calcular la fecha de uno de los calendarios a partir del otro.
     * @param calendarTo Calendario a partir del cual se calcula la fecha del otro.
     */
    private void checkDateFrom( Calendar calendarTo){
        String date = DevolucionesFullInvoiceList.this.txtFromDate.getText().toString();
        String[] dateFromArr = date.split("/");
        if( dateFromArr[0].trim().compareTo("")!=0 ) {
            Calendar calendarFrom = Calendar.getInstance();
            calendarFrom.set(Calendar.YEAR, (new Integer(dateFromArr[2].trim())).intValue());
            calendarFrom.set(Calendar.MONTH, (new Integer(dateFromArr[1].trim())).intValue()-1);
            calendarFrom.set(Calendar.DAY_OF_MONTH, (new Integer(dateFromArr[0].trim())).intValue());

            String cFrom = calendarFrom.get(Calendar.DAY_OF_MONTH)+""+calendarFrom.get(Calendar.MONTH)+""+calendarFrom.get(Calendar.YEAR);
            String cTo = calendarTo.get(Calendar.DAY_OF_MONTH)+""+calendarTo.get(Calendar.MONTH)+""+calendarTo.get(Calendar.YEAR);

            if ( cTo.compareTo( cFrom )<0 ){
                DevolucionesFullInvoiceList.this.txtFromDate.setText(new SimpleDateFormat("dd / MM / yyyy").format(calendarTo.getTime()));
            }
        }
    }

    /**
     * Método con el cual es posible filtrar la lista de facturas, mostrando solo las facturas comprendidas
     * entre las fechas señaladas por el usuario.
     */
    private void filterByDate(){
        String fDate = DevolucionesFullInvoiceList.this.txtFromDate.getText().toString();
        String tDate = DevolucionesFullInvoiceList.this.txtToDate.getText().toString();
        String[] fromDateArr = fDate.split("/");
        String[] toDateArr = tDate.split("/");

        Calendar calendarToDate = Calendar.getInstance();
        calendarToDate.set(Calendar.YEAR, ( new Integer(toDateArr[2].trim()) ).intValue() );
        calendarToDate.set(Calendar.MONTH, (new Integer(toDateArr[1].trim())).intValue() - 1);
        calendarToDate.set(Calendar.DAY_OF_MONTH, (new Integer(toDateArr[0].trim())).intValue());
        calendarToDate.add(Calendar.MILLISECOND, 1000);

        Calendar calendarFromDate = Calendar.getInstance();
        calendarFromDate.set(Calendar.YEAR, ( new Integer(fromDateArr[2].trim()) ).intValue() );
        calendarFromDate.set(Calendar.MONTH, (new Integer(fromDateArr[1].trim())).intValue() - 1);
        calendarFromDate.set(Calendar.DAY_OF_MONTH, (new Integer(fromDateArr[0].trim())).intValue());

        Date toDate = calendarToDate.getTime();
        Date fromDate = calendarFromDate.getTime();

        this.invoiceRowAdapter.setDateFilter(fromDate, toDate);

        this.invoiceRowAdapter.getFilter().filter(this.txtInputSearch.getText().toString());
    }

    /**
     * Inicializa la lista donde es posible visualizar las facturas.
     */
    private void initInvoiceList() {
        if( this.litsInvoice==null ) {
            this.litsInvoice = (ListView) findViewById(R.id.idListDevFullInvoice);

            ArrayList<Invoice> invoiceList = getInvoices();

            this.invoiceRowAdapter = new InvoiceRowAdapter(DevolucionesFullInvoiceList.this, R.layout.devoluciones_full_factura_row, invoiceList);
            this.litsInvoice.setAdapter(DevolucionesFullInvoiceList.this.invoiceRowAdapter);
        }else{
            this.invoiceRowAdapter.notifyDataSetChanged();
        }

        this.litsInvoice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DevolucionesFullInvoiceList.this.litsInvoice.setOnItemClickListener(null);
                ((TextView) view.findViewById(R.id.idTxtDevFullNumInvoice)).setTextColor(Color.parseColor(ColorSelect.COLOR_SELECTED));
                DevolucionesFullInvoiceList.this.invoiceToConsult = view;
                Invoice invoice = DevolucionesFullInvoiceList.this.invoiceRowAdapter.getItem(position);
                String numInvoice = invoice.numInvoice;

                Intent intent = new Intent(DevolucionesFullInvoiceList.this, DevolucionesFullProductList.class);
                intent.putExtra("namePharmacy", DevolucionesFullInvoiceList.this.NAME_PHARMACY);
                intent.putExtra("reasonToReturn", DevolucionesFullInvoiceList.this.REASON_TO_RETURN);
                intent.putExtra("numInvoice", numInvoice);
                startActivity(intent);
            }
        });
    }

    /**
     * Inicializa la barra de búsqueda.
     */
    private void initSearchBar(){
        initEditTextSearch();
        initCancelButtonSearch();
        initStartSearch();
    }

    /**
     * Inicializa solo la el área del texto perteneciente a la barra de búsquedas.
     */
    private void initEditTextSearch(){
        this.txtInputSearch = (EditText)findViewById(R.id.idEditTxtDevFullInputSearh);
        this.txtInputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DevolucionesFullInvoiceList.this.invoiceRowAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Inicializa el botón para cancelar la búsqueda.
     */
    private void initCancelButtonSearch() {
        this.imgCancelSearch = (ImageButton) findViewById(R.id.idIconTxtDevFullCancelSearch);
        this.imgCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevolucionesFullInvoiceList.this.txtInputSearch.setText("");
                DevolucionesFullInvoiceList.this.hidenkeyboard(DevolucionesFullInvoiceList.this.txtInputSearch);
            }
        });
    }

    /**
     * Inicializa el botón para empezar a buscar (Botón con imagen de lupa).
     */
    private void initStartSearch() {
        this.imgStartSearch = (ImageButton) findViewById(R.id.idIconTxtDevFullStartSearch);
        this.imgStartSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevolucionesFullInvoiceList.this.txtInputSearch.requestFocus();
                DevolucionesFullInvoiceList.this.showkeyboard();
            }
        });
    }

    /**
     * Método con el cual se obtener las facturas.
     * @return Lista de facturas.
     */
    ArrayList<Invoice> getInvoices(){

        ArrayList<Invoice> list = (ArrayList<Invoice>)DataBaseInterface.getInvoices( this );
        return list;
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
    private void showkeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * Sobre escritura del método "onRestart".
     */
    @Override
    protected void onRestart() {
        overridePendingTransition(R.anim.devoluciones_full_left_slide_in, R.anim.devoluciones_full_left_slide_out);
        this.initInvoiceList();
        super.onRestart();
    }

    /**
     * Sobre escritura del método "onStop".
     */
    @Override
    protected void onStop() {
        try {
            ((TextView) invoiceToConsult.findViewById(R.id.idTxtDevFullNumInvoice)).setTextColor(Color.parseColor("#000000"));
        }catch (Exception e){}
        super.onStop();
    }

    /**
     * Sobre escritura del método "onResume".
     */
    @Override
    protected void onResume() {
        if( DevolucionesFullInvoiceList.returnToMainMenu ){
            DevolucionesFullInvoiceList.returnToMainMenu = false;
            super.onResume();
            this.onBackPressed();
        } else {
            super.onResume();
        }
    }
}


