import mongoose, { Document, now, Schema } from 'mongoose';

export interface IStoreModel extends Document {}

const StoreSchema: Schema = new Schema(
    {
        store_name: { type: String, required: true },
        store_mobile: { type: String, required: true },
        store_email: { type: String, required: true },
        store_address: { type: String, required: true },
        store_deliverable_pincode: { type: String, required: true }, //make an array
        store_available_productid: { type: String, required: true }, //make an array
        password: { type: String, required: true }
    },
    {
        // versionKey: false
        timestamps: true
    }
);

export default mongoose.model<IStoreModel>('Store', StoreSchema);
