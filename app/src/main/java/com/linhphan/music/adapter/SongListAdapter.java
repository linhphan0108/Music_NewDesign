package com.linhphan.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linhphan.music.R;
import com.linhphan.music.model.SongModel;

import java.util.ArrayList;

/**
 * Created by linhphan on 10/22/15.
 */
public class SongListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SongModel> songs;

    public SongListAdapter(Context context, ArrayList<SongModel> songs) {
        this.songs = songs;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (songs == null)
            return 0;
        else return songs.size();
    }

    @Override
    public Object getItem(int position) {
        if (songs == null || songs.size() <= position) return null;
        else return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * clear songs list
     */
    public void clear(){
        songs.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
            holder = new ViewHolder();
            holder.txtSongTitle = (TextView) convertView.findViewById(R.id.txt_song_title);
            holder.txtArtistName = (TextView) convertView.findViewById(R.id.txt_artist_name);
            convertView.setTag(holder);
        }

        if (holder == null)
            holder = (ViewHolder) convertView.getTag();
        holder.txtSongTitle.setText(songs.get(position).getTitle());
        holder.txtArtistName.setText(songs.get(position).getArtist());

        return convertView;
    }

    class ViewHolder{
        TextView txtSongTitle;
        TextView txtArtistName;
    }
}
