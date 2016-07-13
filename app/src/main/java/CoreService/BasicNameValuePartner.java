package CoreService;

/**
 * Created by Oring on 2015/11/3.
 */
public class BasicNameValuePartner {

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String key;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;
    public BasicNameValuePartner(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
