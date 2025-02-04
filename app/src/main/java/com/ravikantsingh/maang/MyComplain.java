package com.ravikantsingh.maang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ravikantsingh.maang.Adapters.SuggestionAdapter;
import com.ravikantsingh.maang.ModalClass.ModalClass;

import java.util.ArrayList;

/**
 * Created by Ravikant Singh on 27,February,2019
 */
public class MyComplain extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SuggestionAdapter mAdapter;
    FloatingActionButton fab;
    ArrayList<String> postList;
    ArrayList<ModalClass> postList2;
    String userUID;

    @Nullable
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mRecyclerView = findViewById(R.id.suggestionRV);
        postList2 = new ArrayList<>();
        mAdapter = new SuggestionAdapter(postList2, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        fab = findViewById(R.id.fab);
        postList = new ArrayList<>();

        SharedPreferences preferences = getSharedPreferences(StringVariables.SHARED_PREFERENCE_FILE, MODE_PRIVATE);
        userUID = preferences.getString("userUID", "");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyComplain.this, AddComplainActivity.class));
            }
        });
        try {
            final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("complaints");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(userUID).child("complaints");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    postList2.clear();
                    for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                        reference2.child(String.valueOf(ds1.getValue())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot ds) {
                                postList2.add(new ModalClass(String.valueOf(ds.child("related-sector").getValue()),
                                        String.valueOf(ds.child("related-scheme").getValue()),
                                        String.valueOf(ds.child("likes").getValue()),
                                        String.valueOf(ds.child("comments").getValue()),
                                        String.valueOf(ds.child("imglink").getValue()),
                                        String.valueOf(ds.child("pdflink").getValue()),
                                        String.valueOf(ds.child("description").getValue()),
                                        String.valueOf(ds.child("Time").getValue()),
                                        String.valueOf(ds.child("userUID").getValue()),
                                        String.valueOf(ds.child("tag").getValue()),
                                        String.valueOf(ds.child("name").getValue())));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mAdapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
        }
    }
}