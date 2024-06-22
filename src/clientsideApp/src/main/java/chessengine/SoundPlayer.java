package chessengine;

public class SoundPlayer {
    private final double defaultVolume = 1;
    private final double defaultBGVolume = .1d;

    private boolean isPaused;



    private boolean userPrefPaused;

    private int songCount = 0;

    private BGMusic currentSong;

    private double currentVolumeEffects;
    private double currentVolumeBackground;



    public SoundPlayer(double volumeEffects,double volumeBackground){
        this.currentVolumeBackground = volumeBackground;
        this.currentVolumeEffects = volumeEffects;
        this.currentSong = getNextSong();
        this.isPaused = false;

    }
    public SoundPlayer(){
        this.currentVolumeBackground = defaultBGVolume;
        this.currentVolumeEffects = defaultVolume;
        this.currentSong = getNextSong();
        this.isPaused = false;
        


    }



    public void playEffect(Effect effect){
        effect.playClip(currentVolumeEffects);

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
            userPrefPaused = true;
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
            userPrefPaused = false;
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

    public boolean isUserPrefPaused() {
        return userPrefPaused;
    }




    public void changeVolumeEffects(double newVolume){
        this.currentVolumeEffects = newVolume;
    }
    public void changeVolumeBackground(double newVolume){
        this.currentVolumeBackground = newVolume;
    }
}
