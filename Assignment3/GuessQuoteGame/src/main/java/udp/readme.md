Description - this project creates a guess the quote game by showing quotes from the server and allowing the
user to guess who says the quote. It utili

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
