package lab.star.firebase.FirebaseMessage;

/*
 * Copyright (C) 2016 Star labs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;

import org.apache.http.HttpResponse;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FirebaseMessage {
	enum Priority {
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

	private String userId;
	private String registrationToken;
	private Data data;
	private Notification notification;
	private Priority priority;
	private int ttl = -1;
	private boolean collapsible;
	private boolean delayWhileIdeal;

	private FirebaseMessage() {
		super();
	}

	public FirebaseMessage to(String to) {
		this.userId = to;
		return this;
	}

	public static FirebaseMessage intialize(String registration_token) {
		FirebaseMessage firebaseMessage = new FirebaseMessage();
		firebaseMessage.registrationToken = registration_token;
		return firebaseMessage;
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

	public FirebaseMessage timeToLive(int ttl) {
		this.ttl = ttl;
		return this;
	}

	public FirebaseMessage delayWhileIdeal(boolean delayWhileIdeal) {
		this.delayWhileIdeal = delayWhileIdeal;
		return this;
	}

	public ObjectNode getPayload() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode object = mapper.createObjectNode(); 
		if(notification!=null)
		object.putPOJO(JsonKey.NOTIFICATION, notification);
		if(data!=null)
		object.putPOJO(JsonKey.DATA, data.getData());
		object.put(JsonKey.TO, userId);
		object.put(JsonKey.PRIORITY, priority.toString());
		if(ttl>0)
		object.put(JsonKey.TIME_TO_LIVE, ttl);
		object.put(JsonKey.DELAY_WHILE_IDLE, delayWhileIdeal);
		return object;
	}

	/**
	 * Synchronous message sending options
	 * 
	 * @param notification
	 */

	public HttpResponse send() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		try {
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(getPayload()));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * Asynchronous message sending options
	 * 
	 * @param notification
	 */
	public void send(DelivaryNotification notification) {


	}

	interface JsonKey {
		final String NOTIFICATION = "notification";
		final String DATA = "data";
		final String TO = "to";
		final String COLLAPSE_KEY = "collapse_key";
		final String PRIORITY = "priority";
		final String REGISTRATION_IDS = "registration_ids";
		final String TIME_TO_LIVE = "time_to_live";
		final String DELAY_WHILE_IDLE = "delay_while_idle";
		final String CONTENT_AVAILABLE = "content_available";
		final String RESTRICTED_PACKAGE_NAME = "restricted_package_name";
		final String DRY_RUN = "dry_run";
	}
}
