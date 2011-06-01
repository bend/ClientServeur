## Running the client server

### Client

From the console type

	java -jar client.jar
Or double click on it
The GUI will be launched and will prompt you for your username, password, server port, and server host. You can also load an Avatar from here.
### Server

From the console type

	java -jar server.jar [port]
The server support commands

## Features
This Client/Server chat has some features
You can chat, talk using voip, transfert files, store files on the server and download them.
You can talk using a 128-bit RSA encryption of your conversations.
You can share screen. The screen control feature is not working so only share is OK.
Each user has a username, password, nickname, profile information containing:

- name
- surname
- phone
- email
- city/country
- about
- ...

You can access your profile from file menu/ preferences

## Usage

### Server

To add a user, you need to add it server side.
- to do that type in the server command line 

	adduser

The server will ask you the different informations required
- You can also see the number of users connected with 
	
	nbconnected

- You can remove a user with
	
	removeUser

- You can see how many users are registred with
	
	nbregistred

- You can send a message to a spefied user or to all users with
	
	sendMessage -u [username] -m message

- You can kick a user with

	Usage: kickUser -u username [-c cause]

- You can get informations about a user with
	
	getUserInfo

- To exit the server type
	
	exit

- To build the reports type
	
	buildreports

- ...



