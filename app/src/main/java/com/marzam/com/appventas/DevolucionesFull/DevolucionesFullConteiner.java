package com.marzam.com.appventas.DevolucionesFull;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.marzam.com.appventas.DataBase.DataBase;
import com.marzam.com.appventas.DevolucionesFull.DataBaseInterface.DataBaseInterface;
import com.marzam.com.appventas.DevolucionesFull.Devoluciones.DevolucionesFullReturnsList;
import com.marzam.com.appventas.DevolucionesFull.Facturas.DevolucionesFullInvoiceList;
import com.marzam.com.appventas.DevolucionesFull.Interfaz.ScreenChilds;
import com.marzam.com.appventas.DevolucionesFull.Productos.DevolucionesFullProductList;
import com.marzam.com.appventas.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lcabral on 02/06/2015.
 */
public class DevolucionesFullConteiner extends Activity {

    //Barra que indica la disponibilidad de las devoluciones.
    private TextView txtPercentageReturn;
    private ProgressBar progressBarReturnsAvailable;

    //Ancho del contenedor de las subpantallas
    private int width;
    private int height;

    //Nombre de la farmacia.
    public static String NAME_PHARMACY;

    //Constantes, indican en qué estado se encuentra la pantalla.
    private final int ON_INDICATORS = 1;
    private final int ON_MAIN_MENU = 2;

    //Elementos para el manejo de las sub pantallas "Indicadores" y "Menú principal".
    private List<ScreenChilds> listScreens = new ArrayList<ScreenChilds>();
    private DevolucionesFullMenuPrincipal develucionesFullMenuPrincipal;
    private DevolucionesFullIndicadores develucionesFullIndicadores;
    private LinearLayout layoutScreen;

    //Indicador de la subpantalla presente actualmente
    private int statusScreen = 0;

    //Layouts de las subpantallas
    private LinearLayout mainConteiner = null;
    private LinearLayout conteinerAux = null;

    //Tiempo en milisegundos para la transición entre las subpantallas.
    private long timeTransition = 250;

    //Singleton
    public  static DevolucionesFullConteiner thiz;

    /**
     * Creador de la Actividad.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devoluciones_full_contenedor);
        overridePendingTransition(R.anim.devoluciones_full_right_slide_in, R.anim.devoluciones_full_right_slide_out);

        NAME_PHARMACY = DataBaseInterface.getNameClient( DevolucionesFullConteiner.this, DataBaseInterface.getIDCLiente(DevolucionesFullConteiner.this) );
        setTitle("Devoluciones - "+NAME_PHARMACY);

        thiz = this;
        initInterfaz();
    }


    /**
     * Iniciador de la construcción de la interfaz grafica.
     */
    private void initInterfaz() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;

        this.initConteiners();
        this.initProgressBarReturnsAvailable();

