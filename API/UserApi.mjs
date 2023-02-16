import express from "express";
import {CreateUser,findUser} from '../models/UserModel.mjs'
import database from '../Database/db.mjs'
const router = express.Router();


router.post("/register",async(req,res)=>{
    try {
        const {firstName,lastName,email,password} = req.body
        const checkUserExist = await findUser(email)
        if(checkUserExist){
            return res.status(409).send(`user with ${email} already`)
        }
        const salt =
    } catch (error) {
        
    }
})




export  {router as default}