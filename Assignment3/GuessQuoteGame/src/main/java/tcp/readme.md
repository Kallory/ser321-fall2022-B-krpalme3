Description - this project creates a guess the quote game by showing quotes from the server and allowing the
user to guess who says the quote. It utilizes tcp.

Requirements fulfilled: 
    1 - connects to server
    2 - user can send name, server greets user
    3 - user is presented a choice between playing and seeing leader board
    4 - NOT FULFILLED
    5 - NOT FULFILLED
    6 - Server sends first quote of character
    7 - 14, NOT FULFILLED
    16 - partially fulfilled (robust protocol)
        { "type": "sendName", "getLeaderboard", "startGame"},
        "data": {"username": <String>, "guess": <String>}}
        }
        
        JSON Response
        {"ok": <bool>,
         "value": <String>,
         "error": <String>
        }
    17 - partially fulfilled, IO errors are handled properly, as well as type errors
    18, 19 - NOT FULFILLED

Protocol , see 16 above

Robust design - The connection errors are handled on the server side by looping forever until 
another connection is met. Type errors are also handled to ensure that proper input is put in for the server/ip address.
I also had to implement some error handling for keywords, I used boolean flags to prevent a new game from starting
when asking for a name, for example.