# Documentation a email

## Introduction

- Mail server: Email Google
- AI: Claude AI

## API usage

### Gmail

- Get list email
  - API: [GET] https://gmail.googleapis.com/gmail/v1/users/{userId}/messages
  - Reference: https://developers.google.com/gmail/api/reference/rest/v1/users.messages/list
- Send message:
  - API: [POST] https://gmail.googleapis.com/gmail/v1/users/{userId}/messages/send
  - Reference: https://developers.google.com/gmail/api/reference/rest/v1/users.messages/send
- Get list email by threads:
  - API: [GET] https://gmail.googleapis.com/gmail/v1/users/{userId}/threads/{id}
  - Reference: https://developers.google.com/gmail/api/reference/rest/v1/users.threads/get
- Update label read/unread;
  - API: [PUT] https://gmail.googleapis.com/gmail/v1/users/{userId}/labels/{id}
  - Reference: https://developers.google.com/gmail/api/reference/rest/v1/users.labels/update

### Claude AI

-  