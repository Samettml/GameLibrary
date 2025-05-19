###Game Library Management Application
This project is a comprehensive game library management application developed using Java Swing. Users can add, edit, filter games, and import popular games from Steam integration.
![Ekran görüntüsü 2025-05-20 010838](https://github.com/user-attachments/assets/4046db45-e4f1-47d8-b61f-fa66161beb3d)
### Features

- **Game Management**: Add, edit, and delete games
- **Game Recommendations**: Get game recommendations based on user preferences
- **Steam Integration**: Import popular games from Steam
- **Multiple Database Support**: Switch between MongoDB and MySQL
- **Game Information Display**: View information such as genre, platform, year, and Metacritic score
- **Active Player Count Tracking**: View current player counts of games via Steam API
![Ekran görüntüsü 2025-05-20 010709](https://github.com/user-attachments/assets/4c28802d-1e6c-4fa5-840a-470304753277)

## Technology Stack

- **Programming Language**: Java
- **Interface**: Java Swing
- **Database**: MongoDB / MySQL (configurable)
- **Dependencies**:

- MongoDB Java Driver
- MySQL Connector/J
- dotenv-java (for environment variables)
- OkHttp (for HTTP requests)
- Gson (for JSON processing)


## Installation

### Prerequisites

- Java JDK 11 or higher
- Maven
- MongoDB or MySQL database

## Usage

### Main Menu

The main menu includes the following options:

- **All Games**: View all games in the library
- **Add New Game**: Open a form to add a new game
- **Recommend Game**: Get game recommendations based on preferences
- **Steam Library**: Import popular games from Steam

![Ekran görüntüsü 2025-05-20 010804](https://github.com/user-attachments/assets/66993cb1-b604-4be0-b6c4-3c83572af460)


## Database Configuration

The application supports both MongoDB and MySQL databases. To change the database type:

1. Open the `.env` file in the project root directory
2. Change the `DB_TYPE` value:

1. For MongoDB: `DB_TYPE=mongo`
2. For MySQL: `DB_TYPE=mysql`
3. Restart the application


![Ekran görüntüsü 2025-05-20 010804](https://github.com/user-attachments/assets/f596e9de-a20b-47f5-a4fe-0ad8c11322cc)
![Ekran görüntüsü 2025-05-20 010732](https://github.com/user-attachments/assets/72e33a58-2d87-4678-8a2d-39f6d06809f6)





