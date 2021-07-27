package com.rishichandak.jaishreeganeshtextile.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rishichandak.jaishreeganeshtextile.Admin.AdminMaintainProductsActivity;
import com.rishichandak.jaishreeganeshtextile.BuildConfig;
import com.rishichandak.jaishreeganeshtextile.Common.CartActivity;
import com.rishichandak.jaishreeganeshtextile.Common.LoginSignup.RetailerStartUpScreen;
import com.rishichandak.jaishreeganeshtextile.Common.ProductDetails;
import com.rishichandak.jaishreeganeshtextile.Common.SearchProductsActivity;
import com.rishichandak.jaishreeganeshtextile.Database.Products;
import com.rishichandak.jaishreeganeshtextile.HelperClasses.HomeAdapter.CategoriesAdapter;
import com.rishichandak.jaishreeganeshtextile.HelperClasses.HomeAdapter.CategoriesHelperClass;
import com.rishichandak.jaishreeganeshtextile.HelperClasses.HomeAdapter.ProductViewHolder;
import com.rishichandak.jaishreeganeshtextile.PrevalentUser.Prevalent;
import com.rishichandak.jaishreeganeshtextile.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import pl.droidsonroids.gif.GifImageView;

public class UserDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView categoriesRecycler;
    CategoriesAdapter Adapter;
    private String Category;

    private GradientDrawable gradient1,gradient2,gradient3,gradient4;
    GifImageView openCart;

    //Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    LinearLayout contentView;
    ArrayList<CategoriesHelperClass> categoriesHelperClasses;

    View headerView;

    static final float END_SCALE = 0.7f;

    // Products DB reference
    private DatabaseReference ProductsRef;

    // Products Recycler View
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_dashboard);

        // Admin Category se aaya hua intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            // if we're coming from admin activity, only then it'll be not null
            type = getIntent().getStringExtra("Admin");
        }

        categoriesRecycler = findViewById(R.id.categories_recycler);

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        openCart = findViewById(R.id.open_cart);

        // Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        Paper.init(this);

        openCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send user to cart activity
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        categoryRecycler();

        if(type.equals("Admin")){
            menuIcon.setVisibility(View.INVISIBLE);
            openCart.setVisibility(View.INVISIBLE);
        }

        navigationDrawer();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent;

        switch (item.getItemId()){

            case R.id.nav_rate_us :
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Sorry, unable to open Play Store !", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.nav_share :
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Jai Shree Ganesh Textile");
                String shareMessage= "\nHey there, download this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Choose One"));
                break;

            case R.id.nav_search :
                intent = new Intent(getApplicationContext(), SearchProductsActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_cart:
                intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_settings :
                intent = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_logout :
                // destroy the user phone key and password key in paper
                Paper.book().destroy();

                intent = new Intent(getApplicationContext(), RetailerStartUpScreen.class);
                finishAffinity();
                startActivity(intent);
                finish();
                break;

        }

        return true;
    }

    private void navigationDrawer(){

        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        // default item selected (checked)
        navigationView.setCheckedItem(R.id.nav_home);

       headerView = navigationView.getHeaderView(0);

        // when user clicks on menu icon
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else{
                    drawerLayout.openDrawer(GravityCompat.START);
                    TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
                    CircleImageView profileImage = headerView.findViewById(R.id.user_profile_icon);

                    // no name/pic for admin in the drawer
                    if (Prevalent.currentOnlineUser != null) {
                        userNameTextView.setText("Hi, " + Prevalent.currentOnlineUser.getFullName());
                        if (!Prevalent.currentOnlineUser.getImage().equals("")) {
                                Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.user_profile_icon).into(profileImage);
                        }
                    }

                }
            }

        });

        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {

        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        //drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });

    }

    @Override
    public void onBackPressed() {

        if(type.equals("Admin")){
            super.onBackPressed();
            finish();
        }
        else {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                finishAffinity();
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // retrieve product using firebase recycler adapter
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef,Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("Price - ₹ " + model.getPrice());

                        Picasso.get().load(model.getImage()).into(holder.imageView);


                        /** Each product has a unique ID ... call details page when clicked
                        * Setting click listener on item view */
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (type.equals("Admin")) {
                                    // send admin to modify product activity
                                    Intent intent = new Intent(UserDashboard.this, AdminMaintainProductsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);

                                } else {

                                    Intent intent = new Intent(UserDashboard.this, ProductDetails.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void categoryRecycler() {

        gradient1 = new GradientDrawable();
        gradient1.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradient1.setColors(new int[]{0xffd4cbe5, 0xffd4cbe5});
        gradient1.setShape(GradientDrawable.OVAL);
        gradient1.setSize(30,30);

        gradient2 = new GradientDrawable();
        gradient2.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradient2.setColors(new int[]{0xff7adccf, 0xff7adccf});
        gradient2.setShape(GradientDrawable.OVAL);
        gradient2.setSize(30,30);

        gradient3 = new GradientDrawable();
        gradient3.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradient3.setColors(new int[]{0xfff7c59f, 0xFFf7c59f});
        gradient3.setShape(GradientDrawable.OVAL);
        gradient3.setSize(30,30);

        gradient4 = new GradientDrawable();
        gradient4.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradient4.setColors(new int[]{0xffb8d7f5, 0xffb8d7f5});
        gradient4.setShape(GradientDrawable.OVAL);
        gradient4.setSize(30,30);

        categoriesRecycler.setHasFixedSize(true);
        categoriesRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        categoriesHelperClasses = new ArrayList<>();

        categoriesHelperClasses.add(new CategoriesHelperClass(gradient1,R.drawable.pendant,"Pendant"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient2,R.drawable.lace,"Lace"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient3,R.drawable.ribbon,"Ribbon"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient4,R.drawable.cloth,"Cloth"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient1,R.drawable.woman_clothes,"Women Wear"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient2,R.drawable.beads,"Beads"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient3,R.drawable.button,"Button"));

        Adapter = new CategoriesAdapter(categoriesHelperClasses);

        Adapter.setOnItemClickListener(new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Category = categoriesHelperClasses.get(position).getTitle().toLowerCase();

                // retrieve product using firebase recycler adapter
                FirebaseRecyclerOptions<Products> options =
                        new FirebaseRecyclerOptions.Builder<Products>()
                                .setQuery(ProductsRef.orderByChild("category").equalTo(Category),Products.class)
                                .build();


                FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                        new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                                holder.txtProductName.setText(model.getPname());
                                holder.txtProductDescription.setText(model.getDescription());
                                holder.txtProductPrice.setText("Price - ₹ " + model.getPrice());

                                Picasso.get().load(model.getImage()).into(holder.imageView);


                                /** Each product has a unique ID ... call details page when clicked
                                 * Setting click listener on item view */
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (type.equals("Admin")) {
                                            // send admin to modify product activity
                                            Intent intent = new Intent(UserDashboard.this, AdminMaintainProductsActivity.class);
                                            intent.putExtra("pid", model.getPid());
                                            startActivity(intent);

                                        } else {

                                            Intent intent = new Intent(UserDashboard.this, ProductDetails.class);
                                            intent.putExtra("pid", model.getPid());
                                            startActivity(intent);
                                        }
                                    }
                                });

                            }

                            @NonNull
                            @Override
                            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                                ProductViewHolder holder = new ProductViewHolder(view);
                                return holder;
                            }
                        };

                recyclerView.setAdapter(adapter);
                adapter.startListening();
            }
        });

        categoriesRecycler.setAdapter(Adapter);

    }


}