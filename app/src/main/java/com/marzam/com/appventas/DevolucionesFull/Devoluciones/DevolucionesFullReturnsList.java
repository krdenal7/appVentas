package com.marzam.com.appventas.DevolucionesFull.Devoluciones;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.marzam.com.appventas.DevolucionesFull.DataBaseInterface.DataBaseInterface;
import com.marzam.com.appventas.DevolucionesFull.Devoluciones.DevolucionPendiente.DevolucionPendiente;
import com.marzam.com.appventas.DevolucionesFull.DevolucionesFullConteiner;
import com.marzam.com.appventas.DevolucionesFull.Interfaz.ColorSelect;
import com.marzam.com.appventas.DevolucionesFull.PrepareSendingData.PrepareSendingData;
import com.marzam.com.appventas.DevolucionesFull.Productos.DevolucionesFullProductList;
import com.marzam.com.appventas.R;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by luisMikg on 02/07/2015.
 */
public class DevolucionesFullReturnsList extends Activity {
    //Data of List
    private ListView litsReturn;
    private ReturnRowAdapter returnRowAdapter;
    private ArrayList<DevolucionPendiente> returnList;
    private View folioToConsult;

    //Num of returns
    private TextView totalReturnsPending;

    //Elements of search bar;
    private EditText txtInputSearch;
    private ImageButton imgCancelSearch;
    private ImageButton imgStartSearch;

    private Button btnReturnIndicators;

    //Numero de devoluciones pendientes
    public static int NUM_AVALIBLE_RETURNS;

    //Icono de sincronizacion
    public ImageView imgSynchronizeAll;
    public ImageView imgStartSynchronizeAll;

    private DevolucionPendiente devolucionPendiente;
    private DevolucionPendiente devolucionPendienteAUX;
    public static DevolucionesFullReturnsList thiz;

