package com.linhphan.music.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.linhphan.music.R;

/**
 * Created by linhphan on 11/27/15.
 */
public class SingleChoiceFragment extends DialogFragment{

    private final static String ARG_ITEM_LIST = "ARG_ITEM_LIST";

    private ListView mLvSingleChoice;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    private String[] mListItem;

    public static DialogFragment newInstance(String[] list){
        SingleChoiceFragment dialog = new SingleChoiceFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray(ARG_ITEM_LIST, list);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            mListItem = getArguments().getStringArray(ARG_ITEM_LIST);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_single_choice, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        getWidgets(getView());
        setupListView();
    }

    private void getWidgets(View root){
        mLvSingleChoice = (ListView) root.findViewById(R.id.list_view_single_choice);
    }

    private void setupListView(){
        MyAdapter myAdapter = new MyAdapter(mListItem);
        mLvSingleChoice.setOnItemClickListener(mOnItemClickListener);
        mLvSingleChoice.setAdapter(myAdapter);
    }

    public SingleChoiceFragment setOnItemClickListener(AdapterView.OnItemClickListener callback){
        this.mOnItemClickListener = callback;
        return this;
    }

    //=========== list view's adapter ==============================================================
    class MyAdapter extends BaseAdapter{
        private String[] itemList;
        public MyAdapter(String[] list) {
            this.itemList = list;
        }

        @Override
        public int getCount() {
            if (itemList != null) {
                return itemList.length;
            }else{
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            if (itemList != null && itemList.length > position){
                return itemList[position];
            }else{
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
            String path = itemList[position];
            String title = path.substring(path.indexOf('[')+ 1, path.indexOf(']'));
            title = title.substring(0, 3) +"-"+ title.substring(3, title.length());
            textView.setText(title);
            return convertView;
        }
    }
}
