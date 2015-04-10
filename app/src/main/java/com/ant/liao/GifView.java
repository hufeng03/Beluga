package com.ant.liao;


import com.hufeng.filemanager.utils.LogUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
//import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * GifView<br>
 * 本类可以显示一个gif动画，其使用方法和android的其它view（如imageview)一样。<br>
 * 如果要显示的gif太大，会出现OOM的问题。缓冲到tmp
 * @author liao
 *
 */
public class GifView extends ImageView implements GifAction{

    private static final int STOP_MESSAGE_DELAYED_TIME = 500;

    private static final String TAG = "GifView";
    
	/**gif解码器*/
	private GifDecoder gifDecoder = null;
	/**当前要画的帧的图*/
	private Bitmap currentImage = null;
	
	private boolean isRun = true;
	
	private boolean pause = false;

	private DrawThread drawThread = null;
	
	private Context context = null;
	
	private boolean cacheImage = false;
	
	private View backView = null;
	
	private GifImageType animationType = GifImageType.SYNC_DECODER;
	
	private int repeatMode = REPEAT_MODE_ONCE;
	
	private Handler stopHandler;
	
	private boolean isPlaying;
	
	private boolean playAfterDecode;
	
	private String absFileName;
	
	private String decodingFileName;
	
	private boolean needScale = false;

	/**
	 * 解码过程中，Gif动画显示的方式<br>
	 * 如果图片较大，那么解码过程会比较长，这个解码过程中，gif如何显示
	 * @author liao
	 *
	 */
	public enum GifImageType{
		/**
		 * 在解码过程中，不显示图片，直到解码全部成功后，再显示
		 */
		WAIT_FINISH (0),
		/**
		 * 和解码过程同步，解码进行到哪里，图片显示到哪里
		 */
		SYNC_DECODER (1),
		/**
		 * 在解码过程中，只显示第一帧图片
		 */
		COVER (2);
		
		GifImageType(int i){
			nativeInt = i;
		}
		final int nativeInt;
	}
	
	public static final int REPEAT_MODE_ONCE  = 0;
	public static final int REPEAT_MODE_INFINIT  = 1;
	
	
	
	public GifView(Context context) {
        super(context);
        this.context = context;
        //gifDecoder = new GifDecoder(this);
        setScaleType(ImageView.ScaleType.FIT_XY);
    }
    
    public GifView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        
    }  
    
    public GifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
       // TypedArray a = context.obtainStyledAttributes(attrs,R.array.);
        //gifDecoder = new GifDecoder(this);
        setScaleType(ImageView.ScaleType.FIT_XY);
    }
    
    public void setRepeatMode(int mode){
        repeatMode = mode;
    }
    
    
    /**
     * 设置图片，并开始解码
     * @param gif 要设置的图片
     */
    private void setGifDecoderImage(byte[] gif){

        if(gifDecoder == null){
            gifDecoder = new GifDecoder(this);
        }
        gifDecoder.setGifImage(gif);
    	gifDecoder.start();
    }
    
    /**
     * 设置图片，开始解码
     * @param is 要设置的图片
     */
    private void setGifDecoderImage(InputStream is){

//        if(gifDecoder == null){
        if(gifDecoder != null){
            gifDecoder.free();
        }
            gifDecoder = new GifDecoder(this);
            gifDecoder.setGifImage(is);
            gifDecoder.start();
//        }
        
    	
    	
    }
    
    /**
     * 把本Gif动画设置为另外view的背景
     * @param v 要使用gif作为背景的view
     */
    public void setAsBackground(View v){
        backView = v;
    }
    
    protected Parcelable onSaveInstanceState() {
    	super.onSaveInstanceState();
//    	if(gifDecoder != null)
//    		gifDecoder.free();
    	
		return null;
	}
    
    /**
     * @hide
     * 设置缓存图片<br>
     * 如果缓存图片，每一Frame的间隔太快的话，会出现跳帧的现象<br>
     * 如果设置了缓存图片，则你必须调用destroy来作缓存图片的清理。
     */   
