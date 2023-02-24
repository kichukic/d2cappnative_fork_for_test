import { NextFunction, Request, Response } from 'express';
import mongoose from 'mongoose';
import Logging from '../library/Logging';
import Store from '../models/Store';
import bcryptjs, { hash } from 'bcryptjs';
import signJWT from '../functions/signJTW';

const storecreate = (req: Request, res: Response, next: NextFunction) => {
    const {   
        store_name,
        store_mobile,
        store_email,
        store_address,
        store_deliverable_pincode, //make an array
        store_available_productid, //make an array
        password
     } = req.body;

    bcryptjs.hash(password, 10, (hashError, hash) => {
        if (hashError) {
            return res.status(401).json({
                message: hashError.message,
                error: hashError
            });
        }

        const storedetails = new Store({
            store_id: new mongoose.Types.ObjectId(),
            store_name,
            store_mobile,
            store_email,
            store_address,
            store_deliverable_pincode,
            store_available_productid,
            password: hash
        });

        return storedetails
            .save()
            .then((store) => res.status(201).json({ store })) //agentdetails
            .catch((error) => res.status(500).json({ error }));
    });
};

const storeget = (req: Request, res: Response, next: NextFunction) => {
    const storeId = req.params.storeId;

    return Store.findById(storeId)
        .then((store) => (store ? res.status(200).json({ store }) : res.status(404).json({ message: 'Not Found here' })))
        .catch((error) => res.status(500).json({ error }));
};

const storegetall = (req: Request, res: Response, next: NextFunction) => {
    return Store.find()
        .then((store) => res.status(200).json({ store }))
        .catch((error) => res.status(500).json({ error }));
};

const storeupdate = (req: Request, res: Response, next: NextFunction) => {
    const storeId = req.params.storeId;

    return Store.findById(storeId)
        .then((store) => {
            if (store) {
                store.set(req.body);

                return store
                    .save()
                    .then((store) => res.status(201).json({ store }))
                    .catch((error) => res.status(500).json({ error }));
            } else {
                return res.status(404).json({ message: 'not found 2 here' });
            }
        })
        .catch((error) => res.status(500).json({ error }));
};

const storedelete = (req: Request, res: Response, next: NextFunction) => {
    const storeId = req.params.storeId;

    return Store.findByIdAndDelete(storeId)
        .then((store) => (store ? res.status(201).json({ store, message: 'Deleted' }) : res.status(404).json({ message: 'not found 3 here' })))
        .catch((error) => res.status(500).json({ error }));
};


const storelogin = (req: Request, res: Response, next: NextFunction) => {
    let { store_email, password } = req.body;

    Store.find({ store_email })
        .exec()
        .then((stores: any) => {
            if (stores.length !== 1) {
                return res.status(401).json({
                    message: 'Unauthorized'
                });
            }

            bcryptjs.compare(password, stores[0].password, (error, result) => {
                if (error) {
                    return res.status(401).json({
                        message: 'Password Mismatch'
                    });
                } else if (result) {
                    signJWT(stores[0], (_error, token) => {
                        if (_error) {
                            return res.status(500).json({
                                message: _error.message,
                                error: _error
                            });
                        } else if (token) {
                            return res.status(200).json({
                                message: 'Auth successful',
                                token: token,
                                stores: stores[0]
                            });
                        }
                    });
                }
            });
        })
        .catch((err) => {
            console.log(err);
            res.status(500).json({
                error: err
            });
        });
};

// const deleteAgent = (req: Request, res: Response, next: NextFunction) => {};

export default { storecreate, storeget, storegetall, storeupdate, storedelete, storelogin};
