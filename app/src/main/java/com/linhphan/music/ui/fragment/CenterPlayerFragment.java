package com.linhphan.music.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.linhphan.music.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CenterPlayerFragment extends BaseFragment {

    CircleImageView mCrlImgRotation;

    public static CenterPlayerFragment newInstance(String param1, String param2) {
        return new CenterPlayerFragment();
    }

    public CenterPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_player_center, container, false);

        mCrlImgRotation = (CircleImageView) rootView.findViewById(R.id.img_rotation);
        Animation rotationAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.animation_rotation);
        mCrlImgRotation.setAnimation(rotationAnimation);

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
