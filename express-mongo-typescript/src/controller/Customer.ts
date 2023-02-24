import { NextFunction, Request, Response } from 'express';
import mongoose from 'mongoose';
import Logging from '../library/Logging';
import Customer from '../models/Customer';
import bcryptjs, { hash } from 'bcryptjs';
import signJWT from '../functions/signJTW';

const customercreate = (req: Request, res: Response, next: NextFunction) => {
    const {
        customer_firstname,
        customer_secondname,
        customer_email,
        customer_mobile,
        customer_houseno_flat,
        customer_streetaddress,
        customer_city,
        customer_state,
        customer_pincode,
        customer_reference_agentid
    } = req.body;



        const customerdetails = new Customer({
            customer_id: new mongoose.Types.ObjectId(),
            customer_firstname,
            customer_secondname,
            customer_email,
            customer_mobile,
            customer_houseno_flat,
            customer_streetaddress,
            customer_city,
            customer_state,
            customer_pincode,
            customer_reference_agentid
        });

        return customerdetails
            .save()
            .then((customer) => res.status(201).json({ customer })) //agentdetails
            .catch((error) => res.status(500).json({ error }));
};

const customerget = (req: Request, res: Response, next: NextFunction) => {
    const customerId = req.params.customerId;

    return Customer.findById(customerId)
        .then((customer) => (customer ? res.status(200).json({ customer }) : res.status(404).json({ message: 'Not Found here' })))
        .catch((error) => res.status(500).json({ error }));
};

const customergetall = (req: Request, res: Response, next: NextFunction) => {
    return Customer.find()
        .then((customers) => res.status(200).json({ customers }))
        .catch((error) => res.status(500).json({ error }));
};

const customerupdate = (req: Request, res: Response, next: NextFunction) => {
    const customerId = req.params.customerId;

    return Customer.findById(customerId)
        .then((customer) => {
            if (customer) {
                customer.set(req.body);

                return customer
                    .save()
                    .then((customer) => res.status(201).json({ customer }))
                    .catch((error) => res.status(500).json({ error }));
            } else {
                return res.status(404).json({ message: 'not found 2 here' });
            }
        })
        .catch((error) => res.status(500).json({ error }));
};

const customerdelete = (req: Request, res: Response, next: NextFunction) => {
    const customerId = req.params.customerId;

    return Customer.findByIdAndDelete(customerId)
        .then((customer) => (customer ? res.status(201).json({ customer, message: 'Deleted' }) : res.status(404).json({ message: 'not found 3 here' })))
        .catch((error) => res.status(500).json({ error }));
};


// const customerlogin = (req: Request, res: Response, next: NextFunction) => {
//     let { agentemail, password } = req.body;

//     Agent.find({ agentemail })
//         .exec()
//         .then((agents: any) => {
//             if (agents.length !== 1) {
//                 return res.status(401).json({
//                     message: 'Unauthorized'
//                 });
//             }

//             bcryptjs.compare(password, agents[0].password, (error, result) => {
//                 if (error) {
//                     return res.status(401).json({
//                         message: 'Password Mismatch'
//                     });
//                 } else if (result) {
//                     signJWT(agents[0], (_error, token) => {
//                         if (_error) {
//                             return res.status(500).json({
//                                 message: _error.message,
//                                 error: _error
//                             });
//                         } else if (token) {
//                             return res.status(200).json({
//                                 message: 'Auth successful',
//                                 token: token,
//                                 agents: agents[0]
//                             });
//                         }
//                     });
//                 }
//             });
//         })
//         .catch((err) => {
//             console.log(err);
//             res.status(500).json({
//                 error: err
//             });
//         });
// };

// const deleteAgent = (req: Request, res: Response, next: NextFunction) => {};

export default { customercreate, customerget, customergetall, customerupdate, customerdelete, /** customerlogin */ };
