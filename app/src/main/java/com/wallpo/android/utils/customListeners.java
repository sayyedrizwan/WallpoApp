package com.wallpo.android.utils;

public class customListeners {

    public interface exploreFragmentListeners {
        public void onObjectReady(String title);
    }

    public static customListeners.exploreFragmentListeners listener;

    public void exploreFragmentListeners(customListeners.exploreFragmentListeners listener) {
        customListeners.listener = listener;
    }

    public interface playerListeners {
        public void onObjectReady(String title);
    }

    public static customListeners.playerListeners playerlistener;

    public void playerListeners(customListeners.playerListeners listener) {
        customListeners.playerlistener = listener;
    }
}
