package com.example.executablelauncher.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Series implements Serializable {
    public static int NextID = 0;
    public final int id;
    public long thetvdbID = 0;
    public String name = "";
    public String category = "";
    public int order = 0;
    public String coverSrc = "";
    public List<Integer> seasons = new ArrayList<>();

    public Series() {
        this.id = NextID++;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverSrc() {
        return coverSrc;
    }

    public void setCoverSrc(String coverSrc) {
        this.coverSrc = coverSrc;
    }

    public List<Integer> getSeasons() {
        return seasons;
    }

    public void addSeason(Season season) {
        this.seasons.add(season.getId());
    }

    public void removeSeason(int id){
        for (int i = 0; i < seasons.size(); i++){
            if (seasons.get(i) == id) {
                seasons.remove(i);
                break;
            }
        }
    }

    public void clearSeasons(){
        seasons.clear();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}