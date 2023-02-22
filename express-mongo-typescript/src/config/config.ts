import dotenv from 'dotenv';

dotenv.config();

const MONGO_USERNAME = process.env.MONGO_USERNAME || '';
const MONGO_PASSWORD = process.env.MONGO_PASSWORD || '';
const MONGO_URL = `mongodb+srv://${MONGO_USERNAME}:${MONGO_PASSWORD}@cluster0.oa1jidl.mongodb.net/`;
// mongodb+srv://nithin:<password>@cluster0.oa1jidl.mongodb.net/?retryWrites=true&w=majority

// const MONGO_URL = process.env.DB_URL;

const SERVER_PORT = process.env.SERVER_PORT ? Number(process.env.SERVER_PORT) : 1337;
const SERVER_TOKEN_EXPIRETIME = process.env.SERVER_TOKEN_EXPIRETIME || 360000*24; //expire time in seconds
const SERVER_TOKEN_ISSUER = process.env.SERVER_TOKEN_ISSUER || "coolagent";
const SERVER_TOKEN_SECRET = process.env.SERVER_TOKEN_SECRET || "superencryptedsecret";

const config = {
    mongo: {
        url: MONGO_URL
    },
    server: {
        // hostname: SERVER_HOSTNAME,
        port: SERVER_PORT,
        token: {
            expireTime: SERVER_TOKEN_EXPIRETIME,
            issuer: SERVER_TOKEN_ISSUER,
            secret: SERVER_TOKEN_SECRET
        }
    }
};

export default  config;