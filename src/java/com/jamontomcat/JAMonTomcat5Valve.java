package com.jamontomcat;

import com.jamonapi.http.HttpMonFactory;

public class JAMonTomcat5Valve extends JAMonTomcat4Valve {

    public JAMonTomcat5Valve() {
        httpMonFactory=new HttpMonFactory("com.jamontomcat.http.JAMonTomcat5Valve");
    }

}
