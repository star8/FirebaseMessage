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

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Data {
	@JsonProperty
	private HashMap<String, Object> data;
	
	private Data(){
		data=new HashMap<String, Object>();
	}
	
	public static Data add(String key, Object value){
		Data data=new Data();
		data.data.put(key, value);
		return data;
		
	}

	HashMap<String, Object> getData() {
		return data;
	}
	

}
