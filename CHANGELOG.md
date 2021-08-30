# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0] - 2021-08-30Z

### Added
- Added Login Page
- Login by entering a valid username, password, and one country from a set of countries, with input validations
- 'Remember Login' checkbox on login form to retain last known login credentials
- If app is killed, the login state is retained so that user does not need to login again
- Added User List Page
- On successful login, show a list of users from the api https://jsonplaceholder.typicode.com/users
- Allow user to refresh list or to logout
- Select a user to see their details
- User Details Page
- Show the user's details: name, username, email. address, phone, website, and company info
- Show a map where the user is currently at, signified by a pin marker
- 'My Location' button on the map to zoom to device's current location
- Zoom in/out buttons on the map