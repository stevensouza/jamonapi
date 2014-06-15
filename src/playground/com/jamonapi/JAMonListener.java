package com.jamonapi;

import java.util.EventListener;

public interface JAMonListener extends EventListener{

	// java.util.EventObject
	//  java.util.EventListener - tag event
	 public void processEvent(Monitor mon);
}
