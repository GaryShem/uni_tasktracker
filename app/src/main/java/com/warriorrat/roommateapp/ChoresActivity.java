package com.warriorrat.roommateapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import java.util.Collections;

public class ChoresActivity extends AppCompatActivity {

    private DatabaseReference choresRef;
    private ChildEventListener childEventListener;
    private ArrayList<Chore> choresList;
    private ListView listView;
    private ChoreListAdapter choreAdapter;
    private static final String TAG = ChoresActivity.class.getSimpleName();

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chores);
        choresList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.chore_list);
        choreAdapter = new ChoreListAdapter(ChoresActivity.this, choresList);
        listView.setAdapter(choreAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ChoresActivity.this);
                final EditText edittext = new EditText(ChoresActivity.this);
                final Chore editedChore = choresList.get(i);
                alert.setMessage(R.string.edit_chore_description);
                edittext.setText(choresList.get(i).getDescription());
                alert.setView(edittext);
                edittext.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT);
                alert.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String choreDescription = edittext.getText().toString().trim();
                        editedChore.setDescription(choreDescription);
                        pushChoreUpdate(editedChore);
                        choreAdapter.notifyDataSetChanged();
                    }
                });

                alert.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        choresList.remove(i);
                        deleteChore(editedChore.getUuid());
                        choreAdapter.notifyDataSetChanged();
                    }
                });
                AlertDialog alertWindow = alert.create();
                alertWindow.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                alertWindow.show();
                return true;
            }
        });
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildAdded:" + dataSnapshot.toString());
//                Toast.makeText(ChoresActivity.this, R.string.chore_added, Toast.LENGTH_SHORT).show();
                Chore newChore = getChoreFromSnapshot(dataSnapshot);
                choresList.add(newChore);
                Collections.sort(choresList);
                choreAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildChanged:" + dataSnapshot.toString());
//                Toast.makeText(ChoresActivity.this, R.string.chore_changed, Toast.LENGTH_SHORT).show();
                Chore changedChore = getChoreFromSnapshot(dataSnapshot);
                for (Chore chore : choresList) {
                    if (changedChore.getUuid().equals(chore.getUuid())) {
                        chore.setCompleted(changedChore.isCompleted());
                        chore.setDescription(changedChore.getDescription());
                        break;
                    }
                }
                Collections.sort(choresList);
                choreAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onChildRemoved:" + dataSnapshot.toString());
//                Toast.makeText(ChoresActivity.this, R.string.chore_removed, Toast.LENGTH_SHORT).show();
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
//                Log.d(TAG, "onChildMoved:" + dataSnapshot.toString());
//                Toast.makeText(ChoresActivity.this, R.string.chore_moved, Toast.LENGTH_SHORT).show();
                Collections.sort(choresList);
                choreAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(ChoresActivity.this, R.string.failed_to_do_something, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private Chore getChoreFromSnapshot(DataSnapshot snapshot) {
        Chore result = snapshot.getValue(Chore.class);
        return result;
    }

    public void createChore(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(ChoresActivity.this);
        edittext.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT);
        alert.setMessage(R.string.enter_chore_description);

        alert.setView(edittext);
        alert.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String choreDescription = edittext.getText().toString().trim();
                pushChoreUpdate(new Chore(choreDescription));
            }
        });

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(ChoresActivity.this, R.string.chore_creation_cancelled, Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertWindow = alert.create();
        alertWindow.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertWindow.show();
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

    public static void pushChoreUpdate(final Chore chore) {
        DatabaseReference singleChoreRef = Util.getChoresRef().child(chore.getUuid());
        singleChoreRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(chore);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d(ContentValues.TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private static void deleteChore(String uuid) {
        Util.getChoresRef().child(uuid).runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(null);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d(ContentValues.TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
}
