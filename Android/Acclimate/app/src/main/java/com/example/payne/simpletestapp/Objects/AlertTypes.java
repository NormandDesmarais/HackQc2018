package com.example.payne.simpletestapp.Objects;

import android.graphics.drawable.Drawable;

import com.example.payne.simpletestapp.MainActivities.MainActivity;
import com.example.payne.simpletestapp.R;

public enum  AlertTypes {

    // Confirmed pins
    EAU             (MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_goutte)),
    FEU             (MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_feu)),
    TERRAIN         (MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_seisme)),
    METEO           (MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_vent)),

    // User pins
    USER_EAU        (MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_user_water)),
    USER_FEU        (MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_user_fire)),
    USER_TERRAIN    (MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_user_earth)),
    USER_METEO      (MainActivity.mainActivity.getResources().getDrawable(R.drawable.pin_user_wind));

    private final Drawable icon;

    AlertTypes(Drawable icon){
        this.icon = icon;
    }

    public Drawable getIcon(){ return this.icon; }


}
