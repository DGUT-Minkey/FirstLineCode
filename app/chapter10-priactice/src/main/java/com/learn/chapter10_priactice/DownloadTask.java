package com.learn.chapter10_priactice;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<String,Integer,Integer> {//字符串参数，进度显示单位，执行结果
//    下载状态
    public static final int TYPE_SUCCESS = 0;//下载成功
    public static final int TYPE_FAILED = 1;//下载失败
    public static final int TYPE_PAUSED = 2;//暂停下载
    public static final int TYPE_CANCELED = 3;//取消下载

//    回调接口返回DownloadService
    private DownloadListener listener;

//    控制暂停和取消操作
    private boolean isCanceled =false;
    private boolean isPaused =false;

//    上一次下载进度
    private int lastProgress;


    public DownloadTask(DownloadListener listener) {
        this.listener = listener;
    }

//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//    }


    public void pauseDownload(){
        isPaused = true;
    }

    public void cancelDownload(){
        isCanceled = true;
    }



// 执行耗时任务，后台执行具体下载逻辑
    @Override
    protected Integer doInBackground(String... params) {
        InputStream in = null;
        RandomAccessFile savedFile = null;
        File file = null;
        try{
//            记录已下载的文件长度
            long downloadedLength=0;
//            获取下载的URL地址
            String downloadUrl = params[0];
//            URL解析文件名
            Log.d("DownloadTask Url",downloadUrl);
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            Log.d("fileName ",fileName);
//           下载到Environment.DIRECTORY_DOWNLOADS目录下
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            Log.d("DownloadPath",directory);
            file = new File(directory+fileName);
//            判断Download目录是否已经存在要下载的文件
            if(file.exists()){
//                读取已下载的字节数，后面开启断点续传的功能
                downloadedLength = file.length();
            }
//            获取待下载文件的总长度
            long contentLength = getContentLength(downloadUrl);
//            文件长度为0代表有问题
            if(contentLength==0){
                return TYPE_FAILED;
                // 已下载字节与文件的总字节数相等，说明下载完成了
            }else if(contentLength == downloadedLength){
                return TYPE_SUCCESS;
            }
//            使用okhttp发送一条http请求，RANGE从哪个字节开始下载
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
//                    断点下载，指定从哪个字节点开始下载
                                         .addHeader("RANGE","bytes="+downloadedLength+"-")
                                         .url(downloadUrl)
                                         .build();
            Response response = client.newCall(request).execute();

            if(response!=null){
                in = response.body().byteStream();
                // RandomAccessFile是java Io体系中功能最丰富的文件内容访问类。即可以读取文件内容，也可以向文件中写入内容。
                // 但是和其他输入/输入流不同的是，程序可以直接跳到文件的任意位置来读写数据。
                //  因为RandomAccessFile可以自由访问文件的任意位置，所以如果我们希望只访问文件的部分内容，那就可以使用RandomAccessFile类。
                //  与OutputStearm,Writer等输出流不同的是，RandomAccessFile类允许自由定位文件记录指针，所以RandomAccessFile可以不从文件开始的地方进行输出，
                // 所以RandomAccessFile可以向已存在的文件后追加内容。则应该使用RandomAccessFile。
                savedFile = new RandomAccessFile(file,"rw");
//                跳过已下载的字节
                savedFile.seek(downloadedLength);
                byte[]b=new byte[1024];
                int total = 0;
                int len;
//                读取数据写入本地
                while((len = in.read(b) )!=-1){
//                    取消下载
                    if(isCanceled){
                        return TYPE_CANCELED;
//                        暂停下载
                    }else if(isPaused) {
                        return TYPE_PAUSED;
                    }else{
                        total+=len;
                        savedFile.write(b,0,len);
//                        计算已下载的百分比
                        int progress =(int)((total+downloadedLength)*100/contentLength);
                        //通知执行更新界面操作onProgressUpdate
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(in !=null){
                    in.close();
                }
                if(savedFile!=null){
                    savedFile.close();
                }
                if(isCanceled&&file !=null){
                    file.delete();
                }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return TYPE_FAILED;
    }

    //   界面上更新 当前的下载进度
    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if(progress>lastProgress){
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }

    //    后台任务完成 通知最终下载结果
    @Override
    protected void onPostExecute(Integer status) {
        switch (status){
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
        }
    }

    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
            Response response = client.newCall(request).execute();
            if(response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.close();
                return contentLength;
            }
            return 0;
    }

}
