package com.linhphan.music.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.linhphan.androidboilerplate.api.FileDownloadWorker;
import com.linhphan.androidboilerplate.callback.DownloadCallback;
import com.linhphan.androidboilerplate.util.Logger;
import com.linhphan.androidboilerplate.util.TextUtil;
import com.linhphan.music.R;
import com.linhphan.music.data.model.SongModel;
import com.linhphan.music.ui.dialog.SingleChoiceFragment;
import com.linhphan.music.util.ContentManager;


import de.hdodenhof.circleimageview.CircleImageView;

public class CenterPlayerFragment extends BaseFragment implements View.OnClickListener, DownloadCallback, AdapterView.OnItemClickListener{

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
                showSingleChoiceDialog();
                break;
        }
    }

    //================== async download callbacks ==================================================
    @Override
    public void onDownloadSuccessfully(Object data) {
        if (data != null && data instanceof String){
            Logger.d(getClass().getName(), "file is stored at "+ String.valueOf(data));
            Toast.makeText(getContext(), "downloaded file was stored at "+ String.valueOf(data), Toast.LENGTH_SHORT).show();
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

    /**
     * download a song at specified url
     * if downloading is successfully then it will be stored in the public directory in storage.
     */
    private void DownloadFile(String url){
        ContentManager contentManager = ContentManager.getInstance();
        String fileName = contentManager.getCurrentPlayingSongTitle().trim();
        fileName = TextUtil.removeAccent(fileName);
        fileName += url.substring(url.length()-4, url.length());
        final FileDownloadWorker fileDownloadWorker = new FileDownloadWorker(getContext(), true, this);
        fileDownloadWorker
                .setDialogMessage(getContext().getString(R.string.wait_downloading))
                .setHorizontalProgressbar()
                .setDialogCancelCallback(getContext().getString(R.string.hide), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fileDownloadWorker.showNotificationProgress();
                    }
                });
        fileDownloadWorker.execute(url, fileName);
    }

    private void showSingleChoiceDialog(){
        ContentManager contentManager = ContentManager.getInstance();
        SongModel songModel = contentManager.getCurrentPlayingSong();
        SingleChoiceFragment dialog = (SingleChoiceFragment) SingleChoiceFragment.newInstance(songModel.getDirectlyDownloadPath());
        dialog.setOnItemClickListener(this);
        dialog.show(getFragmentManager(), SingleChoiceFragment.class.getName());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ContentManager contentManager = ContentManager.getInstance();
        SongModel songModel = contentManager.getCurrentPlayingSong();
        String directLink = songModel.getDirectlyDownloadPath().get(position);
        DownloadFile(directLink);

        Fragment fragment = getFragmentManager().findFragmentByTag(SingleChoiceFragment.class.getName());
        if (fragment instanceof SingleChoiceFragment){
            SingleChoiceFragment singleChoiceFragment = (SingleChoiceFragment) fragment;
            singleChoiceFragment.dismiss();
        }
    }
}
