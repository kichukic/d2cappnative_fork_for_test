import mongoose from 'mongoose';
import dotenv from 'dotenv'
dotenv.config()



const database = mongoose.connect(process.env.MONGO_URI,{
    useNewUrlParser: true,
    useUnifiedTopology: true
}).then(()=>{
    console.log("database connection established")
}).catch((err)=>{
    console.log("error: " + errnode)
})

export {database as default}
