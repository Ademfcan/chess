package chessengine.Audio;

import chessserver.ChessRepresentations.ChessMove;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundPlayer {
    private final static Logger logger = LogManager.getLogger("Sound_Player");
    private final double defaultVolume = .5d;
    private final double defaultBGVolume = .3d;

    private boolean isSystemPaused;

    private boolean isEffectsMuted;


    private boolean userPrefBgPaused = true;

    private int songCount = 0;

    private BGMusic currentSong;

    private double currentVolumeEffects;
    private double currentVolumeBackground;


    public SoundPlayer() {
        this.currentVolumeBackground = defaultBGVolume;
        this.currentVolumeEffects = defaultVolume;
        this.currentSong = getNextSong();


    }

    public boolean isEffectsMuted() {
        return this.isEffectsMuted;
    }

    public void setEffectsMuted(boolean effectsMuted) {
        this.isEffectsMuted = effectsMuted;
    }

    public void playMoveEffect(ChessMove move, boolean isChecked, boolean isGameOver) {
        if (isGameOver) {
            playEffect(Effect.GAMEOVER);
        } else if (isChecked) {
            playEffect(Effect.CHECK);
        } else if (move.isEating()) {
            playEffect(Effect.CAPTURE);

        } else if (move.isPawnPromo()) {
            playEffect(Effect.PROMOTE);
        } else if (move.isCastleMove()) {
            playEffect(Effect.CASTLING);
        } else {
            playEffect(Effect.MOVE);
        }
    }

    public void playEffect(Effect effect) {
        if (!isEffectsMuted) {
            effect.playClip(currentVolumeEffects);
        } else {
            logger.debug("Not playing effect, effects are muted");
        }

    }

    private void checkStartNextSong() {
        if (!isSystemPaused) {
            currentSong.clip.play();
        } else {
            logger.debug("Not starting song paused");
        }


    }

    private BGMusic getNextSong() {
        BGMusic Song = BGMusic.values()[songCount];

        Song.clip.setVolume(currentVolumeBackground);
        Song.clip.setOnEndOfMedia(() -> {
            currentSong = getNextSong();
            checkStartNextSong();
        });
        songCount++;
        if (songCount >= BGMusic.values().length) {
            songCount = 0;
        }
        return Song;
    }

    public void pauseSong(boolean isCauseOfUserPref) {
        if (!isSystemPaused && !userPrefBgPaused) {
            currentSong.clip.pause();

        } else {
            logger.debug("Song already paused");
        }

        if (isCauseOfUserPref) {
            userPrefBgPaused = true;
        }
        else{
            isSystemPaused = true;
        }

    }

    public void playSong(boolean isCauseOfUserPref) {
        if (!isSystemPaused || !isCauseOfUserPref) {
            if(isSystemPaused || userPrefBgPaused){
                currentSong.clip.play();
                if(isCauseOfUserPref){
                    userPrefBgPaused = false;
                }
                else  {
                    isSystemPaused = false;
                }
            }
            else {
                logger.debug("Song already playing");
            }

        }


    }

    public boolean isPaused(){
        return isSystemPaused || userPrefBgPaused;
    }
    public boolean getSystemPaused() {
        return isSystemPaused;
    }

    public boolean isUserPrefBgPaused() {
        return userPrefBgPaused;
    }


    // for both of these the
    public void changeVolumeEffects(double newVolume) {
        this.currentVolumeEffects = newVolume;
    }

    public void changeVolumeBackground(double newVolume) {
        this.currentVolumeBackground = newVolume;
        currentSong.clip.setVolume(newVolume);
    }
}
