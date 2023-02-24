AGENT REGISTRATION
________________________________________________________________
API -  http://localhost:4001/home/register   (post request)

bodystructure - {
    "firstName":"test",
    "lastName":"tes",
    "phoneNumber":8848970443,
    "email":"testuser@gmail.com",
    "agentAreaPincode":683563,
    "password":"1234"
}


EMAIL VERIFICATION
________________________________________________________________
 there after a verification code will send to the mail provided click the link and get verified ,then login 

----------------------------------------------------------------
AGENT LOGIN
________________________________________________________________
API - http://localhost:4001/home/login (post request)

bodystructure - {
                  "email":"agent@gmail.com",
                  "password":"1234"}




FORGOT PASSWORD
________________________________________________________________
Forgot Password - http://localhost:4001/home/forgot-password (post request)
bodystructure - {
            email":"agent@gmail.com"
                    }

A verification code will be sent to the mail provided

get the code from the mail that u got  and set a new password with this API



SET PASSWORD
_________________________________________________________________
set Password = http://localhost:4001/home/verify-password (post request)
bodystructure =  {
    "email":"agent@gmail.com",
    "token":"a1db7d69848fc39cc6d1",
    "password":"test1234"
}
                    


any of the parameters can be added and modified upon changing models and passing a function to API file

