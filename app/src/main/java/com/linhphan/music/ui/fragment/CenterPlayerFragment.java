package com.linhphan.music.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.linhphan.androidboilerplate.api.FileDownloadWorker;
import com.linhphan.androidboilerplate.callback.DownloadCallback;
import com.linhphan.androidboilerplate.util.Logger;
import com.linhphan.music.R;
import com.linhphan.music.util.ContentManager;

import de.hdodenhof.circleimageview.CircleImageView;

public class CenterPlayerFragment extends BaseFragment implements View.OnClickListener, DownloadCallback{

    private CircleImageView mCrlImgRotation;
    private Animation mRotationAnimation;
    private ImageButton mBtnDownload;

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
        registerEventListener();
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

    //================ all click events will be handled ============================================
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_download:
                DownloadFile();
                break;
        }
    }

    //================== async download callbacks ==================================================
    @Override
    public void onDownloadSuccessfully(Object data) {
        if (data != null && data instanceof String){
            Logger.d(getClass().getName(), "file is stored at "+ String.valueOf(data));
        }
    }

    @Override
    public void onDownloadFailed(Exception e) {
        e.printStackTrace();
    }

    //============== private methods ===============================================================
    private void getWidgets(View root) {
        mCrlImgRotation = (CircleImageView) root.findViewById(R.id.img_rotation);
        mBtnDownload = (ImageButton) root.findViewById(R.id.btn_download);
    }

    private void registerEventListener(){
        mBtnDownload.setOnClickListener(this);
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

    private void DownloadFile(){
        String url = ContentManager.getInstance().getCurrentPlayingSongPath();
        FileDownloadWorker fileDownloadWorker = new FileDownloadWorker(getContext(), this);
        fileDownloadWorker.showProgressbar(true, false);
        fileDownloadWorker.execute(url);
    }
}
