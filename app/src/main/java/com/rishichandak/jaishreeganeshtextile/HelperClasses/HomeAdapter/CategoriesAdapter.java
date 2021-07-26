package com.rishichandak.jaishreeganeshtextile.HelperClasses.HomeAdapter;

//Adapter basically holds values and puts them into designs
//Gets the design & gets the values ... works as a bridge between the two

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rishichandak.jaishreeganeshtextile.Interface.ItemClickListener;
import com.rishichandak.jaishreeganeshtextile.R;

import java.util.ArrayList;


public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {

    ArrayList<CategoriesHelperClass> categories;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    //Thus, when we call this adapter class from our main activity, we must pass list of data inside it
    public CategoriesAdapter(ArrayList<CategoriesHelperClass> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Mention in adapter class what design to use

        //Created a simple view pointing to categories_card_design
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_card,parent,false);

        //Passed this design in CategoriesViewHolder and returned it
        CategoriesViewHolder categoriesViewHolder = new CategoriesViewHolder(view, mListener);

        return categoriesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {

        //Main function that binds design & code

        //Assign the values(text,image...) on position 'position' each time
        CategoriesHelperClass categoriesHelperClass = categories.get(position);

        //CategoriesViewHolder - contains the view
        holder.relativeLayout.setBackground(categoriesHelperClass.getGradient());
        holder.imageView.setImageResource(categoriesHelperClass.getImage());
        holder.textView.setText(categoriesHelperClass.getTitle());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    //Get the design ... holds the view
    public static class CategoriesViewHolder extends RecyclerView.ViewHolder{


        RelativeLayout relativeLayout;
        ImageView imageView;
        TextView textView;

        public CategoriesViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.background_gradient);
            imageView = itemView.findViewById(R.id.categories_image);
            textView = itemView.findViewById(R.id.categories_title);
            imageView.setClipToOutline(true);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

    }

}
