package se.deepthot.playlistbot.spotify.track;

import static se.deepthot.playlistbot.spotify.track.TrackType.*;

public class TrackClassifier {

    public static TrackType classify(AudioFeatures audioFeatures){
        if(audioFeatures.getSpeechiness() > 0.20){
            return RAP;
        }
        if(audioFeatures.getValence() > 0.8 || (audioFeatures.getEnergy() > 0.8 && audioFeatures.getTempo() > 130)){
            return PARTY;
        }

        if(audioFeatures.getEnergy() > 0.7 && audioFeatures.getValence() < 0.4){
            return METAL;
        }

        if(audioFeatures.getEnergy() < 0.5){
            return CALM;
        }


        return NOT_SURE;
    }
}
