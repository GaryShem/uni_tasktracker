package com.warriorrat.roommateapp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import static android.content.ContentValues.TAG;

public class Util {
    private static boolean isLoggedIn;
    private static FirebaseDatabase database;
    private static FirebaseAuth mAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;
    private static DatabaseReference choresRef;
    private static FirebaseUser user;

    public static boolean isLoggedIn() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    public static String userId() {
        if (isLoggedIn()) {
            return user.getUid();
        } else {
            throw new NullPointerException("Not logged in");
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
                        isLoggedIn = true;
                        choresRef = Util.database().getReference().child("groups").child(Util.userId());
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    } else {
                        // User is signed out
                        isLoggedIn = false;
                        choresRef = null;
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                }
            };
        }
        return mAuth;
    }

    public static DatabaseReference getChoresRef() {
        choresRef = Util.database().getReference().child("groups").child(Util.userId());
        return choresRef;
    }

    public static void pushChoreUpdate(final Chore chore) {
        DatabaseReference singleChoreRef = choresRef.child(chore.getUuid());
        singleChoreRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Chore c = mutableData.getValue(Chore.class);
                if (c == null) {
                    c = chore;
                } else if (c.isCompleted() != chore.isCompleted()) {
                    c.setCompleted(chore.isCompleted());
                    c.setDescription(chore.getDescription());
                }
                mutableData.setValue(c);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
}
