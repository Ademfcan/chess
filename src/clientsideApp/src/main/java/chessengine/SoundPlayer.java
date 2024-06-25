package chessengine;

public class SoundPlayer {
    private final double defaultVolume = 1;
    private final double defaultBGVolume = .1d;

    private boolean isPaused;

    private boolean isEffectsMuted;



    private boolean userPrefBgPaused;

    private int songCount = 0;

    private BGMusic currentSong;

    private double currentVolumeEffects;
    private double currentVolumeBackground;



    public SoundPlayer(double volumeEffects,double volumeBackground){
        this.currentVolumeBackground = volumeBackground;
        this.currentVolumeEffects = volumeEffects;
        this.currentSong = getNextSong();
        this.isPaused = true;

    }
    public SoundPlayer(){
        this.currentVolumeBackground = defaultBGVolume;
        this.currentVolumeEffects = defaultVolume;
        this.currentSong = getNextSong();
        this.isPaused = true;



    }

    public void setEffectsMuted(boolean effectsMuted){
        this.isEffectsMuted = effectsMuted;
    }
    public boolean isEffectsMuted(){
        return this.isEffectsMuted;
    }


    public void playEffect(Effect effect){
        if(!isEffectsMuted){
            effect.playClip(currentVolumeEffects);
        }
        else{
            ChessConstants.mainLogger.debug("Not playing effect, effects are muted");
        }

    }
    private void checkStartNextSong(){
        if(!isPaused){
            currentSong.clip.play();
        }
        else{
            ChessConstants.mainLogger.debug("Not starting song paused");
        }


    }

    private BGMusic getNextSong(){
        BGMusic Song = BGMusic.values()[songCount];

        Song.clip.setVolume(currentVolumeBackground);
        Song.clip.setOnEndOfMedia(()->{
            currentSong = getNextSong();
            checkStartNextSong();
        });
        songCount++;
        if(songCount >= BGMusic.values().length){
            songCount = 0;
        }
        return Song;
    }

    public void pauseSong(boolean isCauseOfUserPref) {
        if(isCauseOfUserPref){
            userPrefBgPaused = true;
        }
        if(!isPaused){
            currentSong.clip.pause();
            isPaused = true;
        }
        else{
            ChessConstants.mainLogger.debug("Song already paused");
        }
    }

    public void playSong(boolean isCauseOfUserPref){
        if(isCauseOfUserPref){
            userPrefBgPaused = false;
        }
        if(isPaused){
            currentSong.clip.play();
            isPaused = false;
        }
        else{
            ChessConstants.mainLogger.debug("Song already playing");
        }
    }

    public boolean getPaused(){
        return isPaused;
    }

    public boolean isUserPrefBgPaused() {
        return userPrefBgPaused;
    }




    public void changeVolumeEffects(double newVolume){
        this.currentVolumeEffects = newVolume;
    }
    public void changeVolumeBackground(double newVolume){
        this.currentVolumeBackground = newVolume;
        currentSong.clip.setVolume(newVolume);
    }
}
