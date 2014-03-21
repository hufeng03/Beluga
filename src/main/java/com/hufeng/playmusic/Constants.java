package com.hufeng.playmusic;

public interface Constants {
	
	public final static String PREF_KEY_NUMWEEKS = "numweeks";
	
	public final static String SERVICECMD = "com.hufeng.playmusic.servicecommand";
	public final static String CMDNAME = "command";
	public final static String CMDTOGGLEPAUSE = "togglepause";
	public final static String CMDSTOP = "stop";
	public final static String CMDPAUSE = "pause";
	public final static String CMDPREVIOUS = "previous";
	public final static String CMDNEXT = "next";
	public static final String CMDCYCLEREPEAT = "cyclerepeat";
	public static final String CMDTOGGLESHUFFLE = "toggleshuffle";
	public final static String CMDREFRESHLYRICS = "refreshlyrics";
	public final static String CMDRESENDALLLYRICS = "resendalllyrics";
	public final static String CMDREFRESHMETADATA = "refreshmetadata";
	public final static String CMDTOGGLEFAVORITE = "togglefavorite";
	public final static String CMDMUSICWIDGETUPDATE_4x1 = "musicwidgetupdate4x1";
	public final static String CMDMUSICWIDGETUPDATE_2x2 = "musicwidgetupdate2x2";
	
	public static final String INTERNAL_VOLUME = "internal";
	public static final String EXTERNAL_VOLUME = "external";
	
	public final static boolean DEFAULT_LYRICS_WAKELOCK = false;

	public final static int VISUALIZER_TYPE_WAVE_FORM = 1;
	public final static int VISUALIZER_TYPE_FFT_SPECTRUM = 2;
	
	public final static String TOGGLEPAUSE_ACTION = "com.hufeng.playmusic.musicservicecommand.togglepause";
	public final static String PAUSE_ACTION = "com.hufeng.playmusic.musicservicecommand.pause";
	public final static String PREVIOUS_ACTION = "com.hufeng.playmusic.musicservicecommand.previous";
	public final static String NEXT_ACTION = "com.hufeng.playmusic.musicservicecommand.next";
	public static final String CYCLEREPEAT_ACTION = "com.hufeng.playmusic.musicservicecommand.cyclerepeat";
	public static final String TOGGLESHUFFLE_ACTION = "com.hufeng.playmusic.musicservicecommand.toggleshuffle";

	public final static String LASTFM_APIKEY = "e682ad43038e19de1e33f583b191f5b2";
	
	public final static int LYRICS_STATUS_OK = 0;
	public final static int LYRICS_STATUS_NOT_FOUND = 1;
	public final static int LYRICS_STATUS_INVALID = 2;
	
	public final static int ACTION_NOW = 1;
	public final static int ACTION_NEXT = 2;
	public final static int ACTION_LAST = 3;
	public final static int ID_NOTIFICATION_PLAYBACK = 1;
	public final static int ID_NOTIFICATION_SLEEPTIMER = 2;
	
	public final static int SHUFFLE_NONE = 0;
	public final static int SHUFFLE_NORMAL = 1;

	public final static int REPEAT_NONE = 0;
	public final static int REPEAT_CURRENT = 1;
	public final static int REPEAT_ALL = 2;
	
	public final static String BEHAVIOR_NEXT_SONG = "next_song";
	public final static String BEHAVIOR_PLAY_PAUSE = "play_pause";
	public final static String DEFAULT_SHAKING_BEHAVIOR = BEHAVIOR_NEXT_SONG;
	
	public final static String PLAYLIST_NAME_FAVORITES = "PlayMusic Favorites";
	
	public final static float DEFAULT_SHAKING_THRESHOLD = 5000f;
	
	
	public final static String SHAREDPREFS_PREFERENCES = "playmusic_preferences";
	public final static String SHAREDPREFS_EQUALIZER = "playmusic_equalizer";
	public final static String SHAREDPREFS_STATES = "playmusic_states";
	
	public final static String KEY_RESCAN_MEDIA = "rescan_media";
	public final static String KEY_LYRICS_WAKELOCK = "lyrics_wakelock";
	public final static String KEY_ALBUMART_SIZE = "albumart_size";
	public final static String KEY_DISPLAY_LYRICS = "display_lyrics";
	public final static String KEY_PLUGINS_MANAGER = "plugins_manager";
	public final static String KEY_ENABLE_SCROBBLING = "enable_scrobbling";
	public final static String KEY_GENTLE_SLEEPTIMER = "gentle_sleeptimer";
	public final static String KEY_DISPLAY_VISUALIZER = "display_visualizer";
	public final static String KEY_VISUALIZER_TYPE = "visualizer_type";
	public final static String KEY_VISUALIZER_REFRESHRATE = "visualizer_refreshrate";
	public final static String KEY_VISUALIZER_ACCURACY = "visualizer_accuracy";
	public final static String KEY_VISUALIZER_ANTIALIAS = "visualizer_antialias";
	public final static String KEY_UI_COLOR = "ui_color";
	public final static String KEY_AUTO_COLOR = "auto_color";
	public final static String KEY_CUSTOMIZED_COLOR = "customized_color";
	public final static String KEY_EQUALIZER_ENABLED = "equalizer_enabled";
	public final static String KEY_EQUALIZER_SETTINGS = "equalizer_settings";
	public final static String KEY_SHAKE_ENABLED = "shake_enabled";
	public final static String KEY_SHAKING_THRESHOLD = "shaking_threshold";
	public final static String KEY_SHAKING_BEHAVIOR = "shaking_behavior";
	public final static String KEY_BLUR_BACKGROUND = "blur_background";
	public final static String KEY_LAST_NOWPLAYING = "last_nowplaying";
	
