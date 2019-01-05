# four-in-a-row-in3D-using-Socket-Thread-Synchronisation-ClientServerNetwork

## Objectives
1) This game is on a 3D board. The board is a 4×4×4 = 64 position cube. 32 pieces in both player colours, which can be stuck on top of one another


2) The object of the game is to connect four of one’s own discs of the same color next to each other
vertically, horizontally, or diagonally, before your opponent does so.

![example_red_win](https://user-images.githubusercontent.com/46351057/50723151-b712ee00-1114-11e9-9417-a2e4e56f2fb8.PNG)


6) has a waiting list. 
so unlimited amount of players are allowed to ask whether they can start a new game or not
7) server is able to communicate with players (clients), using port number and server socket. By communication, the programme matches two players in pairs. 
8)	After a game is over, asks both players whether or not they want to restart the game. The game will restart only when both players are willing to 
9)	If any play wants to exit the game, clean the board, delete this player from the player list. Asks the other player whether he wants to exit or wait for another opponent to register in. 

7) unlimited amount of games are allowed to play simoustaneously
8) three options for player to choose: human vs human, human vs ai, ai vs ai
9) this program is able to provide hint to a player

## Techniques used in this project:
1) Network programming: implement client-server pattern
2) Use of sockets for communication over a network.
3) implement Model-View-Controller pattern
4) Thread creation and thread life cycle
5) Synchronisation
6) Wait-notify mechanism

## Diagram

![diagram](https://user-images.githubusercontent.com/46351057/50723109-3ce26980-1114-11e9-9fc9-a9fd2d3eced7.png)
