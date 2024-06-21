package chessengine;

public class SoundPlayer {
    private final double defaultVolume = 1;
    private final double defaultBGVolume = .1d;

    private int songCount = 0;

    private BGMusic currentSong;

    private double currentVolumeEffects;
    private double currentVolumeBackground;



    public SoundPlayer(double volumeEffects,double volumeBackground){
        this.currentVolumeBackground = volumeBackground;
        this.currentVolumeEffects = volumeEffects;
    }
    public SoundPlayer(){
        this.currentVolumeBackground = defaultBGVolume;
        this.currentVolumeEffects = defaultVolume;


    }



    public void playEffect(Effect effect){
        effect.playClip(currentVolumeEffects);

    }
    private boolean isStopped = false;
    public void startBackgroundMusic(){
        if(isStopped){
            isStopped = false;
            if(currentSong == null){
                currentSong = getNextSong();
            }
            currentSong.clip.setOnEndOfMedia(()->{
//                if(!isStopped) {
//                    startBackgroundMusic();
//                }
//                else{
//                    currentSong
//                }
            });
        }
        else{
            ChessConstants.mainLogger.debug("Music already playing");
        }


    }

    private BGMusic getNextSong(){
        BGMusic Song = BGMusic.values()[songCount];

        Song.clip.setVolume(currentVolumeBackground);
        songCount++;
        if(songCount >= BGMusic.values().length){
            songCount = 0;
        }
        return currentSong;
    }

    public void stopMusic(){
        if(!isStopped){
            isStopped =true;
            if(currentSong != null){
                currentSong.clip.pause();
            }
            else{
                ChessConstants.mainLogger.error("Null song trying to stop");
            }
        }
        else{
            ChessConstants.mainLogger.debug("Music Already stopped");
        }


    }
    private boolean isMuted = false;
    public boolean toggleAudio(){
        if(isMuted){
            isMuted = false;
            startBackgroundMusic();
        }
        else{
            isMuted = true;
            stopMusic();
        }
        return isMuted;
    }




    public void changeVolumeEffects(double newVolume){
        this.currentVolumeEffects = newVolume;
    }
    public void changeVolumeBackground(double newVolume){
        this.currentVolumeBackground = newVolume;
    }
}
