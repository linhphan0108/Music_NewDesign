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

    private CircleImageView mCrlImgRotation;
    private Animation mRotationAnimation;
    private boolean isRotated = false;

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
        return inflater.inflate(R.layout.fragment_player_center, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getWidgets(getView());
        loadAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRotationAnimation();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void getWidgets(View root) {
        mCrlImgRotation = (CircleImageView) root.findViewById(R.id.img_rotation);
    }

    private void loadAnimation() {
        mRotationAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.animation_rotation);
    }

    public void startRotationAnimation() {
        if (!isRotated) {
            mCrlImgRotation.clearAnimation();
            mCrlImgRotation.startAnimation(mRotationAnimation);
            isRotated = true;
        }
    }

    public void stopRotationAnimation() {
        mCrlImgRotation.clearAnimation();
        isRotated = false;
    }
}
