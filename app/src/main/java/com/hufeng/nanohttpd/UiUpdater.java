package com.hufeng.nanohttpd;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

public class UiUpdater {
	protected static List<Handler> clients = new ArrayList<Handler>();
	
	static void registerClient(Handler client) {
		if(!clients.contains(client)) {
			clients.add(client);
		}
	}
	
	static void unregisterClient(Handler client) {
		while(clients.contains(client)) {
			clients.remove(client);
		}
	}
	
	static void updateClients() {
		//myLog.l(Log.DEBUG, "UI update");
		//Log.d("UiUpdate", "Update now");
		for (Handler client : clients) {
			client.sendEmptyMessage(0);
		}
	}
}
