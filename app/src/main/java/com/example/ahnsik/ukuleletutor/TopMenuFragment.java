package com.example.ahnsik.ukuleletutor;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopMenuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TopMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TopMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopMenuFragment newInstance(String param1, String param2) {
        TopMenuFragment fragment = new TopMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_top_menu , container, false);

        Button btnGuideTop = rootView.findViewById(R.id.btnGuideTop);
        btnGuideTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GuideTopFragment guideTopMenu = new GuideTopFragment();
                ((MainActivity) getActivity()).replaceFragment(guideTopMenu);
            }
        });

//        Button btnTraining = rootView.findViewById(R.id.btnTraining);
//        btnTraining.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent();
//                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.FileSelectorActivity");
//                i.setComponent(name);
//                i.putExtra("mode", "TrainingMode");
//                startActivity(i);
//            }
//        });

        Button btnStrunmTraining = rootView.findViewById(R.id.btnStrumTraining);
        btnStrunmTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent();
//                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.FileSelectorActivity");
//                i.setComponent(name);
//                i.putExtra("mode", "TrainingMode");
//                startActivity(i);
                Toast.makeText( getContext(), "준비중입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnChordTraining = rootView.findViewById(R.id.btnChordTraining);
        btnChordTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent();
//                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.FileSelectorActivity");
//                i.setComponent(name);
//                i.putExtra("mode", "TrainingMode");
//                startActivity(i);
                Toast.makeText( getContext(), "구상중입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnPlaying = rootView.findViewById(R.id.btnPlaying);
        btnPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.FileSelectorActivity");
                i.setComponent(name);
                i.putExtra("mode", "PlayingMode");
                startActivity(i);
            }
        });
        return rootView;
    }
}