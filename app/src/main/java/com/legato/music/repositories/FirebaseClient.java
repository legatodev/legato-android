package com.legato.music.repositories;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
Any interaction with FireBase frame should be done by this class
*/

class FirebaseClient {

    @Nullable private  static FirebaseClient instance;
    @Nullable private FirebaseDatabase mFirebaseDatabase;
    @Nullable private FirebaseAuth mFirebaseAuth;

    public static FirebaseClient getInstance(){
        if(instance == null){
            instance = new FirebaseClient();
        }
        return instance;
    }

    private FirebaseClient(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public DatabaseReference getChild(String pathString){
        if(mFirebaseDatabase != null) {
            return mFirebaseDatabase.getReference().child(pathString);
        }else{
            return FirebaseDatabase.getInstance().getReference().child(pathString);
        }
    }

    public Boolean isEmailVerified(){
        if(mFirebaseAuth != null) {
            return mFirebaseAuth.getCurrentUser().isEmailVerified();
        }else{
            return FirebaseAuth.getInstance().getCurrentUser().isEmailVerified();
        }
    }

    public void sendEmailVerification(){
        if(mFirebaseAuth != null) {
            mFirebaseAuth.getCurrentUser().sendEmailVerification();
        }else{
            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
        }
    }
}
