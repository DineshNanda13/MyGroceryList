package com.meterstoinches.mygrocerylist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText groceryItem;
    private EditText quantity;
    private Button savebtn;

    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHandler(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                createPopupDialog();

            }
        });
        byPassActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void createPopupDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup,null);

        groceryItem = (EditText) view.findViewById(R.id.groceryItem);
        quantity = (EditText) view.findViewById(R.id.groceryQty);
        savebtn = (Button) view.findViewById(R.id.savebtn);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: save to db
                //Todo: go to next screen
                if(!groceryItem.getText().toString().isEmpty()
                        && !quantity.getText().toString().isEmpty()){

                    saveGroceryToDB(v);
                }else {
                    Snackbar.make(v, "Add Grocery and Quantity",Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
    private void saveGroceryToDB(View v){
        Grocery grocery = new Grocery();
        String newGrocery = groceryItem.getText().toString();
        String newGroceryQuantity = quantity.getText().toString();

        grocery.setName(newGrocery);
        grocery.setQuantity(newGroceryQuantity);

        //Save to DB
        db.addGrocery(grocery);
        Snackbar.make(v, "Item Saved!", Snackbar.LENGTH_LONG).show(); //similar to toast

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                //start a new activity
                startActivity(new Intent(getApplicationContext(),ListActivity.class));
            }
        },1200); //1.2 sec
    }
    public void byPassActivity(){
        //checks if DB is empty, if not, then we just go to ListActivity and show all added items
        if(db.getGroceriesCount()>0){
            startActivity(new Intent(getApplicationContext(),ListActivity.class));
            finish(); // get rid of previous activity if back is pressed
        }
    }
}
