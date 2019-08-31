package com.kriiskrishna.instantx;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Fragment1 extends Fragment {

    private String colorValue;
    private String post_key;
    private static String viewMode = "0";
    private Toolbar mTopToolbar;
    private ImageView NewNoteButton;
    private RecyclerView myNotesRecycler;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private static int phoneHeight;
    private static int phoneWidth;
    static int finalHeight;
    static int finalWidth;
    static int phoneWidthMode;
    private static URL myUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment1, null);
        mTopToolbar = V.findViewById(R.id.my_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mTopToolbar);
        setHasOptionsMenu(true);
        getScreenMetrics();

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference().child("Profile").child(mAuth.getUid()).child("Notes");
        myRef.keepSynced(true);

        NewNoteButton = V.findViewById(R.id.Fragment1NewNoteButton);
        NewNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditNote.class));
            }
        });

        myNotesRecycler = V.findViewById(R.id.MyNotesRecycle);
        setLinearLayoutManagerAsView();

        return V;
    }

    private void getScreenMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        phoneHeight = displayMetrics.heightPixels;
        phoneWidth = displayMetrics.widthPixels;
        Toast.makeText(getContext(),"Height :"+ phoneHeight +" Width :"+ phoneWidth,Toast.LENGTH_SHORT).show();
    }

    private void setLinearLayoutManagerAsView() {
        LinearLayoutManager LayoutManager = new LinearLayoutManager(getContext());
        myNotesRecycler.setLayoutManager(LayoutManager);
    }

    private void setGridLayoutManagerAsView() {
        StaggeredGridLayoutManager LayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        myNotesRecycler.setLayoutManager(LayoutManager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.FragmentToolbarItem1) {
            Toast.makeText(getContext(), "Search clicked", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.FragmentToolbarItem2) {
            Toast.makeText(getContext(), "Alignment clicked", Toast.LENGTH_LONG).show();
            if(viewMode.equals("0")){
                setGridLayoutManagerAsView();
                viewMode = "1";
            }else{
                setLinearLayoutManagerAsView();
                viewMode = "0";
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<SubFragment, MyNotesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SubFragment, MyNotesViewHolder>(
                SubFragment.class,R.layout.notes_view,MyNotesViewHolder.class,myRef
        ) {
            @Override
            protected void populateViewHolder(MyNotesViewHolder viewHolder, SubFragment model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setContent(model.getContent());
                viewHolder.setTime(model.getTime());
                viewHolder.setUri(model.getUri());
                final String postkey = getRef(position).getKey();
                post_key = postkey;
                GradientDrawable shape =  new GradientDrawable();
                shape.setCornerRadius( 15 );
                shape.setColor(Color.parseColor(model.getColor()));
                viewHolder.mView.setBackground(shape);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(),"Height :"+ phoneHeight +" Width :"+ phoneWidth+"\nHeight :"+ finalHeight +" Width :"+ finalWidth,Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public int getItemViewType(int position) {
                return position;
            }
        };
        firebaseRecyclerAdapter.notifyDataSetChanged();
        myNotesRecycler.setAdapter(firebaseRecyclerAdapter);

    }

    public static class MyNotesViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public String colorValue;

        public MyNotesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {
            if(title != null){
                TextView post_Title = mView.findViewById(R.id.RecyclerItemView2);
                post_Title.setText(title);
                post_Title.setVisibility(View.VISIBLE);}
        }
        public void setContent(String content) {
            if(content != null){
                TextView post_Title = mView.findViewById(R.id.RecyclerItemView3);
                post_Title.setText(content);
                post_Title.setVisibility(View.VISIBLE);}
        }
        public void setTime(String time) {
            TextView post_Title = mView.findViewById(R.id.RecyclerItemView4);
            post_Title.setText(time);
        }
        public void setColor(String color) {
            colorValue = color;
        }
        public void setUri(String image) {
            if(image!=null) {
                ImageView post_image = mView.findViewById(R.id.RecyclerItemView1);
                Picasso.get().load(image).into(post_image);
                try {
                    URL url = new URL("http://....");
                    Bitmap imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch(IOException e) {
                    System.out.println(e);
                }
                post_image.setVisibility(View.VISIBLE);
            }
        }
    }
}


