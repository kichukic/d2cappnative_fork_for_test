import mongoose, { Document, now, Schema } from 'mongoose';

// export interface IProduct {
//     agentemail: String;
//     password: String;
// }

export interface IProductModel extends Document {}

const ProductSchema: Schema = new Schema(
    {
        product_name: { type: String, required: true },
        product_image: { type: String, required: true },
        product_price: { type: String, required: true },
        product_category: { type: String, required: true },
        product_description: { type: String, required: true },
        product_store_id: { type: String, required: true },
        product_storequantity: { type: String, required: true },
        product_storeprice: { type: String, required: true }
    },
    {
        // versionKey: false
        timestamps: true
    }
);

export default mongoose.model<IProductModel>('Product', ProductSchema);
