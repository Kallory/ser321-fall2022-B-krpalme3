# Assignment 4 Activity 1
## Description
Successfully does everything as described in the assignment document except switching. 

## Protocol

### Requests
request: { "selected": <int: 1=add, 2=clear, 3=display, 4=remove, 5=index,
0=quit>, "data": <thing to send>}

  add: data <string> -- a string to add to the list
  clear: data <> -- no data given, clears the whole list
  display: data <> -- no data given, displays the whole list
  remove: data <int> -- integer of index in list that should be removed
  index: data <int> -- integer of index in list that should be displayed

### Responses

success response: {"ok" : true, type": <"add",
"pop", "display", "count", "switch", "quit"> "data": <thing to return> }

type <String>: echoes original selected from request
data <string>: 
    add: return complete list
    clear: return empty list
    display: return complete list
    remove: return removed item
    index: return item at index


error response: {"ok" : false, "message"": <error string> }
error string: Should give good error message of what went wrong


## How to run the program
### Terminal
Base Code, please use the following commands:
```
    For Task1, run "gradle runTask1 -Pport=9099 -q --console=plain"
    For Task2, run "gradle runTask2 -Pport=9099 -q --console=plain"
    For Task1, run "gradle runTask3 -Pport=9099 -Plimit=5 -q --console=plain"
    (change 5 to desired limit of max connections to the server)
```
```   
    For Client, run "gradle runClient -Phost=localhost -Pport=9099 -q --console=plain"
```   



