package com.kriiskrishna.instantx;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditNote extends AppCompatActivity {
    private RelativeLayout layout1;
    private LinearLayout layout2,layout3,layout4,layout5;
    private ImageView set1,set2,noteImage;
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
    private String childKey = null;
    private int phoneHeight,phoneWidth;
    private Toolbar mTopToolbar;
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK & data.getData()!=null){
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
                noteImage.setImageBitmap(bitmap);
                noteImage.setVisibility(View.VISIBLE);

                int bitmapHeight = bitmap.getHeight();
                int bitmapWidth = bitmap.getWidth();
                int aspectRatioHeight = (phoneHeight*bitmapWidth)/(phoneWidth);
                //float density = Resources.getSystem().getDisplayMetrics().density;
                //int dp = (int)(aspectRatioHeight / density);
                noteImage.getLayoutParams().height = aspectRatioHeight/5;

                Toast.makeText(getApplication(),"PH:"+phoneHeight+" PW:"+phoneWidth+" BH"+bitmapHeight+" BW"+bitmapWidth,Toast.LENGTH_SHORT).show();
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

        mTopToolbar = findViewById(R.id.EditNoteToolbar);
        mTopToolbar.setNavigationIcon(R.drawable.back_black);
        setSupportActionBar(mTopToolbar);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference().child("Profile").child(mAuth.getUid()).child("Notes");
        myRef.keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference().child((mAuth.getUid()));
        getIntentData();
        getScreenMetrics();

        DateFormat df = new SimpleDateFormat("h:mm:ss a");
        timeWithSec = df.format(Calendar.getInstance().getTime());
        DateFormat df1 = new SimpleDateFormat("EEE, d MMM yyyy");
        date = df1.format(Calendar.getInstance().getTime());
        DateFormat df2 = new SimpleDateFormat("h:mm a");
        timeWithoutSec = df2.format(Calendar.getInstance().getTime());
        editTime.setText(timeWithoutSec);


        mTopToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSettingsInvsible();
                checkContent("0");
            }
        });
        set1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(layout3.getVisibility()==View.GONE)
                    layout3.setVisibility(View.VISIBLE);
                else
                    layout3.setVisibility(View.GONE);
                layout4.setVisibility(View.GONE);
            }
        });
        set2.setOnClickListener(new View.OnClickListener() {
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
        mTopToolbar.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editnote_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.EditNoteToolbarItem1){
            mprogress.setMessage("Posting");
            mprogress.show();
            setSettingsInvsible();
            checkContent("1");
        }
        return super.onOptionsItemSelected(item);
    }

    private void getIntentData() {
        Intent indent_mode = getIntent();
        childKey = indent_mode.getStringExtra("NoteKey");
        if(childKey!=null){
            editMode = true;}
        loadIntentData();
    }

    private void loadIntentData() {
        if(childKey!=null)
        myRef.child(childKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SubFragment subFragment = dataSnapshot.getValue(SubFragment.class);
                backgroundColor = subFragment.getColor();
                setLayoutColor(backgroundColor);
                title.setText(subFragment.getTitle());
                content.setText(subFragment.getContent());
                String image = subFragment.getUri();
                if(image!=null){
                    Picasso.get().load(image).into(noteImage);
                    noteImage.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

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
            path = childKey;
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
        if(!title.getText().toString().isEmpty())
        myRef.child(path).child("title").setValue(title.getText().toString());
        if(!content.getText().toString().isEmpty())
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
        layout2 = findViewById(R.id.EditNoteLayout2);
        layout3 = findViewById(R.id.EditNoteLayout3);
        layout4 = findViewById(R.id.EditNoteLayout4);
        layout5 = findViewById(R.id.EditNoteLayout5);
        set1 = findViewById(R.id.EditNoteButton3);
        set2 = findViewById(R.id.EditNoteButton4);
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

    private void getScreenMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        phoneHeight = displayMetrics.heightPixels;
        phoneWidth = displayMetrics.widthPixels;
        //Toast.makeText(getContext(),"Height :"+ phoneHeight +" Width :"+ phoneWidth,Toast.LENGTH_SHORT).show();
    }
}
