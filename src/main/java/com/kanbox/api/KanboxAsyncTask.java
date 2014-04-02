package com.kanbox.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class KanboxAsyncTask extends AsyncTask<String, Long, String> {
	private HttpRequestBase mHttpRequest;
	private RequestListener mRequestListener;
	private KanboxException mException;
	private int mOpType;
	private String mDestPath;
    private String mPath;
    private boolean pauseRequested = false;
//    private Token mToken;
    private boolean mNeedAccessToken;

    public String getPath () {
        return mPath;
    }

    public void pause() {
        pauseRequested = true;
    }

	/**
	 * @param destPath:长传、下载时的目标路径
	 * @param httpRequest
	 * @param listener
	 * @param opType
	 */
	public KanboxAsyncTask(String path, String destPath, HttpRequestBase httpRequest, RequestListener listener, int opType, boolean need_token) {
	    mPath = path;
        mDestPath = destPath;
		mHttpRequest = httpRequest;
		mRequestListener = listener;
		mOpType = opType;
        mNeedAccessToken = need_token;
	}

	@Override
	protected String doInBackground(String... params) {
		HttpClient sHttpClient = createHttpClient();
		try {
            if (mOpType == RequestListener.OP_GET_TOKEN) {
                return Token.getInstance().getToken(sHttpClient);
            } if (mOpType == RequestListener.OP_REFRESH_TOKEN) {
                return Token.getInstance().refreshToken(sHttpClient);
            } else {
                String token_result = Token.getInstance().refreshTokenIfExpired(sHttpClient);
                String access_token = Token.getInstance().getAccessToken();
                if (mNeedAccessToken) {
                    mHttpRequest.setHeader("Authorization", "Bearer " + access_token);
                }
                if (mOpType == RequestListener.OP_UPLOAD) {
                    InputStream is = new FileInputStream(mPath);
                    ((HttpPost) mHttpRequest).setEntity(new CountingInputStreamEntity(is, is.available()));
                }
                HttpResponse sHttpResponse = sHttpClient.execute(mHttpRequest);
                int statusCode = sHttpResponse.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    switch (mOpType) {
                        case RequestListener.OP_GET_THUMBNAIL:
                            return downloadingThumbnail(sHttpResponse.getEntity());
                        case RequestListener.OP_DOWNLOAD:
                            return downloading(sHttpResponse.getEntity());
                        default:
                            String strResult = EntityUtils.toString(sHttpResponse.getEntity());
                            return strResult;
                    }
                } else {
                    mException = new KanboxException(statusCode);
                    return "error";
                }
            }
		} catch (ClientProtocolException e) {
			mException = new KanboxException(e);
			return "error";
		} catch (IOException e) {
			mException = new KanboxException(e);
            if ("pause".equals(e.getMessage())) {
                return "pause";
            } else {
			    return "error";
            }
		}
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
        if(mRequestListener!=null) {
            if(result == null || result.equals("error")) {
                mRequestListener.onError(mPath, mException, mOpType);
            } else {
                mRequestListener.onComplete(mPath,result, mOpType);
            }
        }
	}
	
	@Override
	protected void onProgressUpdate(Long... values) {
		super.onProgressUpdate(values);
        if(mRequestListener!=null) {
            if (mOpType == RequestListener.OP_DOWNLOAD) {
		        mRequestListener.downloadProgress(mPath,values[0]);
            } else if (mOpType == RequestListener.OP_UPLOAD) {
                mRequestListener.uploadProgress(mPath,values[0]);
            }
        }
	}

    private String downloadingThumbnail(HttpEntity entity) {
        ByteArrayOutputStream bos = null;
        byte[] data = null;
        try {
            InputStream is = entity.getContent();
            Bitmap bm = BitmapFactory.decodeStream(is);

            if(bm!=null) {
                bos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
                data = bos.toByteArray();
            }
        } catch (IllegalStateException e) {
            mException = new KanboxException(e);
        } catch (IOException e) {
            mException = new KanboxException(e);
        } finally {
            if(bos!=null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return data==null?null:new String(data);
    }
	
	/**
	 * 读取数据流，并写到本地
	 * @param entity
	 * @return
	 */
	private String downloading(HttpEntity entity) {
		try {
			int size = 10 * 1024;	//每10K通知一次（用于更新进度条）
			long total = entity.getContentLength();
			InputStream is = entity.getContent();
			FileOutputStream fos = new FileOutputStream(mDestPath);
			byte[] buf = new byte[size];
			int num = -1;
			long count = 0, sendMessageNextPos = 0;
			
			if (is != null) {
				while ((num = is.read(buf)) != -1) {
                    if (pauseRequested) {
                        break;
                    }
					fos.write(buf, 0, num);
					count += num;
					
					if(count > sendMessageNextPos) {
						sendMessageNextPos += size;
                        long percentage = count*100L/total;
                        if(percentage>=100) {
                            percentage = 99;
                        }
						publishProgress(new Long[]{percentage});
					}
				}
			}
            if (pauseRequested) {
                return "pause";
            } else {
			    return "ok";
            }
		} catch (IllegalStateException e) {
			mException = new KanboxException(e);
			return "error";
		} catch (IOException e) {
			mException = new KanboxException(e);
			return "error";
		}
	}
	

	public static DefaultHttpClient createHttpClient() {

		final HttpParams httpParams = createHttpParams();

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));

		ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
		return new DefaultHttpClient(cm, httpParams);
	}

	private static HttpParams createHttpParams() {

		final HttpParams params = new BasicHttpParams();

		HttpConnectionParams.setStaleCheckingEnabled(params, false);

		HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
		HttpConnectionParams.setSoTimeout(params, 10 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
		params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

		return params;
	}

    public void serialExecute(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            execute();
        } else {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public class CountingInputStreamEntity extends InputStreamEntity {

        private long length;

        int size = 10 * 1024;	//每10K通知一次（用于更新进度条）

        public CountingInputStreamEntity(InputStream instream, long length) {
            super(instream, length);
            this.length = length;
        }


        @Override
        public void writeTo(OutputStream outstream) throws IOException {
            super.writeTo(new CountingOutputStream(outstream));
        }

        class CountingOutputStream extends OutputStream {
            private long counter = 0l;
            private long pre_log = 0l;
            private OutputStream outputStream;

            public CountingOutputStream(OutputStream outputStream) {
                this.outputStream = outputStream;
            }

            @Override
            public void write(int oneByte) throws IOException {
                if (pauseRequested) {
                    throw new IOException("pause");
                }
                this.outputStream.write(oneByte);
                counter++;
                if (counter-pre_log > size) {
                    publishProgress(new Long[]{(counter * 100)/ length});
                    pre_log = counter;
                }
//                if (mRequestListener != null) {
//                    int percent = (int) ((counter * 100)/ length);
//                    mRequestListener.uploadProgress(mPath, percent);
//                }
            }
        }

    }


}