    private static ProgressDialog progressDialog;
    private static int indexDevolucionesPendientes;
    public static boolean exitoAlMandarTodas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devoluciones_full_lista_devoluciones);
        overridePendingTransition(R.anim.devoluciones_full_left_slide_in, R.anim.devoluciones_full_left_slide_out);

        thiz = this;
        DevolucionesFullReturnsList.indexDevolucionesPendientes=0;
        DevolucionesFullReturnsList.NUM_AVALIBLE_RETURNS = 0;
        DevolucionesFullReturnsList.exitoAlMandarTodas = true;
        initInterfaz();
    }

    public void initInterfaz() {
        initReturnList();
        initSearchBar();
        initButtons();
    }

    public void initButtons() {
        this.btnReturnIndicators = (Button) findViewById(R.id.idBtnDevFullInvoiceReturnIndicators);
        this.imgSynchronizeAll = (ImageView) findViewById(R.id.idImgDevFullSynchronizeAll);
        this.imgStartSynchronizeAll = (ImageView) findViewById(R.id.idImgDevFullStartSynchronizeAll);

        this.btnReturnIndicators.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevolucionesFullReturnsList.this.onBackPressed();
            }
        });

        DevolucionesFullReturnsList.this.imgStartSynchronizeAll.setVisibility(View.INVISIBLE);
        this.imgSynchronizeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevolucionesFullReturnsList.this.startSynchronize(true, DevolucionesFullReturnsList.this.imgStartSynchronizeAll, 0);
            }
        });
    }

    private void initSearchBar(){
        initEditTextSearch();
        initCancelButtonSearch();
        initStartSearch();
    }

    private void initReturnList() {
        if( this.litsReturn==null ) {
            this.litsReturn = (ListView) findViewById(R.id.idListDevFullReturns);

            this.returnList = getReturnsList();

            this.returnRowAdapter = new ReturnRowAdapter(DevolucionesFullReturnsList.this, R.layout.devoluciones_full_devolucion_row, this.returnList);
            this.litsReturn.setAdapter(DevolucionesFullReturnsList.this.returnRowAdapter);
        }else{
            this.returnRowAdapter.notifyDataSetChanged();
        }

        initSwipeList(this.litsReturn, this.returnList, this.returnRowAdapter);

        this.litsReturn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //DevolucionesFullReturnsList.this.litsReturn.setOnItemClickListener(null);
                ((TextView) view.findViewById(R.id.idTxtDevFullNumFolio)).setTextColor(Color.parseColor(ColorSelect.COLOR_SELECTED));
                DevolucionesFullReturnsList.this.folioToConsult = view;
                DevolucionesFullReturnsList.this.devolucionPendiente = DevolucionesFullReturnsList.this.returnRowAdapter.getItem(position);

                if (DevolucionesFullReturnsList.this.devolucionPendiente.getStatusIBS().trim().compareTo("INT") == 0 && DevolucionesFullReturnsList.this.devolucionPendiente.getIdStatus().trim().compareTo("10") == 0) {
                    String nameClient = devolucionPendiente.getNameClient();

                    if (nameClient == null) {
                        nameClient = DataBaseInterface.getNameClient(DevolucionesFullReturnsList.this, devolucionPendiente.getIDClient());
                        devolucionPendiente.setNameClient(nameClient);
                    }

                    DevolucionesFullReturnsList.this.devolucionPendienteAUX = new DevolucionPendiente();

                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setFolioForThisReturn(DevolucionesFullReturnsList.this.devolucionPendiente.getFolioForThisReturn());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setStatusIBS(devolucionPendiente.getStatusIBS());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setNameClient(devolucionPendiente.getNameClient());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setStatusTransmit(devolucionPendiente.getStatusTransmit());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setIDClient(devolucionPendiente.getIDClient());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setReasonReturnSelected(devolucionPendiente.getReasonReturnSelected());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setNumberInvoiceToReturn(devolucionPendiente.getNumberInvoiceToReturn());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setCostForThisReturn(devolucionPendiente.getCostForThisReturn());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setTypeForThisReturn(devolucionPendiente.getTypeForThisReturn());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setClaveAgente(devolucionPendiente.getClaveAgente());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setNumPackages(devolucionPendiente.getNumPackages());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setHandlerAutorizacion(devolucionPendiente.getHandlerAutorizacion());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setConsecutivoCaptura(devolucionPendiente.getConsecutivoCaptura());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setDiscountByClient(devolucionPendiente.getDiscountByClient());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setIdStatus(devolucionPendiente.getIdStatus());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setDateThatThisReturnWasSave(devolucionPendiente.getDateThatThisReturnWasSave());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setTimeThatThisReturnWasSave(devolucionPendiente.getTimeThatThisReturnWasSave());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setAffectConsummation(devolucionPendiente.getAffectConsummation());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setStatusAutorizacion(devolucionPendiente.getStatusAutorizacion());
                    DevolucionesFullReturnsList.this.devolucionPendienteAUX.setStatusFolio(devolucionPendiente.getStatusFolio());


                    Intent intent = new Intent(DevolucionesFullReturnsList.this, DevolucionesFullProductList.class);

                    intent.putExtra("namePharmacy", devolucionPendiente.getNameClient());
                    intent.putExtra("reasonToReturn", devolucionPendiente.getReasonReturnSelected());
                    intent.putExtra("numInvoice", devolucionPendiente.getNumberInvoiceToReturn());
                    intent.putExtra("fromReturnList", "TRUE");
                    startActivity(intent);
                }

                ((TextView) view.findViewById(R.id.idTxtDevFullNumFolio)).setTextColor(Color.parseColor("#000000"));
            }
        });
        initNumAvaliblesReturns();
    }
    /**
     * Método para obtener la devolución que se editara.
     * @return Devolución que se editara.
     */
    public DevolucionPendiente getDevolucionPendiente(){
        return this.devolucionPendienteAUX;
    }

    private ArrayList<DevolucionPendiente> getReturnsList(){
        ArrayList<DevolucionPendiente> list = DataBaseInterface.getPendingReturns( this );
        return  list;
    }

    private void initNumAvaliblesReturns() {
        if( this.totalReturnsPending==null )
            this.totalReturnsPending = (TextView)findViewById(R.id.idTxtDevFullTotalReturnsPending);

        //this.NUM_AVALIBLE_RETURNS = Integer.parseInt( getNumAvaliblesReturns() );
        this.totalReturnsPending.setText(this.NUM_AVALIBLE_RETURNS + "");
    }

    /*private String getNumAvaliblesReturns(){
        return DataBaseInterface.getNumPendingReturns();
    }*/

    private void initEditTextSearch(){
        this.txtInputSearch = (EditText)findViewById(R.id.idEditTxtDevFullInputSearh);
        this.txtInputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DevolucionesFullReturnsList.this.returnRowAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initCancelButtonSearch() {
        this.imgCancelSearch = (ImageButton) findViewById(R.id.idIconTxtDevFullCancelSearch);
        this.imgCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevolucionesFullReturnsList.this.txtInputSearch.setText("");
                DevolucionesFullReturnsList.this.hidenkeyboard(DevolucionesFullReturnsList.this.txtInputSearch);
            }
        });
    }

    private void initStartSearch() {
        this.imgStartSearch = (ImageButton) findViewById(R.id.idIconTxtDevFullStartSearch);
        this.imgStartSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevolucionesFullReturnsList.this.txtInputSearch.requestFocus();
                DevolucionesFullReturnsList.this.showkeyboard();
            }
        });
    }

    /**
     * Este metodo inicializa lis items de la lista para ser eliminados al hacer swipe sobre ellos.
     *
     * @param litsToWork
     * @param arrayListToWork
     * @param adaptadorListToWork
     */
    private void initSwipeList(final ListView litsToWork, final ArrayList<DevolucionPendiente> arrayListToWork, final ReturnRowAdapter adaptadorListToWork) {

        SwipeListViewTouchListener touchListener = new SwipeListViewTouchListener(litsToWork, new SwipeListViewTouchListener.OnSwipeCallback() {

            @Override
            public void onSwipeLeft(final ListView listView, final int[] reverseSortedPositions) {

                //Quitamos los ecuchadores del listView
                litsToWork.setOnTouchListener(null);
                litsToWork.setOnScrollListener(null);

                final DevolucionPendiente devolucionPendiente = adaptadorListToWork.getItem( reverseSortedPositions[0] );
                if( devolucionPendiente.getStatusIBS().trim().compareTo("INT")==0 && devolucionPendiente.getIdStatus().trim().compareTo("10")==0 ) {
                    AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullReturnsList.this)
                            .setTitle("Aviso")
                            .setMessage("¿Eliminar devolucion: " + devolucionPendiente.getFolioForThisReturn()+"?")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            arrayListToWork.remove(reverseSortedPositions[0]);
                                            DevolucionesFullReturnsList.this.delateDataFromThisDevolution(devolucionPendiente);
                                            adaptadorListToWork.notifyDataSetChanged();
                                        }
                                    })
                                    .setNegativeButton("Cancelar", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(false)
                            .show();
                }
                updateSwipe();
            }

            @Override
            public void onSwipeRight(ListView listView, final int[] reverseSortedPositions) {

                //Quitamos los ecuchadores del listView
                DevolucionesFullReturnsList.this.litsReturn.setOnTouchListener(null);
                DevolucionesFullReturnsList.this.litsReturn.setOnScrollListener(null);

                final DevolucionPendiente devolucionPendiente = adaptadorListToWork.getItem( reverseSortedPositions[0] );
                if( devolucionPendiente.getStatusIBS().trim().compareTo("INT")==0 && devolucionPendiente.getIdStatus().trim().compareTo("10")==0 ) {
                    AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullReturnsList.this)
                            .setTitle("Aviso")
                            .setMessage("¿Eliminar devolucion: " + devolucionPendiente.getFolioForThisReturn()+"?")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    arrayListToWork.remove(reverseSortedPositions[0]);
                                    DevolucionesFullReturnsList.this.delateDataFromThisDevolution(devolucionPendiente);
                                    adaptadorListToWork.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(false)
                            .show();
                }
                updateSwipe();
            }
        }, true, false);
        //Escuchadores del listView
        litsToWork.setOnTouchListener(touchListener);
        litsToWork.setOnScrollListener(touchListener.makeScrollListener());

    }

    private void updateSwipe() {
        //inicializamos de nuevo el listener para poder eliminar las devoluciones
        initSwipeList(this.litsReturn, this.returnList, this.returnRowAdapter);
        initNumAvaliblesReturns();
    }

    private void delateDataFromThisDevolution( DevolucionPendiente devolucionPendiente ) {
        double consumoActual= Double.parseDouble( devolucionPendiente.getCostForThisReturn().trim().replace(",","") );
        String idMotivo = (devolucionPendiente.getReasonReturnSelected().trim().split(" "))[0];
        String folio = devolucionPendiente.getFolioForThisReturn();

        DataBaseInterface.execDelate("DEV_EncabezadoDevoluciones", "FolioDevolucion", folio);
        DataBaseInterface.execDelate("DEV_DetalleDevoluciones", "Folio", folio);

        //Primero checamos, la devolución, es decir, si se trata de una merma o no; la siguiente consulta: si arroja un 2 entonces se trata de una merma, si arroja un 1
        //entonces NO se trata de una merma. Esta consulta se basa en el motivo de la devolución.
        String typeDevolution = DataBaseInterface.typeOfThisDevolution(DevolucionesFullReturnsList.this, idMotivo);//valuesH[19]: "Motivo"
        if (typeDevolution.trim().compareTo("1") == 0) {
            //NO se trata de una merma
            //Checamos si esta devolucion afecta el presupuesto para las devoluciones
            String afffectingTheEstimate = DataBaseInterface.thisAffectingTheEstimate(DevolucionesFullReturnsList.this, idMotivo);//valuesH[19]: "Motivo"
            if (afffectingTheEstimate.trim().compareTo("Y") == 0) {
                //SI afecta e consumo, por lo que guardamos el importe total aproximado de esta devolución con lo ya acumulado de otras devoluciones dentro del presupuesto.
                DataBaseInterface.setFoliosInEstimate(DevolucionesFullReturnsList.this, "0", consumoActual);//valuesH[7]: "importeTotalAproximado"

            } else if (afffectingTheEstimate.trim().compareTo("N") == 0) {
                DataBaseInterface.setFoliosOutEstimation(DevolucionesFullReturnsList.this, "0", consumoActual);//valuesH[7]: "importeTotalAproximado"

            }
        } else if (typeDevolution.trim().compareTo("2") == 0) {
            // SI se trata de una merma
            //Checamos si esta devolucion afecta el presupuesto para las devoluciones
            String afffectingTheEstimate = DataBaseInterface.thisAffectingTheEstimate(DevolucionesFullReturnsList.this, idMotivo);//valuesH[19]: "Motivo"
            if (afffectingTheEstimate.trim().compareTo("Y") == 0) {
                //SI afecta e consumo, por lo que, aun que sea merma, guardamos el importe total aproximado de esta devolución con lo ya acumulado de otras devoluciones dentro del presupuesto.
                DataBaseInterface.setFoliosInEstimate(DevolucionesFullReturnsList.this, "0", consumoActual);//valuesH[7]: "importeTotalAproximado"

            } else if (afffectingTheEstimate.trim().compareTo("N") == 0) {
                DataBaseInterface.setFoliosInMerma(DevolucionesFullReturnsList.this, "0", consumoActual);//valuesH[7]: "importeTotalAproximado"

            }
        }
    }

    public void callbackSendElement(ImageView imgStartSynchronize, int position) {
        this.startSynchronize(false, imgStartSynchronize, position);
    }

    public void startSynchronize(boolean allDevolutions, ImageView imgStartSynchronizeAux, int position) {

        /*final ImageView imgStartSynchronize = imgStartSynchronizeAux;

        imgStartSynchronize.setVisibility(View.VISIBLE);
        RotateAnimation rAnim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rAnim.setRepeatCount(Animation.INFINITE);
        rAnim.setInterpolator(new LinearInterpolator());
        rAnim.setDuration(1000);
        imgStartSynchronize.startAnimation(rAnim);*/

        if(allDevolutions){//All
            AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullReturnsList.this)
                    .setTitle("Aviso")
                    .setMessage("ENVIAR TODAS LAS DEVOLUCIONES")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            DevolucionesFullReturnsList.progressDialog = ProgressDialog.show(DevolucionesFullReturnsList.thiz, "Enviando", "Por favor espere...", true, false);
                            DevolucionesFullReturnsList.indexDevolucionesPendientes=0;
                            DevolucionesFullReturnsList.this.sendDevolution();

                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        }else {
            AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullReturnsList.this)
                    .setTitle("Aviso")
                    .setMessage("ENVIAR SOLO la "+position)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        }

        /*final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
        Runnable task = new Runnable() {
            public void run() {
                imgStartSynchronize.clearAnimation();
                imgStartSynchronize.setVisibility(View.INVISIBLE);
            }
        };
        worker.schedule(task, 5, TimeUnit.SECONDS);*/
    }

    public static void sendDevolution(){
        int i=DevolucionesFullReturnsList.indexDevolucionesPendientes;
        DevolucionesFullReturnsList.indexDevolucionesPendientes++;
        if(i >= DevolucionesFullReturnsList.thiz.returnRowAdapter.getCount()){
            if (DevolucionesFullReturnsList.progressDialog.isShowing()) {
                DevolucionesFullReturnsList.progressDialog.dismiss();
            }
            if( DevolucionesFullReturnsList.exitoAlMandarTodas ) {
                AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullReturnsList.thiz)
                        .setTitle("Aviso")
                        .setMessage("Devoluciones enviadas exitosamente")
                        .setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DevolucionesFullReturnsList.thiz.onBackPressed();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));
            }
            else{
                AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullReturnsList.thiz)
                        .setTitle("Aviso")
                        .setMessage("Algunas devoluciones no pudieron ser enviadas")
                        .setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DevolucionesFullReturnsList.thiz.onBackPressed();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));
            }
            return;
        }

        DevolucionPendiente devolucionPendiente = DevolucionesFullReturnsList.thiz.returnRowAdapter.getItem(i);

        if (devolucionPendiente.getStatusIBS().trim().compareTo("INT") == 0 && devolucionPendiente.getIdStatus().trim().compareTo("10") == 0) {
            String nameClient = devolucionPendiente.getNameClient();

            if (nameClient == null) {
                nameClient = DataBaseInterface.getNameClient(DevolucionesFullReturnsList.thiz, devolucionPendiente.getIDClient());
                devolucionPendiente.setNameClient(nameClient);
            }

            boolean noEeraseDataAndTime = true;
            devolucionPendiente.setFromDetailsFromThisDevolucion(DevolucionesFullReturnsList.thiz, devolucionPendiente, noEeraseDataAndTime);
            devolucionPendiente.sameProductsAndProductsOnDetail();
            devolucionPendiente.setStatusTransmit(" ");
            devolucionPendiente.setTimeTransmission("");

            PrepareSendingData prepareSendingData = new PrepareSendingData(DevolucionesFullReturnsList.thiz, devolucionPendiente);
            prepareSendingData.sendData();
        }
        else {
            DevolucionesFullReturnsList.sendDevolution();
        }
    }

    /**
     * Encargado de ocultar el teclado lógico
     */
    private void hidenkeyboard(TextView textView) {
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

    @Override
    protected void onStop() {
        try {
            ((TextView) folioToConsult.findViewById(R.id.idTxtDevFullNumInvoice)).setTextColor(Color.parseColor("#000000"));
        }catch (Exception e){}
        super.onStop();
    }

    /**
     * Sobre carga del método "onResume".
     */
    @Override
    protected void onRestart() {
        overridePendingTransition(R.anim.devoluciones_full_left_slide_in, R.anim.devoluciones_full_left_slide_out);
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent( DevolucionesFullReturnsList.this, DevolucionesFullConteiner.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(intent);
        finish();
    }
}
