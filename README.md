# Cartrack App

Maintainer: Juan Wilfredo Gales Ibañez

This simple app enables users the following:

#### Login Page
- Login by entering a valid username, password, and one country from a set of countries, with input validations
- 'Remember Login' checkbox on login form to retain last known login credentials
- If app is killed, the login state is retained so that user does not need to login again

#### User List Page
- On successful login, show a list of users from the api https://jsonplaceholder.typicode.com/users
- Allow user to refresh list or to logout
- Select a usee to see their details

#### User Details Page
- Show the user's details: name, username, email. address, phone, website, and company info
- Show a map where the user is currently at, signified by a pin marker
- 'My Location' button on the map to zoom to device's current location
- Zoom in/out buttons on the map

## Usage

The local database is pre-populated with the following list of login accounts (username/password):

- admin/pass1234
- user1/pass1234
- user2/pass1234

Enter any one of the above credentials to access the user list and details pages.

## Implementation

- Written in Kotlin
- One activity with three fragments: login, user list, user detials fragment
- Fragment navigation via Navigation Controller
- Implemented MVVM architecture via View Binding, Data Binding, ViewModels & LiveData
- Dependency injection via Hilt
- Reactive programming via RxJava
- Material design principles
- Networking layer via Retrofit & OkHttp

## Building locally

Clone the repository via
```
$ git clone https://github.com/jwgibanez/super-fortnight-capsule.git
```

Create file `local.properties` on root folder
```
MAPS_API_KEY=your-api-key
```

Build the release apk
```
$ ./gradlew assembleRelease
```

## Testing

The architecture is consiously designed so that all layers are testable as much as possible.

Automated test have been implemented covering all layers in the MVVM architecture:
- View: UITest (HomeActivity)
- ViewModel: LoginViewModelTest (LoginViewModel)
- Model: AppDatabaseTest (AppDatabase), LoginRepositoryTest (LoginRepository), UserServiceTest (UserService) etc.

To run all test, attach device to adb and run
```
$ ./gradlew connectedAndroidTest
```

## CI/CD

On every commit to `main`, automated build is done by Github Actions to check the app is building successfully.


