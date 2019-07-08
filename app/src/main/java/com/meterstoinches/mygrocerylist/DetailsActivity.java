package com.meterstoinches.mygrocerylist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {
    private TextView itemName;
    private TextView quantity;
    private TextView dateAdded;
    private int groceryID;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        itemName=(TextView) findViewById(R.id.itemNameDet);
        quantity=(TextView) findViewById(R.id.quantityDet);
        dateAdded=(TextView) findViewById(R.id.dateaddedDet);

        extras= getIntent().getExtras();
        if (extras!=null){
            itemName.setText(extras.getString("name"));
            quantity.setText(extras.getString("quantity"));
            dateAdded.setText(extras.getString("date"));
            groceryID = extras.getInt("id");
        }
    }
}
