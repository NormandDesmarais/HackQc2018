package com.example.payne.simpletestapp.Notifications;

import android.content.Context;

import com.example.payne.simpletestapp.Objects.AlerteSpring;

public class AlertNotification extends AcclimateNotification{

    AlerteSpring alerte;

    public AlertNotification(Context context, AlerteSpring alerte){
        super(context);
        this.alerte = alerte;

        this.builder.setContentTitle("Nouvelle alerte");
    }



}
