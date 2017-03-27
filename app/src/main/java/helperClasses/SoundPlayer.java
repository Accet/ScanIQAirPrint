package helperClasses;

import android.content.Context;
import android.media.MediaPlayer;

import net.scaniq.scaniqairprint.R;

/**
 * Created by savanpatel on 2017-02-28.
 */

public class SoundPlayer {

    private Context context = null;
//    private static SoundPlayer soundPlayer = null;
    MediaPlayer mp;

    public SoundPlayer(Context context) {
        this.context = context;
         mp = new MediaPlayer();
    }

//    public SoundPlayer getInstance(Context context)
//    {
//        if( soundPlayer == null )
//        {
//            soundPlayer = new SoundPlayer(context);
//        }
//        return soundPlayer;
//    }

    public void playPagesSound(int number) {
        switch(number){
            case 2: playSound(R.raw.pagesscanned2); break;
            case 3: playSound(R.raw.pagesscanned3); break;
            case 1: playSound(R.raw.pagesscanned1); break;
            case 4: playSound(R.raw.pagesscanned4); break;
            case 5: playSound(R.raw.pagesscanned5); break;
            case 6: playSound(R.raw.pagesscanned6); break;
            case 7: playSound(R.raw.pagesscanned7); break;
            case 8: playSound(R.raw.pagesscanned8); break;
            case 9: playSound(R.raw.pagesscanned9); break;
            case 10: playSound(R.raw.pagesscanned10); break;
            default: break;
        }
    }


    public void playFilesSound(int number){
        switch(number){
            case 0: playSound(R.raw.nofilestosend); break;
            case 1: playSound(R.raw.onefilestosend); break;
            case 2: playSound(R.raw.twofilestosend); break;
            case 3: playSound(R.raw.threefilestosend); break;
            case 4: playSound(R.raw.fourfilestosend); break;
            case 5: playSound(R.raw.fivefilestosend); break;
            case 6: playSound(R.raw.sixfilestosend); break;
            case 7: playSound(R.raw.sevenfilestosend); break;
            case 8: playSound(R.raw.eightfilestosend); break;
            case 9: playSound(R.raw.ninefilestosend); break;
            case 10: playSound(R.raw.tenfilestosend); break;
            default: break;
        }

    }
    private void playSound (int sound) {

        mp = MediaPlayer.create(context, sound);
        if (mp != null){
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    //
                    mp.release();
                }
            });
            mp.start();
        }
    }
}