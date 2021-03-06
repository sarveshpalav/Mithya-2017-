package com.pcce.mithya.mithya2017;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;

public class DeveloperFragment extends Fragment {
    public static DiscreteScrollView scrollView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference teamRef = database.getReference("developers");
    ArrayList<Developer> developers;
    TextView fname, lname, designation;
    Button contact;
    String phone;
    public static DeveloperAdapter teamAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_developer, container, false);
        Home.imageadd.setVisibility(View.INVISIBLE);

        Home.toolTitle.setText("Developers - Mithya 2017");
        scrollView = (DiscreteScrollView) view.findViewById(R.id.picker);
        fname = (TextView) view.findViewById(R.id.teamFName);
        lname = (TextView) view.findViewById(R.id.teamLName);
        contact = (Button) view.findViewById(R.id.teamContact);
        designation = (TextView) view.findViewById(R.id.teamDesignation);
        developers = new ArrayList<>();
        teamAdapter = new DeveloperAdapter(getActivity(),developers);
        scrollView.setAdapter(teamAdapter);
        getData();

        scrollView.setClipToPadding(false);
        scrollView.setPaddingRelative(150,0,150,0);
        fname.setTypeface(Main.myCustomFont);
        lname.setTypeface(Main.myCustomFont);
        contact.setTypeface(Main.myCustomFont);
        designation.setTypeface(Main.myCustomFont);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                try{
                    getActivity().startActivity(callIntent);
                } catch (SecurityException e){
                    onCall();
                }
            }
        });
        scrollView.setScrollStateChangeListener(new DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder>() {
            @Override
            public void onScrollStart(RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {

            }

            @Override
            public void onScrollEnd(RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
                getInfo(adapterPosition);
            }

            @Override
            public void onScroll(float scrollPosition, @NonNull RecyclerView.ViewHolder currentHolder, @NonNull RecyclerView.ViewHolder newCurrent) {

            }


        });
        return view;
    }

    public void getInfo(int position){
        fname.setText(Main.splitName(developers.get(position).getName(), "first").toUpperCase());
        lname.setText(Main.splitName(developers.get(position).getName(), "last").toUpperCase());
        designation.setText("Developer");
        phone = "" + developers.get(position).getPhone();
    }

    public void getData(){
        teamRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {

                    Developer developer = new Developer();
                    developer.setImage((String) data.child("Image").getValue());
                    developer.setName((String) data.child("Name").getValue());
                    developer.setPhone((Long) data.child("Phone").getValue());


                    developers.add(developer);
                }
                teamAdapter = new DeveloperAdapter(getContext(), developers);
                scrollView.setAdapter(teamAdapter);
                teamAdapter.notifyDataSetChanged();
                scrollView.setItemTransformer(new ScaleTransformer.Builder()
                        .setMinScale(0.8f)
                        .build());
                getInfo(scrollView.getCurrentItem());




            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Operations", "Failed to read value.", error.toException());
            }


        });
    }
    public void onCall() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    123);
        } else {
            Toast.makeText(getActivity(),phone,Toast.LENGTH_LONG).show();
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + phone)));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    onCall();
                } else {
                    Log.d("Operations", "Call Permission Not Granted");
                }
                break;

            default:
                break;
        }
    }


}
