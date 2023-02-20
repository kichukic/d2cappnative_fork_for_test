import express from 'express'
import dotenv from 'dotenv'
import jwt from 'jsonwebtoken'
import mongoose from 'mongoose'
import morgan from 'morgan'
import route from './API/UserApi.mjs'
import productroute from './API/ProductApi.mjs'
import bodyParser from "body-parser";
dotenv.config()
const app = express();

app.use(bodyParser.json())
app.use("/prod",productroute)
app.use(express.json())
app.use(morgan('tiny'))
app.use("/home",route)







app.listen(process.env.PORT,()=>{
    console.log(`server listening on ${process.env.PORT}`)
})

