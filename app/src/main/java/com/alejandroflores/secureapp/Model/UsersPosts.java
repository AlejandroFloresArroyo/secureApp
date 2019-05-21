package com.alejandroflores.secureapp.Model;

import java.util.List;

public class UsersPosts {


    /**
     * _id : 5ce113bed679d5001784e2aa
     * active : true
     * needHelp : false
     * facebookId : karlini2
     * geometry : {"type":"point","_id":"5ce113bed679d5001784e2ab","coordinates":[-80,25.0001]}
     * __v : 0
     * dis : 11.131884501754893
     */

    private String _id;
    private boolean active;
    private boolean needHelp;
    private String facebookId;
    private GeometryBean geometry;
    private int __v;
    private double dis;

    public UsersPosts(boolean active, boolean needHelp, String facebookId, GeometryBean geometry) {
        this.active = active;
        this.needHelp = needHelp;
        this.facebookId = facebookId;
        this.geometry = geometry;
    }

    public UsersPosts(boolean needHelp) {
        this.needHelp = needHelp;
    }

    public UsersPosts(GeometryBean geometry) {
        this.geometry = geometry;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isNeedHelp() {
        return needHelp;
    }

    public void setNeedHelp(boolean needHelp) {
        this.needHelp = needHelp;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public GeometryBean getGeometry() {
        return geometry;
    }

    public void setGeometry(GeometryBean geometry) {
        this.geometry = geometry;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public double getDis() {
        return dis;
    }

    public void setDis(double dis) {
        this.dis = dis;
    }

    public static class GeometryBean {
        public GeometryBean(List<Number> coordinates) {
            this.coordinates = coordinates;
        }

        /**
         * type : point
         * _id : 5ce113bed679d5001784e2ab
         * coordinates : [-80,25.0001]
         */



        private String type;
        private String _id;
        private List<Number> coordinates;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public List<Number> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Number> coordinates) {
            this.coordinates = coordinates;
        }
    }
}


