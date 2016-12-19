package com.warriorrat.roommateapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;

public class ChoresActivity extends AppCompatActivity {

    private DatabaseReference choresRef;
    ChildEventListener childEventListener;
    ArrayList<Chore> choresList;
    ListView listView;
    ChoreListAdapter choreAdapter;
    private static final String TAG = ChoresActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chores);
        choresList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.chore_list);
        choreAdapter = new ChoreListAdapter(ChoresActivity.this, choresList);
        listView.setAdapter(choreAdapter);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.toString());
                Toast.makeText(ChoresActivity.this, "Child added", Toast.LENGTH_SHORT).show();
                Chore newChore = getChoreFromSnapshot(dataSnapshot);
                choresList.add(newChore);
                choreAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.toString());
                Toast.makeText(ChoresActivity.this, "Child changed", Toast.LENGTH_SHORT).show();
                Chore changedChore = getChoreFromSnapshot(dataSnapshot);
                for (Chore chore : choresList) {
                    if (changedChore.getUuid().equals(chore.getUuid())) {
                        chore.setCompleted(changedChore.isCompleted());
                        chore.setDescription(changedChore.getDescription());
                        break;
                    }
                }
                choreAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.toString());
                Toast.makeText(ChoresActivity.this, "Child removed", Toast.LENGTH_SHORT).show();
                Chore deletedChore = getChoreFromSnapshot(dataSnapshot);
                for (int i = 0; i < choresList.size(); i++) {
                    if (choresList.get(i).getUuid().equals(deletedChore.getUuid())) {
                        choresList.remove(i);
                        break;
                    }
                }
                choreAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.toString());
                Toast.makeText(ChoresActivity.this, "Child moved", Toast.LENGTH_SHORT).show();
                choreAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(ChoresActivity.this, "Failed to do something", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private Chore getChoreFromSnapshot(DataSnapshot snapshot) {
        Chore result = new Chore();
        result.setCompleted((boolean) snapshot.child("completed").getValue());
        result.setDescription((String) snapshot.child("description").getValue());
        result.setUuid((String) snapshot.child("uuid").getValue());
        return result;
    }

    public void createChore(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(ChoresActivity.this);
        alert.setMessage("Enter chore description");

        alert.setView(edittext);

        alert.setPositiveButton("Yes Option", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String choreDescription = edittext.getText().toString();
                Util.pushChoreUpdate(new Chore(choreDescription));
            }
        });

        alert.setNegativeButton("No Option", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(ChoresActivity.this, "Chore creation cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        choresRef = Util.getChoresRef();
        choresRef.addChildEventListener(childEventListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        choresRef.removeEventListener(childEventListener);
    }
}
