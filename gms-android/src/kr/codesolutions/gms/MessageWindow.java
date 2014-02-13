package kr.codesolutions.gms;

import java.io.IOException;

import kr.codesolutions.gms.gcm.GCM;
import kr.codesolutions.gms.gcm.GCMConstants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class MessageWindow extends Activity implements Button.OnClickListener {
	GCM gcm;
	String messageId = "";
	String ownType = "";
	String msgType = "";
	String registrationId = "";
	String content = "";
	MediaPlayer mediaPlayer;
	Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		// Below code will disable locked screen but KeyguardLock is deprecated.		
//		KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//      KeyguardLock kl = km.newKeyguardLock("name");
//      kl.disableKeyguard();
        
		setContentView(R.layout.activity_message_window);

		gcm = new GCM(this);
		registrationId = gcm.getRegistrationId(this.getApplicationContext());

		Intent intent = getIntent();
		String subject = intent.getStringExtra(GCMConstants.GMS_MESSAGE_SUBJECT);
		if(subject != null){
			this.setTitle(subject);
		}else{
			this.setTitle(getString(R.string.GMS_MESSAGEWINDOW_TITLE));
		}
		messageId = intent.getStringExtra(GCMConstants.GMS_MESSAGE_ID);
		ownType = intent.getStringExtra(GCMConstants.GMS_MESSAGE_OWNTYPE); // '0':개인메시지, '1':공지메시지
		msgType = intent.getStringExtra(GCMConstants.GMS_MESSAGE_MSGTYPE); // '0':일반메시지, '1':콜백메시지
		content = intent.getStringExtra(GCMConstants.GMS_MESSAGE_CONTENT);
		
		WebView webView = (WebView)findViewById(R.id.message);
		webView.setWebViewClient(new MyWebClient());
		webView.loadData(content, "text/html", "UTF-8");
		Button btnOk = (Button)findViewById(R.id.btnOk);
		btnOk.setOnClickListener(this);
		
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		int dot = 200;      // Length of a Morse Code "dot" in milliseconds
		int dash = 500;     // Length of a Morse Code "dash" in milliseconds
		int short_gap = 200;    // Length of Gap Between dots/dashes
		int medium_gap = 500;   // Length of Gap Between Letters
		int long_gap = 1000;    // Length of Gap Between Words
		long[] pattern = {
		    0,  // Start immediately
		    dot, short_gap, dot, short_gap, dot,    // s
		    medium_gap,
		    dash, short_gap, dash, short_gap, dash, // o
		    medium_gap,
		    dot, short_gap, dot, short_gap, dot,    // s
		    long_gap
		};
	    vibrator.vibrate(pattern, -1);
		
	    AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
	    audioManager.setStreamVolume (AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);	    
		mediaPlayer = MediaPlayer.create(this, R.raw.ambulance);
		mediaPlayer.setLooping(true);
		mediaPlayer.start();
	}
	
	@Override
	public void onAttachedToWindow() {
        Window window = getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                + WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                + WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onAttachedToWindow();
    }	
	
	@Override
	protected void onDestroy(){
		// Callback 요청메시지인 경우 읽음 처리
		if("1".equals(msgType)){
			readInBackground(registrationId, messageId, ownType);
		}
		super.onDestroy();
       if (mediaPlayer != null) {
    	   mediaPlayer.stop();
    	   mediaPlayer.release();
    	   mediaPlayer = null;
       }
       if (vibrator != null) {
    	   vibrator.cancel();
    	   vibrator = null;
       }
	}
	
	public boolean onTouch(View v, MotionEvent event){
	    if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
	        finish();
	        return true;
	      }

	      // Delegate everything else to Activity.
	      return super.onTouchEvent(event);
	}
	public void onClick(View v){
		if(v.getId() == R.id.btnOk){
			finish();
		}
	}

    public void readInBackground(final String registrationId, final String messageId, final String ownType) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
					// 메시지 읽음 처리
                	gcm.readOnServer(registrationId, messageId, ownType);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);
    }

    class MyWebClient extends WebViewClient {
    	public boolean shouldOverrideUrlLoading(WebView view, String url) {
    	    if( url.startsWith("http:") || url.startsWith("https:") ) {
    	        return false;
    	    }else if (url.startsWith("tel:")) { 
	    	    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
	    	    startActivity( intent ); 
    	    }else{
        	    // Otherwise allow the OS to handle it
        	    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        	    startActivity( intent ); 
    	    }
    	    return true;
    	}
    }

}
