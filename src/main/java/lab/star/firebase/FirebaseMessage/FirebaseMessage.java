package lab.star.firebase.FirebaseMessage;

/*
 * Copyright (C) 2016 Star labs.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
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

	public enum DeviceGroupOperation {
		CREATE("create"), ADD("add"), REMOVE("remove");
		private final String value;

		DeviceGroupOperation(String value) {
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

	private FirebaseMessage() {
		super();
		this.priority=Priority.NORMAL;
		this.ttl=Constants.DEFAUTL_TIME_TO_LIVE;
		this.collapsible=false;
		this.delayWhileIdeal=false;
		this.connTimeOut= Constants.DEFAUTL_CONNECTION_TIMEOUT;
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
	
	public String createDeviceGroup(String projectId, String notificationKeyName, Set<String> regIds)
	        throws IOException {

	    // HTTP request
	    ObjectMapper mapper = new ObjectMapper();
		ObjectNode object = mapper.createObjectNode();
	    object.put(Constants.JSON_OPERATION_KEY, Constants.DEVICE_GROUP_CREATE);
	    object.put(Constants.JSON_NOTIFICATION_KEY_NAME, notificationKeyName);
	    object.putPOJO(Constants.PARAM_REGISTRATION_IDS, regIds);
	    
		String jsonBody=null;
		try {
			jsonBody = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	    HttpResponse response=makeHttpPostWithJSONRequest(Constants.FCM_GROUP_ENDPOINT, jsonBody, projectId);
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		String json = reader.readLine();
		JSONObject jObject = new JSONObject(json);


	}

	public static HttpResponse makeHttpPostWithJSONRequest(String path, String json, String projectId) {
		try {
			HttpPost httpPost = new HttpPost(path);
			httpPost.setEntity(new StringEntity(json));
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			httpPost.setHeader("project_id", projectId);
			return HttpClientBuilder.create().build().execute(httpPost);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ObjectNode getPayloadForGroupCreate(String notificationKeyName, Set<String> regIds) {
	    ObjectMapper mapper = new ObjectMapper();
		ObjectNode object = mapper.createObjectNode();
	    object.put(Constants.JSON_OPERATION_KEY, Constants.DEVICE_GROUP_CREATE);
	    object.put(Constants.JSON_NOTIFICATION_KEY_NAME, notificationKeyName);
	    object.putPOJO(Constants.PARAM_REGISTRATION_IDS, regIds);
	    return object;
	}
	
	public static ObjectNode getPayloadForGroupAdd(String notificationKey, Set<String> regIds) {
	    ObjectMapper mapper = new ObjectMapper();
		ObjectNode object = mapper.createObjectNode();
	    object.put(Constants.JSON_OPERATION_KEY, Constants.DEVICE_GROUP_ADD);
	    object.put(Constants.JSON_NOTIFICATION_KEY, notificationKey);
	    object.putPOJO(Constants.PARAM_REGISTRATION_IDS, regIds);
	    return object;
	}
	
	public static ObjectNode getPayloadForGroupRemove(String notificationKey, Set<String> regIds) {
	    ObjectMapper mapper = new ObjectMapper();
		ObjectNode object = mapper.createObjectNode();
	    object.put(Constants.JSON_OPERATION_KEY, Constants.DEVICE_GROUP_REMOVE);
	    object.put(Constants.JSON_NOTIFICATION_KEY, notificationKey);
	    object.putPOJO(Constants.PARAM_REGISTRATION_IDS, regIds);
	    return object;
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
	private void checkInput(){
		if (notification==null && data==null)
			throw new IllegalArgumentException("Invalid Input");
		if (regIds==null || (regIds!=null && regIds.isEmpty())) 
			throw new IllegalArgumentException("Invalid Input");
		if (notification!=null){
			notification.checkInput();
		}
	}

	/**
	 * Synchronous message sending options
	 * 
	 * @param notification
	 */

	public HttpResponse send(){
		return doNetworkOp(this);
	}
	private HttpResponse doNetworkOp(FirebaseMessage message){
		checkInput();
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(Constants.FCM_SEND_ENDPOINT);
		this.connTimeOut =connTimeOut == 0? Constants.DEFAUTL_CONNECTION_TIMEOUT:connTimeOut;
		RequestConfig config =
				RequestConfig.custom().setConnectionRequestTimeout(connTimeOut).setConnectTimeout(connTimeOut)
						.setSocketTimeout(connTimeOut ).build();
		post.setConfig(config);
		post.setHeader(Constants.PARAM_HEADER_SERVER_KEY, registrationToken);
		post.setHeader(Constants.PARAM_HEADER_CONTENT_TYPE, Constants.HEADER_CONTENT_TYPE_JSON);
		String jsonBody=null;
		try {
			jsonBody = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(getPayload());
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		post.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
		HttpResponse response=null;
		try {
			response=client.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
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
		Future<HttpResponse> response=null;
		try {
			response = executor.submit(task);
		} catch (RejectedExecutionException|NullPointerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HttpResponse result = null;
		try {
			result = response.get(10, TimeUnit.SECONDS);
		} catch (InterruptedException |ExecutionException|TimeoutException e) {
			e.printStackTrace();
		}
		try{
			executor.shutdown();
		}catch (SecurityException e) {
			e.printStackTrace();
		}
		
		if (result != null && result.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
			notification.onSuccess(this);
		}else{
			notification.onFailed(result, this);
		}
	}

	class AsyncNetworkTask implements Callable<HttpResponse> {
		final FirebaseMessage message;

		AsyncNetworkTask(FirebaseMessage message) {
			this.message = message;
		}

		public HttpResponse call(){
			return doNetworkOp(message);
		}

	}

	private interface Constants {

		/**
		 * Endpoint for sending messages.
		 */
		public static final String FCM_SEND_ENDPOINT = "https://fcm.googleapis.com/fcm/send";

		/**
		 * Endpoint for create/add/remove device groups.
		 */
		public static final String FCM_GROUP_ENDPOINT = "https://android.googleapis.com/gcm/googlenotification";

		/**
		 * User defined collapse-key for collapse parameter. Maximum 4 keys allowed for single
		 * device to use collapse.
		 */
		public static final String COLLAPSE_KEY = "collapse_key";


		/**
		 * Parameter for Header content-type.
		 */

		public static final String PARAM_HEADER_CONTENT_TYPE = "Content-Type";

		/**
		 * value for default connection time-Out.
		 */

		public static final int DEFAUTL_CONNECTION_TIMEOUT = 10*1000;
		
		/**
		 * value for default connection time-Out.
		 */

		public static final int DEFAUTL_TIME_TO_LIVE = 4*7*24*60*60;


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
		 * JSON-only field representing the payload data.
		 */
		public static final String JSON_PAYLOAD = "data";

		/**
		 * JSON-only field representing the notification payload.
		 */
		public static final String JSON_NOTIFICATION = "notification";

		public static final String ERROR_QUOTA_EXCEEDED = "QuotaExceeded";

		/**
		 * Too many messages sent by the sender to a specific device. Retry after a while.
		 */
		public static final String ERROR_DEVICE_QUOTA_EXCEEDED = "DeviceQuotaExceeded";

		/**
		 * Missing registration_id. Sender should always add the registration_id to the request.
		 */
		public static final String ERROR_MISSING_REGISTRATION = "MissingRegistration";

		/**
		 * HTTP JSON parameter for creating group of device.
		 */
		public static final String DEVICE_GROUP_CREATE = "create";
		
		/**
		 * HTTP JSON parameter for adding device in group of device.
		 */
		public static final String DEVICE_GROUP_ADD = "add";
		
		/**
		 * HTTP JSON parameter for deleting device from group of device.
		 */
		public static final String DEVICE_GROUP_REMOVE = "remove";
		
		/**
		 * JSON-only field representing the operation type of device group operation.
		 */
		public static final String JSON_OPERATION_KEY = "operation";
		
		/**
		 * JSON-only field representing the unique Notification key name for device group.
		 */
		public static final String JSON_NOTIFICATION_KEY_NAME = "notification_key_name";

		/**
		 * JSON-only field representing the unique Notification key for device group.
		 */
		public static final String JSON_NOTIFICATION_KEY = "notification_key";
	
	}

}
