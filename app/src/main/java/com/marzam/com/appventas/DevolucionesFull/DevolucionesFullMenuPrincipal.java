package com.marzam.com.appventas.DevolucionesFull;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.marzam.com.appventas.DevolucionesFull.DataBaseInterface.DataBaseInterface;
import com.marzam.com.appventas.DevolucionesFull.Devoluciones.DevolucionPendiente.DevolucionPendiente;
import com.marzam.com.appventas.DevolucionesFull.Interfaz.ColorSelect;
import com.marzam.com.appventas.DevolucionesFull.Interfaz.ScreenChilds;

import com.marzam.com.appventas.DevolucionesFull.PrepareSendingData.PrepareSendingData;
import com.marzam.com.appventas.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DevolucionesFullMenuPrincipal  implements ScreenChilds{

    /**
     * Contenedor de este layout.
     */
    private Activity context;

    // Layout de indicadores a mostrar.
    private LinearLayout layoutMainMenu;
    //private RelativeLayout statusInvoiceConteiner;
    // Layout en donde se da la opción de escoger alguna factura1.
    private LinearLayout layoutInvoiceRequired;
    // Layout en donde se da la opción de escoger solo los productos.
    private LinearLayout layoutProductOnly;
    // Layout en donde se da la opción de insertar una nota de crédito.
    private LinearLayout layoutNoteOnly;
    // Switch para determinar si la devolución es en buen o mal estado.
    private Switch switchStatePaper;
    // Spinner para seleccionar los folios.
    private Spinner spinnerFolioReasonRefund;
    // Botón para regresar.
    private Button btnReturnToIndicators;
    // Botón para montrar los motivos de la devolución.
    private Button btnGoToReasonReturnList;
    // Botón para montrar las facturas..
    private Button btnGoToInvoiceList;
    // Botón para mostrar los productos en relación de un folio de nota de cargo.
    private Button btnGoToProductStateNote;
    // Botón para mostrar los productos en relación a una factura.
    private Button btnGoToProduct;
    // Botón para guardar la devolución.
    private Button btnSaveReturn;
    // TextView para mostrar el numero de productos seleccionados.
    private TextView txtNumberProduct;
    // TextView para colocar el numero de la nota de cargo
    private TextView txtNumNote;
    // TextView para colocar el numero de bultos en la seccion de la nota de cargo
    private TextView txtNoteNumPackages;
    // TextView para colocar el numero de bultos en la seccion de la factura
    private TextView txtInvoiceNumPackages;
    // TextView para colocar el numero de bultos en la seccion de los productos
    private TextView txtProductNumPackages;

    public static boolean vieneDelMainMenu;
    private static String ultimoMotivoSeleccionado="";

    /*
    public static String reasonReturnSelected;
    public static String numberInvoiceToReturn;
    public static String numberProductToReturn;
    public static String statusInvoiceReasonReturn;
    */

    // Objeto donde se va acumulando los datos necesarios de la devolución que se esta realizando.
    private DevolucionPendiente devolucionPendiente = null;
    // Singleton
    public static DevolucionesFullMenuPrincipal thiz;
    // Indicador, bandera o clave para saber si se debe de mostrar la opción de captura de una factura.
    private final String INVOICE_REQUIRED = "1";
    // Indicador, bandera o clave para saber si se debe de mostrar la opción, solamente, de nota de cargo.
    private final String NOTE_ONLY = "2";

    /**
     * constructor
     * @param layout Id del layaout de "Indicadores".
     * @param context Activity donde se contendrá este layout.
     */
    public DevolucionesFullMenuPrincipal(LinearLayout layout, Activity context){
        this.layoutMainMenu = layout;
        this.context = context;
        //this.context.setTitle("Farmacia X");
        DevolucionesFullMenuPrincipal.vieneDelMainMenu = false;
        this.initInterfaz();
    }

    /**
     * * Inicializador de la "Interfaz".
     */
    @Override
    public void initInterfaz(){
        this.devolucionPendiente = new DevolucionPendiente();
        thiz = this;
        /*DevolucionesFullMenuPrincipal.reasonReturnSelected = null;
        DevolucionesFullMenuPrincipal.numberInvoiceToReturn = null;
        DevolucionesFullMenuPrincipal.numberProductToReturn = null;*/
        initSwitchStatePaper();
        initStatusInvoice();
    }

    /**
     * Inicializador de los botones.
     */
    @Override
    public void initButtons() {
        this.initButtonReturnToIndicators();
        this.initButtonGoToReasonReturnList();
        this.initButtonGoToInvoiceList();
        this.initButtonGoToProductStateNote();
        this.initButtonGoToProduct();
        this.initButtonSaveReturn();
    }

    /**
     * Remueve los eventos de los botones.
     */
    @Override
    public void removeListenerButtons() {
        this.btnReturnToIndicators.setOnClickListener(null);
        this.btnGoToReasonReturnList.setOnClickListener(null);
        this.btnGoToInvoiceList.setOnClickListener(null);
    }

    /**
     * Inicializador del Botón para regresar a la "Pantalla de indicadores".
     */
    private void initButtonReturnToIndicators(){
        this.btnReturnToIndicators = (Button)this.layoutMainMenu.findViewById(R.id.idBtnDevFullReturnsConsult);
        this.btnReturnToIndicators.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullMenuPrincipal.this.context)
                        .setTitle("Aviso")
                        .setMessage("¿Cancelar devolución?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {*/
                ((DevolucionesFullConteiner) DevolucionesFullMenuPrincipal.this.context).onBackPressed();
                            /*}
                        })
                        .setNegativeButton("Cancelar", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();*/
            }
        });
    }

    /**
     * Inicializador del Botón para mostrar la "Pantalla de motivos".
     */
    private void initButtonGoToReasonReturnList() {
        this.btnGoToReasonReturnList = (Button)this.layoutMainMenu.findViewById(R.id.idbtnGoToReasonReturnList);
        this.btnGoToReasonReturnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevolucionesFullMenuPrincipal.this.btnGoToReasonReturnList.setTextColor(Color.parseColor(ColorSelect.COLOR_SELECTED));
                ((DevolucionesFullConteiner) DevolucionesFullMenuPrincipal.this.context).initCallbackReasonReturnList(!DevolucionesFullMenuPrincipal.this.switchStatePaper.isChecked());
                DevolucionesFullMenuPrincipal.this.initInvoiceAndNumProduct();
            }
        });
    }

    /**
     * Inicializador del Botón para mostrar la "Pantalla de facturas".
     */
    private void initButtonGoToInvoiceList() {
        this.btnGoToInvoiceList = (Button)this.layoutMainMenu.findViewById(R.id.idbtnGoToInvoiceList);
        this.txtInvoiceNumPackages = (TextView)this.layoutMainMenu.findViewById(R.id.idSetDevFullInvoiceNumPackages);
        this.txtInvoiceNumPackages.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strEnteredVal = DevolucionesFullMenuPrincipal.this.txtInvoiceNumPackages.getText().toString();
                if (!strEnteredVal.equals("")) {
                    int num = Integer.parseInt(strEnteredVal);
                    if (num > 99) {
                        DevolucionesFullMenuPrincipal.this.txtInvoiceNumPackages.setText("99");
                    }
                }
            }
        });
        this.btnGoToInvoiceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( DevolucionesFullMenuPrincipal.this.verificationFolio() ) {
                    DevolucionesFullMenuPrincipal.this.btnGoToInvoiceList.setTextColor(Color.parseColor(ColorSelect.COLOR_SELECTED));
                    ((DevolucionesFullConteiner) DevolucionesFullMenuPrincipal.this.context).initCallbackInvoiceList(DevolucionesFullMenuPrincipal.this.devolucionPendiente.getReasonReturnSelected());
                    //((DevolucionesFullConteiner) DevolucionesFullMenuPrincipal.this.context).initCallbackInvoiceList(DevolucionesFullMenuPrincipal.reasonReturnSelected);
                }
            }
        });

        this.txtNumberProduct = (TextView)this.layoutMainMenu.findViewById(R.id.idTxtNumberProduct);
    }

    /**
     * Inicializador del Botón para mostrar la "Pantalla de Productos", en función de una nota de cargo.
     */
    private void initButtonGoToProductStateNote() {
        this.txtNumNote = (TextView)this.layoutMainMenu.findViewById(R.id.idSetDevFullNumNote);
        this.txtNoteNumPackages = (TextView)this.layoutMainMenu.findViewById(R.id.idSetDevFullNoteNumPackages);
        this.txtNoteNumPackages.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strEnteredVal = DevolucionesFullMenuPrincipal.this.txtNoteNumPackages.getText().toString();
                if (!strEnteredVal.equals("")) {
                    int num = Integer.parseInt(strEnteredVal);
                    if (num > 99) {
                        DevolucionesFullMenuPrincipal.this.txtNoteNumPackages.setText("99");
                    }
                }
            }
        });

            this.btnGoToProductStateNote = (Button)this.layoutMainMenu.findViewById(R.id.idbtnGoToProductStateNote);
        this.btnGoToProductStateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( DevolucionesFullMenuPrincipal.this.verificationFolio() ) {
                    DevolucionesFullMenuPrincipal.this.btnGoToProductStateNote.setTextColor(Color.parseColor(ColorSelect.COLOR_SELECTED));
                    ((DevolucionesFullConteiner) DevolucionesFullMenuPrincipal.this.context).initCallbackProductListState(DevolucionesFullMenuPrincipal.this.devolucionPendiente.getReasonReturnSelected());
                    //((DevolucionesFullConteiner) DevolucionesFullMenuPrincipal.this.context).initCallbackProductListState(DevolucionesFullMenuPrincipal.reasonReturnSelected);
                }
            }
        });
    }

    /**
     * Inicializador del Botón para mostrar la "Pantalla de Productos", en función de una factura.
     */
    private void initButtonGoToProduct() {
        this.btnGoToProduct = (Button)this.layoutMainMenu.findViewById(R.id.idbtnGoToProduct);
        this.txtProductNumPackages = (TextView)this.layoutMainMenu.findViewById(R.id.idSetDevFullProductNumPackages);
        this.txtProductNumPackages.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strEnteredVal = DevolucionesFullMenuPrincipal.this.txtProductNumPackages.getText().toString();
                if (!strEnteredVal.equals("")) {
                    int num = Integer.parseInt(strEnteredVal);
                    if (num > 99) {
                        DevolucionesFullMenuPrincipal.this.txtProductNumPackages.setText("99");
                    }
                }
            }
        });

        this.btnGoToProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( DevolucionesFullMenuPrincipal.this.verificationFolio() ) {
                    DevolucionesFullMenuPrincipal.this.btnGoToProduct.setTextColor(Color.parseColor(ColorSelect.COLOR_SELECTED));
                    ((DevolucionesFullConteiner) DevolucionesFullMenuPrincipal.this.context).initCallbackProductList(DevolucionesFullMenuPrincipal.this.devolucionPendiente.getReasonReturnSelected());
                    //((DevolucionesFullConteiner) DevolucionesFullMenuPrincipal.this.context).initCallbackProductList(DevolucionesFullMenuPrincipal.reasonReturnSelected);
                }
            }
        });
    }

    /**
     * Inicializador del Botón para salvar la devolución y regresar a la pantalla de indicadores.
     */
    private void initButtonSaveReturn() {
        this.btnSaveReturn = (Button)this.layoutMainMenu.findViewById(R.id.idBtnDevFullSaveReturn);
        this.btnSaveReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean returnIsReady = true;
                if (DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().isAvalibleReturn()) {
                    if (DevolucionesFullMenuPrincipal.this.layoutNoteOnly.getVisibility() == View.VISIBLE) {
                        if (DevolucionesFullMenuPrincipal.this.txtNumNote.getText().toString().trim().length() == 0) {
                            returnIsReady = false;
                        }
                    }
                } else {
                    returnIsReady = false;
                }

                if (returnIsReady) {//(DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().isAvalibleReturn()) {
                    String reason = DevolucionesFullMenuPrincipal.this.devolucionPendiente.getReasonReturnSelected();
                    String perfilClient = DataBaseInterface.getPerfilCliente(DevolucionesFullMenuPrincipal.this.context );
                    String[] arrayReason = reason.split(" ");
                    String idReason = arrayReason[0];
                    String[] thisReasonNeedAuthorizationAndType = DevolucionesFullMenuPrincipal.this.thisReasonNeedAuthorization(idReason);
                    boolean needAuthorization = thisReasonNeedAuthorizationAndType[0].trim().compareTo("Y") == 0;
                    boolean thisFolioIsAuthorization = true;

                    if (needAuthorization) {
                        String folio = DevolucionesFullMenuPrincipal.this.spinnerFolioReasonRefund.getSelectedItem().toString().trim();
                        thisFolioIsAuthorization = DevolucionesFullMenuPrincipal.this.thisFolioHasThisTypeAuthorization(folio, thisReasonNeedAuthorizationAndType[1].trim());
                    }

                    if (thisFolioIsAuthorization) {

                        //Checamos si es nota de cargo
                        if (idReason.trim().compareTo("10")==0 && perfilClient.trim().compareTo("3")!=0) {
                            AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullMenuPrincipal.this.context)
                                    .setTitle("Aviso")
                                    .setMessage("El perfil del cliente no aplica para este tipo de devolución")
                                    .setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"), null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));
                        }
                        else{
                            AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullMenuPrincipal.this.context)
                                .setTitle("Aviso")
                                .setMessage("¿Guardar devolución?")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DevolucionesFullMenuPrincipal.this.btnSaveReturn.setTextColor(Color.parseColor(ColorSelect.COLOR_SELECTED));

                                        String folio = DevolucionesFullMenuPrincipal.this.spinnerFolioReasonRefund.getSelectedItem().toString().trim();
                                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setFolioForThisReturn(folio);
                                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setSend(false);
                                        Date cDate = new Date();
                                        String fDate = new SimpleDateFormat("yyyyMMdd").format(cDate);
                                        String tDate = new SimpleDateFormat("HHmmss").format(cDate);
                                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setDateThatThisReturnWasSave(fDate);
                                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setTimeThatThisReturnWasSave(tDate);
                                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setNameClient(DevolucionesFullConteiner.NAME_PHARMACY);

                                        String idCliente = DataBaseInterface.getIDCLiente(DevolucionesFullMenuPrincipal.thiz.context);
                                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setIDClient(idCliente);

                                        String claveAgent = DataBaseInterface.getClaveAgent(DevolucionesFullMenuPrincipal.thiz.context);
                                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setClaveAgente(claveAgent);

                                        String strNumPackages = "";
                                        if (DevolucionesFullMenuPrincipal.this.layoutNoteOnly.getVisibility() == View.VISIBLE) {
                                            strNumPackages = DevolucionesFullMenuPrincipal.this.txtNoteNumPackages.getText().toString().trim();
                                            if (strNumPackages.trim().length() == 0) {
                                                strNumPackages = "1";
                                            }
                                        } else if (DevolucionesFullMenuPrincipal.this.layoutInvoiceRequired.getVisibility() == View.VISIBLE) {
                                            strNumPackages = DevolucionesFullMenuPrincipal.this.txtInvoiceNumPackages.getText().toString().trim();
                                            if (strNumPackages.trim().length() == 0) {
                                                strNumPackages = "1";
                                            }
                                        } else if (DevolucionesFullMenuPrincipal.this.layoutProductOnly.getVisibility() == View.VISIBLE) {
                                            strNumPackages = DevolucionesFullMenuPrincipal.this.txtProductNumPackages.getText().toString().trim();
                                            if (strNumPackages.trim().length() == 0) {
                                                strNumPackages = "1";
                                            }
                                        }
                                        DevolucionesFullMenuPrincipal.this.getDevolucionPendiente().setNumPackages(strNumPackages);

                                        String numEmpleado = DataBaseInterface.getNumEmpleado(DevolucionesFullMenuPrincipal.thiz.context);
                                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setHandlerAutorizacion(numEmpleado);

                                        /*String consecutivo = DataBaseInterface.getConsecutivo(DevolucionesFullMenuPrincipal.thiz.context);*/
                                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setConsecutivoCaptura("");
                                        //salvar Integer.parseInt(consecutivo) + 1)

                                        String descuentoComercial = DataBaseInterface.getDescuentoComercialCliente(DevolucionesFullMenuPrincipal.thiz.context);
                                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setDiscountByClient(descuentoComercial);

                                        String[] arrayReason = DevolucionesFullMenuPrincipal.thiz.btnGoToReasonReturnList.getText().toString().split(" ");
                                        String idReason = arrayReason[0];
                                        String porsentajeBonificacion = DataBaseInterface.getPorcentajeBonificacion(DevolucionesFullMenuPrincipal.thiz.context, idReason);
                                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setPercentageBonus(porsentajeBonificacion);

                                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setStatusFolio("A");
                                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setStatusIBS("INI");
                                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setStatusAutorizacion("AUT");
                                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setIdStatus("10");
                                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setStatusTransmit(" ");
                                        DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setTimeTransmission("");

                                        //Simulador de base de datos
                                        DevolucionesFullMenuPrincipal.this.savePendingReturn(DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente());
                                        DevolucionesFullMenuPrincipal.vieneDelMainMenu = true;
                                        ((DevolucionesFullConteiner) DevolucionesFullMenuPrincipal.this.context).onBackPressed();
                                    }
                                })
                                .setNegativeButton("Cancelar", null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setCancelable(false)
                                .show();
                        }
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullMenuPrincipal.this.context)
                                .setTitle("Aviso")
                                .setMessage("El folio seleccionado no está autorizado para este tipo de devolución, contacte a su consultor.")
                                .setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"), null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));
                    }
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullMenuPrincipal.this.context)
                            .setTitle("Alerta")
                            .setMessage("Complete el formulario")
                            .setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"), null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));
                }
            }
        });
    }

    /**
     * Inicializador Switch para inicializar la pantalla, según el estado de la devolución.
     */
    private void initSwitchStatePaper(){
        this.switchStatePaper = (Switch)this.layoutMainMenu.findViewById(R.id.idSwitchStatePaper);
        StateListDrawable thumbStates = new StateListDrawable();
        thumbStates.addState(new int[]{android.R.attr.state_checked}, new ColorDrawable( Color.parseColor("#FA5858") ));
        thumbStates.addState(new int[]{-android.R.attr.state_enabled}, new ColorDrawable( Color.parseColor("#A4A4A4") ));///
        thumbStates.addState(new int[]{}, new ColorDrawable(Color.parseColor("#A4A4A4")));
        this.switchStatePaper.setThumbDrawable(thumbStates);

        this.switchStatePaper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                rebootScreen();
            }
        });
    }

    /**
     * Método para inicializar la pantalla, según el motivo de la devolución, es decir, se requiere factura,
     * solo mostrar los productos o la nota de cargo.
     * @param statusInvoice Estatus con el cual se decide si mostrar la factura, solo los productos o solo la nota de cargo.
     */
    private void initStatusInvoice( String ... statusInvoice ){
        if( this.layoutInvoiceRequired==null||this.layoutProductOnly==null||this.layoutNoteOnly==null) {
            //this.statusInvoiceConteiner = (RelativeLayout) this.layoutMainMenu.findViewById(R.id.idSetDevFullStatusInvoiceConteiner);
            this.layoutInvoiceRequired = (LinearLayout) this.layoutMainMenu.findViewById(R.id.idSetDevFullInvoiceRequired);
            this.layoutProductOnly = (LinearLayout) this.layoutMainMenu.findViewById(R.id.idSetDevFullProductOnly);
            this.layoutNoteOnly = (LinearLayout) this.layoutMainMenu.findViewById(R.id.idSetDevFullNoteOnly);
        }

        if(statusInvoice.length==0) {
            /*this.statusInvoiceConteiner.removeView(this.layoutInvoiceRequired);
            this.statusInvoiceConteiner.removeView(this.layoutNoteOnly);
            this.statusInvoiceConteiner.removeView(this.layoutProductOnly);

            this.statusInvoiceConteiner.addView(this.layoutInvoiceRequired);*/
            this.layoutInvoiceRequired.setVisibility( View.INVISIBLE );
            this.layoutNoteOnly.setVisibility( View.INVISIBLE );
            this.layoutProductOnly.setVisibility( View.INVISIBLE );

            //this.layoutInvoiceRequired.setVisibility( View.INVISIBLE );
        }
        else{
            /*this.statusInvoiceConteiner.removeView(this.layoutInvoiceRequired);
            this.statusInvoiceConteiner.removeView(this.layoutNoteOnly);
            this.statusInvoiceConteiner.removeView(this.layoutProductOnly);*/
            this.layoutInvoiceRequired.setVisibility( View.INVISIBLE );
            this.layoutNoteOnly.setVisibility( View.INVISIBLE );
            this.layoutProductOnly.setVisibility( View.INVISIBLE );

            if( statusInvoice[0].compareTo(this.INVOICE_REQUIRED)==0 ) {
                //this.statusInvoiceConteiner.addView(this.layoutInvoiceRequired);
                this.layoutInvoiceRequired.setVisibility( View.VISIBLE );
            }else if( statusInvoice[0].compareTo(this.NOTE_ONLY)==0 ) {
                //this.statusInvoiceConteiner.addView(this.layoutNoteOnly);
                this.layoutNoteOnly.setVisibility( View.VISIBLE );
            } else {
                //this.statusInvoiceConteiner.addView(this.layoutProductOnly);
                this.layoutProductOnly.setVisibility( View.VISIBLE );
            }

            //this.layoutInvoiceRequired.setVisibility( View.VISIBLE );
        }
    }

    /**
     * Inicializa los folios, según el estado de la devolución.
     */
    private void initFoliosReturn() {
        if( this.spinnerFolioReasonRefund==null )
            this.spinnerFolioReasonRefund = (Spinner) this.layoutMainMenu.findViewById(R.id.idSetDevFullFolioReasonRefund);

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_dropdown_item, getFoliosReturn());
        this.spinnerFolioReasonRefund.setAdapter(listAdapter);

        if( this.spinnerFolioReasonRefund.getCount()<=0 ){
            AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullMenuPrincipal.this.context)
                    .setTitle("Aviso")
                    .setMessage("Los folios de la papeleta seleccionada se encuentran agotados, contacte a su consultor.")
                    .setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DevolucionesFullMenuPrincipal.vieneDelMainMenu = true;
                            //DevolucionesFullMenuPrincipal.this.setReasonReturn();
                            ((DevolucionesFullConteiner) DevolucionesFullMenuPrincipal.this.context).onBackPressed();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));
        }
    }

    /**
     * Obtiene los folios de la devolución.
     * @return Lista de folios.
     */
    private List<String> getFoliosReturn() {
        List<String> list;
        boolean switchStatePaper = DevolucionesFullMenuPrincipal.this.switchStatePaper.isChecked();

        if( switchStatePaper ){
            list = DataBaseInterface.getFoliosForBadStatusReturned(this.context);
            DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setTypeForThisReturn("M");
        }else{
            list = DataBaseInterface.getFoliosForGoodStatusReturned(this.context);
            DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setTypeForThisReturn("B");
        }

        return list;
    }

    /**
     * Verifica si el motivo necesita autorización.
     * @return Lista de folios.
     */
    private String[] thisReasonNeedAuthorization( String idReason ) {
        String[] needAuthorizationAndType = DataBaseInterface.thisReasonNeedAuthorization(this.context, idReason);
        return needAuthorizationAndType;
    }

    /**
     * Verifica si el motivo necesita autorización.
     * @return Lista de folios.
     */
    private boolean thisFolioHasThisTypeAuthorization(String folio, String typeAuthorization ) {

        return DataBaseInterface.thisFolioHasThisTypeAuthorization(this.context, folio, typeAuthorization);
    }



    /**
     * Reinicia la pantalla, si es que a cambiado el estado de la devolución, es decir, si se modifica el switch
     * con el cual se conoce el estado de la devolución (Mal estado o Buen estado).
     */
    public void rebootScreen(){
        this.devolucionPendiente.initDevolucionPendiente();
        /*DevolucionesFullMenuPrincipal.reasonReturnSelected = null;
        DevolucionesFullMenuPrincipal.numberInvoiceToReturn = null;
        DevolucionesFullMenuPrincipal.numberProductToReturn = null;*/

        this.initFoliosReturn();
        this.setReasonReturn();

        this.setNumInvoice();
        this.initStatusInvoice();

        this.setNumberProductToReturn();
    }

    /**
     * Método el cual se encarga de, previamente seleccionado el motivo de la devolución en su respectiva pantalla, colocar, como texto, el motivo de
     * la devolución en el botón que se usa para mostrar dicha pantalla de motivos.
     */
    public void setReasonReturn(){
        if( this.devolucionPendiente.getReasonReturnSelected() == null ){
            this.btnGoToReasonReturnList.setText( "Motivo" );
            ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>());
            this.spinnerFolioReasonRefund.setAdapter(listAdapter);
            this.txtNumNote.setText("");
        } else {
            this.btnGoToReasonReturnList.setText(this.devolucionPendiente.getReasonReturnSelected());
            this.initStatusInvoice(this.devolucionPendiente.getStatusInvoiceReasonReturn());
            this.initFoliosReturn();
            this.btnGoToReasonReturnList.setTextColor(Color.parseColor("#FFFFFF"));
            this.btnGoToInvoiceList.setTextColor(Color.parseColor("#FFFFFF"));

            if(DevolucionesFullMenuPrincipal.ultimoMotivoSeleccionado != this.devolucionPendiente.getReasonReturnSelected()){
                DevolucionesFullMenuPrincipal.ultimoMotivoSeleccionado = this.devolucionPendiente.getReasonReturnSelected();

                this.txtInvoiceNumPackages.setText("");
                this.txtNumNote.setText("");
                this.txtNoteNumPackages.setText("");
                txtProductNumPackages.setText("");
                DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().setAvalibleReturn( false );
            }

        }
    }

    /**
     * Inicializador de los textView para mostrar la factura seleccionada y la cantidad de productos seleccionados de dicha factura.
     */
    private void initInvoiceAndNumProduct(){
        this.devolucionPendiente.setNumberInvoiceToReturn(null);
        this.devolucionPendiente.setNumberProductToReturn(null);

        setNumInvoice();
        setNumberProductToReturn();
    }

    /**
     * Inicializa el texto del botón de motivos.
     */
    public void clearReasonReturn(){
        this.btnGoToReasonReturnList.setTextColor(Color.parseColor("#FFFFFF"));
        this.btnGoToInvoiceList.setTextColor(Color.parseColor("#FFFFFF"));
    }

    /**
     * Método el cual se encarga de, previamente seleccionado la factura en su respectiva pantalla, colocar, como texto, el numero de
     * la factura  en el botón que se usa para mostrar dicha pantalla de facturas.
     */
    public void setNumInvoice(){
        if( this.devolucionPendiente.getNumberInvoiceToReturn() == null ){
            this.btnGoToInvoiceList.setText("Factura");
        }else {
            this.btnGoToInvoiceList.setText(this.devolucionPendiente.getNumberInvoiceToReturn());
            this.btnGoToInvoiceList.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    /**
     * Inicializa el texto del botón de facturas.
     */
    public void clearNumInvoice(){
        this.btnGoToInvoiceList.setTextColor(Color.parseColor("#FFFFFF"));

        this.btnGoToInvoiceList.setText("Factura");

    }

    /**
     * Método el cual se encarga de, previamente seleccionado los productos en su respectiva pantalla, colocar, como texto, el numero de
     * productos seleccionados en el botón o el textView que se usa para mostrar dicha información de la pantalla de productos.
     */
    public void setNumberProductToReturn(){
        if( this.devolucionPendiente.getNumberProductToReturn() == null ){
            this.btnGoToProduct.setText( "0 Productos seleccionados" );
            this.btnGoToProductStateNote.setText( "0 Productos seleccionados" );
            this.txtNumberProduct.setText("0");
        }else {
            this.btnGoToProduct.setText(this.devolucionPendiente.getNumberProductToReturn() + " Productos seleccionados");
            this.btnGoToProductStateNote.setText(this.devolucionPendiente.getNumberProductToReturn() + " Productos seleccionados");
            this.txtNumberProduct.setText(this.devolucionPendiente.getNumberProductToReturn()+"");
            this.btnGoToProduct.setTextColor(Color.parseColor("#FFFFFF"));
            this.btnGoToProductStateNote.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    /**
     * Inicializa el texto del botón de productos o el textView.
     */
    public void clearNumberProductToReturn(){
        this.btnGoToProduct.setTextColor(Color.parseColor("#FFFFFF"));
        this.btnGoToProductStateNote.setTextColor(Color.parseColor("#FFFFFF"));

        this.btnGoToProduct.setText( "0 Productos seleccionados" );
        this.btnGoToProductStateNote.setText( "0 Productos seleccionados");
        this.txtNumberProduct.setText("0");
    }

    /**
     * Salva la devolución realizada.
     * @param pendingReturn Devolución a salvar.
     */
    private void savePendingReturn( DevolucionPendiente pendingReturn ){
        PrepareSendingData prepareSendingData = new PrepareSendingData(this.context, pendingReturn);
        prepareSendingData.saveOnDataBase();
        //DataBaseInterface.addPendingReturns(pendingReturn);
    }

    /**
     * Método para obtener la devolución que se está realizando actualmente.
     * @return Devolución que se está realizando actualmente.
     */
    public DevolucionPendiente getDevolucionPendiente(){
        return this.devolucionPendiente;
    }
    /**
     * Este método verifica si el folio esta autorizado pora el motivo de devolución seleccionado
     */
    private boolean verificationFolio(){
        String reason = DevolucionesFullMenuPrincipal.this.devolucionPendiente.getReasonReturnSelected();
        String perfilClient = DataBaseInterface.getPerfilCliente(DevolucionesFullMenuPrincipal.this.context );
        String[] arrayReason = reason.split(" ");
        String idReason = arrayReason[0];
        String[] thisReasonNeedAuthorizationAndType = DevolucionesFullMenuPrincipal.this.thisReasonNeedAuthorization( idReason );
        boolean needAuthorization = thisReasonNeedAuthorizationAndType[0].trim().compareTo("Y")==0;
        boolean thisFolioIsAuthorization = true;

        if( needAuthorization ){
            String folio = DevolucionesFullMenuPrincipal.this.spinnerFolioReasonRefund.getSelectedItem().toString().trim();
            thisFolioIsAuthorization = DevolucionesFullMenuPrincipal.this.thisFolioHasThisTypeAuthorization(folio, thisReasonNeedAuthorizationAndType[1].trim() );
        }
        if( !thisFolioIsAuthorization ) {
            AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullMenuPrincipal.this.context)
                    .setTitle("Aviso")
                    .setMessage("El folio seleccionado no está autorizado para este tipo de devolución, contacte a su consultor.")
                    .setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"), null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));
            return false;
        }

        //Checamos si es nota de cargo
        if (idReason.trim().compareTo("10")==0 && perfilClient.trim().compareTo("3")!=0) {
            AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullMenuPrincipal.this.context)
                    .setTitle("Aviso")
                    .setMessage("El perfil del cliente no aplica para este tipo de devolución")
                    .setPositiveButton(Html.fromHtml("<font color='#FFFFFF'><b>Aceptar</b></font>"), null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setBackgroundColor(Color.parseColor("#0E3E91"));
            return false;
        }

        return true;
    }

}
