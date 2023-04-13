package com.example.ahnsik.ukuleletutor;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GuideTopFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class GuideTopFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GuideTopFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GuideTopFragment newInstance(String param1, String param2) {
        GuideTopFragment fragment = new GuideTopFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public GuideTopFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_guide_top, container, false);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_guide_top , container, false);

        Button btnReturn = rootView.findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent();
//                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.GuideTopActivity");
//                i.setComponent(name);
//                startActivity(i);
                TopMenuFragment topMenu = new TopMenuFragment();
                ((MainActivity) getActivity()).replaceFragment(topMenu);
            }
        });

        Button btnExplainUkulele = rootView.findViewById(R.id.btnExplainUkulele);
        btnExplainUkulele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ukulele", "Show major chord tables. ");
                Intent i = new Intent();
                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.AboutUkuleleActivity");
                i.setComponent(name);
                startActivity(i);
            }
        });

        Button btnTuning = rootView.findViewById(R.id.btnTuning);
        btnTuning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.TuningActivity");
                i.setComponent(name);
                startActivity(i);
            }
        });

        Button btnHowtoReadTAB = rootView.findViewById(R.id.btnHowtoReadTAB);
        btnHowtoReadTAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.HelpReadTabActivity");
                i.setComponent(name);
                startActivity(i);
            }
        });

        Button btnChordTable  = rootView.findViewById(R.id.btnChordTable);
        btnChordTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chordTableFragment chordFragment = new chordTableFragment();
                ((MainActivity) getActivity()).replaceFragment(chordFragment);
            }
        });

        Button btnSetup = rootView.findViewById(R.id.btnSetup);
        btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText( getContext(), "Setup Selected", Toast.LENGTH_LONG).show();
                setupMenuFragment setupMenu = new setupMenuFragment();
                ((MainActivity) getActivity()).replaceFragment(setupMenu);
            }
        });

        return rootView;
    }
}
