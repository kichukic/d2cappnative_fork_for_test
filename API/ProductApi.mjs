import express from 'express'
import { authoriseUser } from '../middlewares/authMiddleware.mjs';
import { createProduct,deleteProductById,updateProductById } from '../models/productSchema.mjs';
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
    res.status(500).send({message:`some error occured during deletion of ${productId}`})
   }
})

router.put('/editProduct/:prodId',authoriseUser,async(req,res)=>{
    try {
        const prodid = req.params.prodId
        const reqbody =req.body
        reqbody.updatedAt = Date.now()
        const updateProduct = await updateProductById(prodid,reqbody)
        if(!updateProduct){
            res.status(404).send({message: 'Product not found'})
        }
        res.status(200).send({message: `product with id ${prodid} updated with ${updateProduct}`})

    } catch (error) {
        res.status(500).send({message:"some error occured while updating product"})
    }
})





export {router as default}