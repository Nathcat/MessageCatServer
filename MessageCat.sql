/*
This script will set up the MySQL database for MessageCat.

It will create the following tables
 - Users
 - Friendships
 - FriendRequests
 - Chats
 - ChatInvitations
The parts of the database that store encryption keys (KeyStore) and
messages (Messages) will be stored as separate data structures.
*/

-- Create and use the database schema
CREATE schema `MessageCat`;
USE `MessageCat`;

-- Setup the Users table
CREATE TABLE `Users` (
    UserID INT AUTO_INCREMENT,
    Username VARCHAR(255),
    Password VARCHAR(255),
    DisplayName VARCHAR(255),
    DateCreated VARCHAR(255),
    ProfilePicturePath VARCHAR(255),
    PRIMARY KEY (UserID)
);

-- Setup the Friendships table
CREATE TABLE `Friendships` (
    FriendshipID INT AUTO_INCREMENT,
    UserID INT,
    FriendID INT,
    DateEstablished VARCHAR(255),
    PRIMARY KEY (FriendshipID),
    FOREIGN KEY (UserID) REFERENCES `Users`(UserID),
    FOREIGN KEY (FriendID) REFERENCES `Users`(UserID)
);

-- Setup the FriendRequests table
CREATE TABLE `FriendRequests` (
    FriendRequestID INT AUTO_INCREMENT,
    SenderID INT,
    RecipientID INT,
    TimeSent BIGINT,
    PRIMARY KEY (FriendRequestID),
    FOREIGN KEY (SenderID) REFERENCES `Users`(UserID),
    FOREIGN KEY (RecipientID) REFERENCES `Users`(UserID)
);

-- Setup the Chats table
CREATE TABLE `Chats` (
    ChatID INT AUTO_INCREMENT,
    Name VARCHAR(255),
    Description VARCHAR(255),
    PublicKeyID INT,
    PRIMARY KEY (ChatID)
);

-- Setup the ChatInvitations table
CREATE TABLE `ChatInvitations` (
    ChatInviteID INT AUTO_INCREMENT,
    ChatID INT,
    SenderID INT,
    RecipientID INT,
    TimeSent BIGINT,
    PrivateKeyID INT,
    PRIMARY KEY (ChatInviteID),
    FOREIGN KEY (ChatID) REFERENCES `Chats`(ChatID),
    FOREIGN KEY (SenderID) REFERENCES `Users`(UserID),
    FOREIGN KEY (RecipientID) REFERENCES `Users`(UserID)
);

-- Setup the Connections table
CREATE TABLE `connections` (
    ID INT NOT NULL AUTO_INCREMENT,
    client_ip varchar(255),
    `timestamp` varchar(255),
    PRIMARY KEY (ID)
);