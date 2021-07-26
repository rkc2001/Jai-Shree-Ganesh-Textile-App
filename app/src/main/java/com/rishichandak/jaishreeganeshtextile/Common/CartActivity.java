package com.rishichandak.jaishreeganeshtextile.Common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rishichandak.jaishreeganeshtextile.Database.Cart;
import com.rishichandak.jaishreeganeshtextile.HelperClasses.HomeAdapter.CartViewHolder;
import com.rishichandak.jaishreeganeshtextile.PrevalentUser.Prevalent;
import com.rishichandak.jaishreeganeshtextile.R;
import com.rishichandak.jaishreeganeshtextile.User.UserDashboard;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessButton;
    private TextView txtTotalAmount, txtMsg1;

    private int overAllPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextProcessButton = findViewById(R.id.next_process_btn);
        txtTotalAmount = findViewById(R.id.total_price);
        txtMsg1 = findViewById(R.id.msg1);

        nextProcessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(overAllPrice == 0){
                    txtTotalAmount.setText("Please fill some items !!");
                    Toast.makeText(CartActivity.this, "Cart cannot be empty :(", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                    intent.putExtra("Total Price", String.valueOf(overAllPrice));
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),UserDashboard.class);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkOrderState();

        // retrieve item using firebase recycler adapter
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                    .child(Prevalent.currentOnlineUser.getPhoneNo())
                        .child("Products"),Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {

                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductQuantity.setText("Quantity - " + model.getQuantity());
                        holder.txtProductPrice.setText("Price - ₹" + model.getPrice());


                            int individualProductTotalPrice = (Integer.valueOf(model.getPrice())) * (Integer.valueOf(model.getQuantity()));
                            overAllPrice += individualProductTotalPrice;

                            txtTotalAmount.setText("Total Price - ₹" + String.valueOf(overAllPrice));

                            // if user clicks on any item, remove it from cart
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CharSequence option[] = new CharSequence[]
                                            {
                                                    "Edit",
                                                    "Remove"
                                            };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                    builder.setTitle("Cart Options :");

                                    // set click listeners on the two options
                                    builder.setItems(option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (which == 0) {
                                                // send user to product details activity
                                                Intent intent = new Intent(CartActivity.this, ProductDetails.class);

                                                // show only that product which user clicked on
                                                intent.putExtra("pid", model.getPid());
                                                startActivity(intent);
                                                finish();
                                            } else if (which == 1) {
                                                cartListRef.child("User View")
                                                        .child(Prevalent.currentOnlineUser.getPhoneNo())
                                                        .child("Products")
                                                        .child(model.getPid())
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(CartActivity.this, "Item deleted successfully !!", Toast.LENGTH_SHORT).show();

                                                                    Intent intent = new Intent(CartActivity.this, UserDashboard.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });


                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };



            recyclerView.setAdapter(adapter);
            adapter.startListening();

    }

    private void checkOrderState()
    {
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhoneNo());

        ordersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            String shippingState = snapshot.child("state").getValue().toString();
                            String userName = snapshot.child("name").getValue().toString();

                            if(shippingState.equals("shipped"))
                            {
                                txtTotalAmount.setText("Mr. " + userName + "\nOrder shipped successfully !");

                                // disable recycler view as it is empty
                                txtMsg1.setText("Order shipped successfully\nIt will reach your doorstep soon !");
                                txtMsg1.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                nextProcessButton.setVisibility(View.GONE);
                            }
                            else if(shippingState.equals("not shipped"))
                            {
                                txtTotalAmount.setText("Shipping State = Not Shipped");

                                // disable recycler view as it is empty
                                recyclerView.setVisibility(View.GONE);
                                nextProcessButton.setVisibility(View.GONE);

                                txtMsg1.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
