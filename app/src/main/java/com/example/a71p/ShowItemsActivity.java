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

                                //Log.v("每一个item输出：", itemModel.toString());

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
        //Log.v("修改item的name成功",name);
    }
}
//    @Override
//    public void onBindViewHolder(@NonNull OrderAdapter.MyViewHolder holder, int position) {
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference().child(orderList.get(position).getOrder_iamge_name());
//        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Log.v("get image from firebase",uri.toString());
//                Picasso.get().load(uri).into(holder.order_image);
//
//            }
//        });
//
//        Log.v("position",String.valueOf(position));
//        //holder.order_image.setImageURI(orderList.get(position).getOrder_iamge_Uri());
//        holder.order_title.setText(orderList.get(position).getGood_type());
//        holder.order_desc.setText(orderList.get(position).toString());
//
//        //share button functionality
//        String share_txt = orderList.get(position).toString();
//        holder.share_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                shareIntent.setType("text/plain");
//                shareIntent.putExtra(Intent.EXTRA_TEXT,share_txt);
//                context.startActivity(Intent.createChooser(shareIntent,"share with"));
//            }
//        });
//
//        //click card, bring user to order detail fragment
//        OrderModel thisOrder = orderList.get(position);
//        holder.order_item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                OrderDetailFragment fragment = new OrderDetailFragment(thisOrder);
//                FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.current_fragment, fragment)
//                        .addToBackStack(null)
//                        .commit();
//            }
//        });
//    }
//my orders data:
//        SharedPreferences sharedPreferences = getSharedPreferences("my_pref",MODE_PRIVATE);
//        String login_name = sharedPreferences.getString("login_name","");
//        Log.v("login name",login_name);
//        my_order_list = new ArrayList<OrderModel>();
//        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot orderSnapshot : snapshot.getChildren()){
//                    OrderModel orderModel = new OrderModel(orderSnapshot.child("receiver_name").getValue().toString(),
//                            orderSnapshot.child("sender_name").getValue().toString(),
//                            orderSnapshot.child("pick_up_date").getValue().toString(),
//                            orderSnapshot.child("pick_up_time").getValue().toString(),
//                            orderSnapshot.child("drop_off_location").getValue().toString(),
//                            orderSnapshot.child("good_type").getValue().toString(),
//                            orderSnapshot.child("weight").getValue().toString(),
//                            orderSnapshot.child("width").getValue().toString(),
//                            orderSnapshot.child("length").getValue().toString(),
//                            orderSnapshot.child("height").getValue().toString(),
//                            orderSnapshot.child("vehicle_type").getValue().toString(),
//                            orderSnapshot.child("order_iamge_name").getValue().toString(),
//                            Boolean.getBoolean(orderSnapshot.child("finished").getValue().toString())
//                    );
//                    if(orderModel.getSender_name().equals(login_name)){
//                        Log.v("my_order",orderModel.toString());
//                        my_order_list.add(orderModel);
//                    }
//            }
//
//            }