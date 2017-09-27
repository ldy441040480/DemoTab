package com.example.administrator.demotab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 *  Created by lidongyang on 2015/7/26.
 */
public class ItemFragment extends Fragment {

    private String mTitle = "TAB";

    public static ItemFragment newInstance(String title) {
        ItemFragment frag = new ItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString("title");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getBodyView();
    }

    private View getBodyView() {
        TextView textView = new TextView(getActivity());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setGravity(Gravity.CENTER);
        textView.setText(mTitle);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(layoutParams);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "-->" + mTitle, Toast.LENGTH_SHORT).show();
            }
        });
        return textView;
    }
}
