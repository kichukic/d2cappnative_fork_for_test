Agent Registration 
________________________________________________________________
API -  http://localhost:4001/home/register

bodystructure - {"firstName" : "agent",
                 "lastName" : "agent",
                 "email":"agent@gmail.com",
                 "password": "1234"}
----------------------------------------------------------------
Agent Login
________________________________________________________________
API - http://localhost:4001/home/login

bodystructure - {
                  "email":"agent@gmail.com",
                  "password":"1234"}

any of the parameters can be added and modified upon changing models and passing a function to API file