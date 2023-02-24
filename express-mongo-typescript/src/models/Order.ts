import mongoose, { Document, now, Schema } from 'mongoose';

export interface IOrderModel extends Document {}

const OrderSchema: Schema = new Schema(
    {
        order_address: { type: String, required: true },
        order_agentid: { type: String, required: true },
        order_customerid: { type: String, required: true },
        order_customer_mobilenumber: { type: String, required: true },
        order_modeofpay: { type: String, required: true },
        order_placed_date: { type: String, required: true },
        order_productid: { type: String, required: true }, // give an array of product
        order_storeid: { type: String, required: true }, // give an array of store
        order_price: { type: String, required: true }
    },
    {
        // versionKey: false
        timestamps: true
    }
);

export default mongoose.model<IOrderModel>('Order', OrderSchema);
