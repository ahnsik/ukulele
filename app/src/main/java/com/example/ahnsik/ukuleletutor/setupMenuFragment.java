package com.example.ahnsik.ukuleletutor;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link setupMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class setupMenuFragment extends Fragment {

    private int     hiddenTouchCount = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public setupMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment setupMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static setupMenuFragment newInstance(String param1, String param2) {
        setupMenuFragment fragment = new setupMenuFragment();
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_setup_menu, container, false);

        Button btnReturn = (Button)rootView.findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GuideTopFragment guideTopMenu = new GuideTopFragment();
                ((MainActivity) getActivity()).replaceFragment(guideTopMenu);
            }
        });

        Button btnUpdateList = (Button)rootView.findViewById(R.id.btnUpdateList);
        btnUpdateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( getContext(), "음악데이터를 가져올 Dialog 를 준비 중입니다.", Toast.LENGTH_SHORT).show();
//                Dialog dlgLog;
//                dlgLog = new Dialog(getContext() );
//                dlgLog.setContentView(R.layout.dialog_terminal);
//
//                Button btnClose = dlgLog.findViewById(R.id.btnReturn);
//                btnClose.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dlgLog.dismiss();
//                    }
//                });
//
//                dlgLog.show();
            }
        });

        TextView txtAppTitle = (TextView) rootView.findViewById(R.id.txtAppTitle);
        txtAppTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hiddenTouchCount++;
                Log.d("ukulele", "touch 7th will show the management activity : count=" + hiddenTouchCount);
                if (hiddenTouchCount >= 7) {
                    Intent i = new Intent();
                    ComponentName name= new ComponentName("com.example.ahnsik.ukuleletutor", "com.example.ahnsik.ukuleletutor.TerminalActivity");
                    i.setComponent(name);
                    startActivity(i);
                    hiddenTouchCount = 0;
                }
            }       // end of onClick
        });
        hiddenTouchCount = 0;

        return rootView;
    }
}