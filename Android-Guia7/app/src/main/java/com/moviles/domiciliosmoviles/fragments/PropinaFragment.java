package com.moviles.domiciliosmoviles.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.moviles.domiciliosmoviles.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link PropinaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PropinaFragment extends Fragment {
    private int propina;
    private TextView propinaText;
    private static final String TAG = "Propina TAG";
    public PropinaFragment() {
        // Required empty public constructor
    }
    public static PropinaFragment newInstance() {
        PropinaFragment fragment = new PropinaFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        propina=1000;
        Log.d(TAG, "On create fragment");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "On create view fragment");
        return inflater.inflate(R.layout.fragment_propina, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "On view created fragment");
        propinaText =(TextView) view.findViewById(R.id.propina_text);
        Button menos = (Button) view.findViewById(R.id.menos);
        menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(propina>0){
                    propina-=1000;
                    propinaText.setText("$"+propina);
                }else{
                     Snackbar.make(propinaText,"Propina no puede ser negativa",Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        Button mas = (Button)view.findViewById(R.id.mas);
        mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    propina+=1000;
                    propinaText.setText("$"+propina);
            }
        });

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "On attach fragment");

    }
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "On detach fragment");
    }
}
