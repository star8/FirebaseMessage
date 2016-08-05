package lab.star.firebase.FirebaseMessage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@JsonInclude(Include.NON_NULL)
public class Notification {
	@JsonProperty
	private String body;
	@JsonProperty
	private String title;
	@JsonProperty
	private String icon;

	private Notification() {

	}

	public static Notification body(String body) {
		Notification notification = new Notification();
		notification.body = body;
		return notification;
	}

	public Notification title(String title) {
		this.title=title;
		return this;
	}

	public Notification icon(String icon) {
		this.icon=icon;
		return this;
	}

}
