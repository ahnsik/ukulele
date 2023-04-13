package com.example.ahnsik.ukuleletutor;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link chordTableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class chordTableFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public chordTableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment chordTableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static chordTableFragment newInstance(String param1, String param2) {
        chordTableFragment fragment = new chordTableFragment();
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
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chord_table, container, false);

        Button btnReturn = rootView.findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GuideTopFragment guideTopMenu = new GuideTopFragment();
                ((MainActivity) getActivity()).replaceFragment(guideTopMenu);
            }
        });

        ///////////////////////////////////////////////////////////////
        Button btnChord_C = (Button)rootView.findViewById(R.id.btnChord_C);
        btnChord_C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordC_Activity");
            }
        });

        Button btnChord_C7 = (Button)rootView.findViewById(R.id.btnChord_C7);
        btnChord_C7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordC7_Activity");
            }
        });

        Button btnChord_Cm = (Button)rootView.findViewById(R.id.btnChord_Cm);
        btnChord_Cm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordCm_Activity");
            }
        });

        Button btnChord_Cm7 = (Button)rootView.findViewById(R.id.btnChord_Cm7);
        btnChord_Cm7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordCm7_Activity");
            }
        });

        ///////////////////////////////////////////////////////////////
        Button btnChord_D = (Button)rootView.findViewById(R.id.btnChord_D);
        btnChord_D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordD_Activity");
            }
        });

        Button btnChord_D7 = (Button)rootView.findViewById(R.id.btnChord_D7);
        btnChord_D7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordD7_Activity");
            }
        });

        Button btnChord_Dm = (Button)rootView.findViewById(R.id.btnChord_Dm);
        btnChord_Dm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordDm_Activity");
            }
        });

        Button btnChord_Dm7 = (Button)rootView.findViewById(R.id.btnChord_Dm7);
        btnChord_Dm7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordDm7_Activity");
            }
        });

        ///////////////////////////////////////////////////////////////
        Button btnChord_E = (Button)rootView.findViewById(R.id.btnChord_E);
        btnChord_E.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordE_Activity");
            }
        });

        Button btnChord_E7 = (Button)rootView.findViewById(R.id.btnChord_E7);
        btnChord_E7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordE7_Activity");
            }
        });

        Button btnChord_Em = (Button)rootView.findViewById(R.id.btnChord_Em);
        btnChord_Em.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordEm_Activity");
            }
        });

        Button btnChord_Em7 = (Button)rootView.findViewById(R.id.btnChord_Em7);
        btnChord_Em7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordEm7_Activity");
            }
        });

        ///////////////////////////////////////////////////////////////
        Button btnChord_F = (Button)rootView.findViewById(R.id.btnChord_F);
        btnChord_F.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordF_Activity");
            }
        });

        Button btnChord_F7 = (Button)rootView.findViewById(R.id.btnChord_F7);
        btnChord_F7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordF7_Activity");
            }
        });

        Button btnChord_Fm = (Button)rootView.findViewById(R.id.btnChord_Fm);
        btnChord_Fm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordFm_Activity");
            }
        });

        Button btnChord_Fm7 = (Button)rootView.findViewById(R.id.btnChord_Fm7);
        btnChord_Fm7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordFm7_Activity");
            }
        });

        ///////////////////////////////////////////////////////////////
        Button btnChord_G = (Button)rootView.findViewById(R.id.btnChord_G);
        btnChord_G.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordG_Activity");
            }
        });

        Button btnChord_G7 = (Button)rootView.findViewById(R.id.btnChord_G7);
        btnChord_G7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordG7_Activity");
            }
        });

        Button btnChord_Gm = (Button)rootView.findViewById(R.id.btnChord_Gm);
        btnChord_Gm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordGm_Activity");
            }
        });

        Button btnChord_Gm7 = (Button)rootView.findViewById(R.id.btnChord_Gm7);
        btnChord_Gm7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordGm7_Activity");
            }
        });

        ///////////////////////////////////////////////////////////////
        Button btnChord_A = (Button)rootView.findViewById(R.id.btnChord_A);
        btnChord_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordA_Activity");
            }
        });

        Button btnChord_A7 = (Button)rootView.findViewById(R.id.btnChord_A7);
        btnChord_A7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordA7_Activity");
            }
        });

        Button btnChord_Am = (Button)rootView.findViewById(R.id.btnChord_Am);
        btnChord_Am.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordAm_Activity");
            }
        });

        Button btnChord_Am7 = (Button)rootView.findViewById(R.id.btnChord_Am7);
        btnChord_Am7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordAm7_Activity");
            }
        });

        ///////////////////////////////////////////////////////////////
        Button btnChord_B = (Button)rootView.findViewById(R.id.btnChord_B);
        btnChord_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordB_Activity");
            }
        });

        Button btnChord_B7 = (Button)rootView.findViewById(R.id.btnChord_B7);
        btnChord_B7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordB7_Activity");
            }
        });

        Button btnChord_Bm = (Button)rootView.findViewById(R.id.btnChord_Bm);
        btnChord_Bm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordBm_Activity");
            }
        });

        Button btnChord_Bm7 = (Button)rootView.findViewById(R.id.btnChord_Bm7);
        btnChord_Bm7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity("ChordBm7_Activity");
            }
        });

        return rootView;
    }

    protected void gotoActivity(String activityName)
    {
        Intent i = new Intent();
//        ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor."+activityName);
        ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.ChildChord_Activity");
        i.setComponent(name);
        i.putExtra("childActivity", activityName );
        startActivity(i);

        //  모든 코드의 설명을 위해 하나의 Activity 를 공유하여 코드를 간결하게 하기위해,  아래 링크를 참고하고 있음.
        //   https://stackoverflow.com/questions/3913592/start-an-activity-with-a-parameter

    }
}
