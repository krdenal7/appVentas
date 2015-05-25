package com.marzam.com.appventas.DevolucionesLite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.marzam.com.appventas.DataBase.DataBase;
import com.marzam.com.appventas.DevolucionesLite.CustomPrompt.CustomPrompt;
import com.marzam.com.appventas.DevolucionesLite.PrepareSendingData.PrepareSendingData;
import com.marzam.com.appventas.DevolucionesLite.ProductList.Product;
import com.marzam.com.appventas.DevolucionesLite.ProductList.ProductAdapter;
import com.marzam.com.appventas.DevolucionesLite.ProductList.ProductSumaryAdapter;
import com.marzam.com.appventas.DevolucionesLite.ProductList.SwipeListViewTouchListener;
import com.marzam.com.appventas.R;

public class DevolucionesLite extends Activity {

    //DataBase
    private DataBase db;

    //Configuration
    private Spinner spinnerTypeDocument;
    TextView tvAmountPackages;
    TextView tvFolioDocument;
    TextView tvNumberReturn;
    private Button btnAddPackage;
    private Switch swDevLiteConfPackageOrProduct;
    private TextView tvInstructionAddPakageOrProduct;
    private final String CLIENTE_CAPTURA_POR_BULTO_O_DETALLE = "3";
    private String perfilClienteActivo;
    //Capture
    private Spinner spinnerReasonRefund;
    private ListView productList;
    private ProductAdapter adapterList;
    private Product productData[];
    private EditText searchOption;
    private TabHost tabHost;
    private String idDevolucionProductOnSamePakage = null;

    //Captire: ProductList list
    private static Product[] allProducto = null;

    //Sumary
    private static DevolucionesLite thiz;
    private ListView productSumaryList;
    private ProductSumaryAdapter adapterListSumary;
    private Product productSumaryData[];

    //End
    private Button btnFinishReturnProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        DevolucionesLite.thiz = this;
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView( R.layout.devoluciones_lite );
        setTitle( "Devoluciones Lite" );

