package com.example.ka.sqlitedemocontact;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ContactAdapter mAdapter;
    private MyDatabase db;
    private List<Contact> contacts;
    public static final int CODE=1996;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new MyDatabase(this);
        contacts = new ArrayList<>();
        getData();
        handle();
    }

    private void getData() {
        contacts.clear();
        contacts = db.getAllContacts();
        db.close();
    }

    private void handle() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rvContacts);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new ContactAdapter(contacts, MainActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getBaseContext(), DetailActivity.class);
                        intent.putExtra("ID", contacts.get(position).getmId());
                        startActivityForResult(intent, 2);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.addContact:
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intent,CODE);
                break;
            case R.id.deleteAll:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("DROP ALL DATA");
                alertBuilder.setMessage("This action will deleta all data in SQLite. Are you sure?");
                alertBuilder.setCancelable(false);
                alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAll();
                    }
                });
                alertBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAll() {
        db.deleteAllContact();
        contacts.clear();
        mAdapter.notifyDataSetChanged();
        db.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE && resultCode == RESULT_OK) {
            Contact contact = (Contact) data.getExtras().getSerializable("RETURN");
            contacts.add(contact);
            mAdapter.notifyDataSetChanged();
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            String contactID = data.getStringExtra("ID");
            int id = Integer.parseInt(contactID);
            int pos = 0;
            for (int i = 0; i < contacts.size(); i++) {
                if (contacts.get(i).getmId() == id) {
                    pos = i;
                    break;
                }
            }
            contacts.remove(pos);
            mAdapter.notifyDataSetChanged();
        }
        if (requestCode == 2 && resultCode == 1) {
            String editID = data.getStringExtra("ID");
            int id = Integer.parseInt(editID);
            int pos = 0;
            for (int i = 0; i < contacts.size(); i++) {
                if (contacts.get(i).getmId() == id) {
                    pos = i;
                    break;
                }
            }
            contacts.get(pos).setmName(db.getContact(id).getmName());
            db.close();
            mAdapter.notifyDataSetChanged();
        }
    }
}
