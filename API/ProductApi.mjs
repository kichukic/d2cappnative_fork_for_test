import express from 'express'
import { authoriseUser } from '../middlewares/authMiddleware.mjs';
import { createProduct } from '../models/productSchema.mjs';
const router = express.Router();


router.get("/products",authoriseUser,(req,res)=>{
    res.send("api working on product get call")
})

router.post("/product",authoriseUser,async(req,res)=>{
    try {
        const{storeid,name,price,description} =req.body
        const product = {storeid,name,price,description}
        await createProduct(product)
        res.status(201).send(`product ${name} added successfully`)
    } catch (error) {
        console.log(error)
        res.status(500).send("error occured while adding product")
    }
})







export {router as default}