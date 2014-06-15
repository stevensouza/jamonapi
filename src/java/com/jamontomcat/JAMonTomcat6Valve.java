package com.jamontomcat;

import com.jamonapi.http.HttpMonFactory;

public class JAMonTomcat6Valve extends JAMonTomcat55Valve {

    public JAMonTomcat6Valve() {
        httpMonFactory=new HttpMonFactory("com.jamontomcat.http.JAMonTomcat6Valve");
    }

}
