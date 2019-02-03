package se.deepthot.playlistbot.spotify.track;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AudioFeatures {
    private double acousticness;
    private double danceability;
    private double duration_ms;
    private double energy;
    private double instrumentalness;
    private int key;
    private double liveness;
    private double loudness;
    private int mode;
    private double speechiness;
    private double tempo;
    private double valence;
    private int time_signature;

    public void setAcousticness(double acousticness) {
        this.acousticness = acousticness;
    }

    public void setDanceability(double danceability) {
        this.danceability = danceability;
    }

    public void setDuration_ms(double duration_ms) {
        this.duration_ms = duration_ms;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public void setInstrumentalness(double instrumentalness) {
        this.instrumentalness = instrumentalness;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public void setLiveness(double liveness) {
        this.liveness = liveness;
    }

    public void setLoudness(double loudness) {
        this.loudness = loudness;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setSpeechiness(double speechiness) {
        this.speechiness = speechiness;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public void setValence(double valence) {
        this.valence = valence;
    }

    public void setTime_signature(int time_signature) {
        this.time_signature = time_signature;
    }

    public double getAcousticness() {
        return acousticness;
    }

    public double getDanceability() {
        return danceability;
    }

    public double getDuration_ms() {
        return duration_ms;
    }

    public double getEnergy() {
        return energy;
    }

    public double getInstrumentalness() {
        return instrumentalness;
    }

    public int getKey() {
        return key;
    }

    public double getLiveness() {
        return liveness;
    }

    public double getLoudness() {
        return loudness;
    }

    public int getMode() {
        return mode;
    }

    public double getSpeechiness() {
        return speechiness;
    }

    public double getTempo() {
        return tempo;
    }

    public double getValence() {
        return valence;
    }

    public int getTime_signature() {
        return time_signature;
    }
    

}
