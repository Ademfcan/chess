package chessengine.Audio;

import javafx.scene.media.AudioClip;

public enum Effect {
    GAMESTART("/AudioAssets/game-start.mp3") {
        @Override
        public AudioClip getClip() {
            return this.clip;
        }

        @Override
        public void playClip(double volume) {
            this.clip.play(volume);
        }
    },
    CHECK("/AudioAssets/check.mp3") {
        @Override
        public AudioClip getClip() {
            return this.clip;
        }

        @Override
        public void playClip(double volume) {
            this.clip.play(volume);
        }
    },
    MOVE("/AudioAssets/move.mp3") {
        @Override
        public AudioClip getClip() {
            return this.clip;
        }

        @Override
        public void playClip(double volume) {
            this.clip.play(volume);
        }
    },
    PROMOTE("/AudioAssets/promote.mp3") {
        @Override
        public AudioClip getClip() {
            return this.clip;
        }

        @Override
        public void playClip(double volume) {
            this.clip.play(volume);
        }
    },
    GAMEOVER("/AudioAssets/game-end.mp3") {
        @Override
        public AudioClip getClip() {
            return this.clip;
        }

        @Override
        public void playClip(double volume) {
            this.clip.play(volume);
        }
    },
    MESSAGE("/AudioAssets/notify.mp3") {
        @Override
        public AudioClip getClip() {
            return this.clip;
        }

        @Override
        public void playClip(double volume) {
            this.clip.play(volume);
        }
    },
    TIMEWARNING("/AudioAssets/timewarning.mp3") {
        @Override
        public AudioClip getClip() {
            return this.clip;
        }

        @Override
        public void playClip(double volume) {
            this.clip.play(volume);
        }
    },
    ILLEGALMOVE("/AudioAssets/illegal.mp3") {
        @Override
        public AudioClip getClip() {
            return this.clip;
        }

        @Override
        public void playClip(double volume) {
            // cap illegal move sound as to not be annoying
            this.clip.play(Math.max(volume, .5));
        }
    },
    CASTLING("/AudioAssets/castle.mp3") {
        @Override
        public AudioClip getClip() {
            return this.clip;
        }

        @Override
        public void playClip(double volume) {
            this.clip.play(volume);
        }
    },
    CAPTURE("/AudioAssets/capture.mp3") {
        @Override
        public AudioClip getClip() {
            return this.clip;
        }

        @Override
        public void playClip(double volume) {
            this.clip.play(volume);
        }
    };


    public AudioClip clip;

    Effect(String path) {
        this.clip = new AudioClip(getClass().getResource(path).toExternalForm());
    }

    public abstract AudioClip getClip();

    public abstract void playClip(double volume);
}
