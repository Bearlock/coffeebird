package coffeebirds;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ben on 11/21/15.
 */
public class NLPResponseObj {
    @JsonProperty("results")
    private LinkedHashMap<String,Object> results;

    public LinkedHashMap<String,Object> getResults(){
        return results;
    }

    public void setResults(LinkedHashMap<String,Object> results){
        this.results = results;
    }

    @JsonProperty("numOfClusters")
    private long numOfClusters;

    public long getNumOfCluster(){
        return numOfClusters;
    }

    public void setNumOfClusters(long numOfClusters){
        this.numOfClusters = numOfClusters;
    }

    @JsonProperty("clusters")
    private ArrayList<Object> clusters;

    public void setClusters(ArrayList<Object> clusters){
        this.clusters = clusters;
    }

    public ArrayList<Object> getClusters(){
        return clusters;
    }

    @JsonProperty("clusterScore")
    private double clusterScore;

    public void setClusterScore(double clusterScore){
        this.clusterScore = clusterScore;
    }

    public double getClusterScore(){
        return clusterScore;
    }

    public String toString(){
        return ("" + getClusters() + getClusterScore());
    }




}
