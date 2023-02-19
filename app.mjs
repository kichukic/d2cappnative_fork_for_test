import express from 'express'
import dotenv from 'dotenv'
import jwt from 'jsonwebtoken'
import mongoose from 'mongoose'
import morgan from 'morgan'
import route from './API/UserApi.mjs'
import productroute from './API/ProductApi.mjs'
dotenv.config()
const app = express();



app.use("/product",productroute)
app.use(express.json())
app.use(morgan('tiny'))
app.use("/home",route)



const secrectKey = process.env.SECERET_KEY
const authMiddleware =(req,res,next)=>{
const authHeader = req.headers.authorizatoin
if(!authHeader){
    res.status(401).json({error:"authorization key is missing"})
}
const token = authHeader.split(' ')[1];
jwt.verify(token,secrectKey,(err,decrypt)=>{
    if(err){
        return res.status(401).json({error:"Token is not valid"})
    }
    req.user = decrypt
    next()
})
}



app.listen(process.env.PORT,()=>{
    console.log(`server listening on ${process.env.PORT}`)
})

export {authMiddleware}