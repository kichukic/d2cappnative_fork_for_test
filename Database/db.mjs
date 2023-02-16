import mongoose from 'mongoose';


const database = mongoose.connect("mongodb://localhost:27017/IOT",{
    useNewUrlParser: true,
    useUnifiedTopology: true
}).then(()=>{
    console.log("database connection established")
}).catch((err)=>{
    console.log("error: " + errnode)
})

export {database as db}