import express from 'express'
import { authoriseUser } from '../middlewares/authMiddleware.mjs';
import { createProduct,
          deleteProductById,
            updateProductById,
                getAllProduct,
                    getSingleProduct} from '../models/productSchema.mjs';
const router = express.Router();


router.get("/products",authoriseUser,async(req,res)=>{
    try {
        const products = await getAllProduct()
        let details =[]
        for(let i=0;i<products.length;i++){
            let productName=products[i].name 
            let storeID = products[i].storeid
            let productID = products[i].productID
            let productPrice = products[i].price
            let productDescription = products[i].description
            let Alldetails = {productID,productName,productPrice,productDescription,storeID}
            details.push(Alldetails)
        }
        res.status(200).send({products :details })
    } catch (error) {
        res.status(500).send("error getting products")
    }a
})

router.get('/products/:prodId',authoriseUser,async(req,res)=>{
   try {
    let id = req.params.prodId
    let products = await getSingleProduct(id)
    let name = products.name
    let storeID = products.storeid
    let productID = products.productID
    let description = products.description
    let singleDetails = {name,storeID,productID,description}
    res.status(200).send({product:singleDetails})
   } catch (error) {
    res.status(500).send("error getting product")
   }

})


router.post("/productData",authoriseUser,async(req,res)=>{
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