import mongoose from 'mongoose'

const ProductSchema = new mongoose.Schema ({
    storeid:{type: 'string',required: true},
    productID:{type: 'string',required: true},
    name: { type: String, required: true },
    price: { type: Number, required: true },
    description: { type: String, required: true },
    createdAt: { type: Date, default: Date.now },
    updatedAt: { type: Date, default: Date.now },
})

const Add_Product = mongoose.model("Products",ProductSchema)

const createProduct =(productSchema)=>{
    return Add_Product.create(productSchema)
}

const deleteProductById =async(productId)=>{
  try {
    const result = await Add_Product.findOneAndDelete({productID:productId})
    return result
  } catch (error) {
    throw new Error ("error deleting productt")
  }
}

const updateProductById = async(productId,data)=>{
    try {
        const result = await Add_Product.findOneAndUpdate({productID:productId},data,{new:true},)
        return result
    } catch (error) {
        throw error ("error updating product")
    }
}
const getAllProduct =async()=>{
    try {
        const products = await Add_Product.find()
        return products
    } catch (error) {
        
    }
}

export{createProduct,deleteProductById,updateProductById,getAllProduct}