        this.statusScreen = this.ON_INDICATORS;
        this.initIndicatorsScreen();
    }

    /**
     * Inicializador de los contenedores de "Indicadores" y "Menú principal".
     */
    private void initConteiners() {
        if( this.mainConteiner==null || this.conteinerAux==null ) {
            this.mainConteiner = (LinearLayout) findViewById(R.id.idConteiner);
            this.conteinerAux = (LinearLayout) findViewById(R.id.idConteinerAux);
        }

        this.mainConteiner.setTranslationX(0);
        this.conteinerAux.setTranslationX(this.width);
    }

    /**
     * Inicializador de la barra donde se muestra la "Disponibilidad de las devoluciones".
     */
    private void initProgressBarReturnsAvailable() {
        this.txtPercentageReturn = (TextView) findViewById(R.id.idTxtDevFullReturnsContenedorPercentageReturn);
        this.progressBarReturnsAvailable = (ProgressBar) findViewById(R.id.idProgressBarReturnsAvailable);
    }

    /**
     * Coloca el valor del progressBar que muestra el uso de la disponibilidad de las devoluciones.
     * @param intProgress
     */
    public static void setValueProgressBarReturnsAvailable( int intProgress ) {
        thiz.txtPercentageReturn.setText(intProgress+"%");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            ObjectAnimator animation = ObjectAnimator.ofInt(thiz.progressBarReturnsAvailable, "progress", intProgress);
            animation.setDuration(1000); // 0.5 second
            animation.setStartDelay(1000);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
        } else
            thiz.progressBarReturnsAvailable.setProgress( intProgress );
    }

    //-----Metodos para iniciar la pantalla de "Indicatores"--------------------------------------------------------------------
    /**
     * Inicializador del layout de los "Indicadores".
     */
    private void initIndicatorsScreen() {
        if( this.statusScreen==this.ON_INDICATORS ) {
            this.setMainScreen(R.layout.devoluciones_full_indicadores);
            goToIndicators();
            this.listScreens.get(0).initButtons();
        }
        else if( this.statusScreen==this.ON_MAIN_MENU ) {
            this.setNextScreen(R.layout.devoluciones_full_indicadores);
            goToIndicators();
            goToReturnScreen();
        }
    }

    /**
     * Método para ir la ala pantalla de indicadores.
     */
    private void goToIndicators() {
        this.statusScreen = this.ON_INDICATORS;
        this.develucionesFullIndicadores = new DevolucionesFullIndicadores(this.layoutScreen, this);
        //Save screen
        this.listScreens.add(this.develucionesFullIndicadores);
    }


    //-----Metodos para iniciar la pantalla de "Main Menu"---------------------------------------------------------------------

    /**
     * Inicializador del layout de "Main Menu".
     */
    public void initCallbackMainMenuScreen() {
        this.setMainMenuScreen();
        this.goToNextScreen();
    }

    /**
     * Método para preparar el "Menú principal", antes que aparezca.
     */
    private void setMainMenuScreen() {
        this.setNextScreen(R.layout.devoluciones_full_menu_principal);
        goMainMenu();
    }

    /**
     * Método para ir la ala pantalla del "Menú principal".
     */
    private void goMainMenu() {
        this.statusScreen = this.ON_MAIN_MENU;
        this.develucionesFullMenuPrincipal = new DevolucionesFullMenuPrincipal( this.layoutScreen, this );
        //Save screen
        this.listScreens.add(this.develucionesFullMenuPrincipal);
    }

    //-----Metodos para iniciar la pantalla de "Lista de motivos"---------------------------------------------------------------------

    /**
     * Método para iniciar la lista de "Motivos de devolución"
     * @param isGoodStatusReturned Estado de la "papeleta de devolución", TRUE: Buen estado, FALSE: Mal estado.
     */
    public void initCallbackReasonReturnList( final boolean isGoodStatusReturned ){

        Intent intent=new Intent( DevolucionesFullConteiner.this, DevolucionesFullReasonReturnList.class );
        intent.putExtra("isGoodStatusReturned", isGoodStatusReturned);
        intent.putExtra("namePharmacy", this.NAME_PHARMACY );
        startActivity(intent);
    }

    //-----Metodos para iniciar la pantalla de "Lista de facturas"---------------------------------------------------------------------

    /**
     * Método para iniciar la lista de las "Facturas"
     * @param reasonToReturn Motivo de la devolución.
     */
    public void initCallbackInvoiceList( String reasonToReturn  ){
        Intent intent=new Intent( DevolucionesFullConteiner.this, DevolucionesFullInvoiceList.class );
        intent.putExtra("namePharmacy", this.NAME_PHARMACY);
        intent.putExtra("reasonToReturn", reasonToReturn);
        startActivity(intent);
    }

    //-----Metodos para iniciar la pantalla de "Lista de productos"---------------------------------------------------------------------
    /**
     * Método para iniciar la lista de las "Productos"
     */
    public void initCallbackProductList( String reasonToReturn ){
        Intent intent=new Intent( DevolucionesFullConteiner.this, DevolucionesFullProductList.class );
        intent.putExtra("namePharmacy", this.NAME_PHARMACY);
        intent.putExtra("reasonToReturn", reasonToReturn);
        startActivity(intent);
    }

    //-----Metodos para iniciar la pantalla de "Lista de productos"---------------------------------------------------------------------
    /**
     * Método para iniciar la lista de las "Productos"
     */
    public void initCallbackProductListState( String reasonToReturn ){
        Intent intent=new Intent( DevolucionesFullConteiner.this, DevolucionesFullProductList.class );
        intent.putExtra("namePharmacy", this.NAME_PHARMACY);
        intent.putExtra("reasonToReturn", reasonToReturn);
        startActivity(intent);
    }

    //-----Metodos para iniciar la pantalla de "Lista de devoluciones pendientes"---------------------------------------------------------------------
    /**
     * Método para iniciar la lista de las "Deoluciones pendientes"
     */
    public void initCallbackReturnsConsult(){
        Intent intent=new Intent( DevolucionesFullConteiner.this, DevolucionesFullReturnsList.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(intent);
        finish();
    }


    //-----Metodos para cambiar de pantallas------------------------------------------------------------------------------------
    /**
     * Método para mostrar las siguientes pantallas, en este caso el "Menú principal".
     */
    private void goToNextScreen(){
        this.listScreens.remove(0).removeListenerButtons();
        this.conteinerAux.setTranslationX(DevolucionesFullConteiner.this.width);

        this.mainConteiner.animate().setDuration( this.timeTransition );
        this.mainConteiner.animate().translationX(DevolucionesFullConteiner.this.width * -1);

        this.conteinerAux.animate().setDuration(this.timeTransition);
        this.conteinerAux.animate().translationX(0);

        this.conteinerAux.animate().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                DevolucionesFullConteiner.this.conteinerAux.animate().setListener(null);
                DevolucionesFullConteiner.this.listScreens.get(0).initButtons();
                switchContainers();
            }
        });
    }

    /**
     * Método para mostrar las anteriores pantallas, en este caso los "Indicadores".
     */
    private void goToReturnScreen(){
        this.listScreens.remove(0).removeListenerButtons();
        this.conteinerAux.setTranslationX(DevolucionesFullConteiner.this.width*-1);

        this.mainConteiner.animate().setDuration( this.timeTransition );
        this.mainConteiner.animate().translationX(DevolucionesFullConteiner.this.width);

        this.conteinerAux.animate().setDuration(this.timeTransition);
        this.conteinerAux.animate().translationX(0);

        this.conteinerAux.animate().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                DevolucionesFullConteiner.this.conteinerAux.animate().setListener(null);
                DevolucionesFullConteiner.this.listScreens.get(0).initButtons();
                switchContainers();
            }
        });
    }

    /**
     * Método para colocar en el layout principal la pantalla a mostrar.
     * @param screen ID del screen a mostrar.
     */
    private void setMainScreen(int screen) {
        this.mainConteiner.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        this.layoutScreen = (LinearLayout) inflater.inflate(screen, null, false);
        this.mainConteiner.addView(this.layoutScreen);
    }

    /**
     * Método para colocar en el layout siguiete a mostarar.
     * @param screen ID del screen.
     */
    private void setNextScreen(int screen) {
        this.conteinerAux.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        this.layoutScreen = (LinearLayout) inflater.inflate(screen, null, false);
        this.conteinerAux.addView(this.layoutScreen);
    }

    /**
     * Método para intercambiar las referencias a los layout, (principal y siguiente), al terminar
     * la animación de hacia la derecha o izquierda.
     */
    private void switchContainers() {
        LinearLayout auxLayout = this.mainConteiner;
        this.mainConteiner = this.conteinerAux;
        this.conteinerAux = auxLayout;
    }

    /**
     * Sobre carga del método "OnBackPressed".
     */
    @Override
    public void onBackPressed() {
        if( this.statusScreen==this.ON_INDICATORS )
            super.onBackPressed();
        else if( this.statusScreen==this.ON_MAIN_MENU ){
            if( DevolucionesFullMenuPrincipal.vieneDelMainMenu ){
                DevolucionesFullConteiner.this.initIndicatorsScreen();;
            }else {
                AlertDialog alertDialog = new AlertDialog.Builder(DevolucionesFullConteiner.this)
                        .setTitle("Aviso")
                        .setMessage("¿Cancelar devolución?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DevolucionesFullConteiner.this.initIndicatorsScreen();
                                ;
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();
            }
        }
    }

    /**
     * Sobre carga del método "onResume".
     */
    @Override
    protected void onRestart() {
        if(this.statusScreen == this.ON_MAIN_MENU){
            overridePendingTransition(R.anim.devoluciones_full_left_slide_in, R.anim.devoluciones_full_left_slide_out);

            if(DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().getReasonReturnSelected() != null){
                this.develucionesFullMenuPrincipal.setReasonReturn();
            }else{
                this.develucionesFullMenuPrincipal.clearReasonReturn();
            }

            if(DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().getNumberInvoiceToReturn() != null){
                this.develucionesFullMenuPrincipal.setNumInvoice();
            }else{
                this.develucionesFullMenuPrincipal.clearNumInvoice();
            }

            if(DevolucionesFullMenuPrincipal.thiz.getDevolucionPendiente().getNumberProductToReturn() != null) {
                this.develucionesFullMenuPrincipal.setNumberProductToReturn();
            }else{
                this.develucionesFullMenuPrincipal.clearNumInvoice();
                this.develucionesFullMenuPrincipal.clearNumberProductToReturn();
            }
        }else if(this.statusScreen == this.ON_INDICATORS){
            overridePendingTransition(R.anim.devoluciones_full_right_slide_in, R.anim.devoluciones_full_right_slide_out);
        }
        super.onRestart();
    }
}


