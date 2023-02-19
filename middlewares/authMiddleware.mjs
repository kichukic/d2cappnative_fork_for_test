import jwt from 'jsonwebtoken'
import dotenv from 'dotenv';
dotenv.config()


const authoriseUser = (req,res,next)=>{
    let authHeaders = req.headers.authorization
    if(authHeaders==undefined){
        res.status(401).send({error:"no token provided"})
    }
    let token =authHeaders.split(" ")[1]
    jwt.verify(token,process.env.SECERET_KEY,(err,data)=>{
        if(err){
            res.status(500).send({err:"auth failed"})
        }else{
           next()
        }
    })
}

export{authoriseUser}