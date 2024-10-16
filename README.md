## Social Media Analytics System Readme

### Introduction:
Welcome to the Social Media Analytics System! This Java-based application is your go-to solution for comprehensive social media interaction analysis and management. Tailored for user accounts, posts, likes, and followers, this system provides a robust and scalable platform. Its modular design ensures flexibility and straightforward integration.

### Application Overview:
Core Functionality
Within the TemaTest package, the core functionalities reside. The App class serves as the central hub, orchestrating operations related to user accounts, posts, and followers.

### User Management
The Utilizator class handles user-centric operations—authentication, user creation, following/unfollowing, and post retrieval—utilizing efficient file-based storage for user data management.

### Likeable Interface
The Likeable interface defines methods for liking, unliking posts and comments, and counting likes. These methods offer extensibility, ready to be implemented by classes requiring like-related functionalities.

### Code Structure and Extensibility:
The system follows object-oriented principles, ensuring easy extension and maintenance. The Likeable interface allows developers to seamlessly integrate new features related to likes without modifying existing code. By implementing this interface in specific classes, developers can tailor the system to their unique requirements.

### Usage Examples:
Creating a User
To create a new user, execute the createUser method in the Utilizator class. Provide the username and password as command-line arguments:

java TemaTest.App createUser 'newUsername' 'newPassword'
Following a User
To follow another user, use the follow method in the Utilizator class. Include the follower's username as a command-line argument:

java TemaTest.App follow 'yourUsername' 'yourPassword' 'followerUsername'
Analyzing User Metrics
Explore user metrics such as followers, following, and popular users with the provided methods:

java TemaTest.App getFollowing 'yourUsername' 'yourPassword'
java TemaTest.App getFollowers 'yourUsername' 'yourPassword' 'userToAnalyze'
java TemaTest.App getMostFollowed 'yourUsername' 'yourPassword'
java TemaTest.App getMostLikedUsers 'yourUsername' 'yourPassword'

### Conclusion:
The Social Media Analytics System offers a comprehensive framework for managing and analyzing social media interactions. With its modular structure, clear documentation, and extensibility, this system is a versatile foundation for building diverse and customizable social media applications.