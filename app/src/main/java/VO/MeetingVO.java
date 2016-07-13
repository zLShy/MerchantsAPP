package VO;

/**
 * Created by Administrator on 2016/6/27.
 */
public class MeetingVO {
    private String title;
    private String body;

    public MeetingVO(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "MeetingVO{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
