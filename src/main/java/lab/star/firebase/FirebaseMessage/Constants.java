package lab.star.firebase.FirebaseMessage;

/**
 * Constants used on Firebase Cloud Messaging service communication.
 */
public final class Constants {

  /**
   * Endpoint for sending messages.
   */
  public static final String FCM_SEND_ENDPOINT =
      "https://fcm.googleapis.com/fcm/send";
  
  /**
   * User defined collapse-key for collapse parameter.
   * Maximum 4 keys allowed for single device to use collapse.
   */
  public static final String COLLAPSE_KEY="collapse_key"; 
	

  /**
   * Parameter for Header content-type.
   */

  public static final String PARAM_HEADER_CONTENT_TYPE = "Content-Type";
 
  /**
   * value for default connection time-Out.
   */

  public static final int DEFAUTL_CONNECTION_TIMEOUT = 10;

  /**
   * Parameter value for Header content-type.
   */

  public static final String HEADER_CONTENT_TYPE_JSON = "application/json";
  
  /**
   * Parameter for Header authorization server-key.
   */

  public static final String PARAM_HEADER_SERVER_KEY = "Authorization";
  
  /**
   * Parameter for to field.
   */

  public static final String PARAM_TO = "to";

  /**
   * Prefix of the topic.
   */
  public static final String PARAM_REGISTRATION_IDS = "registration_ids";

  /**
   * HTTP parameter for collapse key.
   */
  public static final String PARAM_COLLAPSE_KEY = "collapse_key";

  /**
   * HTTP parameter for delaying the message delivery if the device is idle.
   */
  public static final String PARAM_DELAY_WHILE_IDLE = "delay_while_idle";

  /**
   * HTTP parameter for telling gcm to validate the message without actually sending it.
   */
  public static final String PARAM_DRY_RUN = "dry_run";

  /**
   * HTTP parameter for package name that can be used to restrict message delivery by matching
   * against the package name used to generate the registration id.
   */
  public static final String PARAM_RESTRICTED_PACKAGE_NAME = "restricted_package_name";

  /**
   * Parameter used to set the message time-to-live.
   */
  public static final String PARAM_TIME_TO_LIVE = "time_to_live";

  /**
   * Parameter used to set the message priority.
   */
  public static final String PARAM_PRIORITY = "priority";

  /**
   * Parameter used to set the content available (iOS only)
   */
  public static final String PARAM_CONTENT_AVAILABLE = "content_available";

  /**
   * Too many messages sent by the sender. Retry after a while.
   */
  
  /**
   * JSON-only field representing the payload data.
   */
  public static final String JSON_PAYLOAD = "data";

  /**
   * JSON-only field representing the notification payload.
   */
  public static final String JSON_NOTIFICATION = "notification";

  public static final String ERROR_QUOTA_EXCEEDED = "QuotaExceeded";

  /**
   * Too many messages sent by the sender to a specific device.
   * Retry after a while.
   */
  public static final String ERROR_DEVICE_QUOTA_EXCEEDED ="DeviceQuotaExceeded";

  /**
   * Missing registration_id.
   * Sender should always add the registration_id to the request.
   */
  public static final String ERROR_MISSING_REGISTRATION = "MissingRegistration";

  private Constants() {
    throw new UnsupportedOperationException();
  }

}
