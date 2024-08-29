package chessengine;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public enum BGMusic {

    BACKGROUND1("/BackgroundMusic/thejazzpiano.mp3") {
        @Override
        public MediaPlayer getMedia() {
            return this.clip;
        }
    },
    BACKGROUND2("/BackgroundMusic/ethereal88-rising-dawn.mp3") {
        @Override
        public MediaPlayer getMedia() {
            return this.clip;
        }
    },
    BACKGROUND3("/BackgroundMusic/fscm-productions-flowers.mp3") {
        @Override
        public MediaPlayer getMedia() {
            return this.clip;
        }
    };

    public MediaPlayer clip;


    BGMusic(String path) {
        this.clip = new MediaPlayer(new Media(getClass().getResource(path).toExternalForm()));
    }

    public abstract MediaPlayer getMedia();
}
