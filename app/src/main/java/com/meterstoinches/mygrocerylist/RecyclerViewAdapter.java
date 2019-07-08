package com.meterstoinches.mygrocerylist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Grocery> groceryItems;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private LayoutInflater inflater;

    public RecyclerViewAdapter(Context context, List<Grocery> groceryItems) {
        this.context = context;
        this.groceryItems = groceryItems;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row,
                viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int position) {
        //Bind views with widgets or data with views
        Grocery grocery = groceryItems.get(position);

        viewHolder.groceryItemName.setText(grocery.getName());
        viewHolder.quantity.setText(grocery.getQuantity());
        viewHolder.dateAdded.setText(grocery.getDateItemAdded());
    }

    @Override
    public int getItemCount() {
        return groceryItems.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView groceryItemName;
        public TextView quantity;
        public TextView dateAdded;
        public Button editButton;
        public Button deleteButton;
        public int id;

        public ViewHolder(View itemView, Context ctx) {
            super(itemView);

            context = ctx;

            groceryItemName = (TextView) itemView.findViewById(R.id.name);
            quantity = (TextView) itemView.findViewById(R.id.quantity);
            dateAdded = (TextView) itemView.findViewById(R.id.dateAdded);

            editButton = (Button) itemView.findViewById(R.id.editButton);
            deleteButton = (Button) itemView.findViewById(R.id.deletebtn);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go to next screen //detailsActivity
                    int position = getAdapterPosition();
                    Grocery grocery = groceryItems.get(position);
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("name",grocery.getName());
                    intent.putExtra("quantity",grocery.getQuantity());
                    intent.putExtra("id",grocery.getId());
                    intent.putExtra("date",grocery.getDateItemAdded());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.editButton:
                    int position = getAdapterPosition();
                    Grocery grocery = groceryItems.get(position);
                    editItem(grocery);
                    break;
                case R.id.deletebtn:
                    position = getAdapterPosition();
                    grocery = groceryItems.get(position);
                    deleteItem(grocery.getId());
                    break;
            }

        }
        public void deleteItem(final int id){
            //create an AlertDialog
            alertDialogBuilder = new AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.confirmation_dialog, null);

            Button noButton = (Button) view.findViewById(R.id.nobtn);
            Button yesButton = (Button) view.findViewById(R.id.yesbtn);

            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();
            dialog.show();

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //delete the item
                    DatabaseHandler db = new DatabaseHandler(context);
                    //delete item
                    db.deleteGrocery(id);
                    groceryItems.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    dialog.dismiss();
                }
            });
        }
        public void editItem(final Grocery grocery){
            alertDialogBuilder = new AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.popup,null);

            final EditText groceryItem = (EditText) view.findViewById(R.id.groceryItem);
            final EditText quantity = (EditText) view.findViewById(R.id.groceryQty);
            final TextView title = (TextView) view.findViewById(R.id.title);
            title.setText("Edit Grocery");
            Button saveButton = (Button) view.findViewById(R.id.savebtn);

            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();
            dialog.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    //update item
                    grocery.setName(groceryItem.getText().toString());
                    grocery.setQuantity(quantity.getText().toString());

                    if(!groceryItem.getText().toString().isEmpty() &&
                            !quantity.getText().toString().isEmpty())
                    {
                        db.updateGrocery(grocery);
                        notifyItemChanged(getAdapterPosition(),grocery);

                    }else {
                        Snackbar.make(view, "Add Grocery and Quantity",Snackbar.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                }
            });
        }
    }
}

