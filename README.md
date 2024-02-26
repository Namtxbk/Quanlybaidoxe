# PARKING MANAGEMENT SOFTWARE
# *Work in progress

The Parking Management Software is designed to streamline and simplify the process of managing parking, catering to both administrators and users. The software is developed as a mobile application and utilizes Firebase Firestore as the backend to enhance data management and real-time updates.

## Features

Users register and create accounts, allowing them to register their vehicles. The application facilitates convenient vehicle check-in and check-out processes. Key functionalities include:

### Account Registration and Management

- Users register accounts and link their vehicles to the system.
- Each vehicle is registered to a single account, but an account can have multiple vehicles.
- Users can link their accounts to bank accounts or e-wallets for seamless payment.

### Vehicle Check-In and Check-Out Management

- The check-in process involves automatic recognition of the vehicle's license plate through a camera and verification of the registration information.
- Users can choose between hourly, daily, or long-term parking options (monthly, quarterly, yearly).
- Administrators can confirm parking details, automatically raising the barrier for user access.

### Payment and Payment Method Linking

- Users can link their bank accounts or e-wallets for payment.
- Administrators confirm check-out, and the system sends a payment invoice to the user.
- Payment can be made immediately or at a later time, depending on network connectivity.

### Account Management and Check-Out Confirmation

- Administrators use the application to manage user accounts, check parking and check-out information.
- QR code scanning is used to confirm check-out, updating the system with the latest information.

### Firestore Integration

The application leverages Firebase Firestore for efficient data storage and real-time updates. Firestore is utilized for:

- Storing user account information.
- Managing parking records and transaction details.
- Enabling seamless synchronization of data between the application and the backend.
## MVVM Architecture

The Parking Management Software follows the MVVM (Model-View-ViewModel) architecture pattern, enhancing code organization and separation of concerns. The architecture consists of the following components:

### Model

- Represents the data and business logic of the application.
- Manages interactions with Firestore, handling user and parking-related information.

### View

- Represents the UI components of the application.
- Displays information to users and captures user input.

### ViewModel

- Acts as an intermediary between the Model and the View.
- Handles business logic, processes user input, and updates the View accordingly.

## Installation

### Requirements

Make sure you have the following tools installed on your system:

- [Android Studio](https://developer.android.com/studio)
- [Kotlin Plugin](https://kotlinlang.org/docs/tutorials/android/getting-started.html)
- [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)

### Clone the Repository
git clone https://github.com/LManhL/ParkingQR.git

## App preview
![Frame 38](https://github.com/LManhL/ParkingQR/assets/95266634/026d1257-b6ea-4c7e-a63a-6668ac01ede1)
