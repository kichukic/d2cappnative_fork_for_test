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
  }
});

const User = mongoose.model('User', userSchema);

const findUser =()=>{
    return User.find()
}

 const CreateUser = (user)=>{
    return User.create(user)
}

export {findUser,CreateUser}
