import { NextFunction, Request, Response } from 'express';
import mongoose from 'mongoose';
import Logging from '../library/Logging';
import Order from '../models/Order';
import bcryptjs, { hash } from 'bcryptjs';
import signJWT from '../functions/signJTW';

const ordercreate = (req: Request, res: Response, next: NextFunction) => {
    const {
        order_address,
        order_agentid,
        order_customerid,
        order_customer_mobilenumber,
        order_modeofpay,
        order_placed_date,
        order_productid, // give an array of product
        order_storeid, // give an array of store
        order_price
    } = req.body;

    const orderdetails = new Order({
        order_id: new mongoose.Types.ObjectId(),
        order_address,
        order_agentid,
        order_customerid,
        order_customer_mobilenumber,
        order_modeofpay,
        order_placed_date,
        order_productid, // give an array of product
        order_storeid, // give an array of store
        order_price
    });

    return orderdetails
        .save()
        .then((order) => res.status(201).json({ order })) //agentdetails
        .catch((error) => res.status(500).json({ error }));
};

const orderget = (req: Request, res: Response, next: NextFunction) => {
    const orderId = req.params.orderId;

    return Order.findById(orderId)
        .then((order) => (order ? res.status(200).json({ order }) : res.status(404).json({ message: 'Not Found here' })))
        .catch((error) => res.status(500).json({ error }));
};

const ordergetall = (req: Request, res: Response, next: NextFunction) => {
    return Order.find()
        .then((orders) => res.status(200).json({ orders }))
        .catch((error) => res.status(500).json({ error }));
};

const orderupdate = (req: Request, res: Response, next: NextFunction) => {
    const orderId = req.params.orderId;

    return Order.findById(orderId)
        .then((order) => {
            if (order) {
                order.set(req.body);

                return order
                    .save()
                    .then((order) => res.status(201).json({ order }))
                    .catch((error) => res.status(500).json({ error }));
            } else {
                return res.status(404).json({ message: 'not found 2 here' });
            }
        })
        .catch((error) => res.status(500).json({ error }));
};

const orderdelete = (req: Request, res: Response, next: NextFunction) => {
    const orderId = req.params.orderId;

    return Order.findByIdAndDelete(orderId)
        .then((order) => (order ? res.status(201).json({ order, message: 'Deleted' }) : res.status(404).json({ message: 'not found 3 here' })))
        .catch((error) => res.status(500).json({ error }));
};

export default { ordercreate, orderget, ordergetall, orderupdate, orderdelete };
