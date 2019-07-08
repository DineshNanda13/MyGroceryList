package com.meterstoinches.mygrocerylist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Grocery> groceryList;
    private List<Grocery> listItems;
    private DatabaseHandler db;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    EditText groceryItem1;
    EditText quantity1;
    Button savebtn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                createAnotherPopupDialog();
            }
        });

        db = new DatabaseHandler(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewID);
        // in conList.xml populating with list_row
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groceryList = new ArrayList<>();
        listItems = new ArrayList<>();

        //Get items from database
        groceryList = db.getAllGroceries();

        for (Grocery c: groceryList){
            Grocery grocery = new Grocery();
            grocery.setName(c.getName());
            grocery.setQuantity("Qty: "+c.getQuantity());
            grocery.setId(c.getId());
            grocery.setDateItemAdded("Added on: "+c.getDateItemAdded());

            listItems.add(grocery);
        }
        recyclerViewAdapter = new RecyclerViewAdapter(this, listItems);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();//notify change in adapter

    }
    private void createAnotherPopupDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup,null);

        groceryItem1 = (EditText) view.findViewById(R.id.groceryItem);
        quantity1 = (EditText) view.findViewById(R.id.groceryQty);
        savebtn1 = (Button) view.findViewById(R.id.savebtn);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        savebtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!groceryItem1.getText().toString().isEmpty()
                        && !quantity1.getText().toString().isEmpty()){

                    saveGroceryToDB1(v);
                }else {
                    Snackbar.make(v, "Add Grocery and Quantity",Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
    private void saveGroceryToDB1(View v){
        final Grocery grocery = new Grocery();
        String newGrocery = groceryItem1.getText().toString();
        String newGroceryQuantity = quantity1.getText().toString();

        grocery.setName(newGrocery);
        grocery.setQuantity(newGroceryQuantity);

        //Save to DB
        db.addGrocery(grocery);
        Snackbar.make(v, "Item Saved!", Snackbar.LENGTH_LONG).show(); //similar to toast

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                db.updateGrocery(grocery);
                //start a new activity
                startActivity(new Intent(getApplicationContext(),ListActivity.class));
            }
        },1200); //1.2 sec
    }
}
