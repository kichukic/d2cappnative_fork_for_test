import mongoose from 'mongoose'
import database from '../Database/db.mjs'


const userSchema = new mongoose.Schema({
  firstName: {
    type: String,
    required: true
  },
  lastName: {
    type: String,
    required: true
  },
  email: {
    type: String,
    required: true,
    unique: true,
    lowercase: true
  },
  password: {
    type: String,
    required: true
  },
  reserPasswordToken:{
    type: String,
    default:null
  },
  tokenExpiry:{
    type : Date,
    default:null
  }
});


const Users = mongoose.model('User', userSchema);

const findUser =(email)=>{
    return Users.findOne({email})
}

 const CreateUser = (user)=>{
    return Users.create(user)
}


const set_password =async (email,token,password,expiresIn)=>{
  const user_update =await  Users.findOneAndUpdate({email:email},
   {password:password,reserPasswordToken:token,tokenExpiry:expiresIn} )
   return user_update
}



const ForgotPassword = async(email,token,expiresIn)=>{
  const user = await Users.findOneAndUpdate({email:email},
    {reserPasswordToken:token,tokenExpiry:expiresIn},
    {new:true})
    return user;
}


export {findUser,CreateUser,ForgotPassword,set_password}
export {Users}







