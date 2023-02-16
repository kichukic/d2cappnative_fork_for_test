import express from 'express'
import dotenv from 'dotenv'
import mongoose from 'mongoose'
import morgan from 'morgan'
import route from './API/UserApi.mjs'
dotenv.config()
const app = express();




app.use(express.json())
app.use(morgan('tiny'))
app.use("/home",route)




app.listen(process.env.PORT,()=>{
    console.log(`server listening on ${process.env.PORT}`)
})