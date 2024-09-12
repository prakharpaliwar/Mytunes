Project Overview
MyTunes is a full-stack desktop music management application designed to provide a seamless user experience for managing and playing local music libraries. Built using Java Swing for the frontend and MySQL with JDBC for the backend, the application offers a comprehensive solution for music playback, playlist management, and song metadata editing. It integrates modern design principles and cutting-edge technologies to deliver a responsive, user-centric experience with scalability, cloud integration, and secure authentication.
Features
Core Features:
•	Music Playback: Play, pause, stop, skip, and control volume for MP3 files with intuitive, real-time controls.
•	Library and Playlist Management:
o	Add, delete, and modify song information such as title, artist, album, genre, and year.
o	Create, edit, and delete playlists; drag and drop songs into playlists with persistence.
•	Metadata Handling: Automatically extract and display metadata from MP3 IDTags, and allow users to edit it directly in the app.
•	Session Persistence: Save user configurations, recently played songs, and playlists to the database for session continuity.
•	Volume Control: Each open window or playlist has individual volume control sliders.
Advanced Features:
•	Context Menus & Hotkeys: Right-click and context-sensitive menus for quick actions like adding/removing songs or playlists. Shortcut keys for essential functions such as play (Space), skip (Ctrl-Right), and volume control (Ctrl-I/Ctrl-D).
•	Shuffle & Repeat Mode: Implemented advanced music control features like shuffle and repeat with persistent session states.
•	Playlist Tree: Manage multiple playlists with an expandable/collapsible playlist tree for easy navigation.
•	Timers & Progress Bar: Visual representation of song duration, elapsed time, and remaining time, using advanced Java Swing timers.
Technology Stack
Frontend:
•	Java Swing: Provides a rich and dynamic desktop GUI using MVC (Model-View-Controller) architecture, ensuring clean separation of concerns and a responsive user interface.
Backend:
•	MySQL: A powerful relational database used to store and manage song metadata, playlists, and user preferences.
•	JDBC (Java Database Connectivity): Enables smooth and secure communication between the frontend and the MySQL database, handling all database queries and transactions.
Additional Technologies:
•	Spring Boot: Integrated to handle backend services and support future scalability. Spring Boot's REST API framework enables seamless connection with web and mobile interfaces, making it a full-stack solution.
•	Docker: Containerization with Docker ensures the application runs consistently across all environments. The MyTunes app is Dockerized to simplify deployment and manage dependencies.
•	OAuth 2.0 Authentication: Implements secure user authentication, allowing multiple users to log in with unique profiles and securely manage their personal playlists and song libraries.
•	Restful API: A built-in RESTful API layer allows access to the MyTunes music library and playlists via web or mobile clients. The API follows modern REST conventions, ensuring easy integration with other systems.
•	Cloud Integration: Leveraged AWS (Amazon Web Services) for cloud backup, allowing users to store their playlists and libraries remotely and access them from multiple devices.
IDE:
•	NetBeans IDE: Utilized for development, offering integrated tools for code management, UI design, and debugging. Its seamless integration with Java Swing and JDBC enhances development efficiency.

