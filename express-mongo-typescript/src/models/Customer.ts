import mongoose, { Document, now, Schema } from 'mongoose';

export interface ICustomerModel extends Document {}

const CustomerSchema: Schema = new Schema(
    {
        customer_firstname: { type: String, required: true },
        customer_secondname: { type: String, required: true },
        customer_mobile: { type: String, required: true, unique: true },
        customer_pincode: { type: String, required: true },
        customer_email: { type: String, required: true },
        customer_city: { type: String, required: true },
        customer_reference_agentid: { type: String, required: true },
        customer_state: { type: String, required: true },
        customer_streetaddress: { type: String, required: true },
        customer_houseno_flat: { type: String, required: true },
        // customer_orderid: { type: String, required: true } //need an array
        // password: { type: String, required: true }
    },
    {
        // versionKey: false
        timestamps: true
    }
);

export default mongoose.model<ICustomerModel>('Customer', CustomerSchema);
