package com.debalink.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageThreadDownloader {

	// the simplest in-memory cache implementation. This should be replaced with
	// something like SoftReference or BitmapOptions.inPurgeable(since 1.6)
	/** The cache. */
	private HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();

	/** The cache dir. */
	private File cacheDir;
	private Activity act;
	private int imgWidth, imgHeight;

	/**
	 * Instantiates a new image loader.
	 * 
	 * @param context
	 *            the context
	 */
	public ImageThreadDownloader(Context context) {
		// Make the background thead low priority. This way it will not affect
		// the UI performance

		photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);

		// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "cache_dir_img");
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public ImageThreadDownloader(Context context, Activity act, int imgWidth, int imgHeight) {
		// Make the background thead low priority. This way it will not affect
		// the UI performance
		this.act = act;
		this.imgHeight = imgHeight;
		this.imgWidth = imgWidth;
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);

		// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "cache_dir_img");
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	// This is used for a stub when the user can not see the actual image..
	// this images will be seen
	final int stub_id = com.debalink.R.drawable.ic_launcher;

	private boolean isAspectRatio;

	/**
	 * Display image.
	 * 
	 * @param url
	 *            the url
	 * @param activity
	 *            the activity
	 * @param imageView
	 *            the image view
	 */
	public void displayImage(String url, WeakReference<ImageView> imageView) {

		if (cache.containsKey(url)) {
			imageView.get().setImageBitmap(cache.get(url));
			// System.out.println("......cache");
		}

		else {
			// System.out.println("......download");
			queuePhoto(url, imageView);
			imageView.get().setImageResource(stub_id);
		}
	}

	/**
	 * Queue photo.
	 * 
	 * @param url
	 *            the url
	 * @param activity
	 *            the activity
	 * @param imageView
	 *            the image view
	 */
	private void queuePhoto(String url, WeakReference<ImageView> imageView) {
		// This ImageView may be used for other images before. So there may be
		// some old tasks in the queue. We need to discard them.
		photosQueue.Clean(imageView);
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		synchronized (photosQueue.photosToLoad) {
			boolean flag = photosQueue.photosToLoad.add(p);
			// System.out.println(flag);
			photosQueue.photosToLoad.notifyAll();
		}

		// start thread if it's not started yet
		if (photoLoaderThread.getState() == Thread.State.NEW)
			photoLoaderThread.start();
	}

	/**
	 * Gets the bitmap.
	 * 
	 * @param url
	 *            the url
	 * @return the bitmap
	 */
	public Bitmap getBitmap(String url) {
		// I identify images by hashcode. Not a perfect solution, good for the
		// demo.

		String filename = String.valueOf(url.hashCode());
		filename = URLEncoder.encode(filename);
		File f = new File(cacheDir, filename);

		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;

		// from web
		try {
			System.out.println("......" + url);
			Bitmap bitmap = null;
			InputStream is = new URL(url).openStream();
			OutputStream os = new FileOutputStream(f);
			copyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// decodes image and scales it to reduce memory consumption
	/**
	 * Decode file.
	 * 
	 * @param f
	 *            the f
	 * @return the bitmap
	 */
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			// final
			/*
			 * int REQUIRED_SIZE=70; int width_tmp=o.outWidth,
			 * height_tmp=o.outHeight; int scale=1; while(true){
			 * if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
			 * break; width_tmp/=2; height_tmp/=2; scale++; }
			 */

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			Bitmap photo = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);

			if (isAspectRatio) {
				int Aspect = photo.getWidth() / photo.getHeight();

				int newHeight = imgWidth / Aspect;
				photo = photo.createScaledBitmap(photo, imgWidth, newHeight, false);

			} else {
				if (imgWidth != 0 && imgHeight != 0) {
					photo = photo.createScaledBitmap(photo, 150, 150, false);
				} else {

				}

			}

			// System.out.println("photo.getHeight() " + photo.getHeight()
			// + " photo.getWidth() " + photo.getWidth());
			// o2.inSampleSize=scale;
			// return BitmapFactory.decodeStream(new FileInputStream(f), null,
			// o2);
			return photo;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Task for the queue
	/**
	 * The Class PhotoToLoad.
	 */
	private class PhotoToLoad {

		/** The url. */
		public String url;

		/** The image view. */
		public WeakReference<ImageView> imageView;

		/**
		 * Instantiates a new photo to load.
		 * 
		 * @param u
		 *            the u
		 * @param i
		 *            the i
		 */
		public PhotoToLoad(String u, WeakReference<ImageView> i) {
			url = u;
			imageView = i;
		}
	}

	/** The photos queue. */
	PhotosQueue photosQueue = new PhotosQueue();

	/**
	 * Stop thread.
	 */
	public void stopThread() {
		photoLoaderThread.interrupt();
	}

	// stores list of photos to download
	/**
	 * The Class PhotosQueue.
	 */
	class PhotosQueue {

		/** The photos to load. */
		private Queue<PhotoToLoad> photosToLoad = new LinkedList<PhotoToLoad>();

		// removes all instances of this ImageView
		/**
		 * Clean.
		 * 
		 * @param image
		 *            the image
		 */
		public void Clean(WeakReference<ImageView> image) {
			// photosToLoad.clear();
			// for(int j=0 ;j<photosToLoad.size();){
			// if(photosToLoad.get(j).imageView==image)
			// photosToLoad.remove(j);
			// else
			// ++j;
			// }

			// for(PhotoToLoad tmp:photosToLoad){
			// if(tmp.imageView==image){
			// photosToLoad.remove(tmp);
			// }
			// }

			// for (Iterator<PhotoToLoad> it = photosToLoad.iterator();
			// it.hasNext();) {
			// PhotoToLoad p = it.next();

			// if (p.imageView==image) {
			// it.remove();
			// }
			// }
		}
	}

	/**
	 * The Class PhotosLoader.
	 */
	class PhotosLoader extends Thread {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			try {
				while (true) {
					// thread waits until there are any images to load in the
					// queue
					if (photosQueue.photosToLoad.size() == 0)
						synchronized (photosQueue.photosToLoad) {
							photosQueue.photosToLoad.wait();
						}
					if (photosQueue.photosToLoad.size() != 0) {
						PhotoToLoad photoToLoad;
						synchronized (photosQueue.photosToLoad) {
							photoToLoad = photosQueue.photosToLoad.poll();
							// System.out.println("photosQueue.photosToLoad "
							// + photosQueue.photosToLoad.size());
						}
						Bitmap bmp;
						// if (imgHeight != 0 && imgHeight != 0) {
						bmp = getBitmap(photoToLoad.url);
						// } else {
						// bmp = DownloadImage(photoToLoad.url);
						// }

						if (!photoToLoad.url.equalsIgnoreCase("http://maxisinema.com.tr//pub/mobile/banner/mobile_banner.png"))
							if (bmp != null)
								cache.put(photoToLoad.url, bmp);

						if (((String) photoToLoad.imageView.get().getTag()).equals(photoToLoad.url)) {
							BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad.imageView.get());
							Activity a = act;
							a.runOnUiThread(bd);
						}
					}
					if (Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** The photo loader thread. */
	PhotosLoader photoLoaderThread = new PhotosLoader();

	// Used to display bitmap in the UI thread
	/**
	 * The Class BitmapDisplayer.
	 */
	class BitmapDisplayer implements Runnable {

		/** The bitmap. */
		Bitmap bitmap;

		/** The image view. */
		ImageView imageView;

		/**
		 * Instantiates a new bitmap displayer.
		 * 
		 * @param b
		 *            the b
		 * @param i
		 *            the i
		 */
		public BitmapDisplayer(Bitmap b, ImageView i) {
			bitmap = b;
			imageView = i;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			if (bitmap != null)
				imageView.setImageBitmap(bitmap);
			else
				imageView.setImageResource(stub_id);
		}
	}

	/**
	 * Clear cache.
	 */
	public void clearCache() {
		// clear memory cache
		cache.clear();

		// clear SD cache
		File[] files = cacheDir.listFiles();
		for (File f : files)
			f.delete();
	}

	public static void copyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void displayImageWithLoader(final String url, final WeakReference<ImageView> imageView, final ProgressBar dialog) {
		// if (cache.containsKey(url)) {
		// imageView.setImageBitmap(cache.get(url));
		// System.out.println("......");
		// }

		// else {
		// System.out.println(".......2");
		// queuePhoto(url, imageView);
		// imageView.setImageResource(stub_id);
		// }

		class UpdateCurrentGamesFirstTime extends AsyncTask<Void, Void, Void> {

			protected void onPreExecute() {

				dialog.setVisibility(View.VISIBLE);

			}

			protected Void doInBackground(Void... params) {
				if (cache.containsKey(url)) {

					// System.out.println("......");
					BitmapDisplayer bd = new BitmapDisplayer(cache.get(url), imageView.get());
					Activity a = (Activity) imageView.get().getContext();
					a.runOnUiThread(bd);
				}

				else {
					// System.out.println(".......2");
					photosQueue.Clean(imageView);
					PhotoToLoad p = new PhotoToLoad(url, imageView);
					synchronized (photosQueue.photosToLoad) {
						boolean flag = photosQueue.photosToLoad.add(p);
						System.out.println(flag);
						photosQueue.photosToLoad.notifyAll();
					}

					// start thread if it's not started yet
					Bitmap bmp = getBitmap(url);
					cache.put(url, bmp);
					if (((String) imageView.get().getTag()).equals(url)) {
						BitmapDisplayer bd = new BitmapDisplayer(bmp, imageView.get());
						// Activity a = (Activity) imageView
						// .getContext();
						act.runOnUiThread(bd);

					}

				}
				return null;
			}

			protected void onPostExecute(Void unused) {

				dialog.setVisibility(View.INVISIBLE);
				// System.out.println("current games");

			}
		}
		new UpdateCurrentGamesFirstTime().execute();

	}

	public Bitmap getBitmapImage(String url) {
		if (cache.containsKey(url)) {
			return cache.get(url);

		} else {
			return getBitmap(url);
		}
	}

	public Bitmap DownloadImage(String URL) {
		Bitmap in = null;
		try {
			URL url = new URL(URL);
			HttpGet httpRequest = null;

			httpRequest = new HttpGet(url.toURI());

			UsernamePasswordCredentials creds = new UsernamePasswordCredentials("tr-mx-com-tr", "919314320!*");

			httpRequest.addHeader(new BasicScheme().authenticate(creds, httpRequest));

			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);

			HttpEntity entity = response.getEntity();
			BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
			InputStream input = bufHttpEntity.getContent();

			in = BitmapFactory.decodeStream(input);

			// ImageActivity.this.i.setImageBitmap(bitmap);
			// ImageActivity.this.i.refreshDrawableState();
			input.close();

			// System.out.println("size "+response.getStatusLine());

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}
		return in;
	}
}