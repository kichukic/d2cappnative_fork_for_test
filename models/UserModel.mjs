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

const User = mongoose.model('User', userSchema);

const findUser =(email)=>{
    return User.findOne({email})
}

 const CreateUser = (user)=>{
    return User.create(user)
}

const ForgotPassword = async(email,token,expiresIn)=>{
  const user = await User.findOneAndUpdate({email},
    {reserPasswordToken:token,tokenExpiry:expiresIn},
    {new:true})
    return user;
}


export {findUser,CreateUser,ForgotPassword}







