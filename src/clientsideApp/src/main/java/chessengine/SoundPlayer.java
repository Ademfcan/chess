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
        isStopped = false;
        currentSong = BGMusic.values()[songCount];

        currentSong.clip.setVolume(currentVolumeBackground);
        currentSong.clip.play();
        songCount++;
        if(songCount >= BGMusic.values().length){
            songCount = 0;
        }
        currentSong.clip.setOnEndOfMedia(()->{
            if(!isStopped) {
                startBackgroundMusic();
            }
        });

    }

    public void muteBackgroundMusic(){
        isMuted = true;
        stopMusic();
    }

    public void stopMusic(){
        isStopped =true;
        if(currentSong != null){
            currentSong.clip.stop();
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
