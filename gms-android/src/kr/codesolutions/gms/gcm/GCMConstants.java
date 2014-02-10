package kr.codesolutions.gms.gcm;

public class GCMConstants {
	
    public static final String TAG = "GMS";
    public static final String PROPERTY_REG_ID = "registrationId";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String DISPLAY_MESSAGE_ACTION = "kr.codesolutions.gms.DISPLAY_MESSAGE";
    public static final String ERROR_MESSAGE_ACTION = "kr.codesolutions.gms.ERROR_MESSAGE";
    public static final int NOTIFICATION_ID = 91756942;
    
	/**
	 * Intent's extra that contains the message to be displayed.
	 */
    public static final String GMS_ERROR = "GMS.error";
    public static final String GMS_ERRORCODE = "GMS.errorcode";
    public static final String GMS_ERROR_REGISTOR_FAILED = "REGISTER_FAILED";
    public static final String GMS_ERROR_UNREGISTOR_FAILED = "UNREGISTER_FAILED";
    
    public static final String GMS_MESSAGE_ID = "GMS.id";
    public static final String GMS_MESSAGE_OWNTYPE = "GMS.ownType";
    public static final String GMS_MESSAGE_MSGTYPE = "GMS.msgType";
    public static final String GMS_MESSAGE_SUBJECT = "GMS.subject";
    public static final String GMS_MESSAGE_CONTENT = "GMS.content";
    public static final String GMS_MESSAGE_SENDER_USERID = "GMS.senderId";


}
