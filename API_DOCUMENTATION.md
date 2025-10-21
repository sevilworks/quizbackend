# Quiz Platform API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication
All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Endpoints

### Authentication Endpoints

#### 1. Login
- **POST** `/auth/login`
- **Body:**
```json
{
  "username": "string",
  "password": "string"
}
```
- **Response:**
```json
{
  "token": "jwt-token",
  "user": { ... },
  "role": "ADMIN|PROFESSOR_FREE|PROFESSOR_VIP|STUDENT"
}
```

#### 2. Register Student
- **POST** `/auth/register/student`
- **Body:**
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "firstName": "string",
  "lastName": "string"
}
```

#### 3. Register Professor
- **POST** `/auth/register/professor`
- **Body:**
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "firstName": "string",
  "lastName": "string"
}
```

#### 4. Get Current User
- **GET** `/auth/me`
- **Headers:** Authorization required

### Quiz Endpoints

#### 1. Create Quiz (Professor)
- **POST** `/quiz/create`
- **Headers:** Authorization required
- **Body:**
```json
{
  "title": "string",
  "description": "string",
  "duration": 30
}
```

#### 2. Update Quiz (Professor)
- **PUT** `/quiz/{quizId}`
- **Headers:** Authorization required
- **Body:**
```json
{
  "title": "string",
  "description": "string",
  "duration": 30
}
```

#### 3. Delete Quiz (Professor)
- **DELETE** `/quiz/{quizId}`
- **Headers:** Authorization required

#### 4. Get My Quizzes (Professor)
- **GET** `/quiz/my-quizzes`
- **Headers:** Authorization required

#### 5. Add Question to Quiz (Professor)
- **POST** `/quiz/{quizId}/questions`
- **Headers:** Authorization required
- **Body:**
```json
{
  "questionText": "string"
}
```

#### 6. Add Response to Question (Professor)
- **POST** `/quiz/questions/{questionId}/responses`
- **Headers:** Authorization required
- **Body:**
```json
{
  "responseText": "string",
  "isCorrect": true
}
```

#### 7. Get Quiz by Code (Public)
- **GET** `/quiz/join/{code}`
- **No authentication required**

#### 8. Submit Quiz Answers
- **POST** `/quiz/{quizId}/submit`
- **Body:**
```json
{
  "selectedResponseIds": [1, 2, 3],
  "guestId": 1  // Optional, for guest users
}
```

#### 9. Get Quiz Participations (Professor)
- **GET** `/quiz/{quizId}/participations`
- **Headers:** Authorization required

#### 10. Get My Participations (Student)
- **GET** `/quiz/my-participations`
- **Headers:** Authorization required

### Guest Endpoints

#### 1. Create Guest
- **POST** `/quiz/guest/create`
- **Body:**
```json
{
  "pseudo": "string",
  "email": "string"
}
```

#### 2. Get Guest
- **GET** `/quiz/guest/{guestId}`

### Admin Endpoints

#### 1. Get All Professors
- **GET** `/admin/users/professors`
- **Headers:** Authorization required (Admin role)

#### 2. Get All Students
- **GET** `/admin/users/students`
- **Headers:** Authorization required (Admin role)

#### 3. Get All Guests
- **GET** `/admin/users/guests`
- **Headers:** Authorization required (Admin role)

#### 4. Create Subscription
- **POST** `/admin/subscriptions`
- **Headers:** Authorization required (Admin role)
- **Body:**
```json
{
  "name": "string",
  "price": 9.99,
  "durationDays": 30
}
```

#### 5. Get All Subscriptions
- **GET** `/admin/subscriptions`
- **Headers:** Authorization required (Admin role)

#### 6. Update Subscription
- **PUT** `/admin/subscriptions/{subscriptionId}`
- **Headers:** Authorization required (Admin role)

#### 7. Delete Subscription
- **DELETE** `/admin/subscriptions/{subscriptionId}`
- **Headers:** Authorization required (Admin role)

#### 8. Assign Subscription to Professor
- **POST** `/admin/professors/{professorId}/assign-subscription`
- **Headers:** Authorization required (Admin role)
- **Body:**
```json
{
  "subscriptionId": 1
}
```

#### 9. Get All Quizzes
- **GET** `/admin/quizzes`
- **Headers:** Authorization required (Admin role)

#### 10. Delete Any Quiz
- **DELETE** `/admin/quizzes/{quizId}`
- **Headers:** Authorization required (Admin role)

## Default Admin Account
- **Username:** admin
- **Password:** admin123
- **Email:** admin@quizplatform.com

## Default Subscriptions
1. **Free** - $0.00 for 30 days
2. **Premium** - $9.99 for 30 days
3. **VIP** - $19.99 for 30 days

## Error Responses
All error responses follow this format:
```json
{
  "error": "Error message"
}
```

## Database Setup
1. Create MySQL database named `quiz_platform`
2. Update `application.properties` with your database credentials
3. The application will automatically create tables on start

## Swagger Ui:

http://localhost:8080/api/swagger-ui/index.html