package com.jamonapi;


final class TimeMon2 extends TimeMon {

    public TimeMon2() {
        super(new MonKeyImp("timer","ms."),new MonInternals());
        monData.setActivityStats(new ActivityStats());
        monData.isTimeMonitor=true;
    }


    @Override
    public String toString() {
        return getLastValue()+" ms.";
    }

}
