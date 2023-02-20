import express from "express";
import { CreateUser, findUser } from "../models/UserModel.mjs";
import database from "../Database/db.mjs";
import { authoriseUser } from "../middlewares/authMiddleware.mjs";
import bcrypt from "bcrypt";
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

export { router as default };
