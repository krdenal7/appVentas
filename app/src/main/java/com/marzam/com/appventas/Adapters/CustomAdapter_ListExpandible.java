package com.marzam.com.appventas.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * Created by SAMSUMG on 26/12/2014.
 */
public class CustomAdapter_ListExpandible extends BaseExpandableListAdapter {

    private String[] groups;//={"Grupo1","Grupo2","Grupo3"};
    private String[][] children;//={{"sub1","sub2"},{"sub2"},{"sub3"},{"sub4"},{"sub5"}};
    Context context;


    public CustomAdapter_ListExpandible(Context context,String [] groups,String[][]children){
        this.context=context;
        this.groups=groups;
        this.children=children;
        String a="";
    }
    public TextView getGenericView(){
        AbsListView.LayoutParams lp=new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,64);
        TextView textView=new TextView(context);

        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
        textView.setPadding(36,0,0,0);

        return textView;
    }


    @Override
    public int getGroupCount() {

        return groups.length;
    }

    @Override
    public int getChildrenCount(int i) {
        int i2=0;
        try{

            i2=children[i].length;

        }catch (Exception e){
            String err=e.toString();
        }
        return i2;
    }

    @Override
    public Object getGroup(int i) {
        return groups[i];
    }

    @Override
    public Object getChild(int i, int i2) {
        return children[i][i2];
    }

    @Override
    public long getGroupId(int i) {

        return i;
    }

    @Override
    public long getChildId(int i, int i2) {
        return i2;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

       TextView textView=getGenericView();
       textView.setTypeface(null, Typeface.BOLD);
       textView.setText(getGroup(i).toString());
        return textView;
    }

    @Override
    public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {

        TextView textView=getGenericView();
        textView.setText(getChild(i,i2).toString());


        return textView;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }
}
