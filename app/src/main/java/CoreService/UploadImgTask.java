package CoreService;

/**
 * Created by Oring on 2015/11/4.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import Model.GlobalModel;


public class UploadImgTask<P, S, T> extends AsyncTask<Void, Void, T> {
    private GlobalModel globalModel = GlobalModel.get_Instanst();
    private String end = "\r\n";
    private String twoHyphens = "--";
    private String boundary = "*****";
    private String newName = "image.jpg";
    private String actionUrl = globalModel.BaseUri;
    private Context context;
    private String funcName;
    private ArrayList<BasicNameValuePartner> dataArr;
    private CallBacks callBacks;
    private Bitmap bitmap;

    Dialog dialog;

    public UploadImgTask(Context context, ArrayList<BasicNameValuePartner> dataArr, Bitmap bitmap, String functioName, CallBacks callBacks) {
        this.context = context;
        this.dataArr = dataArr;
        this.bitmap = bitmap;
        this.funcName = functioName;
        this.callBacks = callBacks;
    }

    public void doAsyncTask() {
        this.execute();
    }

    @Override
    protected T doInBackground(Void... params) {
        String result = "";
        try {
            URL url = new URL(actionUrl + funcName);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                         /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
                         /* 设置传送的method=POST */
            con.setRequestMethod("POST");
                         /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("contentType", "UTF-8");
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                         /* 设置DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            //ds.writeBytes("Content-Disposition: form-data; " + "name=\"file1\";filename=\"" + newName + "\"" + end);
            ds.writeBytes("Content-Disposition: form-data; " + "name=\"image\";filename=\"" + newName + "\"" + ";boundary=" + boundary);
            ds.writeBytes(end + end);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            InputStream fStream = new ByteArrayInputStream(baos.toByteArray());

                         /* 设置每次写入1024bytes */
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
                         /* 从文件读取数据至缓冲区 */
            while ((length = fStream.read(buffer)) != -1) {
                         /* 将资料写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
            dataArr.add(new BasicNameValuePartner("from", "android"));
            dataArr.add(new BasicNameValuePartner("timestamp", MilliTime_Ten()));

            dataArr = sortDataAr(dataArr);
            String signStr = "";
            for (int i = 0; i < dataArr.size(); i++) {
                signStr += dataArr.get(i).getValue();
            }
            dataArr.add(new BasicNameValuePartner("sign", Sing_Md5(signStr)));
            ds.writeBytes(end);
            addFormField(dataArr, ds);

            //ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end + end);
                         /* close streams */
            fStream.close();
            ds.flush();
                         /* 取得Response内容 */
            InputStream is = con.getInputStream();
            StringBuffer sb = new StringBuffer();
            BufferedReader b = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String data = "";
            while ((data = b.readLine()) != null) {
                sb.append(data);
            }
            result = sb.toString();
                         /* 关闭DataOutputStream */
            ds.close();
        } catch (Exception e) {
            showDialog("上传失败" + e);
        }
        return (T) result;
    }

    public String Sing_Md5(String strObj) {
        strObj = strObj + "56a8d122ec0d330d6d9f541b459e43e1";
        strObj = MD5Util.MD5(strObj);
        return strObj;
    }

    public String MilliTime_Ten() {
        // 获取系统的时间
        long time = System.currentTimeMillis();
        String re = (time + "").substring(0, 10);
        return re;
    }

    private ArrayList<BasicNameValuePartner> sortDataAr(ArrayList<BasicNameValuePartner> dataArr) {
        for (int i = 0; i < dataArr.size(); i++) {
            for (int j = 0; j < dataArr.size() - 1 - i; j++) {
                if (dataArr.get(j).getKey().compareTo(dataArr.get(j + 1).getKey()) > 0) {
                    BasicNameValuePartner tempData = dataArr.get(j + 1);
                    dataArr.remove(j + 1);
                    dataArr.add(j, tempData);
                }
            }
        }

        return dataArr;
    }

    private void addFormField(ArrayList<BasicNameValuePartner> params, DataOutputStream output) {
        String lineEnd = System.getProperty("line.separator");
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < params.size(); i++) {
//            sb.append(twoHyphens + boundary + end + "Content-Disposition: form-data; name=\"" + params.get(i).getKey() + "\"");
//            sb.append(lineEnd + end);
//            sb.append(params.get(i).getValue() + lineEnd);
//        }
//        try {
//            output.writeBytes(sb.toString());
//        } catch (Exception e) {
//            if (e != null) {
//
//            }
//        }
        String sb = "";
        for (int i = 0; i < params.size(); i++) {
            sb += (twoHyphens + boundary + end + "Content-Disposition: form-data; name=\"" + params.get(i).getKey() + "\"");
            sb += (lineEnd + end);
            sb += (params.get(i).getValue() + lineEnd);
        }
        try {
            output.write(sb.getBytes());
        } catch (Exception e) {
            if (e != null) {

            }
        }
    }

    @Override
    protected void onPostExecute(T t) {
        callBacks.OnResult(t);
        super.onPostExecute(t);
    }


    /* 显示Dialog的method */
    private void showDialog(String mess) {
        new AlertDialog.Builder(context).setTitle("Message").setMessage(mess)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }
}