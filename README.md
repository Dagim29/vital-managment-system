# Vital Management System

The Vital Management System (VMS) is a comprehensive solution designed to manage vital records such as births, marriages, deaths, and divorces. The system provides functionalities for registration, updating, viewing, and printing certificates of these records. There are two main actors in the system: VMS Officers and Admins.

## Features

- **Birth Registration**: Register and manage birth records.
- **Marriage Registration**: Register and manage marriage records.
- **Death Registration**: Register and manage death records.
- **Divorce Registration**: Register and manage divorce records.
- **User Management**: Admins can add, remove, and manage VMS Officers.
- **Certificate Printing**: Officers can print certificates for registered records.

## Actors and Their Roles

### VMS Officer

The VMS Officer is responsible for:
- Registering new records (births, marriages, deaths, divorces).
- Updating existing records.
- Viewing records.
- Printing certificates.

### Admin

The Admin is responsible for:
- Checking VMS activities.
- Viewing an overall summary of registered data.
- Adding new VMS Officers.
- Removing VMS Officers.

## Database Structure

The database includes the following tables:
- `activity_logs`: Logs of user activities.
- `births`: Records of births.
- `deaths`: Records of deaths.
- `divorces`: Records of divorces.
- `marriages`: Records of marriages.
- `notices`: System notices.
- `users`: User accounts and roles.

## Getting Started

To get started with the Vital Management System, follow these steps:

1. **Clone the repository**:
    ```sh
    git clone <repository-url>
    cd <repository-directory>
    ```

2. **Set up the database**:
    - Import the `database.sql` file into your MySQL database.
    - Update the database configuration in your application.

3. **Run the application**:
    - Follow the instructions in the project documentation to run the application.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with your changes.


## Contact

For any questions or support, please contact the project maintainer at yworkdada@gmail.com.
