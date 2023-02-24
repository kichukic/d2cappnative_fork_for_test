import express from "express";
import {
  CreateUser,
  findUser,
  ForgotPassword,
  set_password,
} from "../models/UserModel.mjs";
import database from "../Database/db.mjs";
import { Users } from "../models/UserModel.mjs";
import { authoriseUser } from "../middlewares/authMiddleware.mjs";
import bcrypt from "bcrypt";
import nodemailer from "nodemailer";
import crypto from "crypto";
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
      return res.status(409).json(`agent with ${email} already exists`);
    }
    const transporter = nodemailer.createTransport({
      service: "gmail",
      auth: {
        user: process.env.GOOGLE_USER,
        pass: process.env.GOOGLE_PASSWORD,
      },
    });
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);
    const verificationToken = crypto.randomBytes(20).toString("hex");
    const user = {
      firstName,
      lastName,
      email,
      password: hashedPassword,
      emailVerficationToken: verificationToken,
      emailVerified: false,
    };
      await CreateUser(user);
const mailoptions = {
      to: user.email,
      from: process.env.FROM_MAIL,
      subject: "email verfication code ",
      text: `hello ${user.firstName} your verfication code is ${verificationToken}`,
    };
      await transporter.sendMail(mailoptions)
  
    res.status(201).json(`agent ${firstName} registered sucessfully`);
  } catch (error) {
    console.error(error);
    res.status(500).json(`error occured while registering agent `);
  }
});

router.get('/verify-email/:token',async(req,res)=>{
  try {
    const token = req.params.token;
    const user = await Users.findOne({ emailVerificationToken: token });
    if(!user){
      return res.status(404).json({message: "not a valid link or expired"})
    }
    user.emailVerficationToken = undefined;
    user.emailVerified = true;
    await user.save();
    res.status(200).json({message: "email verfication success"})

  } catch (error) {
    return res.status(500).json({message: "something went wrong while verification"})
  }
})


router.post("/login", async (req, res) => {
  try {
    const { email, password } = req.body;
    const checkUserExist = await findUser(email);
    console.log(checkUserExist.emailVerified)
    if (!checkUserExist) {
      return res.status(401).send(`user with ${email} dosent exists`);
    }

    if(!checkUserExist.emailVerified){
      return res.status(401).json({message:"email not verified"})
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

router.post("/forgot-password", async (req, res) => {
  try {
    const { email } = req.body;
    const user = await findUser(email);
    const buffer = await crypto.randomBytes(10);
    const token = buffer.toString("hex");
    console.log(token);
    if (!user) {
      res.status(404).send({ message: `user with ${email} does not exist` });
    }
    ForgotPassword(email, token, Date.now() + 25000);

    const transporter = nodemailer.createTransport({
      service: "gmail",
      auth: {
        user: process.env.GOOGLE_USER,
        pass: process.env.GOOGLE_PASSWORD,
      },
    });

    const mailoptions = {
      to: user.email,
      from: process.env.FROM_MAIL,
      subject: "password reset request ",
      text: `hello ${user.firstName} your reset code is ${token}`,
    };
    await transporter.sendMail(mailoptions);
    res.json({
      message: `an password reset link has sent to your ${user.email}`,
    });
  } catch (error) {
    console.log(error);
    res.status(500).json({ message: `An error has occurred` });
  }
});

router.post("/verify-password", async (req, res) => {
  try {
    const { email, token, password } = req.body;
    const user = await Users.findOne({
      email: email,
      reserPasswordToken: token,
    });
    console.log("<<<>>>", user);
    if (!user) {
      return res.status(400).json({ message: "invalid token or email" });
    }
    if (!password) {
      return res.status(400).json({ message: "password cannot be empty" });
    }
    const hashedPassword = await bcrypt.hash(password, 10);
    await set_password(email, undefined, hashedPassword, undefined);
    user.reserPasswordToken = undefined;
    user.tokenExpiry = undefined;
    await user.save();
    return res.json({ message: "your password has been sucessfully changed" });
  } catch (error) {
    return res.status(500).json({ message: "something went wrong" });
  }
});

export { router as default };
