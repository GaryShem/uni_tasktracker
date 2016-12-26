package com.warriorrat.roommateapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;

public class BillsActivity extends AppCompatActivity {

    private DatabaseReference billsRef;
    private ChildEventListener childEventListener;
    private ArrayList<Bill> billsList;
    private ListView listView;
    private BillListAdapter billAdapter;
    private static final String TAG = BillsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills);
        billsList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.bill_list);
        billAdapter = new BillListAdapter(BillsActivity.this, billsList);
        listView.setAdapter(billAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder alert = new AlertDialog.Builder(BillsActivity.this);

                alert.setMessage(R.string.enter_bill_description);

                LinearLayout linearLayout = new LinearLayout(alert.getContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                alert.setView(linearLayout);

                final EditText descriptionEditText = new EditText(BillsActivity.this);
                descriptionEditText.setHint(R.string.description);
                descriptionEditText.setInputType(TYPE_CLASS_TEXT);
                linearLayout.addView(descriptionEditText);

                final EditText amountEditText = new EditText(BillsActivity.this);
                amountEditText.setHint(R.string.bill_amount);
                amountEditText.setInputType(TYPE_NUMBER_FLAG_DECIMAL);
                linearLayout.addView(amountEditText);
                final Bill editedBill = billsList.get(i);


                alert.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String billDescription = descriptionEditText.getText().toString().trim();
                        try {
                            double billAmount = Double.parseDouble(amountEditText.getText().toString().trim());
                            editedBill.setDescription(billDescription);
                            editedBill.setAmount(billAmount);
                            pushBillUpdate(editedBill);
                            billAdapter.notifyDataSetChanged();
                        } catch (NumberFormatException nfe) {
                            Toast.makeText(BillsActivity.this, R.string.incorrect_amount_format, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alert.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        billsList.remove(i);
                        deleteBill(editedBill.getUuid());
                        billAdapter.notifyDataSetChanged();
                    }
                });

                alert.show();
                return true;
            }
        });
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                //Log.d(TAG, "onChildAdded:" + dataSnapshot.toString());
                //Toast.makeText(BillsActivity.this, R.string.bill_added, Toast.LENGTH_SHORT).show();
                Bill newBill = getBillFromSnapshot(dataSnapshot);
                billsList.add(newBill);
                Collections.sort(billsList);
                billAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                //Log.d(TAG, "onChildChanged:" + dataSnapshot.toString());
                //Toast.makeText(BillsActivity.this, R.string.bill_changed, Toast.LENGTH_SHORT).show();
                Bill changedBill = getBillFromSnapshot(dataSnapshot);
                for (Bill bill : billsList) {
                    if (changedBill.getUuid().equals(bill.getUuid())) {
                        bill.setCompleted(changedBill.isCompleted());
                        bill.setDescription(changedBill.getDescription());
                        bill.setAmount(changedBill.getAmount());
                        break;
                    }
                }
                Collections.sort(billsList);
                billAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //Log.d(TAG, "onChildRemoved:" + dataSnapshot.toString());
                //Toast.makeText(BillsActivity.this, R.string.bill_removed, Toast.LENGTH_SHORT).show();
                Bill deletedBill = getBillFromSnapshot(dataSnapshot);
                for (int i = 0; i < billsList.size(); i++) {
                    if (billsList.get(i).getUuid().equals(deletedBill.getUuid())) {
                        billsList.remove(i);
                        break;
                    }
                }
                billAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                //Log.d(TAG, "onChildMoved:" + dataSnapshot.toString());
                //Toast.makeText(BillsActivity.this, R.string.bill_moved, Toast.LENGTH_SHORT).show();
                Collections.sort(billsList);
                billAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(BillsActivity.this, R.string.failed_to_do_something, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private Bill getBillFromSnapshot(DataSnapshot snapshot) {
        Bill result = snapshot.getValue(Bill.class);
        return result;
    }

    public void createBill(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setMessage(R.string.enter_bill_description);

        LinearLayout linearLayout = new LinearLayout(alert.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        alert.setView(linearLayout);

        final EditText descriptionEditText = new EditText(BillsActivity.this);
        descriptionEditText.setHint(R.string.description);
        descriptionEditText.setInputType(TYPE_CLASS_TEXT);
        linearLayout.addView(descriptionEditText);

        final EditText amountEditText = new EditText(BillsActivity.this);
        amountEditText.setHint(R.string.bill_amount);
        amountEditText.setInputType(TYPE_NUMBER_FLAG_DECIMAL);
        linearLayout.addView(amountEditText);

        alert.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    String billDescription = descriptionEditText.getText().toString().trim();
                    double billAmount = Double.parseDouble(amountEditText.getText().toString().trim());
                    pushBillUpdate(new Bill(billDescription, billAmount));
                } catch (NumberFormatException nfe) {
                    Toast.makeText(BillsActivity.this, R.string.incorrect_amount_format, Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(BillsActivity.this, R.string.bill_creation_cancelled, Toast.LENGTH_SHORT).show();
            }
        });

        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        billsRef = Util.getBillsRef();
        billsRef.addChildEventListener(childEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        billsRef.removeEventListener(childEventListener);
    }

    public static void pushBillUpdate(final Bill bill) {
        DatabaseReference singleChoreRef = Util.getBillsRef().child(bill.getUuid());
        singleChoreRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(bill);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private static void deleteBill(String uuid) {
        Util.getBillsRef().child(uuid).runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(null);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
}
