package lab.star.firebase.FirebaseMessage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Apns {
	
	@JsonProperty
	private JsonNode headers;
	@JsonProperty
	private JsonNode payload;
	
	
	public static Apns headers(JsonNode body) {
		Apns notification = new Apns();
		notification.headers = body;
		return notification;
	}

	public Apns payload(JsonNode payload) {
		this.payload=payload;
		return this;
	}
	JsonNode getApns() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode object = mapper.createObjectNode();
		if (headers!=null) {
			object.set("headers", headers);
		}
		if (payload!=null) {
			object.set("payload", payload);
		}
		return object;
	}
	

}
