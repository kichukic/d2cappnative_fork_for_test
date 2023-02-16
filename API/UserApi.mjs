import express from "express";
import {CreateUser,findUser} from '../models/UserModel.mjs'
import database from '../Database/db.mjs'
import bcrypt from 'bcrypt'
import jwt from 'jsonwebtoken'
const router = express.Router();


router.post("/register",async(req,res)=>{
    try {
        const {firstName,lastName,email,password} = req.body
        console.log(email,"<><><><><")
        const checkUserExist = await findUser(email)
        if(checkUserExist){
            return res.status(409).send(`user with ${email} already exists`)
        }
        const salt = await bcrypt.genSalt(10)
        const hashedPassword = await bcrypt.hashPassword(password)
        const user  = {firstName,lastName,email,password:hashedPassword}
       
        await CreateUser(user)
        res.status(201).send(`agent ${firstName} registered sucessfully`)
    } catch (error) {
        console.error(error)
        res.status(500).send(`error occured while registering agent `)
    }
})




export  {router as default}