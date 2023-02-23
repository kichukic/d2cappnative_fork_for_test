import express from "express";
import { CreateUser, findUser,ForgotPassword } from "../models/UserModel.mjs";
import database from "../Database/db.mjs";
import { authoriseUser } from "../middlewares/authMiddleware.mjs";
import bcrypt from "bcrypt";
import nodemailer from 'nodemailer'
import crypto from 'crypto'
import dotenv from "dotenv";
dotenv.config();
import jwt from "jsonwebtoken";
const router = express.Router();

router.post("/register", async (req, res) => {
  try {
    const { firstName, lastName, email, password } = req.body;
    const checkUserExist = await findUser(email);
    console.log(checkUserExist);
    if (checkUserExist) {
      return res.status(409).send(`agent with ${email} already exists`);
    }
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);
    const user = { firstName, lastName, email, password: hashedPassword };

    await CreateUser(user);
    res.status(201).send(`agent ${firstName} registered sucessfully`);
  } catch (error) {
    console.error(error);
    res.status(500).send(`error occured while registering agent `);
  }
});

router.post("/login", async (req, res) => {
  try {
    const { email, password } = req.body;
    const checkUserExist = await findUser(email);
    if (!checkUserExist) {
      return res.status(401).send(`user with ${email} dosent exists`);
    }
    const check_password = await bcrypt.compare(
      password,
      checkUserExist.password
    );
    if (!check_password) {
      return res.status(401).send(`password is incorrect for ${email}`);
    }
    const token = jwt.sign(
      { userId: checkUserExist._id },
      process.env.SECERET_KEY
    );
    res.status(200).send({ token });
  } catch (error) {
    console.error(error);
    res.status(500).send("error logging in ");
  }
});

router.post("/forgot-password",async(req,res)=>{
  try {
    const{email} = req.body
    const user = await findUser(email)
    const buffer = await crypto.randomBytes(10);
    const token = buffer.toString('hex');
    console.log(token)
    if(!user){
      res.status(404).send({message:`user with ${email} does not exist`})
    }
   ForgotPassword(email,token,Date.now()+25000)

   
    const transporter = nodemailer.createTransport({
      service: "gmail",
      auth:{
        user:process.env.GOOGLE_USER,
        pass:process.env.GOOGLE_PASSWORD
      }
    })

    console.log(process.env.GOOGLE_PASSWORD)

    const mailoptions = {
      to : user.email,
      from : "alcodextest@gmail.com",
      subject : "password reset request ",
      text : `hello ${user.name} reset your password here ${process.env.CLIENT_URL}/reset-password/${token}`

    }
    await transporter.sendMail(mailoptions)
    res.json({message : `an password reset link has sent to your ${user.email}`})

   } catch (error) {
    console.log(error)
    res.status(500).json({message : `An error has occurred`}) 
   }
})

export { router as default };
