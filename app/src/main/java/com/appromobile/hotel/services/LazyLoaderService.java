package com.appromobile.hotel.services;

import android.os.Handler;

import com.appromobile.hotel.utils.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LazyLoaderService {
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static  int BUFFER_SIZE=8192;//16384
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            NUMBER_OF_CORES * 2,
            NUMBER_OF_CORES * 2,
            30L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>()
    );

    private static ThreadPoolExecutor executor2 = new ThreadPoolExecutor(
            NUMBER_OF_CORES * 2,
            NUMBER_OF_CORES * 2,
            30L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>()
    );

    /**
     * Download list of image with background function
     * @param urls: Image urls
     * @param rootFolder: Folder is stored file after download
     */
    public static void download(boolean isNew, final List<String> urls, final File rootFolder) {
        try {
            if (urls != null) {
                int start = urls.size()-2;
                for (int i = start; i >=0 ; i--) {
                    final int finalI = i;
                    System.out.println("executor2.getActiveCount(): "+executor2.getActiveCount());
                    executor2.execute(new Runnable() {
                        @Override
                        public void run() {
                            String urlString = urls.get(finalI);
                            int count;
                            try {
                                URL url = new URL(urlString);
                                URLConnection conexion = url.openConnection();
                                conexion.connect();
                                String targetFileName = Utils.md5(urlString);//Change name and subname
                                if (!rootFolder.exists()) {
                                    rootFolder.mkdirs();//If there is no folder it will be created.
                                }
                                File imgFile = new File(rootFolder, targetFileName);
                                if (!imgFile.exists()) {

                                    InputStream input = new BufferedInputStream(url.openStream(), BUFFER_SIZE);
                                    OutputStream output = new FileOutputStream(imgFile);

                                    byte data[] = new byte[BUFFER_SIZE];
                                    while ((count = input.read(data)) != -1) {
                                        output.write(data, 0, count);
                                    }
                                    output.flush();
                                    output.close();
                                    input.close();
                                    System.out.println("DownloadFinished: " + rootFolder.getPath() + "/" + targetFileName);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * Download list of image with background function
     * @param urls: Image urls
     * @param rootFolder: Folder is stored file after download
     */
    public static void download(final List<String> urls, final File rootFolder) {
        try {
            if (urls != null) {
                int start = urls.size()-2;
                for (int i = start; i >=0 ; i--) {
                    final int finalI = i;
                    System.out.println("executor.getActiveCount(): "+executor.getActiveCount());
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            String urlString = urls.get(finalI);
                            int count;
                            try {
                                URL url = new URL(urlString);
                                URLConnection conexion = url.openConnection();
                                conexion.connect();
                                String targetFileName = Utils.md5(urlString);//Change name and subname
                                if (!rootFolder.exists()) {
                                    rootFolder.mkdirs();//If there is no folder it will be created.
                                }
                                File imgFile = new File(rootFolder, targetFileName);
                                if (!imgFile.exists()) {

                                    InputStream input = new BufferedInputStream(url.openStream());
                                    OutputStream output = new FileOutputStream(imgFile);

                                    byte data[] = new byte[BUFFER_SIZE];
                                    while ((count = input.read(data)) != -1) {
                                        output.write(data, 0, count);
                                    }
                                    output.flush();
                                    output.close();
                                    input.close();
                                    System.out.println("DownloadFinished: " + rootFolder.getPath() + "/" + targetFileName);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * Download an image with callback handler
     * @param url: Image url
     * @param rootFolder: Folder is stored file after download
     * @param downloadId: Download identify to handle response result
     * @param handler: Handler callback
     */
    public static void download(final String url, final File rootFolder, final int downloadId, final Handler handler) {
        try {
            System.out.println("executor.getActiveCount(): "+executor.getActiveCount());
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    String urlString = url;
                    int count;
                    try {
                        URL url = new URL(urlString);
                        URLConnection conexion = url.openConnection();
                        conexion.connect();
                        String targetFileName = Utils.md5(urlString);//Change name and subname
                        if (!rootFolder.exists()) {
                            rootFolder.mkdirs();//If there is no folder it will be created.
                        }
                        File imgFile = new File(rootFolder, targetFileName);
                        if (!imgFile.exists()) {

                            InputStream input = new BufferedInputStream(url.openStream());
                            OutputStream output = new FileOutputStream(imgFile);

                            byte data[] = new byte[BUFFER_SIZE];
                            while ((count = input.read(data)) != -1) {
                                output.write(data, 0, count);
                            }
                            output.flush();
                            output.close();
                            input.close();
                            System.out.println("DownloadFinished: " + rootFolder.getPath() + "/" + targetFileName);
                        }
                        handler.sendEmptyMessage(downloadId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    public static void download(boolean isNew, final String url, final File rootFolder, final int downloadId, final Handler handler) {
        try {
            System.out.println("executor2.getActiveCount(): "+executor2.getActiveCount());
            executor2.execute(new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlString = url;
                    int count;
                    try {
                        URL url = new URL(urlString);
                        URLConnection conexion = url.openConnection();
                        conexion.connect();
                        String targetFileName = Utils.md5(urlString);//Change name and subname
                        if (!rootFolder.exists()) {
                            rootFolder.mkdirs();//If there is no folder it will be created.
                        }
                        File imgFile = new File(rootFolder, targetFileName);
                        if (!imgFile.exists()) {

                            InputStream input = new BufferedInputStream(url.openStream());
                            OutputStream output = new FileOutputStream(imgFile);

                            byte data[] = new byte[BUFFER_SIZE];
                            while ((count = input.read(data)) != -1) {
                                output.write(data, 0, count);
                            }
                            output.flush();
                            output.close();
                            input.close();
                            System.out.println("DownloadFinished: " + rootFolder.getPath() + "/" + targetFileName);
                        }
                        handler.sendEmptyMessage(downloadId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }));

        } catch (Exception e) {
        }
    }
}