        this.db = new DataBase( this );
        initConfiguration();
        initCaptureByProduct();
        initSumaryProduct();
        initButtonFinishReturnProduct();
    }

    /**
     * Inicializa la vista de configuración
     */
    private void initConfiguration() {
        initTab();
        initTypeDocument();
        initSwitchPakageOrProduct();
        initButtonAddPackages();
    }

    /**
     * Inicializa la configuracion los tabuladores "Configuración, Captura por producto e historial".
     */
    private void initTab(){
        this.tabHost = (TabHost)findViewById( R.id.tabHost );
        tabHost.setSoundEffectsEnabled(true);
        tabHost.setup();
        TabHost.TabSpec tab1 = tabHost.newTabSpec( "tabConfiguracion" );
        tab1.setIndicator( "Bultos" );
        tab1.setContent(R.id.Configuracion);
        tabHost.addTab( tab1 );

        TabHost.TabSpec tab2 = tabHost.newTabSpec( "tabCaptura" );
        tab2.setIndicator("Productos");
        tab2.setContent(R.id.Captura);
        tabHost.addTab( tab2 );

        TabHost.TabSpec tab3 = tabHost.newTabSpec( "tabResumen" );
        tab3.setIndicator( "Resumen" );
        tab3.setContent( R.id.Resumen );
        tabHost.addTab( tab3 );

        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
        {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor( Color.parseColor("#FFFFFF") );
        }

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                DevolucionesLite.this.refreshListSumary();
            }
        });
    }

    /**
     * Inicializa el Spinner para el tipo de documento
     */
    private void initTypeDocument() {
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_spinner_dropdown_item, getTypeDocument() );
        this.spinnerTypeDocument = ( Spinner ) findViewById( R.id.idSetDevLiteConfDocumentType );
        this.spinnerTypeDocument.setAdapter( listAdapter );

        this.tvAmountPackages = (TextView)findViewById( R.id.idSetDevLiteConfAmountPackages );
        this.tvFolioDocument = (TextView)findViewById( R.id.idSetDevLiteConfFolioDocument );
        this.tvNumberReturn = (TextView)findViewById( R.id.idSetDevLiteConfNumberRepayment );
    }

    /**
     * Inicializa el botón encargado de agregar bultos a la lista de resumen
     */
    private void initButtonAddPackages() {

        this.btnAddPackage = (Button) findViewById(R.id.idBtnDevLiteConfAddPackage);

        //Cunsultar perfil del cliente
        String idClienteActivo = this.db.execSelect(this.db.QUERY_CLIENT);
        this.perfilClienteActivo = this.db.execSelect(this.db.QUERY_PROFILE_CLIENT, idClienteActivo);

        //CADENAS
        if (perfilClienteActivo.compareTo(CLIENTE_CAPTURA_POR_BULTO_O_DETALLE) == 0) {
            this.tabHost.getTabWidget().getChildAt(1).setEnabled(false);
            this.tabHost.getTabWidget().getChildAt(1).setAlpha(0.3f);
            this.btnAddPackage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String amountPackages = DevolucionesLite.this.tvAmountPackages.getText().toString().trim();
                    String typeDocument = DevolucionesLite.this.spinnerTypeDocument.getSelectedItem().toString();
                    String folioDocument = DevolucionesLite.this.tvFolioDocument.getText().toString().trim();
                    String numberReturn = DevolucionesLite.this.tvNumberReturn.getText().toString().trim();

                    if ((amountPackages.length() > 0) && (tvFolioDocument.length() > 0) && (tvNumberReturn.length() > 0)) {
                        String idDevolucion = getIdDevolucion();
                        Product product = new Product(true, amountPackages, typeDocument, folioDocument, numberReturn, idDevolucion);
                        DevolucionesLite.this.addProductSumary(product);
                        Toast.makeText(DevolucionesLite.this, "Bulto agregado", Toast.LENGTH_SHORT).show();

                        DevolucionesLite.this.clearForm();
                    } else showAlert("AVISO", "Debes de completar el formulario");
                }
            });
        }
        else{
            this.btnAddPackage.setVisibility( View.INVISIBLE );
            this.swDevLiteConfPackageOrProduct.setVisibility( View.INVISIBLE );
            this.tvInstructionAddPakageOrProduct.setVisibility( View.INVISIBLE );
        }
    }

    /**
     * Inicializa el switch encargado de habilitar o deshabilitar el modo de devolución: "por paquete" o "por producto"
     */
    private void initSwitchPakageOrProduct(){

        this.tvInstructionAddPakageOrProduct = (TextView) findViewById(R.id.idDevLiteConfInstructionAddPakageOrProduct);
        this.swDevLiteConfPackageOrProduct = (Switch) findViewById(R.id.idSwDevLiteConfPackageOrProduct);
        this.swDevLiteConfPackageOrProduct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // isChecked = false => boton paquete habilitado
                // isChecked = true => boton paquete deshabilitado
                if(isChecked){
                    DevolucionesLite.this.btnAddPackage.setVisibility( View.INVISIBLE );
                    DevolucionesLite.this.tabHost.getTabWidget().getChildAt(1).setEnabled(true);
                    DevolucionesLite.this.tabHost.getTabWidget().getChildAt(1).setAlpha(1);
                }
                else{
                    DevolucionesLite.this.btnAddPackage.setVisibility( View.VISIBLE );
                    DevolucionesLite.this.tabHost.getTabWidget().getChildAt(1).setEnabled(false);
                    DevolucionesLite.this.tabHost.getTabWidget().getChildAt(1).setAlpha(0.3f);
                }

                clearForm();
                clearSumaryListAndFormPakage();
            }

        });
    }

    /**
     * Inicializa el botón encargado de enviar las devoluciones que se encuentran en la lista de resumen
     */
    private void initButtonFinishReturnProduct(){
        this.btnFinishReturnProduct = (Button)findViewById( R.id.idBtnFinishReturnProduct );
        this.btnFinishReturnProduct.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( DevolucionesLite.thiz.productSumaryData!=null ) {
                    PrepareSendingData prepareSendingData = new PrepareSendingData(DevolucionesLite.this);
                }
            }
        });
    }

    /**
     * Obtiene el tipo de documento mediante una consulta en la base de datos local
     * @return Una lista con los tipos de documentos
     */
    private List<String> getTypeDocument(){
        List<String> list = db.execSelectList( DataBase.QUERY_TYPE_DOCUMENT );

        return list;
    }

    /**
     * Inicializa la vista de captura por producto
     */
    private void initCaptureByProduct() {
        initReasonReturn();
        initProductList();
        initBarSearch();
    }

    /**
     * Inicializa la vista del resumen
     */
    private void initSumaryProduct(){
        initProductSumaryList();
    }

    /**
     * Inicializa el Spinner para los motivos de la devolución
     */
    private void initReasonReturn() {
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getReasonRefund());
        this.spinnerReasonRefund = (Spinner) findViewById(R.id.idSetDevLiteCaptReasonRefund);
        this.spinnerReasonRefund.setAdapter(listAdapter);
    }

    /**
     *Obtiene los motivos de la devolución mediante una consulta en la base de datos local
     * @return Una lista con los motivo de devolución
     */
    private List<String> getReasonRefund() {
        List<String> list = db.execSelectList( DataBase.QUERY_REASON_RETURN );
        return list;
    }

    /**
     * Inicializa la lista de productos.
     */
    private void initProductList() {

        this.productList = (ListView) findViewById( R.id.idSetDevLiteCaptProductList );
        this.productData = getAllProduct();

        this.adapterList = new ProductAdapter(this, R.layout.devoluciones_lite_product_row, this.productData);
        this.productList.setAdapter(adapterList);

        this.productList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String amountPackages = DevolucionesLite.this.tvAmountPackages.getText().toString().trim();
                String typeDocument = DevolucionesLite.this.spinnerTypeDocument.getSelectedItem().toString();
                String folioDocument = DevolucionesLite.this.tvFolioDocument.getText().toString().trim();
                String numberReturn = DevolucionesLite.this.tvNumberReturn.getText().toString().trim();
                String reasonReturn = DevolucionesLite.this.spinnerReasonRefund.getSelectedItem().toString();

                if( (amountPackages.length()>0)&&(tvFolioDocument.length()>0)&&(tvNumberReturn.length()>0) ) {
                    Product p = ((Product)DevolucionesLite.this.productData[position]);
                    int productWillChange = changeNumberReturnProduct(p);
                    //Producto ya en la lista de resumen
                    if( productWillChange!=-1 ){
                        p = ((Product)DevolucionesLite.this.productSumaryData[productWillChange]);
                        CustomPrompt customPrompt = new CustomPrompt( DevolucionesLite.this );
                        customPrompt.show();
                        customPrompt.setTitle("Cambiar cantidad");
                        customPrompt.setProductChangeAmount(p, productWillChange);
                        return;
                    }

                    //Nuevo producto
                    if( DevolucionesLite.this.idDevolucionProductOnSamePakage==null )
                        DevolucionesLite.this.idDevolucionProductOnSamePakage = getIdDevolucion();
                    Product productAdd = new Product( p.name, p.marzamCode, p.marzamCode, amountPackages, typeDocument, folioDocument, numberReturn,  reasonReturn, DevolucionesLite.this.idDevolucionProductOnSamePakage);

                    CustomPrompt customPrompt = new CustomPrompt( DevolucionesLite.this );
                    customPrompt.show();
                    customPrompt.setTitle("Introduce una cantidad");
                    customPrompt.setProduct(productAdd, view);

                    //DevolucionesLite.this.clearForm();
                }else DevolucionesLite.this.showAlert("Aviso", "Debes de completar el formulari del apartado de \"BULTOS\"");
            }
        });
    }

    /**
     * Inicializa la barra de búsqueda, para filtrar los productos
     */
    private void initBarSearch(){
        this.searchOption = (EditText) findViewById( R.id.idSetDevLiteCaptProductSearch );
        this.searchOption.setImeActionLabel("Buscar", EditorInfo.IME_ACTION_SEARCH);
        this.searchOption.setOnKeyListener( new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE ||
                   event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
                    DevolucionesLite.this.executeSearchBy();
                    return true;
                }
                return false;
            }
        });

        ImageButton cancelSearch = (ImageButton) findViewById(R.id.idSetDevLiteCaptCancelSearch);
        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevolucionesLite.this.searchOption.setText("");
                DevolucionesLite.this.executeSearchBy();
            }
        });
        ImageButton iconSearch = (ImageButton) findViewById(R.id.idSetDevLiteCaptIconSearch);
        iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeSearchBy();
            }
        });
    }

    /**
     * Método encargado de efectuar todos los procedimientos para filtrar los productos mediante algún argumento
     */
    private void executeSearchBy(){

        String searchCriterion = DevolucionesLite.this.searchOption.getText().toString().trim();

        if( searchCriterion.length()==0 ){

            DevolucionesLite.this.productData = DevolucionesLite.this.getAllProduct();
            DevolucionesLite.this.adapterList = new ProductAdapter(DevolucionesLite.this, R.layout.devoluciones_lite_product_row, DevolucionesLite.this.productData);
            DevolucionesLite.this.productList.setAdapter( adapterList );

            Toast.makeText(getBaseContext(), "Todos los productos", Toast.LENGTH_SHORT).show();
            //keyboard hide
            hidenkeyboard();
            return;
        }

        DevolucionesLite.this.productData = DevolucionesLite.this.getOnlyProductBy(searchCriterion);

        DevolucionesLite.this.adapterList = new ProductAdapter(DevolucionesLite.this, R.layout.devoluciones_lite_product_row, DevolucionesLite.this.productData);
        DevolucionesLite.this.productList.setAdapter(adapterList);
        Toast.makeText(getBaseContext(), "Filtrados por : \""+searchCriterion+"\"", Toast.LENGTH_SHORT).show();
        hidenkeyboard();
    }

    /**
     * Obtiene todos los productos encontrados en la base de datos local
     * @return Una lista con todos los productos encontrados
     */
    private Product[] getAllProduct(){
        if( DevolucionesLite.allProducto != null ){
            return DevolucionesLite.allProducto;
        }

        List<String> listName = this.db.execSelectList( DataBase.QUERY_NAME_PRODUCT );
        List<String> marzamCode = this.db.execSelectList( DataBase.QUERY_MARZAM_CODE_PRODUCT );
        List<String> barCode = this.db.execSelectList( DataBase.QUERY_BAR_CODE_PRODUCT );

        DevolucionesLite.allProducto = new Product[listName.size()];
        for(int i=0; i<DevolucionesLite.allProducto.length; i++){
            DevolucionesLite.allProducto[i] = new Product( R.drawable.returm_product, listName.get(i), marzamCode.get(i), barCode.get(i) );
        }

        return DevolucionesLite.allProducto;
    }

    /**
     * Realiza un filtro de los productos que cumplan el argumento de filtrado
     * Nombre corto, codigo marzam y codigo de barras
     * @param optionSearch Argumento para definir el filtro de los productos
     *
     * @return Una lista que cumple con el argumento de filtro
     */
    private Product[] getOnlyProductBy( String optionSearch ){
        List<String> listName = this.db.execSelectList( DataBase.QUERY_NAME_PRODUCT_SEARCH, "%"+optionSearch+"%", "%"+optionSearch+"%", "%"+optionSearch+"%" );
        List<String> marzamCode = this.db.execSelectList( DataBase.QUERY_MARZAM_CODE_PRODUCT_SEARCH, "%"+optionSearch+"%", "%"+optionSearch+"%", "%"+optionSearch+"%" );
        List<String> barCode = this.db.execSelectList( DataBase.QUERY_BAR_CODE_PRODUCT_SEARCH, "%"+optionSearch+"%", "%"+optionSearch+"%", "%"+optionSearch+"%" );

        Product producto[] = new Product[listName.size()];
        for(int i=0; i<producto.length; i++){
            producto[i] = new Product( R.drawable.returm_product, listName.get(i), marzamCode.get(i), barCode.get(i) );
        }
        return producto;
    }

    /**
     * Inicializa la lista de resumen
     */
    private void initProductSumaryList() {

        this.productSumaryList = (ListView) findViewById( R.id.idSetDevLiteCaptProductSumaryList );

        this.productSumaryList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = ((Product)DevolucionesLite.this.productSumaryData[position]);

                CustomPrompt customPrompt = new CustomPrompt( DevolucionesLite.this );
                customPrompt.show();
                customPrompt.setTitle("Cambiar cantidad");
                customPrompt.setProductChangeAmount(product, position);
            }
        });

        SwipeListViewTouchListener touchListener =new SwipeListViewTouchListener(this.productSumaryList,new SwipeListViewTouchListener.OnSwipeCallback() {
            @Override
            public void onSwipeLeft(final ListView listView, int [] reverseSortedPositions) {

                //Aqui ponemos lo que hara el programa cuando deslizamos un item ha la izquierda
                DevolucionesLite.this.adapterListSumary.remove(reverseSortedPositions[0]);
                DevolucionesLite.this.refreshDataSumary(reverseSortedPositions[0]);
                DevolucionesLite.this.adapterListSumary.notifyDataSetChanged();
            }
            @Override
            public void onSwipeRight(ListView listView, int [] reverseSortedPositions) {

            //Aqui ponemos lo que hara el programa cuando deslizamos un item ha la derecha
                DevolucionesLite.this.adapterListSumary.remove(reverseSortedPositions[0]);
                DevolucionesLite.this.refreshDataSumary(reverseSortedPositions[0]);
                DevolucionesLite.this.adapterListSumary.notifyDataSetChanged();

            }
        },true, false);

        //Escuchadores del listView
        DevolucionesLite.this.productSumaryList.setOnTouchListener( touchListener );
        DevolucionesLite.this.productSumaryList.setOnScrollListener( touchListener.makeScrollListener() );
    }

    /**+
     * Agrega un producto a la lista de resumen
     * @param product Producto a agregar a la lista
     */
    public static void addProductSumary( Product product ){
        int length = 0;
        int indexPakages = 0;
        int indexProduct = 0;
        if( DevolucionesLite.thiz.productSumaryData!=null ){
            length = DevolucionesLite.thiz.productSumaryData.length;
        }
        ArrayList<Product> productSumaryData = new ArrayList();
        for( int i=0; i<length; i++ ){
            Product p = DevolucionesLite.thiz.productSumaryData[i];
            if( p.isPackage )
                indexPakages = i;
            else if( !p.isSection )
                indexProduct = i;
            productSumaryData.add( p );
        }

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH'|'mm'|'ss");
        String date = df.format(Calendar.getInstance().getTime());

        product.setDate( date );
        if( product.isPackage ){
            if( indexPakages==0 ) {
                productSumaryData.add(new Product("Paquetes", true));
                indexPakages = productSumaryData.size();
            }else indexPakages++;
            productSumaryData.add( indexPakages, product );
        }else{
            if( indexProduct==0 ) {
                productSumaryData.add(new Product("Productos", true));
                indexProduct = productSumaryData.size();
            }else indexProduct++;
            productSumaryData.add( indexProduct, product );
        }

        DevolucionesLite.thiz.productSumaryData = new Product[ productSumaryData.size() ];
        for( int i=0; i<DevolucionesLite.thiz.productSumaryData.length; i++ ){
            DevolucionesLite.thiz.productSumaryData[i] = (Product)productSumaryData.get(i);
        }
        DevolucionesLite.thiz.adapterListSumary = new ProductSumaryAdapter(DevolucionesLite.thiz, R.layout.devoluciones_lite_product_sumary_row, R.layout.seccion_item, productSumaryData);
        DevolucionesLite.thiz.productSumaryList.setAdapter( DevolucionesLite.thiz.adapterListSumary );
    }

    /**
     * Método llamado desde la clase "CustomPrompt" para cambiar la cantidad de producto a devolver
     * @param isPakage Bandera indicando si el producto en realidad se trata de un paquete
     * @param amountProductReturn Cantidad nueva del producto a devolver
     * @param position Posición del producto en la lista de resumen
     */
    public static void changeProductSumary( boolean isPakage, String amountProductReturn, int position){
        if( isPakage ) {
            DevolucionesLite.thiz.productSumaryData[position].setAmountPackages(amountProductReturn);
        }
        else {
            DevolucionesLite.thiz.productSumaryData[position].setAmountProductReturn(amountProductReturn);
        }
        int firstVisiblePosition = DevolucionesLite.thiz.productSumaryList.getFirstVisiblePosition();
        View v = DevolucionesLite.thiz.productSumaryList.getChildAt( position - firstVisiblePosition );

        if( v == null )
            return;

        TextView txtAmountProductReturn = (TextView) v.findViewById(R.id.idSetAmountProductReturn);
        txtAmountProductReturn.setText( amountProductReturn );
    }

    /**
     * Encargado de ocultar el teclado lógico
     */
    private void hidenkeyboard() {
        InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Método encargado de mostrar una alerta
     * @param tittle Titulo de la alerta
     * @param message Mensaje de la alerta
     */
    private void showAlert(String tittle, String message) {
        new AlertDialog.Builder(DevolucionesLite.this)
                .setTitle(tittle)
                .setMessage(message)
                .setPositiveButton("Ok", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Método encargado de limpiar el formulario de "Bultos"
     */
    private void clearForm(){
        this.tvAmountPackages.setText("");
        this.tvFolioDocument.setText("");
        this.tvNumberReturn.setText("");
    }

    /**
     * Devuelve la lista de resumen
     * @return La lista de resumen
     */
    public Product[] getProductSumaryData(){ return this.productSumaryData; }

    String idAgent = null;
    private String getIdDevolucion(){
        if( this.idAgent == null ) {
            this.idAgent = this.db.execSelect(DataBase.QUERY_ID_AGENT);
            while(this.idAgent.length()<4){
                this.idAgent = "0"+this.idAgent;
            }
        }

        String date = getDate();
        String folio;

        folio = "D"+this.idAgent+date;
        return folio;
    }

    /**
     * Método encargado de calcular la fecha mas la hora , minutos y segundos
     * @return Un String de la fecha actual, mas la hora, minuto y segundo
     */
    private String getDate(){
        Calendar cal = new GregorianCalendar();
        Date dt = cal.getTime();
        int dia=cal.get(Calendar.DAY_OF_YEAR);
        SimpleDateFormat df=new SimpleDateFormat("yy");
        String formatteDate=df.format(dt.getTime());
        SimpleDateFormat df1=new SimpleDateFormat("ddd");
        int tam=3-String.valueOf(dia).length();
        String formatteDate1="";
        for(int i=0;i<tam;i++){ formatteDate1+="0"; }
        formatteDate1+=String.valueOf(dia);
        SimpleDateFormat df2=new SimpleDateFormat("HHmmss");
        String formatteDate2=df2.format(dt.getTime());
        return formatteDate+formatteDate1+formatteDate2;
    }

    /**
     * Método encargado de refrescar todos los productos que se encuentran en la lista del resumen,
     * este método se invoca cada vez que se cambia de tabview
     */
    public void refreshListSumary(){
        if( DevolucionesLite.thiz.productSumaryData==null )
            return;
        if( (perfilClienteActivo.compareTo(CLIENTE_CAPTURA_POR_BULTO_O_DETALLE) == 0)&&(!this.swDevLiteConfPackageOrProduct.isChecked()) )
            return;


        int length = DevolucionesLite.thiz.productSumaryData.length;
        String amountPackages = this.tvAmountPackages.getText().toString();
        String typeDocument = this.spinnerTypeDocument.getSelectedItem().toString();
        String folioDocument = this.tvFolioDocument.getText().toString();
        String numberReturn = this.tvNumberReturn.getText().toString();
        String reazonReturn = this.spinnerReasonRefund.getSelectedItem().toString();
        for( int i=0; i<length; i++){
            if( !DevolucionesLite.thiz.productSumaryData[i].isSection ) {
                if (amountPackages.length() != 0)
                    DevolucionesLite.thiz.productSumaryData[i].amountPackages = amountPackages;
                if (typeDocument.length() != 0)
                    DevolucionesLite.thiz.productSumaryData[i].typeDocument = typeDocument;
                if (folioDocument.length() != 0)
                    DevolucionesLite.thiz.productSumaryData[i].folioDocument = folioDocument;
                if (numberReturn.length() != 0)
                    DevolucionesLite.thiz.productSumaryData[i].numberReturn = numberReturn;
                if (reazonReturn.length() != 0)
                    DevolucionesLite.thiz.productSumaryData[i].reasonReturn = reazonReturn;
            }

        }

        ArrayList<Product> productSumaryData = new ArrayList();
        for( int i=0; i<DevolucionesLite.thiz.productSumaryData.length; i++ ){
            productSumaryData.add( DevolucionesLite.thiz.productSumaryData[i] );
        }

        DevolucionesLite.thiz.adapterListSumary = new ProductSumaryAdapter(DevolucionesLite.thiz, R.layout.devoluciones_lite_product_sumary_row, R.layout.seccion_item, productSumaryData);
        DevolucionesLite.thiz.productSumaryList.setAdapter( DevolucionesLite.thiz.adapterListSumary );
    }

    /**
     * Refresca los datos que se encuentran en el arreglo lógico de todos los datos que se encuentran en la lista de resumen
     * @param indexDataErase Posicion del producto borrado de la lista de resumen
     */
    void refreshDataSumary( int indexDataErase ){
        int length = this.productSumaryData.length - 1;
        if( length==1 ){//Solo queda la categoria
            clearSumaryList();
            return;
        }

        Product[] productSumaryDataAux = new Product[ length ];

        int j=0;
        for( int i=0; i<this.productSumaryData.length; i++  ){
            if( i!=indexDataErase ) {
                productSumaryDataAux[j] = this.productSumaryData[i];
                j++;
            }
        }
        this.productSumaryData = productSumaryDataAux;
    }

    /**
     * Limpia la lista de resumen
     */
    public static void clearSumaryList(){
        DevolucionesLite.thiz.productSumaryData = null;
        DevolucionesLite.thiz.productSumaryList.setAdapter( null );
        DevolucionesLite.thiz.idDevolucionProductOnSamePakage=null;
    }

    /**
     * Limpia la lista de resumen, además del formulario de "Bultos"
     */
    public static void clearSumaryListAndFormPakage(){
        DevolucionesLite.thiz.clearForm();

        DevolucionesLite.thiz.productSumaryData = null;
        DevolucionesLite.thiz.productSumaryList.setAdapter( null );
        DevolucionesLite.thiz.idDevolucionProductOnSamePakage=null;
    }

    /**
     * Metodo para detectar si un producto ya se encuentra en la lista de resumen
     * @param product Producto a examinar si ya se encuentra en la lista de resumen
     * @return Posición del producto en la lista de resumen, -1 Si el producto no se encuentra en la lista de resumen
     */
    private int changeNumberReturnProduct(Product product){
        String marzamCodeToChange = product.marzamCode;
        int productWillChange = -1;

        if(this.productSumaryData==null)
            return productWillChange;

        for( int i=0; i<this.productSumaryData.length; i++ ){
            Product productOnSumary = ((Product)this.productSumaryData[i]);
            String marzamCodeOnSumary = productOnSumary.marzamCode;
            if( productOnSumary.isSection )
                continue;

            if( marzamCodeToChange.compareTo(marzamCodeOnSumary)==0 ){
                productWillChange = i;
                break;
            }
        }

        return productWillChange;
    }

    @Override
    public void onBackPressed() {
        String title = "Confirmación";
        String message = "Salir de devoluciones";
        new AlertDialog.Builder(DevolucionesLite.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DevolucionesLite.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
