package com.linhphan.music.model;

import java.io.Serializable;

/**
 * Created by linhphan on 10/22/15.
 */
public class SongModel implements Serializable {
    private static final long serialVersionUID = 11111L;
    private String originPath;
    private String path;
    private String title;
    private String artist;
    private String composer;
    private String album;
    private String coverPath;
    private String lyrics;
    private String downloaded;
    private String viewed;
    private String year;
    private int duration;


    public String getLyrics() {
        return lyrics;
    }


    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }


    public SongModel() {
    }


    public SongModel(String name, String artist, String path, String originPath) {
        this.title = name;
        this.artist = artist;
        this.path = path;
        this.originPath = originPath;
    }


    public void cloneSong(SongModel songModel) {
        this.title = songModel.getTitle();
        this.artist = songModel.getArtist();
        this.path = songModel.getPath();
        this.originPath = songModel.getOriginPath();
        this.composer = songModel.getComposer();
        this.album = songModel.getAlbum();
        this.coverPath = songModel.getCoverPath();
        this.lyrics = songModel.getLyrics();
        this.downloaded = songModel.downloaded;
        this.viewed = songModel.getViewed();
        this.year = songModel.getYear();
    }


    public String getTitle() {
        return title;
    }


    public String getArtist() {
        return artist;
    }


    public String getPath() {
        return path;
    }


    public String getOriginPath() {
        return originPath;
    }


    public String getComposer() {
        return composer;
    }


    public String getYear() {
        return year;
    }


    public void setYear(String year) {
        this.year = year;
    }


    public void setComposer(String composer) {
        this.composer = composer;
    }


    public String getAlbum() {
        return album;
    }


    public void setAlbum(String album) {
        this.album = album;
    }


    public String getCoverPath() {
        return coverPath;
    }


    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }


    public String getDownloaded() {
        return downloaded;
    }


    public void setDownloaded(String downloaded) {
        this.downloaded = downloaded;
    }


    public String getViewed() {
        return viewed;
    }


    public void setViewed(String view) {
        this.viewed = view;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
