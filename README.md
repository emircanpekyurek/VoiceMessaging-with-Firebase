# Usage
You need to add google-services.json and server key to use the app.
  - SERVER_KEY is inside the PushNotificationConstants class.

# Description
- Login: Firstly, sign in with the Google account and create a unique nickname if you haven't created it before.
- Select User: Users with chat history are listed first. If you want, you can list all or search for users.
- Messaging: Record your voice, choose the effect you want and send the recording

# Using Technology

- Firebase
  - Firebase Authentication
  - Firebase Firestore realtime database (for chat)
  - Firebase datastore (for voice file)
  - Firebase push notification
- Hilt
- FFmpeg (for audio effect)
- MVVM
- Kotlin
- UseCase
- Night Mode