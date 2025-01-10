package es.cm.dam.dos.pmdm.mivideoplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl{

    private MediaController mc;
    private MediaPlayer mp;
    private Handler h;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Crear el objeto
        mc= new MediaController(this);
        mc.setMediaPlayer(this);

        videoView =findViewById(R.id.videoView);
        // Establecemos el ancho del MediaController
        mc.setAnchorView(videoView);
        // Al contenedor VideoView le añadimos los controles
        videoView.setMediaController(mc);
        //Cargar el contenido multimedia en el videoview
        videoView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.ski));
        videoView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.carretera));
        //streaming http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" "
        //videoView.setVideoURI(Uri.parse("https://www.sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4"));
        //videoView.setVideoURI(Uri.parse("https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_30MB.mp4"));
        h=new Handler();
        // Registramos el callback que será invocado cuando el vídeo esté cargado y preparado
        // Lo hacemos una vez que esté cargado, y que sea de manera asíncrona para mejorar el rendimiento
        // Cuando termina la preparación de los medios, se llama al metodo onPrepared() de MediaPlayer.OnPreparedListener,
        // que se configuró a través de setOnPreparedListener().
        videoView.setOnPreparedListener(mediaPlayer -> h.post(() -> {
            //Ajustar la relación de aspecto
            int videoWidth = mediaPlayer.getVideoWidth();
            int videoHeight = mediaPlayer.getVideoHeight();

            // Obtener las dimensiones de la pantalla
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;

            // Calcular la relación de aspecto
            float videoAspectRatio = (float) videoWidth / videoHeight;
            float screenAspectRatio = (float) screenWidth / screenHeight;

            ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();

            if (videoAspectRatio > screenAspectRatio) {
                // Ajustar el ancho al máximo disponible
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth / videoAspectRatio);
            } else {
                // Ajustar la altura al máximo disponible
                layoutParams.width = (int) (screenHeight * videoAspectRatio);
                layoutParams.height = screenHeight;
            }

            videoView.setLayoutParams(layoutParams);
            mc.show(1000);
            videoView.start();
        }));
    }

    @Override
    public void start() {
        if(!mp.isPlaying())
            mp.start();
    }

    @Override
    public void pause() {
        if(mp.isPlaying())
            mp.pause();
    }

    @Override
    public int getDuration() {
        return mp.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mp.getCurrentPosition();
    }

    @Override
    public void seekTo(int i) {
        mp.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return mp.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return mp.getAudioSessionId();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN)
            if(!mc.isShowing())
                mc.show(0);
            else
                mc.hide();
        return false;
    }
}