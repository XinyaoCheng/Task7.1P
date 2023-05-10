package com.example.a71p;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ShowItemsActivity extends AppCompatActivity {

    FirebaseRecyclerAdapter itemAdapter;
    RecyclerView items_recycleView;
    DatabaseReference firebaseRef;
    ArrayList<ItemModel> item_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_items);
        items_recycleView = findViewById(R.id.items_recycleView);
        //firebaseRef = FirebaseDatabase.getInstance().getReference();
        Query query = FirebaseDatabase.getInstance().getReference().child("items");
        FirebaseRecyclerOptions<ItemModel> options=
                new FirebaseRecyclerOptions.Builder<ItemModel>()
                        .setQuery(query, new SnapshotParser<ItemModel>() {
                            @NonNull
                            @Override
                            public ItemModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                                ItemModel itemModel = new ItemModel(snapshot.child("item_id").getValue().toString(),
                                        snapshot.child("types").getValue().toString(),
                                        snapshot.child("name").getValue().toString(),
                                        snapshot.child("phone").getValue().toString(),
                                        snapshot.child("description").getValue().toString(),
                                        snapshot.child("date").getValue().toString(),
                                        snapshot.child("location").getValue().toString());

                                Log.v("每一个item输出：", itemModel.toString());

                                return itemModel;
                            }
                        })
                        .build();

        itemAdapter = new FirebaseRecyclerAdapter<ItemModel, myViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ItemModel model) {
                holder.setItemName(model.getTypes()+" "+model.getName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ShowItemsActivity.this, RemoveItems.class);
                        intent.putExtra("item_title",model.getTypes()+" "+model.getName());
                        intent.putExtra("item_date",model.getDate());
                        intent.putExtra("item_location",model.getLocation());
                        intent.putExtra("item_id",model.getItem_id());
                        Log.v("我倒要看看item id到底是多少",model.getItem_id());
                        startActivity(intent);

                    }
                });
            }


            @NonNull
            @Override
            public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycle_items, parent, false);

                return new myViewHolder(view);
            }
        };
        items_recycleView.setLayoutManager(new GridLayoutManager(this,1));
        items_recycleView.setAdapter(itemAdapter);
//        itemAdapter = new ItemAdapter(options);
//        items_recycleView.setAdapter(itemAdapter);



    }
    @Override
    protected void onStart() {
        super.onStart();
        itemAdapter.startListening();
        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference().child("items");
        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowItemsActivity.this, "Failed to reload data: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        itemAdapter.stopListening();
    }
}

class myViewHolder extends RecyclerView.ViewHolder{

    TextView item_name;
    public myViewHolder(@NonNull View itemView) {
        super(itemView);
        item_name = itemView.findViewById(R.id.items_name);

    }
    public void setItemName(String name){
        item_name.setText(name);
        Log.v("修改item的name成功",name);
    }
}
