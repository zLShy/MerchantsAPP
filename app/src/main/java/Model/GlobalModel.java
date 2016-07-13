package Model;

import java.util.ArrayList;

/**
 * Created by Oring on 2015/11/3.
 */
public class GlobalModel {
    private static GlobalModel _Instanst;

    public static GlobalModel get_Instanst() {
        if (_Instanst == null) {
            _Instanst = new GlobalModel();
        }
        return _Instanst;
    }

    public int screenWidth = 0;//屏幕宽度
    public String BaseUri =
//            "http://192.168.16.86:8091/";
                "http://v3api.thinkorange.cn/";//"http://192.168.16.211:8088/";服务器端接口地址

    public String SocketUri =
//        "http://190.168.16.232:8088/";
            "http://socket.thinkorange.cn:2125";//websocket地址

    public String imageList;
    public String[] types;
    public String[] typesId;
    public String project_name;
    public String company_email;
    public String company_name;
    public String company_web;
    public String company_add;
    public String company_tel;
    public String company_intro;
    public String company_fax;
    public String map_polygon_category_id;
    public ArrayList<String> imageUrls = new ArrayList<String>();
    public boolean hasMessage = false;
    public String userName;
}
