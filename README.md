# Firebase Message

A Java library to help you send downstream message to a client. 

## Installation

Follow the steps as below:

1) `git clone git@github.com:star8/FirebaseMessage.git`

2) `Make jar file of the project and copy in your MAIN PROJECT`
 
3) Add below jar dependency to your project pom.xml
 
```xml
 <dependency>
		    <groupId>lab.star.firebase</groupId>
		    <artifactId>FirebaseMessage</artifactId>
		    <version>0.0.1-SNAPSHOT</version>
		    <scope>system</scope>
		    <systemPath>${project.basedir}/${location of file}/FirebaseMessage.jar</systemPath>
	</dependency>
  <dependency>
	    	<groupId>org.apache.httpcomponents</groupId>
	    	<artifactId>httpclient</artifactId>
	    	<version>4.5.2</version>
	</dependency>
```


## Sample usage

To send message synchronously

1) send notification

```java
FirebaseMessage.intialize("YOUR_SERVER_KEY").to("user_registration_token")
.notification(Notification.body("MSG_BODY").title("MSG_TITLE").send();
```

2) Send message

```java
FirebaseMessage.intialize("YOUR_SERVER_KEY").to("user_registration_token")
.data(Data.add("KEY1", "VALUE1").add("KEY2", "VALUE2")).send();
```

3) Send message & notification

```java
FirebaseMessage.intialize("YOUR_SERVER_KEY").to("user_registration_token").data(Data.add("KEY1","VALUE1")
.priority(Priority.NORMAL).notification(Notification.body("BODY")
.title("YOUR_TITLE").icon("Notification icon")).send();
```

To create Group of devices for single user having multiple devices


1) Create Group of Devices

```javaString 
String notificationKey= FirebaseMessage.intialize("<SERVER_API_KEY>").instanceIds("<LIST_OF_REGIDS>")
.project("<SENDER_ID>").notificationKeyName("<NEW_NOTIFICATION_KEYNAME>").createDeviceGroup();
```

2) Add device in Group

```java
String notificationKey= FirebaseMessage.intialize("SERVER_API_KEY>").instanceIds(<LIST_OF_REGIDS>)
.project("<SENDER_ID>").notificationKey("<NOTIFICATION_KEY_OF_DEVICE_GROUP>")
.notificationKeyName("<NOTIFICATION_KEYNAME>").addDeviceGroup();
```

3) Delete device from Group

```java
String notificationKey= FirebaseMessage.intialize("SERVER_API_KEY>").instanceIds(<LIST_OF_REGIDS>)
.project("<SENDER_ID>").notificationKey("<NOTIFICATION_KEY_OF_DEVICE_GROUP>")
.notificationKeyName("<NOTIFICATION_KEYNAME>").removeDeviceGroup();
```

Use/Store the notificationKey to send message to list/group of devices through send methods mentioned above.

# Firebase Cloud Messaging

Firebase Cloud Messaging (FCM) is a service that lets developers send data from
servers to users' devices, and receive messages from devices on the same
connection. The service provides a simple, lightweight mechanism that servers
can use to tell mobile applications to contact the server directly to fetch
updated application user data. The FCM service handles all aspects of queueing
of messages and delivery to client applications running on target devices.

This project contains client libraries and samples to help developers interface
with and explore the Firebase Cloud Messaging APIs.

For more information on FCM, including an overview and integration
instructions, see [Cloud Messaging](https://firebase.google.com/docs/cloud-messaging/).

For help getting started with GCM, see the
[FCM Quickstart for Android](https://firebase.google.com/docs/cloud-messaging/android/client)
or the [FCM Quickstart for iOS](https://firebase.google.com/docs/cloud-messaging/ios/client).

## Support

- Stack Overflow: http://stackoverflow.com/questions/tagged/firebase-cloud-messaging

If you've found an error in this project's code, please file an issue:
https://github.com/star8/FirebaseMessage/issues



## Contributing

1. Fork this repo and make changes in your own copy
2. Add a test if applicable and run the existing tests with `mvn clean test` to make sure they pass
3. Commit your changes and push to your fork `git push origin master`
4. Create a new pull request and submit it back to us!

## License

  Copyright (C) 2016 Star labs.
 
  Licensed under the Apache License, Version 2.0 (the "License"),
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
       http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
