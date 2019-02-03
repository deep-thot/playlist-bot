package se.deepthot.playlistbot.spotify.track;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AudioFeatures {
    private final double acousticness;
    private final double danceability;
    private final double duration_ms;
    private final double energy;
    private final double instrumentalness;
    private final int key;
    private final double liveness;
    private final double loudness;
    private final int mode;
    private final double speechiness;
    private final double tempo;
    private final double valence;
    private final int time_signature;

    public AudioFeatures(double acousticness, double danceability, double duration_ms, double energy, double instrumentalness, int key, double liveness, double loudness, int mode, double speechiness, double tempo, double valence, int time_signature) {
        this.acousticness = acousticness;
        this.danceability = danceability;
        this.duration_ms = duration_ms;
        this.energy = energy;
        this.instrumentalness = instrumentalness;
        this.key = key;
        this.liveness = liveness;
        this.loudness = loudness;
        this.mode = mode;
        this.speechiness = speechiness;
        this.tempo = tempo;
        this.valence = valence;
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
    
    public static Builder newBuilder(){
        return new Builder();
    }



    public static class Builder {
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

        public Builder withAcousticness(double acousticness) {
            this.acousticness = acousticness;
            return this;
        }

        public Builder withDanceability(double danceability) {
            this.danceability = danceability;
            return this;
        }

        public Builder withDuration_ms(double duration_ms) {
            this.duration_ms = duration_ms;
            return this;
        }

        public Builder withEnergy(double energy) {
            this.energy = energy;
            return this;
        }

        public Builder withInstrumentalness(double instrumentalness) {
            this.instrumentalness = instrumentalness;
            return this;
        }

        public Builder withKey(int key) {
            this.key = key;
            return this;
        }

        public Builder withLiveness(double liveness) {
            this.liveness = liveness;
            return this;
        }

        public Builder withLoudness(double loudness) {
            this.loudness = loudness;
            return this;
        }

        public Builder withMode(int mode) {
            this.mode = mode;
            return this;
        }

        public Builder withSpeechiness(double speechiness) {
            this.speechiness = speechiness;
            return this;
        }

        public Builder withTempo(double tempo) {
            this.tempo = tempo;
            return this;
        }

        public Builder withValence(double valence) {
            this.valence = valence;
            return this;
        }

        public Builder withTime_signature(int time_signature) {
            this.time_signature = time_signature;
            return this;
        }

        public AudioFeatures build(){
            return new AudioFeatures(acousticness, danceability, duration_ms, energy, instrumentalness, key, liveness, loudness, mode, speechiness, tempo, valence, time_signature);
        }
    }
}
