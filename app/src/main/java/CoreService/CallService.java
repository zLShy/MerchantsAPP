package CoreService;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import Model.GlobalModel;

/**
 * Created by abc on 2015/10/29.
 */
public class CallService {
    private GlobalModel globalModel = GlobalModel.get_Instanst();
    final String serverPath = globalModel.BaseUri;

    public JSONObject run(String functionName, ArrayList<BasicNameValuePartner> dataArr){
        JSONObject result = new JSONObject();
        try {
            URL url = new URL(serverPath + functionName);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String time = MilliTime_Ten();

            //对hashmap排序
            /*List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(hashMap.entrySet());
            Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    //return (o2.getValue() - o1.getValue());
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });*/

            //Iterator iterator = hashMap.entrySet().iterator();

            dataArr.add(new BasicNameValuePartner("from", "android"));
            dataArr.add(new BasicNameValuePartner("timestamp", time.toString()));
            dataArr = sortDataAr(dataArr);

            String data = "";
            String signStr = "";

            /*while (iterator.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                data += ("&" + entry.getKey().toString() + "=" + entry.getValue().toString());
                singStr += entry.getValue().toString();
            }*/

            for (int i = 0; i < dataArr.size(); i++){
                if (dataArr.get(i).getValue().toString().contains("&")) {
                    dataArr.get(i).setValue(dataArr.get(i).getValue().replace("&", ""));
                }else if (dataArr.get(i).getValue().toString().contains("＆")){
                    dataArr.get(i).setValue(dataArr.get(i).getValue().replace("＆", ""));
                }else if (dataArr.get(i).getValue().toString().contains("+")){
                    dataArr.get(i).setValue(dataArr.get(i).getValue().replace("+", "%2B"));
                }
                data += ("&" + dataArr.get(i).getKey() + "=" + dataArr.get(i).getValue().toString());
                signStr += dataArr.get(i).getValue().toString();
            }


            signStr = Sing_Md5(signStr);
            data += "&sign=" + signStr;
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200){
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1){
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                String resultStr  = new String(baos.toByteArray());
                if (!resultStr.equals("")){
                    result = new JSONObject(resultStr);
                }
            }
            urlConnection.disconnect();
        }catch (Exception e){
            if (e != null){

            }
        }
        return result;
    }

    public static String MilliTime_Ten() {
        // 获取系统的时间
        long time = System.currentTimeMillis();
        String re = (time + "").substring(0, 10);
        return re;
    }

    public static String Sing_Md5(String strObj) {
        strObj = strObj + "56a8d122ec0d330d6d9f541b459e43e1";
        strObj = MD5Util.MD5(strObj);
        return strObj;
    }

    private ArrayList<BasicNameValuePartner> sortDataAr(ArrayList<BasicNameValuePartner> dataArr){
        for (int i = 0; i < dataArr.size(); i++){
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
}
