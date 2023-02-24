import { NextFunction, Request, Response } from 'express';
import mongoose from 'mongoose';
import Logging from '../library/Logging';
import Product from '../models/Product';
import bcryptjs, { hash } from 'bcryptjs';
import signJWT from '../functions/signJTW';

const productcreate = (req: Request, res: Response, next: NextFunction) => {
    const { product_name, product_image, product_price, product_category, product_description, product_store_id,product_storequantity, product_storeprice} = req.body;

        const productdetails = new Product({
            _id: new mongoose.Types.ObjectId(),
            product_name,
            product_image,
            product_price,
            product_category,
            product_description,
            product_store_id,
            product_storequantity,
            product_storeprice
        });

        return productdetails
            .save()
            .then((productdetails) => res.status(201).json({ productdetails })) //productdetails
            .catch((error) => res.status(500).json({ error }));
   
};

const productget = (req: Request, res: Response, next: NextFunction) => {
    const productId = req.params.productId;

    return Product.findById(productId)
        .then((product) => (product ? res.status(200).json({ product }) : res.status(404).json({ message: 'Not Found here' })))
        .catch((error) => res.status(500).json({ error }));
};

const productgetall = (req: Request, res: Response, next: NextFunction) => {
    return Product.find()
        .then((products) => res.status(200).json({ products }))
        .catch((error) => res.status(500).json({ error }));
};

const productupdate = (req: Request, res: Response, next: NextFunction) => {
    const productId = req.params.productId;

    return Product.findById(productId)
        .then((product) => {
            if (product) {
                product.set(req.body);

                return product
                    .save()
                    .then((product) => res.status(201).json({ product }))
                    .catch((error) => res.status(500).json({ error }));
            } else {
                return res.status(404).json({ message: 'not found 2 here' });
            }
        })
        .catch((error) => res.status(500).json({ error }));
};

const productdelete = (req: Request, res: Response, next: NextFunction) => {
    const productId = req.params.productId;

    return Product.findByIdAndDelete(productId)
        .then((product) => (product ? res.status(201).json({ product, message: 'Deleted' }) : res.status(404).json({ message: 'not found 3 here' })))
        .catch((error) => res.status(500).json({ error }));
};



// const deleteAgent = (req: Request, res: Response, next: NextFunction) => {};

export default { productcreate, productgetall, productget, productupdate, productdelete };

