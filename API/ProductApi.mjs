import express from 'express'
import { authoriseUser } from '../middlewares/authMiddleware.mjs';
import { createProduct,deleteProductById } from '../models/productSchema.mjs';
const router = express.Router();


router.get("/products",authoriseUser,(req,res)=>{
    res.send("api working on product get call")
})

router.post("/product",authoriseUser,async(req,res)=>{
    try {
        const{storeid,productID,name,price,description} =req.body
        const product = {storeid,productID,name,price,description}
        await createProduct(product)
        res.status(201).send(`product ${name} added successfully`)
    } catch (error) {
        console.log(error)
        res.status(500).send("error occured while adding product")
    }
})

router.delete('/delproduct/:prodId',authoriseUser,async(req,res)=>{
   try {
    const productId = req.params.prodId;
    
    const checkproduct = await deleteProductById(productId);
    if(!checkproduct){
        res.status(404).send({message:`no product with id ${productId}`})
    }
    res.status(200).send({message:`product ${productId} deleted`})
    
   } 
   catch (error) {
    console.log(">>>>>>>>..",productId)
    res.status(500).send({message:`some error occured during deletion of ${productId}`})
   }
})





export {router as default}