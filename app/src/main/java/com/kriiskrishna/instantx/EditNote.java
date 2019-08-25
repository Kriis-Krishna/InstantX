package com.kriiskrishna.instantx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditNote extends AppCompatActivity {
    private RelativeLayout layout1;
    private LinearLayout layout2,layout3,layout4,layout5;
    private ImageView set1,set2,set3,set4,noteImage;
    private EditText title,content;
    private TextView editTime,option1,option2,option3,option4;
    private CircleImageView color1,color2,color3,color4,color5;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference storageReference;
    private boolean editMode = false;
    private String backgroundColor = "#eeeeee",timeWithSec,date,timeWithoutSec,path;
    private int PICK_IMAGE = 123;
    private Uri imagePath,downloadUri;
    private ProgressDialog mprogress;
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK & data.getData()!=null){
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
                noteImage.setImageBitmap(bitmap);
                noteImage.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note);
        loadView();
        setLayoutColor(backgroundColor);
        mprogress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference().child("Profile").child(mAuth.getUid()).child("Notes");
        myRef.keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference().child((mAuth.getUid()));

        DateFormat df = new SimpleDateFormat("h:mm:ss a");
        timeWithSec = df.format(Calendar.getInstance().getTime());
        DateFormat df1 = new SimpleDateFormat("EEE, d MMM yyyy");
        date = df1.format(Calendar.getInstance().getTime());
        DateFormat df2 = new SimpleDateFormat("h:mm a");
        timeWithoutSec = df2.format(Calendar.getInstance().getTime());
        editTime.setText(timeWithoutSec);

        set1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSettingsInvsible();
                checkContent("0");
            }
        });
        set2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mprogress.setMessage("Posting");
                mprogress.show();
                setSettingsInvsible();
                checkContent("1");
            }
        });
        set3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(layout3.getVisibility()==View.GONE)
                    layout3.setVisibility(View.VISIBLE);
                else
                    layout3.setVisibility(View.GONE);
                layout4.setVisibility(View.GONE);
            }
        });
        set4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(layout4.getVisibility()==View.GONE)
                    layout4.setVisibility(View.VISIBLE);
                else
                    layout4.setVisibility(View.GONE);
                layout3.setVisibility(View.GONE);
            }
        });

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSettingsInvsible();
            }
        });
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSettingsInvsible();
            }
        });
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSettingsInvsible();
            }
        });
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSettingsInvsible();
            }
        });

        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSettingsInvsible();
            }
        });
        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSettingsInvsible();
                setImageAction();
            }
        });
        color1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                identifyBackgroundColor(0);
            }
        });
        color2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                identifyBackgroundColor(1);
            }
        });
        color3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                identifyBackgroundColor(2);
            }
        });
        color4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                identifyBackgroundColor(3);
            }
        });
        color5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                identifyBackgroundColor(4);
            }
        });
    }

    private void checkContent(String s) {
        if(!content.getText().toString().isEmpty() || imagePath != null){
            updateNote();
            finish();}
        else if(s.equals("0")){
            mprogress.dismiss();
            finish();}
        else{
            mprogress.dismiss();
            Toast.makeText(getApplicationContext(),"Can't upload empty file",Toast.LENGTH_SHORT).show();
        }
    }
    private void updateNote() {
        if(editMode == false){
            path = timeWithSec+date;
        }else{
            //path = childKey;
        }
        if(imagePath != null){
            StorageReference filePath = storageReference.child("Notes").child(imagePath.getLastPathSegment());
            filePath.putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mprogress.dismiss();
                    downloadUri = taskSnapshot.getDownloadUrl();
                    Toast.makeText(getApplicationContext(),"Image Uploading",Toast.LENGTH_SHORT).show();
                    myRef.child(path).child("uri").setValue(downloadUri.toString());
                }
            });
        }else mprogress.dismiss();
        myRef.child(path).child("title").setValue(title.getText().toString());
        myRef.child(path).child("content").setValue(content.getText().toString());
        myRef.child(path).child("color").setValue(backgroundColor);
        myRef.child(path).child("time").setValue(timeWithoutSec);
        myRef.child(path).child("date").setValue(date);
    }

    private void setImageAction() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select image"),PICK_IMAGE);
    }
    private void identifyBackgroundColor(int position) {
        String[] colorValue = {"#eeeeee","#e7e56e","#b6ff94","#91ff9a","#80c6ff"};
        setLayoutColor(colorValue[position]);
        backgroundColor = colorValue[position];
    }
    private void setLayoutColor(String s){
        layout2.setBackgroundColor(Color.parseColor(s));
        layout3.setBackgroundColor(Color.parseColor(s));
        layout4.setBackgroundColor(Color.parseColor(s));
        layout5.setBackgroundColor(Color.parseColor(s));
        setSettingsInvsible();
    }
    private void setSettingsInvsible() {
        layout3.setVisibility(View.GONE);
        layout4.setVisibility(View.GONE);
    }
    private void loadView() {
        layout1 = findViewById(R.id.EditNoteLayout1);
        layout2 = findViewById(R.id.EditNoteLayout2);
        layout3 = findViewById(R.id.EditNoteLayout3);
        layout4 = findViewById(R.id.EditNoteLayout4);
        layout5 = findViewById(R.id.EditNoteLayout5);
        set1 = findViewById(R.id.EditNoteButton1);
        set2 = findViewById(R.id.EditNoteButton2);
        set3 = findViewById(R.id.EditNoteButton3);
        set4 = findViewById(R.id.EditNoteButton4);
        noteImage = findViewById(R.id.EditNoteImage);
        title = findViewById(R.id.EditNoteTitle);
        content = findViewById(R.id.EditNoteContent);
        editTime = findViewById(R.id.EditNoteTime);
        option1 = findViewById(R.id.EditNoteOption1);
        option2 = findViewById(R.id.EditNoteOption2);
        option3 = findViewById(R.id.EditNoteOption3);
        option4 = findViewById(R.id.EditNoteOption4);
        color1 = findViewById(R.id.NotesColor1);
        color2 = findViewById(R.id.NotesColor2);
        color3 = findViewById(R.id.NotesColor3);
        color4 = findViewById(R.id.NotesColor4);
        color5 = findViewById(R.id.NotesColor5);
    }
}
