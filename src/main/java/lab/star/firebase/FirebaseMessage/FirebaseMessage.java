package lab.star.firebase.FirebaseMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FirebaseMessage {
	public enum Priority {
		NORMAL("normal"), HIGH("high");
		private final String value;

		Priority(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return this.value;
		}
	}

	private String registrationToken;
	private Data data;
	private Notification notification;
	private Priority priority;
	private int ttl;
	private boolean collapsible;
	private boolean delayWhileIdeal;
	private int connTimeOut;
	private Set<String> regIds;
	private String projectId;
	private String keyName;
	private String notificationKey;

	private FirebaseMessage() {
		super();
		this.priority = Priority.NORMAL;
		this.ttl = Constants.DEFAUTL_TIME_TO_LIVE;
		this.collapsible = false;
		this.delayWhileIdeal = false;
		this.connTimeOut = Constants.DEFAUTL_CONNECTION_TIMEOUT;
	}

	public FirebaseMessage to(String to) {
		this.regIds = new HashSet<String>();
		regIds.add(to);
		return this;
	}

	public FirebaseMessage to(Set<String> regIds) {
		this.regIds = regIds;
		return this;
	}

	public static FirebaseMessage intialize(String registration_token) {
		FirebaseMessage firebaseMessage = new FirebaseMessage();
		firebaseMessage.registrationToken = "key=" + registration_token;
		return firebaseMessage;
	}

	public FirebaseMessage connTimeOut(int connTimeOut) {
		this.connTimeOut = connTimeOut;
		return this;
	}

	public FirebaseMessage collapsible(boolean collapsible) {
		this.collapsible = collapsible;
		return this;
	}

	public FirebaseMessage data(Data data) {
		this.data = data;
		return this;
	}

	public FirebaseMessage priority(Priority priority) {
		this.priority = priority;
		return this;
	}

	public FirebaseMessage lifespan(int ttl) {
		this.ttl = ttl;
		return this;
	}

	public FirebaseMessage notification(Notification notification) {
		this.notification = notification;
		return this;
	}

	public FirebaseMessage ttl(int ttl) {
		this.ttl = ttl;
		return this;
	}

	public FirebaseMessage delayWhileIdeal(boolean delayWhileIdeal) {
		this.delayWhileIdeal = delayWhileIdeal;
		return this;
	}

	private ObjectNode getPayload() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode object = mapper.createObjectNode();
		if (notification != null)
			object.putPOJO(Constants.JSON_NOTIFICATION, notification);
		if (data != null)
			object.putPOJO(Constants.JSON_PAYLOAD, data.getData());
		object.put(Constants.PARAM_PRIORITY, priority.toString());
		object.put(Constants.PARAM_TIME_TO_LIVE, ttl);
		object.put(Constants.PARAM_DELAY_WHILE_IDLE, delayWhileIdeal);
		if (regIds != null) {
			if (regIds.size() == 1) {
				object.put(Constants.PARAM_TO, regIds.iterator().next());
			} else if (regIds.size() > 1) {
				object.putPOJO(Constants.PARAM_REGISTRATION_IDS, regIds);
			}
		}
		if (collapsible)
			object.put(Constants.PARAM_COLLAPSE_KEY, Constants.COLLAPSE_KEY);
		return object;
	}

	private void checkInput() {
		if (notification == null && data == null)
			throw new IllegalArgumentException("Invalid Input");
		if (regIds == null || (regIds != null && regIds.isEmpty()))
			throw new IllegalArgumentException("Invalid Input");
		if (notification != null) {
			notification.checkInput();
		}
	}

	/**
	 * Synchronous message sending options
	 * 
	 * @param notification
	 */

	public HttpResponse send() {
		return doNetworkOp(this);
	}

	private HttpResponse doNetworkOp(FirebaseMessage message) {
		checkInput();
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(Constants.FCM_SEND_ENDPOINT);
		this.connTimeOut = connTimeOut == 0 ? Constants.DEFAUTL_CONNECTION_TIMEOUT : connTimeOut;
		RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(connTimeOut)
				.setConnectTimeout(connTimeOut).setSocketTimeout(connTimeOut).build();
		post.setConfig(config);
		post.setHeader(Constants.PARAM_HEADER_SERVER_KEY, registrationToken);
		post.setHeader(Constants.PARAM_HEADER_CONTENT_TYPE, Constants.HEADER_CONTENT_TYPE_JSON);
		String jsonBody = null;
		try {
			jsonBody = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(getPayload());
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		post.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
		HttpResponse response = null;
		try {
			response = client.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}
		return response;
	}

	/**
	 * Asynchronous message sending options
	 * 
	 * @param notification
	 */
	public void send(final DelivaryNotification notification) {
		if (notification == null)
			throw new IllegalArgumentException("Invalid Parameter");
		ExecutorService executor = Executors.newFixedThreadPool(1);
		AsyncNetworkTask task = new AsyncNetworkTask(this);
		Future<HttpResponse> response = null;
		try {
			response = executor.submit(task);
		} catch (RejectedExecutionException | NullPointerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HttpResponse result = null;
		try {
			result = response.get(10, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		try {
			executor.shutdown();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		if (result != null && result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			notification.onSuccess(this);
		} else {
			notification.onFailed(result, this);
		}
	}

	class AsyncNetworkTask implements Callable<HttpResponse> {
		final FirebaseMessage message;

		AsyncNetworkTask(FirebaseMessage message) {
			this.message = message;
		}

		public HttpResponse call() {
			return doNetworkOp(message);
		}

	}

	public FirebaseMessage project(String sender) {
		this.projectId = sender;
		return this;
	}

	public FirebaseMessage instanceIds(Set<String> sets) {
		this.regIds = sets;
		return this;
	}

	public FirebaseMessage notificationKey(String key) {
		this.notificationKey = key;
		return this;
	}

	public FirebaseMessage notificationKeyName(String key_name) {
		this.keyName = key_name;
		return this;
	}

	private void checkInputForDeviceGroupMessage() {
		if (this.registrationToken == null
				|| (this.registrationToken != null && this.registrationToken.length() == 0)) {
			throw new IllegalArgumentException("Invalid Input");
		}
		if (this.projectId == null || (this.projectId != null && this.projectId.length() == 0)) {
			throw new IllegalArgumentException("Invalid Input");
		}
		if (this.regIds == null || (this.projectId != null && this.regIds.size() == 0)) {
			throw new IllegalArgumentException("Invalid Input");
		}
	}

	public String createDeviceGroup() {
		checkInputForDeviceGroupMessage();
		// write up your network code here
		if (this.keyName == null || (this.keyName != null && this.keyName.length() == 0)) {
			throw new IllegalArgumentException("Invalid Input");
		}

		// Make json payload for create new group of device
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode object = mapper.createObjectNode();
		object.put("operation", "create");
		object.put("notification_key_name", keyName);
		object.putPOJO("registration_ids", regIds);

		String jsonBody = null;
		try {
			jsonBody = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}

		return makeHttpPostForDeviceGroup(jsonBody);
	}

	public String addDeviceGroup() {
		checkInputForDeviceGroupMessage();
		if (this.notificationKey == null || (this.notificationKey != null && this.notificationKey.length() == 0)) {
			throw new IllegalArgumentException("Invalid Input");
		}
		if (this.keyName == null || (this.keyName != null && this.keyName.length() == 0)) {
			throw new IllegalArgumentException("Invalid Input");
		}
		// Make json payload for add device in group
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode object = mapper.createObjectNode();
		object.put("operation", "add");
		object.put("notification_key_name", keyName);
		object.put("notification_key", notificationKey);
		object.putPOJO("registration_ids", regIds);

		String jsonBody = null;
		try {
			jsonBody = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}

		return makeHttpPostForDeviceGroup(jsonBody);
	}

	public String removeDeviceGroup() {
		checkInputForDeviceGroupMessage();
		if (this.notificationKey == null || (this.notificationKey != null && this.notificationKey.length() == 0)) {
			throw new IllegalArgumentException("Invalid Input");
		}

		// Make json payload for remove device in group
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode object = mapper.createObjectNode();
		object.put("operation", "remove");
		object.put("notification_key_name", keyName);
		object.put("notification_key", notificationKey);
		object.putPOJO("registration_ids", regIds);

		String jsonBody = null;
		try {
			jsonBody = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}

		return makeHttpPostForDeviceGroup(jsonBody);

	}

	public String makeHttpPostForDeviceGroup(String json) {
		HttpPost httpPost = new HttpPost(Constants.FCM_DEVICE_GROUP_ENDPOINT);
		try {
			httpPost.setEntity(new StringEntity(json));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Authorization", this.registrationToken);
		httpPost.setHeader("Content-type", "application/json");
		httpPost.setHeader("project_id", projectId);
		HttpResponse response = null;
		try {
			response = HttpClientBuilder.create().build().execute(httpPost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (response == null)
			return null;

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		} catch (UnsupportedOperationException | IOException e) {
			e.printStackTrace();
		}
		String jsonResponse = "";
		String line=null;
		try {
			while((line = reader.readLine()) != null)
			jsonResponse = jsonResponse+line;
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Extract notification Key
		JSONObject jObject = new JSONObject(jsonResponse);
		try {
			return jObject.getString("notification_key");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private interface Constants {

		/**
		 * Endpoint for sending messages.
		 */
		public static final String FCM_SEND_ENDPOINT = "https://fcm.googleapis.com/fcm/send";

		/**
		 * Endpoint for create/add/remove device groups.
		 */
		public static final String FCM_DEVICE_GROUP_ENDPOINT = "https://android.googleapis.com/gcm/notification";
		
		/**
		 * User defined collapse-key for collapse parameter. Maximum 4 keys
		 * allowed for single device to use collapse.
		 */
		public static final String COLLAPSE_KEY = "collapse_key";

		/**
		 * Parameter for Header content-type.
		 */

		public static final String PARAM_HEADER_CONTENT_TYPE = "Content-Type";

		/**
		 * value for default connection time-Out.
		 */

		public static final int DEFAUTL_CONNECTION_TIMEOUT = 10 * 1000;

		/**
		 * value for default connection time-Out.
		 */

		public static final int DEFAUTL_TIME_TO_LIVE = 4 * 7 * 24 * 60 * 60;

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
		 * HTTP parameter for delaying the message delivery if the device is
		 * idle.
		 */
		public static final String PARAM_DELAY_WHILE_IDLE = "delay_while_idle";

		/**
		 * HTTP parameter for telling gcm to validate the message without
		 * actually sending it.
		 */
		public static final String PARAM_DRY_RUN = "dry_run";

		/**
		 * HTTP parameter for package name that can be used to restrict message
		 * delivery by matching against the package name used to generate the
		 * registration id.
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
		 * JSON-only field representing the payload data.
		 */
		public static final String JSON_PAYLOAD = "data";

		/**
		 * JSON-only field representing the notification payload.
		 */
		public static final String JSON_NOTIFICATION = "notification";

		public static final String ERROR_QUOTA_EXCEEDED = "QuotaExceeded";

		/**
		 * Too many messages sent by the sender to a specific device. Retry
		 * after a while.
		 */
		public static final String ERROR_DEVICE_QUOTA_EXCEEDED = "DeviceQuotaExceeded";

		/**
		 * Missing registration_id. Sender should always add the registration_id
		 * to the request.
		 */
		public static final String ERROR_MISSING_REGISTRATION = "MissingRegistration";

	}

}