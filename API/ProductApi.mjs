import express from 'express'
import { authoriseUser } from '../middlewares/authMiddleware.mjs';
const router = express.Router();






router.get("/product",authoriseUser,(req,res)=>{
res.send("hiiii")
})







export {router as default}