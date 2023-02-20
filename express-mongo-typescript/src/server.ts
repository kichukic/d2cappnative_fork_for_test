import express from "express";
import http from 'http';
import mongoose from "mongoose";
import { config } from "./config/config";
import Logging from "./library/Logging";
// import { config } from './config/config';
import agentRoutes from './routes/Agent';

const router = express();

/** Connect to Mongo */
mongoose.set('strictQuery', false);
mongoose
    .connect(config.mongo.url, { retryWrites: true, w: 'majority' })
    .then(() => { 
        Logging.info('conected to mongoDB..');
        StartServer(); //works if connceted succesfully
    })
    .catch(error => {
        Logging.error(error);
    });

/** Only start the server if Mongo Connects */
const StartServer = () => {
    router.use((req, res, next) => {
        /**Log the Request */

        Logging.info(`Incomming -> Method: [${req.method}] - Url:[${req.url}] - IP: [${req.socket.remoteAddress}]`);

        res.on('finish',() =>{
            /**Log the response */
             Logging.info(`Incomming -> Method: [${req.method}] - Url:[${req.url}] - IP: [${req.socket.remoteAddress}] - Status: [${req.statusCode}]`);
        })

        next();
    })

    router.use(express.urlencoded({  extended: true}))
    router.use(express.json());

    /** Rules of our API */
    router.use((req, res, next) =>{

    res.header('Access-Control-Allow-Origin','*');
    res.header('Access-Control-Allow-Headers','Origin, X-Requested-With, Content-Type, Accept, Authorization');

    if(req.method == 'OPTIONS'){
        res.header('Access-Control-Allow-Methods', 'PUT, POST, PATCH, DELETE, GET');
        return res.status(200).json({});
    }

    next();
    });

    /**Routes */

    router.use('/agents', agentRoutes);

    /** Healthcheck */

    router.get('/getme', (req,res,next) =>{
        res.status(200).json({ message:'hello there'});
    });

    /** Error handling */
    router.use((req,res,next) => {
        const error = new Error('not found');
        Logging.error(error);

        return res.status(404).json( {message: error.message});
    });

    http.createServer(router).listen(config.server.port, () => Logging.info(`Server is running port ${config.server.port}.`));
};
