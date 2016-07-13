package VO;

/**
 * Created by Administrator on 2016/3/9.
 */
public class PointVO {

    public String getProjectDeviceId() {
        return projectDeviceId;
    }

    public void setProjectDeviceId(String projectDeviceId) {
        this.projectDeviceId = projectDeviceId;
    }

    public String getProjectDeviceMac() {
        return projectDeviceMac;
    }

    public void setProjectDeviceMac(String projectDeviceMac) {
        this.projectDeviceMac = projectDeviceMac;
    }

    public String getProjectDeviceNumber() {
        return projectDeviceNumber;
    }

    public void setProjectDeviceNumber(String projectDeviceNumber) {
        this.projectDeviceNumber = projectDeviceNumber;
    }

    public String getMapDataId() {
        return mapDataId;
    }

    public void setMapDataId(String mapDataId) {
        this.mapDataId = mapDataId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    private String projectDeviceId;//点位id
    private String projectDeviceMac;//mac地址
    private String projectDeviceNumber;//设备id
    private String mapDataId;//地图id
    private String projectId;//项目id
}
