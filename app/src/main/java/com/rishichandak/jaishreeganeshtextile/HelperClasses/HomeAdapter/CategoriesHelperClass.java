package com.rishichandak.jaishreeganeshtextile.HelperClasses.HomeAdapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

public class CategoriesHelperClass {

    //This helper class is to manage the cardView variables for categories card view (design)

    //for image, we use int as resources can be fetched using int
    GradientDrawable gradient;
    int image;
    String title;

    public CategoriesHelperClass(GradientDrawable gradient,int image, String title) {
        this.gradient = gradient;
        this.image = image;
        this.title = title;
    }

    public GradientDrawable getGradient() {
        return gradient;
    }

    public void setGradient(GradientDrawable gradient) {
        this.gradient = gradient;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

//Main Class is the Adapter Class which will hold our design and also our code