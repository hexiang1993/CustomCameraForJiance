package com.car300.customcamera.vindriver;


import com.car300.customcamera.utils.StringUtil;

/**
 * Created by xhe on 2016/11/24.
 * 识别出的行驶证信息
 */
public class DriverLicenseInfo{
    private String engine_num;
    private String model;
    private String owner;
    private String plate_num;//车牌号
    private String vin;
    private String use_character;//车辆性质 非营运，营运
    private boolean success;

    @Override
    public String toString() {
        return "DriverLicenseInfo{" +
                "engine_num='" + engine_num + '\'' +
                ", model='" + model + '\'' +
                ", owner='" + owner + '\'' +
                ", plate_num='" + plate_num + '\'' +
                ", vin='" + vin + '\'' +
                ", use_character=" + use_character +
                ", success=" + success +
                '}';
    }

    public String getUse_character() {
        return use_character;
    }

    public void setUse_character(String use_character) {
        this.use_character = use_character;
    }

    public String getEngine_num() {
        return engine_num;
    }

    public void setEngine_num(String engine_num) {
        this.engine_num = engine_num;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPlate_num() {
        return plate_num;
    }

    public void setPlate_num(String plate_num) {
        this.plate_num = plate_num;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        if (!StringUtil.isEmpty(vin)&&vin.length()>17){
            vin = vin.substring(0,17);
        }
        this.vin = vin;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
