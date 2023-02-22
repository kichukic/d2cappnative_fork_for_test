import jwt from 'jsonwebtoken';
import config  from '../config/config';
import logging from '../library/Logging';
import { Request, Response, NextFunction } from 'express';


const extractJWT = (req: Request, res: Response, next: NextFunction) => {
    logging.info( 'Auth::Validating token');

    let token = req.headers.authorization?.split(' ')[1]; 

    if (token) {
        jwt.verify(token, config.server.token.secret, (error, decoded) => {
            if (error) {
                return res.status(404).json({
                    message: error.message,
                    error
                });
            } else {
                res.locals.jwt = decoded;
                next();
            }
        });
    } else {
        return res.status(401).json({
            message: 'Unauthorized token'
        });
    }
};

export default extractJWT;
