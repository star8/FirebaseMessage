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

import org.apache.http.HttpResponse;

public class FirebaseMessage {
	enum Priority{
		NORMAL, HIGH
	}
	
	private String userId;
	private String registrationToken;
	private Data data;
	private Notification notification;
	private Priority priority;
	private int ttl=-1;
	
	private FirebaseMessage(){
		super();
	}
	public FirebaseMessage to(String to){
		this.userId=to;
		return this;
	}
	public static FirebaseMessage intialize(String registration_token){
		FirebaseMessage firebaseMessage=new FirebaseMessage();
		firebaseMessage.registrationToken=registration_token;
		return firebaseMessage;
	}
	public FirebaseMessage data(Data data){
		this.data=data;
		return this;
	}
	public FirebaseMessage priority(Priority priority){
		this.priority=priority;
		return this;
	}
	public FirebaseMessage lifespan(int ttl){
		this.ttl=ttl;
		return this;
	}
	public FirebaseMessage notification(Notification notification){
		this.notification=notification;
		return this;
	}
	
	/**
	 * Synchronous message sending options
	 * @param notification
	 */
	
	public HttpResponse send(){
		return null;
		
	}
	/**
	 * Asynchronous message sending options
	 * @param notification
	 */
	public void send(DelivaryNotification notification){
		
		
	}
	


}
