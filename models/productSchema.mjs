import mongoose from 'mongoose'

const ProductSchema = new mongoose.Schema ({
    storeid:{type: 'string',required: true},
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

export{createProduct}