//    public void setCahceImage(){
//        if(gifDecoder == null){
//            gifDecoder = new GifDecoder(this);
//        }
//        cacheImage = true;
//        gifDecoder.setCacheImage(true, context);
//    }
    
    
    /**
     * 以字节数据形式设置gif图片
     * @param gif 图片
     */
    public void setGifImage(byte[] gif){
    	setGifDecoderImage(gif);
    }
    
    /**
     * 以字节流形式设置gif图片
     * @param is 图片
     */
    public void setGifImage(InputStream is){
    	setGifDecoderImage(is);
    }
    
    public void setGifImage(String filename){
//        if(decodingFileName!= null && filename != null){
//            if(decodingFileName.equals(filename)){
//                if(getParseStatus() == GifDecoder.STATUS_FINISH && !isPlaying() && playAfterDecode){
//                    play();
//                    return;
//                }
//            }
//        }
        if(!TextUtils.isEmpty(filename)){
            FileInputStream is;
            try {
//                if(gifDecoder!=null){
//                    gifDecoder.free();
//                }
                is = new FileInputStream(filename);
                setGifDecoderImage(is);
                decodingFileName = filename;
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 以资源形式设置gif图片
     * @param resId gif图片的资源ID
     */
    public void setGifImage(int resId){
    	Resources r = getResources();
    	InputStream is = r.openRawResource(resId);
    	setGifDecoderImage(is);
    }
    

    public void destroy(){
        if(gifDecoder != null)
            gifDecoder.free();
    }
    
    /**
     * 只显示第一帧图片<br>
     * 调用本方法后，gif不会显示动画，只会显示gif的第一帧图
     */
    public void showCover(){
    	if(gifDecoder == null)
    		return;
    	pause = true;
    	currentImage = gifDecoder.getImage();
//    	invalidate();
    	reDraw();
    }
    
    /**
     * 继续显示动画<br>
     * 本方法在调用showCover后，会让动画继续显示，如果没有调用showCover方法，则没有任何效果
     */
    public void showAnimation(){
    	if(pause){
    		pause = false;
    	}
    }
    
    /**
     * 设置gif在解码过程中的显示方式<br>
     * <strong>本方法只能在setGifImage方法之前设置，否则设置无效</strong>
     * @param type 显示方式
     */
    public void setGifImageType(GifImageType type){
    	if(gifDecoder == null)
    		animationType = type;
    }
  
    public void play(){
        if (gifDecoder != null && gifDecoder.getStatus() == gifDecoder.STATUS_FINISH && isPlaying) {
            // isPlaying = true;
            // setPlaying(true);
            DrawThread dt = new DrawThread();
            dt.start();
        }
    }
    
    
    public static final int STOP_AND_QUIT = 0;
    public static final int STOP_AND_NOT_QUIT = 1;
    
    /**
     * @hide
     */
    public void parseOk(boolean parseStatus,int frameIndex){
        if(!parseStatus){
            if(stopHandler != null){
                if(repeatMode == REPEAT_MODE_INFINIT){
                    stopHandler.sendEmptyMessageDelayed(STOP_AND_NOT_QUIT, STOP_MESSAGE_DELAYED_TIME);
                }else{
                    stopHandler.sendEmptyMessageDelayed(STOP_AND_QUIT, STOP_MESSAGE_DELAYED_TIME);
                }
            }
            return;
        }
    	if(parseStatus && playAfterDecode){
    		if(gifDecoder != null){
    			switch(animationType){
    			case WAIT_FINISH:
    				if(frameIndex == -1){
    					if(gifDecoder.getFrameCount() > 1){     //当帧数大于1时，启动动画线程
    	    				play();
    	    			}else{
    	    				reDraw();
    	    			}
    				}
    				break;
    			case COVER:
    				if(frameIndex == 1){
    					currentImage = gifDecoder.getImage();
    					reDraw();
    				}else if(frameIndex == -1){
    					if(gifDecoder.getFrameCount() > 1){
//    						if(drawThread == null){
//        						drawThread = new DrawThread();
//        						drawThread.start();
//        					}
    					    play();
    					}else{
    						reDraw();
    					}
    				}
    				break;
    			case SYNC_DECODER:
    				if(frameIndex == 1){
    					currentImage = gifDecoder.getImage();
    					reDraw();
    				}else if(frameIndex == -1){
    					reDraw();
    				}else{
//    					if(drawThread == null){
//    					    isPlaying = true;
//    						drawThread = new DrawThread();
//    						drawThread.start();
//    					}
    				    play();
    				}
    				break;
    			}
 
    		}else{
    			Log.e("gif","parse error");
    		}
    		
    	}
    }
    
    private void reDraw(){
    	if(redrawHandler != null){
			Message msg = redrawHandler.obtainMessage();
			redrawHandler.sendMessage(msg);
    	}
    	
    }
    
    public int getParseStatus(){
        if(gifDecoder != null){
            return gifDecoder.getStatus();
        }
        return 3;
    }
    
    private void drawImage(){
//        BitmapDrawable bd = new BitmapDrawable(currentImage);
        
//        bd.setTargetDensity(DisplayMetrics.DENSITY_HIGH);
//        setImageDrawable(bd);
//        setImageBitmap(currentImage);
        setScaledImageBitmap(currentImage);
    	invalidate();
    }
     
    public Handler getStopHandler() {
        return stopHandler;
    }

    public void setStopHandler(Handler stopHandler) {
        this.stopHandler = stopHandler;
    }

    public synchronized boolean isPlaying() {
        return isPlaying;
    }

    public synchronized void setPlaying(boolean isPlaying) {
    	LogUtil.d(TAG, "set playing = "+isPlaying);
        this.isPlaying = isPlaying;
    }

    public boolean isPlayAfterDecode() {
        return playAfterDecode;
    }

    public void setPlayAfterDecode(boolean playAfterDecode) {
        this.playAfterDecode = playAfterDecode;
    }

    /**
     * @return the filename
     */
    public String getAbsFilename() {
        return absFileName;
    }

    /**
     * @param absFileName the filename to set
     */
    public void setAbsFilename(String absFileName) {
        this.absFileName = absFileName;
    }

    /**
     * @return the needScale
     */
    public boolean isNeedScale() {
        return needScale;
    }

    /**
     * @param needScale the needScale to set
     */
    public void setNeedScale(boolean needScale) {
        this.needScale = needScale;
    }

    private Handler redrawHandler = new Handler(){
    	public void handleMessage(Message msg) {
    	    try{
    	        if(isRunning() && currentImage != null && !currentImage.isRecycled()){
            	    if(backView != null){
                        backView.setBackgroundDrawable(new BitmapDrawable(currentImage));
                    }else{
                        drawImage();
                    }
    	        }
    	    }catch(Exception ex){
    	        Log.e("GifView", ex.toString());
    	    }
    	}
    };
    
    public synchronized void stopRun(){
        isRun = false;
    }
    
    public synchronized boolean isRunning(){
        return isRun;
    }
    
    /**
     * 动画线程
     * @author liao
     *
     */
    private class DrawThread extends Thread{	
    	public void run(){
    	    boolean start = false;
    		if(gifDecoder == null){
    			return;
    		}
    		int frameIndex = 0;
    		gifDecoder.reset(true);
    		synchronized (GifView.this) {
    		    isRun = true;
            }
    		while(isRunning()){
    		    if(gifDecoder.getFrameCount() == 1){
    		        //如果单帧，不进行动画
//    		        GifFrame f = gifDecoder.next();
//    		        if(f != null){
//    		            currentImage = f.image;
//    		        }
//                    reDraw();
    		        break;
    		    }
                if (pause == false) {
                    GifFrame frame = gifDecoder.next();
                    
                    if (frame == null) {
                        SystemClock.sleep(50);
                        continue;
                    }
                    
                    LogUtil.d(TAG, "show frame "+frame.hashCode());
                    
                    if (frame.image != null)
                        currentImage = frame.image;
                    else if (frame.imageName != null) {
                        currentImage = BitmapFactory.decodeFile(frame.imageName);
                    }
                    
                    if(currentImage != null && currentImage.isRecycled()){
                          break;
                    }
                    long sp = frame.delay < 100 ? 100 : frame.delay;
                    
                    LogUtil.d(TAG, "sp = "+sp);

                    if (redrawHandler != null) {
                        reDraw();
                        SystemClock.sleep(sp);
                    } else {
                        break;
                    }
                    
                    if(repeatMode == REPEAT_MODE_ONCE){
                        if(frameIndex == gifDecoder.getFrameCount()){
                            break;
                        }
                        frameIndex++;
                    }
                } else {
                    SystemClock.sleep(50);
    			}
    		}
            if (stopHandler != null) {
                // stopHandler.sendEmptyMessageDelayed(0,
                // STOP_MESSAGE_DELAYED_TIME);
                stopHandler.sendEmptyMessage(0);
            } else {
                setPlaying(false);
            }
    		
    	}
    }
    
    public void setScaledImageBitmap(Bitmap bm){
        Matrix scaleMatrix = new Matrix();
        float scale = (float)getResources().getDisplayMetrics().density / (float)1.5;
        scaleMatrix.postScale(scale, scale);
        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), scaleMatrix, true);
        setImageBitmap(bm);
    }
    
}