	public final static String BROADCAST_KEY_ID = "id";
	public final static String BROADCAST_KEY_ARTIST = "artist";
	public final static String BROADCAST_KEY_ALBUM = "album";
	public final static String BROADCAST_KEY_TRACK = "track";
	public final static String BROADCAST_KEY_PLAYING = "playing";
	public final static String BROADCAST_KEY_ISFAVORITE = "isfavorite";
	public final static String BROADCAST_KEY_SONGID = "songid";
	public final static String BROADCAST_KEY_ALBUMID = "albumid";
	public final static String BROADCAST_KEY_POSITION = "position";
	public final static String BROADCAST_KEY_REPEATMODE = "repeatmode";
	public final static String BROADCAST_KEY_SHUFFLEMODE = "shufflemode";
	public final static String BROADCAST_KEY_DURATION = "duration";
	public final static String BROADCAST_KEY_LISTSIZE = "listsize";
	public final static String BROADCAST_KEY_STATE = "state";
	public final static String BROADCAST_KEY_APP_NAME = "app-name";
	public final static String BROADCAST_KEY_APP_PACKAGE = "app-package";
	public final static String BROADCAST_KEY_LYRICS_STATUS = "lyrics_status";
	public final static String BROADCAST_KEY_LYRICS_ID = "lyrics_id";
	public final static String BROADCAST_KEY_LYRICS = "lyrics";

	
	public final static String BROADCAST_PLAYSTATE_CHANGED = "com.hufeng.playmusic.playstatechanged";
	public final static String BROADCAST_META_CHANGED = "com.hufeng.playmusic.metachanged";
	public final static String BROADCAST_FAVORITESTATE_CHANGED = "com.hufeng.playmusic.favoritestatechanged";
	public final static String BROADCAST_NEW_LYRICS_LOADED = "com.hufeng.playmusic.newlyricsloaded";
	public final static String BROADCAST_LYRICS_REFRESHED = "com.hufeng.playmusic.lyricsrefreshed";
	public final static String BROADCAST_QUEUE_CHANGED = "com.hufeng.playmusic.queuechanged";
	public final static String BROADCAST_REPEATMODE_CHANGED = "com.hufeng.playmusic.repeatmodechanged";
	public final static String BROADCAST_SHUFFLEMODE_CHANGED = "com.hufeng.playmusic.shufflemodechanged";
	public final static String BROADCAST_PLAYBACK_COMPLETE = "com.hufeng.playmusic.playbackcomplete";
	public final static String BROADCAST_ASYNC_OPEN_COMPLETE = "com.hufeng.playmusic.asyncopencomplete";
	public final static String BROADCAST_REFRESH_PROGRESSBAR = "com.hufeng.playmusic.refreshprogress";
	public final static String BROADCAST_PLAYSTATUS_REQUEST = "com.hufeng.playmusic.playstatusrequest";
	public final static String BROADCAST_PLAYSTATUS_RESPONSE = "com.hufeng.playmusic.playstatusresponse";
	
	public final static int SCROBBLE_PLAYSTATE_START = 0;
	public final static int SCROBBLE_PLAYSTATE_RESUME = 1;
	public final static int SCROBBLE_PLAYSTATE_PAUSE = 2;
	public final static int SCROBBLE_PLAYSTATE_COMPLETE = 3;
	
	public final static String MAP_KEY_NAME = "name";
	public final static String MAP_KEY_ID = "id";
	
	public final static long PLAYLIST_UNKNOWN = -1;
	public final static long PLAYLIST_ALL_SONGS = -2;
	public final static long PLAYLIST_QUEUE = -3;
	public final static long PLAYLIST_NEW = -4;
	public final static long PLAYLIST_FAVORITES = -5;
	public final static long PLAYLIST_RECENTLY_ADDED = -6;
	public final static long PLAYLIST_PODCASTS = -7;
	
	public final static String STATE_KEY_CURRENTTAB = "currenttab";
	public final static String STATE_KEY_CURRPOS = "curpos";
	public final static String STATE_KEY_CARDID = "cardid";
	public final static String STATE_KEY_QUEUE = "queue";
	public final static String STATE_KEY_HISTORY = "history";
	public final static String STATE_KEY_SEEKPOS = "seekpos";
	public final static String STATE_KEY_REPEATMODE = "repeatmode";
	public final static String STATE_KEY_SHUFFLEMODE = "shufflemode";
	
	public final static String SCROBBLE_SLS_API = "com.adam.aslfms.notify.playstatechanged";
	
	/**
	 * Following genres data is copied from from id3lib 3.8.3
	 */
	public final static String[] GENRES_DB = { "Blues", "Classic Rock", "Country", "Dance",
			"Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other",
			"Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska",
			"Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal",
			"Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game",
			"Sound Clip", "Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk", "Space",
			"Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave",
			"Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock",
			"Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle",
			"Native American", "Cabaret", "New Wave", "Psychedelic", "Rave", "Showtunes",
			"Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical",
			"Rock & Roll", "Hard Rock", "Folk", "Folk-Rock", "National Folk", "Swing",
			"Fast Fusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde",
			"Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock", "Slow Rock",
			"Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson",
			"Opera", "Chamber Music", "Sonata", "Symphony", "Booty Bass", "Primus", "Porn Groove",
			"Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad",
			"Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo", "A capella",
			"Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror",
			"Indie", "Britpop", "Negerpunk", "Polsk Punk", "Beat", "Christian Gangsta Rap",
			"Heavy Metal", "Black Metal", "Crossover", "Contemporary Christian", "Christian Rock ",
			"Merengue", "Salsa", "Thrash Metal", "Anime", "JPop", "Synthpop" };

}
