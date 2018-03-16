package com.example.ka.sqlitedemocontact;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by KA on 3/15/2018.
 */

public class DetailActivity extends AppCompatActivity {
    private Bundle b;
    Contact contact;
    MyDatabase db;
    TextView tvDetailName, tvDetailPhone, tvDetailAddress, tvDetailGender, tvDetailDate, tvDetailTime;
    ImageView ivCall, ivText;
    ImageButton imageButton;
    boolean edited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setTitle("Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        buttonAction();
        getData();
        setData();
    }

    private void init() {
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailPhone = findViewById(R.id.tvDetailPhone);
        tvDetailAddress = findViewById(R.id.tvDetailAddress);
        tvDetailGender = findViewById(R.id.tvDetailGender);
        tvDetailDate = findViewById(R.id.tvDetailDate);
        tvDetailTime = findViewById(R.id.tvDetailTime);

        ivCall = findViewById(R.id.ivDetailCall);
        ivText = findViewById(R.id.ivDetailText);
        imageButton = findViewById(R.id.ivDetailLogo);
    }

    private void buttonAction() {
        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent call = new Intent(Intent.ACTION_CALL);
                call.setData(Uri.parse("tel:" + tvDetailPhone.getText()));
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(call);
            }
        });
        ivText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent text = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + tvDetailPhone.getText()));
                text.putExtra("sms_body", "");
                startActivity(text);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tell = new Intent(Intent.ACTION_CALL);
                tell.setData(Uri.parse("tel:" + tvDetailPhone.getText()));
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(tell);
            }
        });
    }

    private void getData() {
        db = new MyDatabase(this);
        b = getIntent().getExtras();
        contact = new Contact();
        contact.setmId(b.getInt("ID"));
        contact = db.getContact(contact.getmId());

    }

    private void setData() {
        tvDetailName.setText(contact.getmName());
        tvDetailAddress.setText(contact.getmAddress());
        tvDetailGender.setText(contact.getmGender());
        tvDetailDate.setText(contact.getmDate());
        tvDetailTime.setText(contact.getmTime());
        tvDetailPhone.setText(contact.getmPhone());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (edited) {
                    Intent i = new Intent();
                    i.putExtra("ID", String.valueOf(contact.getmId()));
                    setResult(1, i);
                }
                this.finish();
                break;
            case R.id.btnEdit:
                Intent intent = new Intent(getBaseContext(), AddActivity.class);
                intent.putExtra("ID", contact.getmId());
                startActivityForResult(intent, 3);
                break;
            case R.id.btnDelete:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("Delete this contact");
                alertBuilder.setMessage("This action will delete this contact. Are you sure?");
                alertBuilder.setCancelable(true);
                alertBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.deleteContact(contact);
                        db.close();
                        Intent intent = new Intent();
                        intent.putExtra("ID", String.valueOf(contact.getmId()));
                        setResult(RESULT_OK, intent);
                        finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && resultCode == RESULT_OK) {
            contact = (Contact) data.getExtras().getSerializable("RETURN");
            setData();
            edited = true;
        }
    }

    @Override
    public void onBackPressed() {
        if (edited) {
            Intent i = new Intent();
            i.putExtra("ID", String.valueOf(contact.getmId()));
            setResult(1, i);
        }
        this.finish();
    }


}
