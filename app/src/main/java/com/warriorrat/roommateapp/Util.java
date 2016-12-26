package com.warriorrat.roommateapp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

public class Util {
    private static FirebaseDatabase database;
    private static FirebaseAuth mAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;
    private static DatabaseReference choresRef;
    private static DatabaseReference billsRef;
    private static FirebaseUser user;

    public static boolean isLoggedIn() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    public static String userId() {
        if (isLoggedIn()) {
            return user.getUid();
        } else {
            throw new NullPointerException("Not Logged In");
        }
    }

    public static FirebaseDatabase database() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
        }
        return database;
    }

    public static FirebaseAuth auth() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        choresRef = Util.database().getReference().child("groups").child(Util.userId());
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    } else {
                        // User is signed out
                        choresRef = null;
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                }
            };
        }
        return mAuth;
    }

    public static DatabaseReference getChoresRef() {
        choresRef = Util.database().getReference().child("groups").child(Util.userId()).child("chores");
        return choresRef;
    }

    public static DatabaseReference getBillsRef() {
        billsRef = Util.database().getReference().child("groups").child(Util.userId()).child("bills");
        return billsRef;
    }

    public static void logOff() {
        mAuth.signOut();
    }
}
